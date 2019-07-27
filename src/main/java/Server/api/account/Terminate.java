package Server.api.account;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Server.db.Database;
import Server.mail.Mail;

@Path("/account/terminate")
public class Terminate {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(@HeaderParam("email") String email, @HeaderParam("code") String code) {
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
		if (!terminate(email)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok().build();
	}

	private static boolean terminate(String email) {
		Database.deleteAccount("email = '" + email + "'");
		return sendTerminateEmail(email);
	}

	private static boolean sendTerminateEmail(String email) {
		return Mail.sendTerminateMail(email);
	}
}