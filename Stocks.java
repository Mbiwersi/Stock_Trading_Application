import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Stocks {
	
	Database db = Database.getInstance();

	public Stocks() throws SQLException {
		this.displayStocksPage();
	}
	
	public void displayStocksPage() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.println("---------------------------------------------------------------------------------------------------------\n"
				+ "                                           Stocks\n"
				+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
				+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n"
				+ "                       Press(8) to see stocks unowned by BiwersiTrade users\n");
		showStocks();
		int ans = scan.nextInt();
		if(ans == 8) {
			this.unownedStocks();
		}
		else {
			UI.navigate(ans);
		}
	}
	
	public void unownedStocks() throws SQLException {
		Scanner scan = new Scanner(System.in);
		db.connect();
		int count = 0;
		ResultSet results = db.unownedStocks();
		System.out.println("---------------------------------------------------------------------------------------------------------\n"
				+ "                                           Stocks\n"
				+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
				+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n"
				+ "                          Stocks not own by users of BiwersiTrades:\n");
		while(results.next()) {
			String tick = results.getString("Ticker");
			String name = results.getString("Name");
			double price = results.getDouble("Price");
			System.out.println(tick+", "+name+", $"+price);
			count++;
		}
		for(int i=0; i<(38-count);i++) {
			System.out.println();
		}
		db.disconnect();
		UI.navigate(scan.nextInt());
	}
	
	public void showStocks() throws SQLException {
		int count = 0;
		db.connect();
		ResultSet results = db.runQuery("SELECT * FROM Stock ORDER BY Stock.Ticker");
		while(results.next()) {
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			double price = results.getDouble("Price");
			System.out.println(ticker+", "+name+", $"+price);
			count++;
		}
		for(int i=0; i<(38-count);i++) {
			System.out.println();
		}
    	db.disconnect();
	}
}
