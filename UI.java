import java.sql.SQLException;

public class UI {
	
	static Login login;
	static Stocks s;
	static HoldingsPage hp;
	static MakeOfferPage mop;
	static BuyStock bs;
	static HistoryPage his;
	Database db = Database.getInstance();
	
	public UI() throws SQLException {
		login = new Login();
	}
	
	
	
	public static void navigate(int ans) throws SQLException {
		//(1)HOme, 2.Stocks, 3.Holdings, 4.Trades, 5.Make an offer, 6.logout, 7. Buy Stocks
		if(ans == 1) {
			//Go to Home page
			HomePage.displayHomePage();
		}
		else if(ans == 2) {
			//Go to stocks page
			s = new Stocks();
		}
		else if(ans == 3) {
			//go to holdings page
			hp = new HoldingsPage();
		}
		else if(ans == 4) {
			his = new HistoryPage();
		}
		else if(ans == 5) {
			mop = new MakeOfferPage();
		}
		else if(ans == 7) {
			login = new Login();
		}
		else if(ans == 6) {
			//buy stock page
			bs = new BuyStock();
		}
		else {
			HomePage.displayHomePage();
		}
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		UI ui = new UI();
	}
	

}
