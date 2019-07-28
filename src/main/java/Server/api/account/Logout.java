package Server.api.account;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Server.db.Database;

@Path("/account/logout")
public class Logout {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(@HeaderParam("auth_token") String token) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
		if (!Account.checkLoginToken(token)) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		AccountObject acc = Database.getAccount("id", Database.getAccountId("login_token", token));
		if (!deleteLoginToken(acc.id, token)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok().build();
	}

	private static boolean deleteLoginToken(int id, String token) {
		return Database.deleteToken("login_token", "acc_id = " + id + " && login_token = '" + token + "'") != null;
	}
}
