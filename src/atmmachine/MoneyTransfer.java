package atmmachine;

import java.util.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import atmmachine.*;

public class MoneyTransfer {
	static final Logger logger = LogManager.getLogger(MoneyTransfer.class.getName());
	int temporary;

	void transfer(int acc_no) {
		Receipt r1 = new Receipt();
		ATM_Machine a1 = new ATM_Machine();
		int sender_acc_balance = 0; // account balance of sender
		int receiver_acc_balance = 0; // account balance of receiver
		int sender_acc_number = acc_no;
		int receiver_acc_number = 0;
		int amt = 0;
		int invalid2 = 0;// to check invalid amount entry by user
		boolean flag2 = true;
		while (flag2) {
			try {
				// step1 load the driver class
				Connection con = DBConnection.getConnection();
				// step3 create the statement object
				Statement stmt = con.createStatement();
				// step4 execute query retrieve
				ResultSet rs = stmt.executeQuery(
						"select account_balance from account where account_no='" + sender_acc_number + "'");
				while (rs.next()) {
					sender_acc_balance = rs.getInt("account_balance");
					System.out.println("Your balance is " + sender_acc_balance);

					// sender_acc_number = rs.getInt("account_no");
					System.out.println("Your account no is " + sender_acc_number);

				}

				System.out.print("Please Enter Amount to transfer:\n");
				try {
					Scanner amtobj = new Scanner(System.in);

					amt = amtobj.nextInt(); // Amount to be transfered
				} catch (Exception e) {
					logger.debug(acc_no + "Overflow Error");
					invalid2 = invalid2 + 1;
					if (invalid2 == 3)// user gets only 3 attempts
					{
						System.out.println("Sorry you exceeded the chances please insert card and try again\n");
						// l1.logout();
						Receipt r99 = new Receipt();
						r99.receipt(0000, 1, sender_acc_number);
					} else {
						System.out.println(
								"Min Transfer is ₹1 and Max Transfer is ₹1,00,000\n Please Enter Correct Amount");
						System.out.println((3 - invalid2) + " attempts left!");
						continue;
					}
				}
				if (amt <= 0 || amt >= 100000)

				{
					invalid2 = invalid2 + 1;
					if (invalid2 == 3) {
						System.out.println("Sorry you exceeded the chances please insert card and try again\n");
						// l1.logout();
						Receipt r199 = new Receipt();
						r199.receipt(0000, 1, sender_acc_number);
					} else {
						System.out.println(
								"Min Transfer is ₹1 and Max Transfer is ₹1,00,000\n Please Enter Correct Amount");
						// transfer(sender_acc_number);
						System.out.println((3 - invalid2) + " attempts left!");
						continue;
					}
				}

				if (sender_acc_balance < amt) {
					logger.info(acc_no + " insufficeint balance");
					System.out.print("You do not have enough balance\n");
					// Again call transfer function if the account number is incorrect

					System.out.print("Press 1 to Continue Transfer\nPress 2 Log Out\n");
					Scanner myObj = new Scanner(System.in);
					int choice = myObj.nextInt();
					switch (choice) {
					case 1:
						transfer(sender_acc_number);
						break;
					case 2:
						Receipt r123 = new Receipt();
						r123.receipt(0000, 1, sender_acc_number);
						// new
						/*
						 * Log_Out logut=new Log_Out(); logut.logout();
						 */
						break;
					default:
						Receipt r1234 = new Receipt();
						r1234.receipt(0000, 1, sender_acc_number);
						break;
					}

				} else {
					boolean flag = true;
					int invalid = 0;

					while (flag) {
						try {
							System.out.print("Please Enter beneficiary account number\n");
							Scanner receiver_acc_numberobj = new Scanner(System.in);
							receiver_acc_number = receiver_acc_numberobj.nextInt();
						} catch (Exception e) {
							invalid = invalid + 1;
							System.out.println("Invalid account number");
							System.out.println((5 - invalid) + " attempts left!");

							if (invalid == 5) {
								Log_Out logut1 = new Log_Out();
								logut1.logout();
							} else
								continue;
						}
						if (receiver_acc_number == sender_acc_number) {
							System.out.println("Can't Enter own Account Number ");
							continue;
						} else {
							ResultSet rs2 = stmt.executeQuery("select account_balance from account where account_no='"
									+ receiver_acc_number + "'");
							if (!(rs2.next())) {
								invalid = invalid + 1;
								System.out.println("Invalid account number");
								System.out.println((5 - invalid) + " attempts left!");

								if (invalid == 5) {
									Log_Out logut1 = new Log_Out();
									logut1.logout();
								} else
									continue;
							} else {
								flag = false;
								receiver_acc_balance = rs2.getInt("account_balance");
								sender_acc_balance = sender_acc_balance - amt; // updates senders account balance in
																				// variable
								receiver_acc_balance = receiver_acc_balance + amt; // updates receivers account balance
																					// in variable

								// updates senders account balance
								String query2 = "update account set account_balance ='" + receiver_acc_balance
										+ "' where account_no='" + receiver_acc_number + "'";
								stmt.executeUpdate(query2);
								// updates receivers account balance
								String query = " update account set account_balance ='" + sender_acc_balance
										+ "' where account_no='" + sender_acc_number + "'";
								stmt.executeUpdate(query);
								String sql1 = "INSERT INTO TRANSACTION(TRANSACTION_TYPE,ACCOUNT_NO,TRANSACTION_AMT) VALUES('TRANSFER',"
										+ sender_acc_number + "," + amt + ")";
								stmt.executeUpdate(sql1);
								String sql2 = "insert into transfer values ((select max(transaction_id) from transaction where account_no="
										+ sender_acc_number + " )," + receiver_acc_number + ")";
								stmt.executeUpdate(sql2);

								String sql7 = "select max(transaction_id) from transaction where account_no='"
										+ sender_acc_number + "'";
								ResultSet r = stmt.executeQuery(sql7);
								r.next();
								this.temporary = r.getInt(1);
								r1.receipt(temporary, 3, sender_acc_number); // call receipt function to print receipt
							}
						}
					}
				}

			} catch (Exception e) {
				logger.debug(acc_no + " Invalid account");
				System.out.print("Invalid account number\n");
				// call transfer function
				transfer(sender_acc_number);
			}
		}
	}
}
