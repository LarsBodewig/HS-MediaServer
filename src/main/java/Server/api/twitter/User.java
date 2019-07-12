package Server.api.twitter;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.gargoylesoftware.css.parser.javacc.ParseException;
import com.google.gson.Gson;

@Path("twitter" + "/{user}/" + "user")
public class User {

	private static final String TWITTER = "https://twitter.com";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("user") String user) throws IOException, ParseException {
		String url = TWITTER + "/" + user;
		Document doc = Jsoup.connect(url).userAgent(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
				.get();
		UserInfo userInfo = new UserInfo();
		Element profile = doc.getElementsByClass("profile").first();
		Element details = profile.getElementsByClass("profile-details").first();
		userInfo.avatar = details.getElementsByClass("avatar").first().child(0).attr("src");
		userInfo.fullname = details.getElementsByClass("fullname").first().ownText();
		userInfo.username = details.getElementsByClass("username").first().text();
		String locationString = details.getElementsByClass("location").first().ownText();
		if (!locationString.equals("")) {
			userInfo.location = locationString;
		}
		String bioString = details.getElementsByClass("bio").first().child(0).html();
		if (!bioString.trim().equals("")) {
			userInfo.bio = bioString;
		}
		String urlString = details.getElementsByClass("url").first().getElementsByTag("a").first().attr("data-url");
		if (!urlString.equals("")) {
			userInfo.url = urlString;
		}
		Element stats = profile.getElementsByClass("profile-stats").first();
		String tweetStat = stats.getElementsByClass("statnum").eq(0).first().ownText();
		userInfo.tweets = Long.parseLong(tweetStat.replace(",", ""));
		String followingStat = stats.getElementsByClass("statnum").eq(1).first().ownText();
		userInfo.following = Long.parseLong(followingStat.replace(",", ""));
		String followersStat = stats.getElementsByClass("statnum").eq(2).first().ownText();
		userInfo.followers = Long.parseLong(followersStat.replace(",", ""));
		return new Gson().toJson(userInfo);
	}

	static class UserInfo {
		public String avatar;
		public String fullname;
		public String username;
		public String location;
		public String bio;
		public String url;
		public long tweets;
		public long following;
		public long followers;
	}
}