

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class MyConnection {

	public static Connection con;

	public static Connection makeConnection() {
		Connection con=null;
		try {

			// load and register the Driver
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			String url = "jdbc:sqlserver://DESKTOP-OPBS2NM:1433;databaseName=RewardCrowdFunding;encrypt=false";

			
			
			// Get connection using URL through Driver Manager
			con = DriverManager.getConnection(url, "iqeq126", "1320");
			
			if ( con != null) {
				System.out.println("Database connection Established Succusfully");
				//System.out.println("Connected!..");
				DatabaseMetaData dm = (DatabaseMetaData) con.getMetaData();
				System.out.println("Driver name: " + dm.getDriverName());
				System.out.println("Driver version: " + dm.getDriverVersion());
				System.out.println("Product name: " + dm.getDatabaseProductName());
				System.out.println("Product version: " + dm.getDatabaseProductVersion());
				
			}
		} catch (Exception e) {
			System.out.println("Connection Failed");
			System.out.println(e.getMessage());
	
		}
		return con;

	}

}
