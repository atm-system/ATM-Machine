package atmmachine;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Receipt {
	static final Logger logger = LogManager.getLogger(Receipt.class.getName());

	public void receipt(int receipt_no, int category, int accno) {

		ATM_Machine atm = new ATM_Machine(accno, receipt_no);
		Connection con = DBConnection.getConnection();

		String sql9 = "update account set SOFT_LOCK = 1 WHERE account_no=" + accno;

		switch (category) {
		case 1:
			System.out.println("--------------------------------------");
			try {

// Connection Object

// create the statement object
				PreparedStatement p = con
						.prepareStatement("SELECT account_no, account_balance FROM account where account_no = ?");

				p.setInt(1, accno);

// execute query
				ResultSet rs = p.executeQuery();
				while (rs.next()) {

// Retrieve by column name
					int id = rs.getInt("account_no");
					int amount = rs.getInt("account_balance");

// Display		

					logger.info("Receipt printed");

					Date date = new Date();// timestamp
					long time = date.getTime();
					Timestamp ts = new Timestamp(time);
//System.out.print("Receipt#: " + receipt_no);
					System.out.println("Timestamp:" + ts);

					System.out.println("Account Number: " + id);
					System.out.println("Current Balance: ₹" + amount);
					System.out.println("Statement Printed Successfully");
					System.out.println("--------------------------------------");
					System.out.println("Do You want to continue with transaction:\n" + "0: CONTINUE 1: EXIT");
					Scanner myObj = new Scanner(System.in);
					int choice = myObj.nextInt();

					if (choice == 0)

					{

						atm.menu();
					} else {
						Statement stmt = con.createStatement();
						ResultSet r = stmt.executeQuery(sql9);
						Log_Out l1 = new Log_Out();
						l1.logout();

					}

				}
			} catch (Exception e) {
				System.out.println(e);

			}
			break;

		case 2:
			System.out.println("Withdrawal done Successfully");
			try {

// Connection Object

				// SQL QUERY
				String query = "select TRANSACTION_ID ,\n" + "TRANSACTION_TYPE ,\n" + "transaction.ACCOUNT_NO ,\n"
						+ "TIMESTAMPS ,\n" + "TRANSACTION_AMT,account.account_balance as cur from transaction \n"
						+ "join account\n" + "on account.account_no=transaction.account_no\n"
						+ "where transaction.account_no = ? and \n"
						+ "timestamps = (select max(timestamps) from transaction\n"
						+ "where transaction.account_no = ?)\n" + "";

// create the statement object
				PreparedStatement p = con.prepareStatement(query);
				p.setInt(1, accno);
				p.setInt(2, accno);

// execute query
				ResultSet rs = p.executeQuery();
				while (rs.next()) {

// Retrieve by column name
					int id = rs.getInt("account_no");
					String trans_type = rs.getString("Transaction_type");
					String time = rs.getString("timestamps");
					int trans_amount = rs.getInt("transaction_amt");
					int current_balance = rs.getInt("cur");

//DISPLAY
					System.out.println("Receipt#: " + receipt_no);
					get_timestamp(time);

					System.out.println("Account#: " + id);
					System.out.println("Transaction_type: " + trans_type);
					System.out.println("Transaction_amount: ₹ " + trans_amount);
					System.out.println("Current Balance: ₹ " + current_balance);
					System.out.println("Do You want to continue with transaction:\n" + "0: CONTINUE 1: EXIT");
					Scanner myObj = new Scanner(System.in);
					int choice = myObj.nextInt();

					if (choice == 0)

					{

						atm.menu();
					} else {
						Statement stmt = con.createStatement();
						ResultSet r = stmt.executeQuery(sql9);
						Log_Out l1 = new Log_Out();
						l1.logout();

					}

// System.out.print(", First: " + first);
// System.out.println(", Last: " + last);

				}
			} catch (Exception e) {
				System.out.println(e);
			}
			break;
		case 3:
			System.out.println("Transfer done Successfully");

			try {

//SQL QUERY
				String query = "select transaction.*,transfer.beneficiary_acc,account.account_balance as cur from transaction \n"
						+ "join account\n" + "on account.account_no=transaction.account_no\n" + "inner join transfer\n"
						+ "on transfer.transaction_id=transaction.transaction_id\n"
						+ "where transaction.account_no= ? and \n"
						+ "timestamps = (select max(timestamps) from transaction\n"
						+ "where transaction.account_no= ?)";

// create the statement object
				PreparedStatement p = con.prepareStatement(query);

				p.setInt(1, accno);
				p.setInt(2, accno);
				ResultSet rs = p.executeQuery();
				while (rs.next()) {

// Retrieve by column name
					int id = rs.getInt("account_no");
					String trans_type = rs.getString("Transaction_type");
					String time = rs.getString("timestamps");
					int beneficiary = rs.getInt("Beneficiary_acc");
					int trans_amount = rs.getInt("transaction_amt");
					int current_balance = rs.getInt("cur");

// Display
					System.out.println("Receipt#: " + receipt_no);
					get_timestamp(time);
					System.out.println("Account#: " + id);
					System.out.println("Transaction_type: " + trans_type);
					System.out.println("Transferred To: " + beneficiary);
					System.out.println("Transfer_amount: ₹ " + trans_amount);
					System.out.println("Current Balance: ₹ " + current_balance);
					System.out.println("Do You want to continue with transaction:\n" + "0: CONTINUE 1: EXIT");
					Scanner myObj = new Scanner(System.in);
					int choice = myObj.nextInt();

					if (choice == 0) {
						atm.menu();
					} else {
						Statement stmt = con.createStatement();
						ResultSet r = stmt.executeQuery(sql9);
						Log_Out l1 = new Log_Out();
						l1.logout();
					}

				}
			} catch (Exception e) {
				System.out.println(e);

			}
			break;
		case 4:
			System.out.println("Money deposited Successfully");
			try {

// Connection Object

				String query = "select TRANSACTION_ID ,\n" + "TRANSACTION_TYPE ,\n" + "transaction.ACCOUNT_NO ,\n"
						+ "TIMESTAMPS ,\n" + "TRANSACTION_AMT,account.account_balance as cur from transaction \n"
						+ "join account\n" + "on account.account_no=transaction.account_no\n"
						+ "where transaction.account_no = ? and \n"
						+ "timestamps = (select max(timestamps) from transaction\n"
						+ "where transaction.account_no = ?)\n" + "";

				PreparedStatement p = con.prepareStatement(query);

				p.setInt(1, accno);
				p.setInt(2, accno);
				ResultSet rs = p.executeQuery();
				while (rs.next()) {

// Retrieve by column name
					int id = rs.getInt("account_no");
					String trans_type = rs.getString("Transaction_type");
					String time = rs.getString("timestamps");
					int trans_amount = rs.getInt("transaction_amt");
					int current_balance = rs.getInt("cur");

					System.out.println("Receipt#: " + receipt_no);
					get_timestamp(time);

					System.out.println("Account#: " + id);
					System.out.println("Transaction_type: " + trans_type);
					System.out.println("Transaction_amount: ₹ " + trans_amount);
					System.out.println("Current Balance: ₹ " + current_balance);
					System.out.println("Do You want to continue with transaction:\n" + "0: CONTINUE 1: EXIT");
					Scanner myObj = new Scanner(System.in);
					int choice = myObj.nextInt();

					if (choice == 0) {
						atm.menu();
					} else {
						Statement stmt = con.createStatement();
						ResultSet r = stmt.executeQuery(sql9);
						Log_Out l1 = new Log_Out();
						l1.logout();

					}

				}
			} catch (Exception e) {
				System.out.println(e);

			}
			break;

		}

	}

	static void get_timestamp(String timestamp) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date fechaNueva = format.parse(timestamp);

			System.out.println("Timestamp: " + format.format(fechaNueva));
		} catch (Exception e) {
			System.out.println("Error " + e);
		}

	}
}
