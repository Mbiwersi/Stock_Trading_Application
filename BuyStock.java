import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BuyStock {
	
	Database db = Database.getInstance();

	public BuyStock() throws SQLException {
		displayPage();
	}
	
	public void displayPage() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.println("---------------------------------------------------------------------------------------------------------\n"
				+ "                                           Buy\n"
				+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
				+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n"
				+ "\nCurrent Offers:\n");
		int count = getOffers();
		System.out.println("\nStocks avaiable in the market:\n");
		db.connect();
		ResultSet results = db.runQuery("SELECT * FROM Stock ORDER BY Stock.Ticker");
		while(results.next()) {
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			double price = results.getDouble("Price");
			System.out.println(ticker+", "+name+", $"+price);
			count++;
		}
    	db.disconnect();
				System.out.println("\n                    Type in the Ticker of the stock you want to buy");
		for(int i=0; i<(32-count);i++) {
			System.out.println();
		}
		String tick = scan.nextLine();
		this.getListingsAndMarket(tick);
		System.out.println("\nGood deals for "+tick);
		this.getGoodDeals(tick);
		System.out.println("Type (1) to buy from Market, (2) to buy from offer, (3) cancel\n");
		int ans = scan.nextInt();
		
		if(ans ==1) {
			//run and update query to add from holdings, add to history for the buyer
			System.out.println("How many shares would you like to buy?");
			int q = scan.nextInt();
			db.connect();
			db.updateHoldingsBuyer(tick, q, Login.user);
			System.out.println("---------------------------------------------------------------------------------------------------------\n"
					+ "                                           Buy\n"
					+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
					+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n");
					System.out.println("                              Buy has been completed!");
			for(int i=0; i<39;i++) {
				System.out.println();
			}
			db.disconnect();
		}
		else if(ans == 2) {
			db.connect();
			System.out.println("\nType in the offer# of the offer you would like to complete\n");
			int offerId = scan.nextInt();
			//run update query to add to holdings for buyer
			//take away from seller 
			//DELECT offer from offer table
			System.out.println(Login.user.username);
			if(db.excuteTransaction(tick, Login.user, offerId)) {
			//add the transaction in history for both buyer and seller
				System.out.println("---------------------------------------------------------------------------------------------------------\n"
						+ "                                           Buy\n"
						+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
						+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n");
					System.out.println("                              Buy has been completed!");
					for(int i=0; i<39;i++) {
						System.out.println();
					}			
			}
			else
				System.out.println("Buy not completed");
			db.disconnect();
		}
		else if(ans == 3) {
			//goes to home page
			UI.navigate(1);
		}
		else {
			System.out.println("---------------------------------------------------------------------------------------------------------\n"
					+ "                                           Buy\n"
					+ "(1)Home                 (2)Stocks                 (3)Holdings                  (4)History       \n"
					+ "      (5)Make an Offer              (6)Buy Stock                (7)Logout\n\n");
		}
		UI.navigate(scan.nextInt());

	}
	public int getOffers() throws SQLException {
		db.connect();
		int count = 0;
		ResultSet results = db.listOffers();
		while(results.next()) {
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			int quantity = results.getInt("Quantity");
			double price = results.getDouble("AskingPrice");
			System.out.println(ticker+", "+name+", "+quantity+" $"+price);
			count++;
		}
		db.disconnect();
		return count;
	}
	
	//might delete
	public int getOffers(String tick) throws SQLException {
		db.connect();
		int count = 0;
		ResultSet results = db.listOffers(tick);
		System.out.println();
		while(results.next()) {
			int id = results.getInt("Offer.OfferId");
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			int quantity = results.getInt("Quantity");
			double price = results.getDouble("AskingPrice");
			System.out.println("Offer Id = "+id+", "+ticker+", "+name+", "+quantity+" $"+price);
			count++;
			System.out.println();
		}
		db.disconnect();
		return count;
		
	}
	
	public int getGoodDeals(String tick) throws SQLException {
		db.connect();
		int count = 0;
		ResultSet results = db.goodDeal(tick);
		System.out.println();
		while(results.next()) {
			int id = results.getInt("OfferId");
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			int quantity = results.getInt("Quantity");
			double price = results.getDouble("AskingPrice");
			System.out.println("Offer Id = "+id+", "+name+", "+ticker+", "+quantity+" $"+price);
			count++;
		}
		db.disconnect();
		return count;
	}
	
	
	public int getListingsAndMarket(String tick) throws SQLException {
		db.connect();
		int count = 0;
		//market price
		double price = db.tickerPrice(tick);
		System.out.println("\nMarket Price for "+tick+" is "+price);
		//offers
		System.out.println("\nCurrent offers for "+tick);
		ResultSet results = db.listOffers(tick);
		while(results.next()) {
			int id = results.getInt("OfferId");
			String ticker = results.getString("Ticker");
			String name = results.getString("Name");
			int quantity = results.getInt("Quantity");
			price = results.getDouble("AskingPrice");
			System.out.println("Offer Id = "+id+", "+ticker+", "+name+", "+quantity+" $"+price);
			count++;
		}
		db.disconnect();
		return count;
	}
	//method to buy stock from the market or other offers
	
	//verify that both users have enough funds or have enough of the stock still available.
	
	//update buy the buy and selling of Trades in holdings and in the history tab for both
	
	
	
	
}
