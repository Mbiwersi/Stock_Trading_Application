import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HoldingsPage {
	
	Database db = Database.getInstance();
	

	public HoldingsPage() throws SQLException {
		Scanner scan = new Scanner(System.in);
		HoldingsPage.displayHoldingsPage();
		showHoldings(Login.user);
		UI.navigate(scan.nextInt());
	}
	
	public static void displayHoldingsPage() throws SQLException {
		System.out.println("---------------------------------------------------------------------------------------------------------\n"
				+ "                                           Holdings\n"
				+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
				+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n"
				+ "                                       Current Holdings\n");
	}
	
	public void showHoldings(Trader t) throws SQLException {
		db.connect();
		int count = 0;
		double value = 0;
		ResultSet results = db.getHoldings(t);
		while(results.next()) {
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			double price = results.getDouble("Price");
			int q = results.getInt("Quantity");
			System.out.println(ticker+", "+name+", $"+price+", Quantity:"+q);
			value += (price*q);
			count++;
		}
		System.out.println("\nTotal Value: $"+UI.round(value, 2));
		for(int i=0; i<(36-count);i++) {
			System.out.println();
		}
    	db.disconnect();
	}
}
