package atmmachine;

import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBConnection {

	static Connection con = null;
	static final Logger logger = LogManager.getLogger(DBConnection.class.getName());

	private DBConnection() {
		logger.info("DB Connection Initialized");

	}

	public static Connection getConnection() {
		try {
			if (con == null)
				con = DriverManager.getConnection("jdbc:oracle:thin:@OSCTrain1DB01.oneshield.com:1521:TRAIN1", "amdias",
						"password");

		} catch (Exception e) {
			logger.debug("DBConnection issue!!");
			System.out.println("DBConnection Issue!!");
		}
		return con;

	}

}