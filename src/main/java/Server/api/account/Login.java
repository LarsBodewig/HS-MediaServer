package Server.api.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import Server.db.Database;

@Path("/account/login")
public class Login {

	// @POST
	@GET
	// @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post( // @FormParam("email")
			@QueryParam("email") String email, // @FormParam("password")
			@QueryParam("password") String password) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		if (Account.userExists(email)) {
			Account.clearUserTokens(email);
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!loginValid(email, password)) {
			return Response.status(Response.Status.UNAUTHORIZED).header("Access-Control-Allow-Origin", "*").build();
		}
		String token = createLoginToken(email);
		if (token == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		return Response.ok(new Gson().toJson(wrap("token", token))).header("Access-Control-Allow-Origin", "*").build();
	}

	private static JsonElement wrap(String key, Object value) {
		JsonObject res = new JsonObject();
		res.add(key, new Gson().toJsonTree(value));
		return res;
	}

	private static String createLoginToken(String email) {
		return email + System.currentTimeMillis();
	}

	private static boolean loginValid(String email, String password) {
		return password.equals("def");
	}
}