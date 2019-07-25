package Server.api.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Server.db.Database;
import Server.mail.Mail;

@Path("/account/verify")
public class Verify {

	// @POST
	@GET
	// @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post( // @FormParam("verify_token")
			@QueryParam("verify_token") String token) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		if (!checkVerifyToken(token)) {
			return Response.status(Response.Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!verifyAccount(token)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
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
