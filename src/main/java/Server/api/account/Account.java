package Server.api.account;

import Server.db.Database;
import Server.db.Hash;

public abstract class Account {

	private static final int PW_MAX_LENGTH = 55;
	static final int LOGIN_TOKEN_TIMEOUT_AMOUNT = 2;
	static final int VERIFY_TOKEN_TIMEOUT_AMOUNT = 15;
	static final String VERIFY_TOKEN_TIMEOUT_INTERVAL = "MINUTE";

	public static boolean checkLoginToken(String token) {
		return Database.tokenExists("login_token", token);
	}

	public static AccountObject getAccount(String email) {
		return Database.getAccount("email", email);
	}

	public static void clearUserTokens(int id) {
		Database.deleteToken("login_token", "acc_id = " + id);
	}

	public static boolean validPassword(String password) {
		return password.length() > 0 && password.length() <= PW_MAX_LENGTH; // min length, numbers?
	}

	public static boolean securityCodeValid(AccountObject acc, String code) {
		return code != null && code.equals(acc.securityCode);
	}

	public static String createSecurityToken(String email) {
		String code = Hash.randomToken();
		Database.updateAccount("email", email, "security_code", code);
		return code;
	}
}
