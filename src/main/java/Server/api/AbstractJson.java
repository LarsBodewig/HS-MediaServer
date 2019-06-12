package Server.api;

public abstract class AbstractJson {

	@Override
	public String toString() {
		return Api.getGson().toJson(this);
	}
}
