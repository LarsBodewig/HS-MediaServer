package Server.api.youtube;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import Server.api.account.Account;

@Path("/youtube/{prefix:(user|channel)}/{user}")
public class Youtube {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("prefix") String prefix, @PathParam("user") String user, @QueryParam("to") String to,
			@HeaderParam("auth_token") String token) {
		if (!Account.checkLoginToken(token)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		List<Video> results = new ArrayList<>();
		try {
			results = getVideos(results, "https://youtube.com/" + prefix + "/" + user + "/videos", to);
			return Response.ok(new Gson().toJson(results)).build();
		} catch (IOException e) {
			e.printStackTrace();
			if (results.isEmpty()) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			} else {
				return Response.status(Response.Status.PARTIAL_CONTENT).entity(new Gson().toJson(results)).build();
			}
		}
	}

	private List<Video> getVideos(List<Video> results, String url, String to) throws IOException {
		Connection.Response res = Jsoup.connect(url).method(Method.GET).execute();
		Document doc = res.parse();
		Element avatarElement = doc.getElementsByClass("channel-header-profile-image").first();
		String avatar = avatarElement.attr("src");
		String author = avatarElement.attr("title");
		Elements videos = doc.getElementsByClass("channels-content-item");
		for (Element video : videos) {
			Video result = new Video();
			result.avatar = avatar;
			result.author = author;
			result.thumbnail = video.getElementsByClass("yt-thumb-clip").first().child(0).attr("src");
			Element titleElement = video.getElementsByClass("yt-lockup-title").first().child(0);
			result.source = titleElement.attr("abs:href");
			result.id = video.getElementsByClass("yt-lockup-video").first().attr("data-context-item-id");
			result.title = titleElement.ownText();
			Element metadataElement = video.getElementsByClass("yt-lockup-meta-info").first();
			Element viewsElement = metadataElement.child(0);
			if (viewsElement != null) {
				String views = viewsElement.ownText();
				if (views.contains("Keine")) {
					result.views = 0L;
				} else {
					views = views.substring(0, views.indexOf(" ")).replace(".", "");
					result.views = Long.parseLong(views);
				}
			}
			String timestamp = metadataElement.child(1).ownText();
			if (timestamp.contains("live")) {
				result.live = true;
			} else {
				String[] duration = video.getElementsByClass("video-time").first().child(0).ownText().split(":");
				long seconds = 0;
				for (int i = 0; i < duration.length; i++) {
					seconds += Long.parseLong(duration[duration.length - 1 - i]) * Math.pow(60, i);
				}
				result.duration = seconds;
				result.timestamp = parseTime(timestamp);
			}
			results.add(result);
			if (result.id.equals(to)) {
				return results;
			}
		}
		Element moreResults = doc.getElementsByClass("load-more-button").first();
		if (moreResults != null && to != null) {
			return parseJson(results, "https://www.youtube.com" + moreResults.attr("data-uix-load-more-href"), to,
					res.cookies(), res.headers());
		}
		return results;
	}

	private List<Video> parseJson(List<Video> results, String url, String to, Map<String, String> cookies,
			Map<String, String> headers) throws IOException {
//		Connection.Response res = Jsoup.connect(url).ignoreContentType(true).cookies(cookies).headers(headers)
//				.method(Method.GET).execute();
//		String doc = res.body();
		return results;
	}

	private Long parseTime(String timestamp) {
		if (timestamp.contains("live")) {
			return null;
		} else {
			LocalDateTime dateTime = LocalDateTime.now();
			TemporalUnit unit = null;
			long amount = Long.parseLong(timestamp.split(" ")[1]);
			switch (timestamp.substring(timestamp.lastIndexOf(' ') + 1)) {
			case "Jahren":
			case "Jahr":
				dateTime = dateTime.minusYears(amount);
				break;
			case "Monaten":
			case "Monat":
				dateTime = dateTime.minusMonths(amount);
				break;
			case "Wochen":
			case "Woche":
				dateTime = dateTime.minusWeeks(amount);
				break;
			case "Tagen":
			case "Tag":
				unit = ChronoUnit.DAYS;
				break;
			case "Stunden":
			case "Stunde":
				unit = ChronoUnit.HOURS;
				break;
			case "Minuten":
			case "Minute":
				unit = ChronoUnit.MINUTES;
				break;
			case "Sekunden":
			case "Sekunde":
				unit = ChronoUnit.SECONDS;
				break;
			}
			if (unit != null) {
				TemporalAmount ta = Duration.of(amount, unit);
				dateTime = dateTime.minus(ta);
			}
			if (unit == null || unit == ChronoUnit.DAYS) {
				dateTime = dateTime.toLocalDate().atStartOfDay();
			}
			return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		}
	}
}
