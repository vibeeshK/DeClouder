package espot;

public class CredentialsPojo {
	/*
	 * Convenience class to hold credentials data
	 */

	public String userName;
	public String password;

	public CredentialsPojo() {
	}

	public CredentialsPojo(String inUserName, String inPassword) {
		setCredentials(inUserName, inPassword);
	}

	public void setCredentials(String inUserName, String inPassword) {
		userName = inUserName;
		password = inPassword;
	}
}
