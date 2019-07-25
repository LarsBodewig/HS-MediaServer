package Server.api.account;

public class AccountObject {
	public int id;
	public String email;
	public String hash;
	public boolean verified;
	public String securityCode;
	
	public AccountObject(Integer id, String email, String hash, Boolean verified, String securityCode) {
		this.id = id;
		this.email = email;
		this.hash = hash;
		this.verified = verified;
		this.securityCode = securityCode;
	}
}