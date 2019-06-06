package Server.api.facebook;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Server.api.Api;

@Path(Facebook.BASEURL + "/{pageid}")
public class FacebookPage {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getPosts(@PathParam("pageid") String pageid) {
		StringBuilder firstText = new StringBuilder();
		Document doc = Api.connect(Facebook.SOURCEBASEURL + "/" + pageid);
		Element el = doc.getElementById("pagelet_timeline_main_column");
		Elements els = el.getElementsByAttributeValue("data-testid", "post_message");
		els.first().getElementsByTag("p").forEach(element -> {
			firstText.append(element.text() + System.lineSeparator());
		});
		return firstText.toString();
	}
}
