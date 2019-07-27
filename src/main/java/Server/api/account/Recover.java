package Server.api.account;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Server.db.Database;
import Server.db.Hash;
import Server.mail.Mail;

@Path("/account/recover")
public class Recover {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(@HeaderParam("email") String email, @HeaderParam("code") String code,
			@HeaderParam("password") String password) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
		AccountObject acc = Account.getAccount(email);
		if (acc != null) {
			Account.clearUserTokens(acc.id);
		} else {
			return Response.status(Response.Status.CONFLICT).build();
		}
		if (!Account.securityCodeValid(acc, code)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		if (!Account.validPassword(password)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		if (!changePassword(email, password)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok().build();
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