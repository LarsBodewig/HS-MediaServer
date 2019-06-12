package Server.api.facebook;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Server.api.Api;

@Path(Facebook.BASE_URL + "/{pageid}")
public class FacebookPage {

	private static final DateTimeFormatter fromFbPattern = DateTimeFormatter.ofPattern("dd.MM.yy, HH:mm")
			.withZone(ZoneId.of("Europe/Berlin"));

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getPosts(@PathParam("pageid") String pageid) {
		Document doc = Api.connect(Facebook.SOURCE_BASE_URL + "/" + pageid);
		Element timeline = doc.getElementById("pagelet_timeline_main_column");
		Elements posts = timeline.getElementsByClass("userContentWrapper");
		List<FacebookPost> facebookPosts = new ArrayList<>(posts.size());

		for (Element post : posts) {
			try {
				Element author = post.getElementsByTag("img").first();
				Element subtitle = post.getElementsByAttributeValue("data-testid", "story-subtitle").first();
				Element source = subtitle.getElementsByTag("a").first();
				Element timestamp = source.getElementsByTag("abbr").first();

				Element message = post.getElementsByAttributeValue("data-testid", "post_message").first();
				Element messageSibling = message.nextElementSibling();
				Element photo = null;
				if (messageSibling != null) {
					photo = messageSibling.getElementsByTag("img").first();
				}
				Element avi = null; // TODO

				Element interaction = post.getElementsByTag("form").first();
				Element feedback = interaction
						.getElementsByAttributeValue("data-testid", "fbFeedStoryUFI/feedbackSummary").first();

				Element reactionsParent = null; // feedback.getElementsByAttributeValue("data-testid",
												// "UFI2ReactionsCount/sentenceWithSocialContext").first();
				Element reactions = new Element("p").appendText("-1"); // reactionsParent.child(0).child(0);
				Element comments = new Element("p").appendText("-1 Kommentare"); // feedback.getElementsByAttributeValue("data-testid",
																					// "UFI2CommentsCount/root").first();

				FacebookPost postJson = new FacebookPost();
				postJson.author = author.attr("aria-label");
				postJson.source = Facebook.SOURCE_BASE_URL + source.attr("href");
				ZonedDateTime timestampDate = null;
				try {
					timestampDate = ZonedDateTime.parse(timestamp.attr("title"), fromFbPattern);
				} catch (DateTimeParseException e) {
					e.printStackTrace();
					System.out.println(timestamp.attr("title"));
				}
				postJson.timestamp = timestampDate.toOffsetDateTime().toString();
				postJson.post = message.getElementsByTag("p").outerHtml();
				postJson.photo = (photo != null) ? photo.attr("src") : null;
				postJson.avi = (avi != null) ? avi.attr("src") : null; // TODO check if src
				postJson.likes = Integer.parseInt(reactions.text());
				String commentsLabel = comments.text();
				String commentsCount = commentsLabel.substring(0, commentsLabel.indexOf(' '));
				postJson.comments = Integer.parseInt(commentsCount);

				facebookPosts.add(postJson);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(post.outerHtml());
			}
		}

		return Api.arrayToJson(facebookPosts.toArray());
	}
}
