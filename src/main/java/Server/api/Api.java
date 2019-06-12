package Server.api;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;

public abstract class Api {

	private static Gson gson;

	public static Document connect(String url) {
		try {
			return Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
			return new Document("");
		}
	}

	static Gson getGson() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}
	
	public static String arrayToJson(Object[] o) {
		return getGson().toJson(o);
	}
}
