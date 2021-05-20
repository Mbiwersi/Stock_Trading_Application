import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MakeOfferPage {
	
	Database db = Database.getInstance();

	public MakeOfferPage() throws SQLException {
		Scanner scan = new Scanner(System.in);
		displayPage();
		db.connect();
		ResultSet results = db.getHoldings(Login.user);
		int count = 0;
		while(results.next()) {
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			double price = results.getDouble("Price");
			int q = results.getInt("Quantity");
			double total = results.getDouble("Total");
			System.out.println(ticker+", "+name+", price per share:$"+price+", Quantity:"+q+", Total:$"+total);
			count++;
		}
		System.out.println("\nType in the Ticker of the Stock you would like to Sell\n\n");
		for(int i=0; i<(34-count);i++) {
			System.out.println();
		}
		db.disconnect();
		makeOffer();
		UI.navigate(scan.nextInt());

	}
	
	public static void displayPage() throws SQLException {
		System.out.println("---------------------------------------------------------------------------------------------------------\n"
				+ "                                           Make an Offer\n"
				+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
				+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n"
				+ "                                       Current Holdings\n");
	}
	
	public void makeOffer() throws SQLException {
		Scanner scan = new Scanner(System.in);
		String tick = scan.nextLine();
		System.out.println("Type in how many you would like to sell?");
		int count = scan.nextInt();
		System.out.println("How much would you like to sell each stock for? ie \"1.00\"");
		double sellPrice = scan.nextDouble();
		db.connect();
		
		//Check to see if the offer can be made (Send message saying the offer has been posted)
		if(db.checkAvailability(tick, count, Login.user)) {
			//post the offer
			db.postOffer(Login.user, tick, count, sellPrice);
			System.out.println("---------------------------------------------------------------------------------------------------------\n"
					+ "                                           Make an Offer\n"
					+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
					+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n"
					+ "                                     Offer has been posted!"
					+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		}
		else {
			System.out.println("---------------------------------------------------------------------------------------------------------\n"
					+ "                                           Make an Offer\n"
					+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
					+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n"
				+ "                                   Offer couldn't be posted!\n"
				+ "                     Either you don't own the stock or have enough of the stock\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		}
		db.disconnect();
		UI.navigate(scan.nextInt());

		
		//Update how many stocks you have after the offer goes through(might be in buystock page)
		
	}
	
}
