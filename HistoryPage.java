import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HistoryPage {

	
	Database db = Database.getInstance();

	public HistoryPage() throws SQLException {
		Scanner scan = new Scanner(System.in);
		HistoryPage.displayPage();
		this.listHistory();
		UI.navigate(scan.nextInt());

	}
	
	public static void displayPage() throws SQLException {
		System.out.println("---------------------------------------------------------------------------------------------------------\n"
				+ "                                           History\n"
				+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
				+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n"
				+ "                                         Transactions\n");
	}
	
	public void listHistory() throws SQLException {
		db.connect();
		int count = 0;
		ResultSet results = db.getBuyHistory(Login.user);
		while(results.next()) {
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			double price = results.getDouble("AskingPrice");
			int q = results.getInt("Quantity");
			double total = results.getDouble("Total");
			System.out.println("BUY--"+ticker+", "+name+", price per share:$"+price+", Quantity:"+q+", Total:$"+total);
			count++;
		}
		results = db.getSellHistory(Login.user);
		while(results.next()) {
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			double price = results.getDouble("AskingPrice");
			int q = results.getInt("Quantity");
			double total = results.getDouble("Total");
			System.out.println("SELL--"+ticker+", "+name+", price per share:$"+price+", Quantity:"+q+", Total:$"+total);
			count++;
		}
		db.disconnect();
		for(int i=0; i<(38-count);i++) {
			System.out.println();
		}
	}
}
