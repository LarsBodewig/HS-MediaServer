package Server.api.account;

public abstract class Account {

	public static boolean checkLoginToken(String token) {
		return token != null;
	}

	public static boolean userExists(String email) {
		return email.equals("abc");
	}
	
	public static void clearUserTokens(String email) {
		// clear
	}
	
	public static boolean validPassword(String password) {
		return true;
	}
	
	public static boolean securityCodeValid(String email, String code) {
		return true;
	}
	
	public static String createSecurityToken(String email) {
		return email + System.currentTimeMillis();
	}
}
