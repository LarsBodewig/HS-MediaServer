package Server.api.account;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import Server.api.twitter.TwitterItem;
import Server.db.Database;

public class AccountObject {
	public int id;
	public String email;
	public String hash;
	public boolean verified;
	public String securityCode;
	public TreeFolder items;

	public AccountObject(Integer id, String email, String hash, Boolean verified, String securityCode) {
		this.id = id;
		this.email = email;
		this.hash = hash;
		this.verified = verified;
		this.securityCode = securityCode;
		items = buildTreeFolder(id);
	}

	public static TreeFolder buildTreeFolder(int id) {
		TreeFolder root = new TreeFolder(null, 0, null, null, null);
		Map<Integer, TreeFolder> folders = Database.getNodes(id, "folder", "parent_id", TreeFolder.class);
		folders.forEach((k, v) -> {
			if (v.parentId == 0) {
				root.add(v);
			} else {
				folders.get(v.parentId).add(v);
			}
		});
		Map<Integer, TwitterItem> twitter = Database.getNodes(id, "item_twitter", "folder_id", TwitterItem.class);
		twitter.forEach((k, v) -> {
			if (v.folderId == 0) {
				root.add(v);
			} else {
				folders.get(v.folderId).add(v);
			}
		});
		return root;
	}

	public static class TreeFolder implements Node {
		public final String type = "folder";
		public Integer id;
		public Integer parentId;
		public String title;
		public String url;
		public Set<Node> children;

		public TreeFolder(Integer acc_id, Integer id, Integer parent_id, String title, String url) {
			this.id = id;
			this.parentId = parent_id;
			this.title = title;
			this.url = url;
			this.children = new TreeSet<>(new Comparator<Node>() {
				@Override
				public int compare(Node o1, Node o2) {
					if (o1.getClass().equals(o2.getClass())) {
						return o1.getId().compareTo(o2.getId());
					} else if (o1 instanceof TreeFolder) {
						return -1;
					} else if (o2 instanceof TreeFolder) {
						return 1;
					} else {
						return o1.getTitle().compareTo(o2.getTitle());
					}
				}
			});
		}

		public void add(Node child) {
			if (children != null) {
				this.children.add(child);
			}
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

	public static interface Node {
		String getTitle();

		Integer getId();
	}
}
