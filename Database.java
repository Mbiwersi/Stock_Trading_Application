import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Database {
	
	private Connection connection;
	private String url = "jdbc:sqlite:C:/Users/mjbiw/OneDrive/Desktop/CS 364/Final Project/BiwersiTrades.db";
	private static final Database INSTANCE = new Database();
	
	private Database() {
		
	}
	
	public static Database getInstance() {
		return INSTANCE;
	}
	
	public void connect()  {
		try {
			connection = DriverManager.getConnection(url);
			//System.out.println("Connected!");
		} catch (SQLException e) {
			System.out.println("Connection failed...");
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			connection.close();
			//System.out.println("Disconnected!");
		} catch (SQLException e) {
			System.out.println("Disconnection failed");
			e.printStackTrace();
		}
	}
	
	public ResultSet runQuery(String query) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet results = stmt.executeQuery();
		return results;
	}
	
	
	//inserts a trader after they go through the registration on the login page
	public void insertTrader(Trader t) throws SQLException {
		String sql = "INSERT INTO Trader(SSN, FirstName, LastName, UserName, Password) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, t.getSsn());
		stmt.setString(2, t.getFirstName());
		stmt.setString(3, t.getLastName());
		stmt.setString(4, t.getUserName());
		stmt.setString(5, t.getPassword());
		stmt.executeUpdate();
	}
	
	//Checks to see if the user is in the database given a username and password
	public boolean checkLogin(String u, String p) throws SQLException {
		String sql = "SELECT Trader.UserName, Trader.Password FROM Trader";
		PreparedStatement stmt = connection.prepareStatement(sql);
		ResultSet results = stmt.executeQuery();
		while(results.next()) {
		if(u.equals(results.getString("UserName"))&&p.equals(results.getString("Password")))
			return true;
		}
		return false;
	}
	
	public Trader getTrader(String u, String p) throws SQLException {
		String sql = "SELECT * FROM Trader WHERE Trader.UserName = ? AND Trader.Password = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, u);
		stmt.setString(2, p);
		ResultSet results = stmt.executeQuery();
		Trader t = new Trader(results.getInt("SSN"), results.getString("FirstName"), results.getString("LastName"),results.getString("UserName"),results.getString("Password"));
		return t;
	}
	
	public Trader getTrader(int SSN) throws SQLException {
		String sql = "SELECT * FROM Trader WHERE Trader.SSN = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, SSN);
		ResultSet results = stmt.executeQuery();
		Trader t = new Trader(results.getInt("SSN"), results.getString("FirstName"), results.getString("LastName"),results.getString("UserName"),results.getString("Password"));
		return t;
	}
	
	
	//returns all holdings that the current user owns
	public ResultSet getHoldings(Trader t) throws SQLException {
		String sql = "SELECT Stock.Ticker, Stock.Name, Stock.Price, Own.Quantity, Own.Total FROM Trader JOIN Own JOIN Stock ON Trader.SSN = Own.TraderSSN AND Own.StockId = Stock.StockId WHERE Trader.SSN = ? ORDER BY Stock.Ticker";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, t.getSsn());
		ResultSet results = stmt.executeQuery();
		return results;

	}
	
	public int getStockId(String tick) throws SQLException {
		String sql = "SELECT Stock.StockId FROM Stock WHERE Stock.Ticker = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, tick);
		ResultSet results = stmt.executeQuery();
		int id = results.getInt("StockId");
		return id;
	}
	
	
	//method to post an offer, takes a Ticker, # of stock and user and how much they would like the sell each stock with
	public void postOffer(Trader t, String tick, int q, double sellPrice) throws SQLException {
		//get the stockid from the ticker
		int id = getStockId(tick);
		
		//insert the offer
		String sql = "INSERT INTO Offer(TraderSSN, StockId, AskingPrice, Quantity, Total) VALUES(?,?,?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, t.getSsn());
		stmt.setInt(2, id);
		stmt.setDouble(3, sellPrice);
		stmt.setInt(4, q);
		stmt.setDouble(5, q*sellPrice);
		stmt.executeUpdate();
	}
	
	//returns all offers that are not the current users offers
	public ResultSet listOffers() throws SQLException {
		String sql = "SELECT Stock.Ticker AS Ticker, Stock.Name AS Name, Offer.Quantity AS Quantity, Offer.AskingPrice AS AskingPrice, Offer.Total As Total FROM Trader JOIN Offer JOIN Stock ON Trader.SSN = Offer.TraderSSN AND Offer.StockId = Stock.StockId WHERE Trader.SSN != ? ORDER BY Stock.Ticker";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, Login.user.getSsn());
		ResultSet results = stmt.executeQuery();
		return results;
	}
	
	//returns all offers of the stock given the ticker and that they are not the current users offers
	public ResultSet listOffers(String tick) throws SQLException {
		String sql = "SELECT Offer.OfferId, Stock.Ticker AS Ticker, Stock.Name AS Name, Offer.Quantity AS Quantity, Offer.AskingPrice AS AskingPrice, Offer.Total AS Total FROM Trader JOIN Offer JOIN Stock ON Trader.SSN = Offer.TraderSSN AND Offer.StockId = Stock.StockId WHERE Trader.SSN != ? AND Stock.Ticker = ? ORDER BY OfferId";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, Login.user.getSsn());
		stmt.setString(2, tick);
		ResultSet results = stmt.executeQuery();
		return results;
	}
	
	//finds the current price of a stock given the ticker
	public double tickerPrice(String tick) throws SQLException {
		String sql = "SELECT Price FROM Stock WHERE Stock.Ticker = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, tick);
		ResultSet results = stmt.executeQuery();
		return results.getDouble("Price");
	}
	
	//checks to see if a seller has the number of stocks to sell
	public boolean checkAvailability(String tick, int quantity, Trader seller) throws SQLException {
		String sql = "SELECT Stock.Ticker ,Own.Quantity FROM Trader JOIN Own JOIN Stock ON Trader.SSN = Own.TraderSSN AND Own.StockId = Stock.StockId WHERE TraderSSN = ? AND Stock.Ticker = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, seller.getSsn());
		stmt.setString(2, tick);
		ResultSet results = stmt.executeQuery();
		if(results.next()) {
			//if owner doesn't own the stock
			if(tick.equalsIgnoreCase(results.getString("Ticker"))&& results.getInt("Quantity")>= quantity) 
				return true;
			else 
				return false;
		}
		else 
			return false;
	}
	
	//checks to see if a user owns a stock
	//returns the OwnId if true or -1 for false
	private int getOwnId(Trader t, String tick) throws SQLException {
		String sql = "SELECT Own.OwnId AS OwnId FROM Trader JOIN Own JOIN Stock ON Trader.SSN = Own.TraderSSN AND Own.StockId = Stock.StockId WHERE Stock.Ticker = ? AND Trader.SSN = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, tick);
		stmt.setInt(2, t.getSsn());
		ResultSet results = stmt.executeQuery();
		if(results.next())
			return results.getInt("OwnId");
		else
			return -1;
	}
	
	//update the holdings of both the buyer if bought from the market
	public void updateHoldingsBuyer(String tick, int quantity, Trader t) throws SQLException {
		int ownId = getOwnId(t, tick);
		if(ownId==-1) {//if the user does not own the stock already
			String sql = "INSERT INTO Own(TraderSSN, StockId, Quantity, Total) VALUES(?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, Login.user.getSsn());
			stmt.setInt(2, this.getStockId(tick));
			stmt.setInt(3, quantity);
			stmt.setDouble(4, quantity*this.tickerPrice(tick));
			stmt.executeUpdate();
		}
		else {//user does own the stock update the Own
			//Updates the current Own tuple by adding to it
			String sql = "SELECT Quantity FROM Own WHERE OwnId = ?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, ownId);
			ResultSet result = stmt.executeQuery();
			sql = "UPDATE Own SET Quantity = ? WHERE OwnId = ?";
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, (quantity+result.getInt("Quantity")));
			stmt.setInt(2, ownId);
			stmt.executeUpdate();
			sql = "UPDATE Own SET Total = ? WHERE OwnId = ?";
			PreparedStatement s = connection.prepareStatement(sql);
			s.setDouble(1, quantity*this.tickerPrice(tick));
			s.setInt(2, ownId);
			s.executeUpdate();
		}
		//update the trade/history
		this.updateHistory(tick, quantity, this.tickerPrice(tick), t);
	}
	
	public void updateHoldingsSeller(String tick, int quantity, Trader t) throws SQLException {
		int ownId = getOwnId(t, tick);
		//find the quantity of shares the seller owns
		String sql = "SELECT Quantity FROM Own WHERE OwnId = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, ownId);
		ResultSet result = stmt.executeQuery();
		int q = result.getInt("Quantity");
		if(ownId==-1) {//if the user does not own the stock already
			sql = "INSERT INTO Own(TraderSSN, StockId, Quantity, Total) VALUES(?, ?, ?, ?)";
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, Login.user.getSsn());
			stmt.setInt(2, this.getStockId(tick));
			stmt.setInt(3, quantity);
			stmt.setDouble(4, q*this.tickerPrice(tick));
			stmt.executeUpdate();
		}
		else {//user does own the stock update the Own
			//Updates the current Own tuple by adding to it
			if(q>quantity) {
				sql = "UPDATE Own SET Quantity = ? WHERE OwnId = ?";
				PreparedStatement stm = connection.prepareStatement(sql);
				stm.setInt(1, (result.getInt("Quantity")-quantity));
				stm.setInt(2, ownId);
				stm.executeUpdate();
				sql = "UPDATE Own SET Total = ? WHERE OwnId = ?";
				PreparedStatement s = connection.prepareStatement(sql);
				s.setDouble(1, quantity*this.tickerPrice(tick));
				s.setInt(2, ownId);
				s.executeUpdate();
			}
			else {//if shares goes to 0 delete the Own's tuple
				sql = "DELETE FROM Own WHERE OwnId = ?";
				PreparedStatement state = connection.prepareStatement(sql);
				state.setInt(1, ownId);
				state.executeUpdate();
			}
		}
	}
	
	//update the holdings of both the buyer if bought a seller
	//check to see if the seller is able to still sell the stocks(use checkAvailablity())
	public boolean excuteTransaction(String tick, Trader buyer, int offerId) throws SQLException {
		//get seller
		String sql = "SELECT TraderSSN, Quantity, AskingPrice FROM Offer WHERE OfferId = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, offerId);
		ResultSet result = stmt.executeQuery();
		int ssn = result.getInt("TraderSSN");
		Trader seller = this.getTrader(ssn);
		int quantity = result.getInt("Quantity");
		double askingPrice = result.getDouble("AskingPrice");
		//add the shares to the buyers account and take away sellers shares
		if(this.checkAvailability(tick, quantity, seller)) { //if the seller owns the shares complete the transaction
			//update the holdings of the buyer
			this.updateHoldingsBuyer(tick, quantity, buyer);
			//delete the shares from the sellers account
			this.updateHoldingsSeller(tick, quantity, seller);
			//update trade/history
			this.updateTransactionHistory(tick, quantity, askingPrice, buyer, seller);
			//Delete offer
			String s = "DELETE FROM Offer WHERE OfferId = ?";
			PreparedStatement stment = connection.prepareStatement(s);
			stment.setInt(1, offerId);
			stment.executeUpdate();
			return true;
		}
		else //if the seller does not own the shares anymore cancel the transaction
			return false;
		
	}
	
	//update the history of a trader from the market
	private void updateHistory(String tick, int quantity, double askingPrice, Trader buyer) throws SQLException {
		String sql = "INSERT INTO Trade(StockId, BuyerSSN, AskingPrice, Quantity, Total) VALUES(?,?,?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, this.getStockId(tick));
		stmt.setInt(2, buyer.getSsn());
		stmt.setDouble(3, askingPrice);
		stmt.setInt(4, quantity);
		stmt.setDouble(5, askingPrice*quantity);
		stmt.executeUpdate();
	}
	
	//update the history of both the buyer and seller
	private void updateTransactionHistory(String tick, int quantity, double askingPrice, Trader buyer, Trader seller) throws SQLException {
		String sql = "INSERT INTO Trade(StockId, SellerSSN, BuyerSSN, AskingPrice, Quantity, Total) VALUES(?, ?, ?,?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, this.getStockId(tick));
		stmt.setInt(2, seller.getSsn());
		stmt.setInt(3, buyer.getSsn());
		stmt.setDouble(4, askingPrice);
		stmt.setInt(5, quantity);
		stmt.setDouble(6, askingPrice*quantity);
	    stmt.executeUpdate();
	}
	
	//get the buy history of the trader
	public ResultSet getBuyHistory(Trader t) throws SQLException {
		String sql = "SELECT * FROM Trader JOIN Trade JOIN Stock ON Trader.SSN = Trade.BuyerSSN AND Trade.StockId = Stock.StockId WHERE Trade.BuyerSSN = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, t.getSsn());
		ResultSet results = stmt.executeQuery();
		return results;
	}
	
	//get the sell history of a trader
	public ResultSet getSellHistory(Trader t) throws SQLException {
		String sql = "SELECT * FROM Trader JOIN Trade JOIN Stock ON Trader.SSN = Trade.BuyerSSN AND Trade.StockId = Stock.StockId WHERE Trade.SellerSSN = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, t.getSsn());
		ResultSet results = stmt.executeQuery();
		return results;
	}
	
	//tells if a stock is a good deal based on the average of the price others have bought a stock.
	public ResultSet goodDeal(String tick) throws SQLException {
		String sql = "SELECT Offer.OfferId, Stock.Name, Stock.Ticker, Offer.Quantity, Offer.AskingPrice FROM Offer JOIN Stock ON Stock.StockId = Offer.StockId WHERE Stock.Ticker = ? AND Offer.TraderSSN != ? AND Offer.AskingPrice < (SELECT avg(Trade.AskingPrice) FROM Stock JOIN Trade ON Trade.StockId = Stock.StockId GROUP BY Stock.StockId HAVING Stock.Ticker = ?) ORDER BY Offer.AskingPrice";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, tick);
		stmt.setInt(2, Login.user.getSsn());
		stmt.setString(3, tick);
		ResultSet result = stmt.executeQuery();
		return result;

	}
	
	//see stocks of that a Trader does not own using an outer join
	public ResultSet unownedStocks() throws SQLException {
		String sql = "SELECT Stock.Ticker, Stock.Name, Stock.Price FROM Stock NATURAL LEFT JOIN Own WHERE Own.TraderSSN IS NULL";
		PreparedStatement stmt=  connection.prepareStatement(sql);
		ResultSet result = stmt.executeQuery();
		return result;
	}
}
