package Server.api.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/account/terminate")
public class Terminate {

	// @POST
	@GET
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post( // @FormParam("email")
			@QueryParam("email") String email, // @FormParam("code")
			@QueryParam("code") String code) {
		if (!Account.hasDBC()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		if (Account.userExists(email)) {
			Account.clearUserTokens(email);
		} else {
			return Response.status(Response.Status.CONFLICT).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!Account.securityCodeValid(email, code)) {
			return Response.status(Response.Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		}
		if (!terminate(email)) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", "*")
					.build();
		}
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	private static boolean terminate(String email) {
		sendTerminateEmail(email);
		// delete
		return true;
	}

	private static boolean sendTerminateEmail(String email) {
		return true;
	}
}