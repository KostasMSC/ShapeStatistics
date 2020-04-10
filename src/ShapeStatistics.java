import java.sql.*;
import java.util.Random;
import java.io.*;

public class ShapeStatistics {

	// number of table rows to generate
	private static final int ITERN = 20;
	
	// max time to sleep between table row insertions
	private static final int MAXSLEEP = 3000;

	// shape names
	private static final String[] shapes = { "Circle", "Ellipse", "Right Triangle", "Right Rectangle",
			"Isosceles Trapezoid", "Isosceles Triangle", "Equilateral Triangle", "Square", "Pentagon", "Hexagon" };

	public static void main(String[] args) {
		Connection conn;

		try {
			// obtain a connection to the DB, use DB driver, URL, credentials
			String connURL = "jdbc:mysql://localhost:3306/page_visits";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(connURL, "root", "root");

			/*
			 * reset the DB
			 */
			Statement stmt0 = conn.createStatement();
			stmt0.executeUpdate("TRUNCATE PAGE_VISITS");

			/*
			 * insert random data into to DB
			 */
			Random ridx = new Random();
			Random sleept = new Random();

			for (int i = 0; i < ITERN; i++) {

				// select a random index value for the shapes table
				int idx = ridx.nextInt(shapes.length);

				// add the corresponding shape into the DB along with timestamp
				stmt0.executeUpdate("INSERT INTO PAGE_VISITS (SHAPE, TS) VALUES ('" + shapes[idx] + "', NOW())");

				// sleep a random amount of time up to MAXSLEEP / 1000 seconds;
				try {
					Thread.sleep(sleept.nextInt(MAXSLEEP));
				} catch (InterruptedException e) {

				}
			}

			/*
			 * generate statistics
			 */
			Statement stmt1 = conn.createStatement();
			// calculate number of visits per shape
			ResultSet rs1 = stmt1
					.executeQuery("SELECT SHAPE, COUNT(*) NUM FROM PAGE_VISITS GROUP BY SHAPE ASC ORDER BY SHAPE");
			
			Statement stmt2 = conn.createStatement();
			// calculate visit timestamps per shape
			ResultSet rs2 = stmt2.executeQuery("SELECT SHAPE, TS FROM PAGE_VISITS ORDER BY SHAPE ASC, TS ASC");
			
			// for each shape print its name and number of visits, followed by the list of visit timestamps
			while (rs1.next()) {
				int num = rs1.getInt("NUM");
				System.out.println(rs1.getString("SHAPE") + ": " + num + " visit(s).");
				for (int idx = 0; idx < num; idx++) {
					rs2.next();
					System.out.println("        " + rs2.getTimestamp("TS"));
				}
			}
			
			/*
			 * release resources
			 */
			stmt0.close();
			rs1.close();
			stmt1.close();
			rs2.close();
			stmt2.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
