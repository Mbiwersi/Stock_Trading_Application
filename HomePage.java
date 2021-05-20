import java.sql.SQLException;
import java.util.Scanner;

public class HomePage {

	
	public HomePage() {
		
	}
	//(1)Home, 2.Stocks, 3.Holdings, 4.Trades, 5.Make an offer, 6.logout

	public static void displayHomePage() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.println("---------------------------------------------------------------------------------------------------------\n"
				+ "                                           Home\n"
				+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
				+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n"
				+ "                              Welcome to Biwersi Trades "+Login.user.getFirstName()+"!");
		for(int i = 1; i<40; i++) {
			System.out.println();
		}
		UI.navigate(scan.nextInt());
	}
}
