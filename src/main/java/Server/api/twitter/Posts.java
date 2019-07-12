package Server.api.twitter;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

@Path("twitter" + "/{user}/" + "posts")
public class Posts {

	private static final String TWITTER = "https://twitter.com";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("user") String user, @QueryParam("from") String from, @QueryParam("to") String to,
			@QueryParam("replies") @DefaultValue("true") boolean replies) throws IOException, ParseException {
		List<Tweet> results = new ArrayList<>();
		String url = TWITTER + "/" + user;
		if (from != null && from.matches("^[-,0-9]+$")) {
			url += "?max_id=" + from;
		}
		if (to != null && to.matches("^[-,0-9]+$")) {
			results = getTweets(results, url, to, replies);
		} else {
			results = getTweets(results, url, null, replies);
		}
		return new Gson().toJson(results);
	}

	private List<Tweet> getTweets(List<Tweet> results, String from, String to, boolean replies)
			throws IOException, ParseException {
		Document doc = null;
		try {
			doc = Jsoup.connect(from).userAgent(
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
					.get();
		} catch (HttpStatusException hse) {
			if (hse.getStatusCode() == 429) {
				System.out.println("Catched Exception: Exceeded twitter rate limit - on cooldown."
						+ System.lineSeparator() + "\t" + hse);
				return results;
			} else {
				throw hse;
			}
		}
		Elements tweets = doc.getElementsByClass("tweet");
		for (Element tweet : tweets) {
			Tweet result = new Tweet();
			result.id = tweet.getElementsByClass("tweet-text").attr("data-id");
			result.author = tweet.getElementsByClass("username").first().ownText();
			result.source = TWITTER + tweet.attr("href");
			Element post = tweet.getElementsByClass("tweet-text").first();
			result.post = post.child(0).html();
			String dateString = tweet.getElementsByClass("timestamp").first().child(0).ownText();
			LocalDateTime date = null;
			if (dateString.matches("^[1-5]?[0-9](m|h|s)$")) {
				TemporalUnit unit = null;
				switch (dateString.charAt(dateString.length() - 1)) {
				case 'h':
					unit = ChronoUnit.HOURS;
					break;
				case 'm':
					unit = ChronoUnit.MINUTES;
					break;
				case 's':
					unit = ChronoUnit.SECONDS;
					break;
				}
				String amountString = dateString.substring(0, dateString.length() - 1);
				TemporalAmount amount = Duration.of(Long.parseLong(amountString), unit);
				date = LocalDateTime.now().minus(amount);
			} else if (dateString.matches("^[A-Z][a-z]{2} [1-3]?[0-9]$")) {
				MonthDay md = MonthDay.parse(dateString, DateTimeFormatter.ofPattern("MMM d", Locale.US));
				if (md.atYear(LocalDate.now().getYear()).isBefore(LocalDate.now())) {
					date = md.atYear(LocalDate.now().getYear()).atStartOfDay();
				} else {
					date = md.atYear(LocalDate.now().minus(1L, ChronoUnit.YEARS).getYear()).atStartOfDay();
				}
			} else if (dateString.matches("^[1-3]?[0-9] [A-Z][a-z]{2} [0-9]{2}$")) {
				date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("d MMM yy", Locale.US)).atStartOfDay();
			}
			result.timestamp = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

			Element reply = tweet.getElementsByClass("tweet-reply-context").first();
			if (reply != null) {
				result.reply = reply.getElementsByTag("a").first().text();
			}
			Element retweet = tweet.getElementsByClass("tweet-social-context").first();
			result.retweet = retweet != null;
			result.avatar = tweet.getElementsByClass("avatar").first().child(0).child(0).attr("src");
			Element media = post.getElementsByAttribute("data-pre-embedded").first();
			if (media != null) {
				result.media = media.attr("data-url");
			}
			if (result.reply != null && replies || result.reply == null) {
				results.add(result);
			}
			if (result.id.equals(to)) {
				return results;
			}
		}

		Element moreResults = doc.getElementsByClass("w-button-more").first();
		if (moreResults != null && to != null) {
			return getTweets(results, doc.getElementsByClass("w-button-more").first().child(0).attr("abs:href"), to,
					replies);
		} else {
			return results;
		}
	}

	static class Tweet {
		public String id;
		public String author;
		public String source;
		public String post;
		public long timestamp;
		public String reply;
		public boolean retweet;
		public String avatar;
		public String media;
	}
}
