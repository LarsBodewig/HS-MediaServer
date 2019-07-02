package Server.api.twitter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

@Path("twitter" + "/{user}")
public class Twitter {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("user") String user) throws IOException, ParseException {
		return new Gson()
				.toJson(getTweets(new ArrayList<Tweet>(), user, "https://twitter.com/" + user, "1088253306592468997"));
	}

	private List<Tweet> getTweets(List<Tweet> results, String user, String from, String to)
			throws IOException, ParseException {
		Document doc = Jsoup.connect(from).userAgent(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
				.get();
		Elements tweets = doc.getElementsByClass("tweet");
		for (Element tweet : tweets) {
			if (to.equals(tweet.getElementsByClass("tweet-text").attr("data-id"))) {
				System.out.println(tweet.outerHtml());
				return results;
			}
			Tweet result = new Tweet();
			result.author = tweet.getElementsByClass("username").first().ownText();
			result.post = tweet.getElementsByClass("tweet-text").first().child(0).html();
			// result.timestamp = new
			// SimpleDateFormat().parse(tweet.getElementsByClass("timestamp").first().ownText()).getTime();
			result.source = tweet.attr("abs:href");
			results.add(result);
		}
		return getTweets(results, user, doc.getElementsByClass("w-button-more").first().child(0).attr("abs:href"), to);
	}

	static class Tweet {
		public String author;
		public String avi;
		public String post;
		public long timestamp;
		public long likes;
		public String photo;
		public long comments;
		public String source;
	}
}
