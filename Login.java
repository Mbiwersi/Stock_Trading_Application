import java.sql.SQLException;
import java.util.Scanner;

public class Login {
	Scanner scan = new Scanner(System.in);
	Database db = Database.getInstance();
	static Trader user;


	public Login() throws SQLException {
		displayLoginScreen();
	}
	
	
	public Trader getTrader(String u, String p) throws SQLException {
		return this.user;
	}
	
	
	public void displayLoginScreen() throws SQLException {
		System.out.println("-------------------------------------------------\n"
				+ "                     Login\n\n"
				+ "Press (1) to enter your username or (2) to register"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n");
		int responce = scan.nextInt();
		if(responce == 2) {
			register();
		}
		else if(responce == 1) {
			System.out.println("Please enter your Username:");
			String userName = scan.next();
			System.out.println("Please enter your Password");
			String password = scan.next();
			db.connect();
			boolean result = db.checkLogin(userName, password);
			db.disconnect();
			if(result) {
				System.out.println("Correct!");
				db.connect();
				user = db.getTrader(userName, password);
				db.disconnect();
				HomePage.displayHomePage();
			}
			else {
				System.out.println("Username or Password is incorrect. Try again");
				displayLoginScreen();
			}
		}
		else {
			errorMessage();
		}
	}
	
	public void register() throws SQLException {
		System.out.println("--------------------------------------------------------------------------\n"
				+ "Register\nType in your SSN or type (1) to go back to the login screen");
		int ans = scan.nextInt();
		if(ans == 1) {
			displayLoginScreen();
		}
		else {
			int ssn = ans;
			System.out.println("Type in your First name");
			String firstName = scan.next();
			System.out.println("Type in your last name");
			String lastName = scan.next();
			System.out.println("Type in your username");
			String userName = scan.next();
			System.out.println("Type in your password");
			String password = scan.next();
			Trader t = new Trader(ssn, firstName, lastName, userName, password);
			db.connect();
			db.insertTrader(t);
			db.disconnect();
			displayLoginScreen();
		}
	}
	
	private void errorMessage() throws SQLException {
		System.out.println("You have typed in an incorrect responce. Try again");
		displayLoginScreen();
	}

}