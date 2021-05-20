
public class Trader {

	int ssn;
	String firstName;
	String lastName;
	String username;
	String password;
	
	public Trader(int ssn, String firstName, String lastName, String username, String password) {
		this.ssn = ssn;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
	}
	
	public int getSsn() {
		return this.ssn;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public String getUserName() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
}
