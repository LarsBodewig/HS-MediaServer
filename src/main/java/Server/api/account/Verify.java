package Server.api.account;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Server.db.Database;
import Server.mail.Mail;

@Path("/account/verify")
public class Verify {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(@HeaderParam("code") String token) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
		if (!checkVerifyToken(token)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		if (!verifyAccount(token)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok().build();
	}

	private static boolean verifyAccount(String token) {
		AccountObject acc = Database.getAccount("id", Database.getAccountId("verify_token", token));
		Database.updateAccount("id", acc.id, "verified", true);
		Database.deleteToken("verify_token", "acc_id ='" + acc.id + "'");
		Account.createSecurityToken(acc.email);
		acc = Database.getAccount("id", acc.id);
		return acc.verified && sendSecurityEmail(acc.email, acc.securityCode);
	}

	private static boolean sendSecurityEmail(String email, String code) {
		return Mail.sendSecurityMail(email, code);
	}

	private static boolean checkVerifyToken(String token) {
		return Database.tokenExists("verify_token", token);
	}
}
