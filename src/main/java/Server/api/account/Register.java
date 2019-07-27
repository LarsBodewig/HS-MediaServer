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

@Path("/account/register")
public class Register {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(@HeaderParam("email") String email, @HeaderParam("password") String password) {
		if (!Database.hasConnection()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
		AccountObject acc = Account.getAccount(email);
		if (acc != null) {
			Account.clearUserTokens(acc.id);
			return Response.status(Response.Status.CONFLICT).build();
		}
		if (!validEmail(email)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		if (!Account.validPassword(password)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		if (!createUser(email, password)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok().build();
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
