package Server.api.account;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import Server.db.Database;
import Server.db.Hash;

@Path("/account/login")
public class Login {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(@HeaderParam("email") String email, @HeaderParam("password") String password) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
		AccountObject acc = Account.getAccount(email);
		if (acc == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if (!loginValid(password, acc.hash)) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		String token = createLoginToken(acc.id);
		if (token == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(new Gson().toJson(wrap("token", token))).build();
	}

	private static JsonElement wrap(String key, Object value) {
		JsonObject res = new JsonObject();
		res.add(key, new Gson().toJsonTree(value));
		return res;
	}

	private static String createLoginToken(int id) {
		String token = null;
		do {
			token = Hash.randomToken();
		} while (Account.checkLoginToken(token));
		Database.insertToken("login_token", id, token,
				"DATE_ADD(NOW(), INTERVAL " + Account.LOGIN_TOKEN_TIMEOUT_AMOUNT + " HOUR)");
		return token;
	}

	private static boolean loginValid(String password, String hash) {
		return Hash.checkPassword(password, hash);
	}
}