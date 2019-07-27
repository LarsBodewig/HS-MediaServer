package Server.api.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import Server.db.Database;

@Path("/account/update")
public class Update {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@HeaderParam("auth_token") String token) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
		if (!Account.checkLoginToken(token)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		AccountObject account = Database.getAccount("id", Database.getAccountId("login_token", token));
		if (account == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(new Gson().toJson(account)).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(AccountObject account, @HeaderParam("auth_token") String token) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
		if (!Account.checkLoginToken(token)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		if (!updateAccount(token, account)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok().build();
	}

	private static boolean updateAccount(String token, AccountObject account) {
		// Database.executeUpdate("UPDATE account SET WHERE id = " + account.id);
		return true;
	}
}
