package Server.api.youtube;

import Server.api.account.AccountObject.Node;

public class YoutubeItem implements Node {
	public final String type = "youtube";
	public Integer id;
	public Integer folderId;
	public String title;
	public String url;
	public String source;

	public YoutubeItem(Integer acc_id, Integer id, Integer folder_id, String title, String url, String prefix,
			String username) {
		this.id = id;
		this.folderId = folder_id;
		this.title = title;
		this.url = url;
		this.source = "/youtube/" + prefix + "/" + username;
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
