package Server.api.facebook;

import Server.api.AbstractJson;

class FacebookPost extends AbstractJson {

	String author;
	String avi;
	String post;
	String photo;
	String source;
	String timestamp;
	int likes;
	int comments;

	FacebookPost(String author, String post, String source, String timestamp, String photo, String avi, int likes,
			int comments) {
		this.author = author;
		this.avi = avi;
		this.post = post;
		this.photo = photo;
		this.source = source;
		this.timestamp = timestamp;
		this.likes = likes;
		this.comments = comments;
	}

	public FacebookPost() {
	}
}