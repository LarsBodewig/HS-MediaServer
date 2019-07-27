package Server.api.twitter;

import Server.api.account.AccountObject.Node;

public class TwitterItem implements Node {
	public final String type = "twitter";
	public Integer id;
	public Integer folderId;
	public String title;
	public String url;
	public String source;

	public TwitterItem(Integer acc_id, Integer id, Integer folder_id, String title, String url, String username) {
		this.id = id;
		this.folderId = folder_id;
		this.title = title;
		this.url = url;
		this.source = "/twitter/" + username + "/posts/";
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Integer getId() {
		return id;
	}
}
