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

@Path("/account/register")
public class Register {

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
		AccountObject acc = Account.getAccount(email);
		if (acc != null) {
			Account.clearUserTokens(acc.id);
			return Response.status(Response.Status.CONFLICT).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!validEmail(email)) {
			return Response.status(Response.Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!Account.validPassword(password)) {
			return Response.status(Response.Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!createUser(email, password)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	private static boolean createUser(String email, String password) {
		String token = Hash.randomToken();
		String hash = Hash.createHash(password);
		Database.insertAccount(email, hash, false);
		AccountObject acc = Database.getAccount("email", email);
		Database.insertToken("verify_token", acc.id, token, "DATE_ADD(NOW(), INTERVAL "
				+ Account.VERIFY_TOKEN_TIMEOUT_AMOUNT + " " + Account.VERIFY_TOKEN_TIMEOUT_INTERVAL + ")");
		return sendVerifyEmail(email, token);
	}

	private static boolean sendVerifyEmail(String email, String token) {
		String url = "http://localhost:8080/verify/" + token;
		return Mail.sendVerifyMail(email, url);
	}

	private boolean validEmail(String email) {
		return email.length() < 255; // apache commons for regex?
	}
}
