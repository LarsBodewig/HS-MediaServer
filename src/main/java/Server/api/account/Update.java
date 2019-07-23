package Server.api.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import Server.db.Database;

@Path("/account/update")
public class Update {

	@GET
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response get( // @FormParam("auth_token")
			@QueryParam("auth_token") String token) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		if (!Account.checkLoginToken(token)) {
			return Response.status(Response.Status.FORBIDDEN).header("Access-Control-Allow-Origin", "*").build();
		}
		AccountObject account = getAccount(token);
		if (account == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		return Response.ok(new Gson().toJson(account)).header("Access-Control-Allow-Origin", "*").build();
	}

	// @POST
	@GET
	//@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(AccountObject account, // @FormParam("auth_token")
			@QueryParam("auth_token") String token) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		if (!Account.checkLoginToken(token)) {
			return Response.status(Response.Status.FORBIDDEN).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!updateAccount(token, account)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	static class AccountObject {

	}

	private static AccountObject getAccount(String token) {
		return new AccountObject();
	}

	private static boolean updateAccount(String token, AccountObject account) {
		// change
		return true;
	}
}
