package Server.api.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Server.db.Database;
import Server.db.Hash;
import Server.mail.Mail;

@Path("/account/recover")
public class Recover {

	// @POST
	@GET
	// @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post( // @FormParam("email")
			@QueryParam("email") String email, // @FormParam("code")
			@QueryParam("code") String code, // @FormParam("password")
			@QueryParam("password") String password) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		AccountObject acc = Account.getAccount(email);
		if (acc != null) {
			Account.clearUserTokens(acc.id);
		} else {
			return Response.status(Response.Status.CONFLICT).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!Account.securityCodeValid(acc, code)) {
			return Response.status(Response.Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!Account.validPassword(password)) {
			return Response.status(Response.Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!changePassword(email, password)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	private static boolean changePassword(String email, String password) {
		String hash = Hash.createHash(password);
		Database.updateAccount("email", email, "pw_hash", hash);
		String token = Account.createSecurityToken(email);
		return sendRecoverEmail(email, token);
	}

	private static boolean sendRecoverEmail(String email, String code) {
		return Mail.sendRecoverMail(email, code);
	}
}