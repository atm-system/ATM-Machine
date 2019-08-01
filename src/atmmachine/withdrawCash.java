package atmmachine;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import atmmachine.ATM_Machine;
import atmmachine.Receipt;
import java.sql.*;

public class withdrawCash {
	static final Logger logger = LogManager.getLogger(withdrawCash.class.getName());
	int receiptnumber;
	Scanner k = new Scanner(System.in);

	// The Constant Currency Denominations.
	protected final int[] currDenom = { 2000, 1000, 500, 100, 50 };

	// The Number of Currencies of each type
	// private int a1,a2,a3,a4,a5=0;
	protected int[] currNo = { 0, 0, 0, 0, 0 };

	// The count.
	protected int[] count = { 0, 0, 0, 0, 0 };

	// The total corpus.
	protected int totalCorpus = 0;

	// The amount.
	protected int amount = 0;
	int initAmount = 0;
	int daily_limit = 0;
	protected int actNo = 0;
	protected int atmBalance = 0;
	protected int userAccountBalance = 0;
	int dispensedAmount = 0;

	// Constructor to initialize the variables
	public withdrawCash(int acntNo) {
//		System.out.println("Please enter the amount to withdraw");
//		int userAmt = k.nextInt();
//		this.amount = userAmt;
//		initAmount = userAmt;
		this.actNo = acntNo;
//		dbConnect();// here u need to call the method which will fetch denomination from atm DB
//		dbConnectForAccount();
//		calcTotalCorpus();// function to determine amount of balance in ATM

	}

	public void dbConnect() {// Method to fetch available denomination from DB

		String sql = "Select * from ATM_MACHINE";

		try {

			Connection con = DBConnection.getConnection();

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {

				int a1 = rs.getInt(4);
				int a2 = rs.getInt(5);
				int a3 = rs.getInt(6);
				int a4 = rs.getInt(7);
				int a5 = rs.getInt(8);
				int a6 = rs.getInt(9);
				// System.out.println(a1+" "+a2+" "+a3+" "+a4+" "+a5);
				currNo[0] = a1;
				currNo[1] = a2;
				currNo[2] = a3;
				currNo[3] = a4;
				currNo[4] = a5;
				atmBalance = a6;
				// System.out.println(currNo[0]+" "+currNo[1]+" "+currNo[2]+" "+currNo[3]+"
				// "+currNo[4]);

			}

			// con.close();
		} catch (Exception ex) {
			System.out.println("ATM machine db issue!!");
		}

	}

	public void dbConnectForAccount() {// Method to fetch available denomination from users account

		String sql = "Select * from ACCOUNT where ACCOUNT_NO=" + this.actNo + "";
		// System.out.println("inside 2nd func"+this.actNo);

		try {
			Connection con = DBConnection.getConnection();

			Statement st = con.createStatement();
			ResultSet rs2 = st.executeQuery(sql);

			// System.out.println("inside try");

			while (rs2.next()) {
				// System.out.println("inside while");
				userAccountBalance = rs2.getInt(3);
				// System.out.println(userAccountBalance);
			}
			String sql1 = "select sum(transaction_amt) from transaction where account_no = " + this.actNo
					+ " and trunc(timestamps) = trunc(sysdate) and transaction_type = 'WITHDRAWAL'";
			// Statement st1 = con.createStatement();
			ResultSet rs3 = st.executeQuery(sql1);
			while (rs3.next()) {
				daily_limit = rs3.getInt(1);
				if ((daily_limit + initAmount) > 20000) {
					System.out.println("This amount will cross your daily limit!!");
					System.out.println("Your remaining daily limit is: " + (20000 - daily_limit));
					Receipt r90 = new Receipt();
					r90.receipt(0000, 1, actNo);
				}
			}

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	// total amount calculate karpak
	public void calcTotalCorpus() {
		for (int i = 0; i < currDenom.length; i++) {
			totalCorpus = totalCorpus + currDenom[i] * currNo[i];

		}
	}

	public void getDenominationChoice() {
		System.out.println("Please Enter Available Denomination");
		if (currNo[0] != 0) {
			System.out.print("2000 ");
		}
		if (currNo[1] != 0) {
			System.out.print("1000 ");
		}
		if (currNo[2] != 0) {
			System.out.print("500 ");
		}
		if (currNo[3] != 0) {
			System.out.print("100 ");
		}
		if (currNo[4] != 0) {
			System.out.print("50 ");
		}
	}

	public void withdrawal() {
		try {
			Connection con = DBConnection.getConnection();

			String sql1 = "select sum(transaction_amt) from transaction where account_no = " + this.actNo
					+ " and trunc(timestamps) = trunc(sysdate) and transaction_type = 'WITHDRAWAL'";
			Statement st1 = con.createStatement();
			ResultSet rs3 = st1.executeQuery(sql1);
			while (rs3.next()) {
				daily_limit = rs3.getInt(1);
				// System.out.println(daily_limit);
				if (daily_limit >= 20000) {
					System.out.println("You have crossed your daily limit!!");
					System.out.println("Your remaining daily limit is:0");
					Receipt r91 = new Receipt();
					r91.receipt(0000, 1, actNo);
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		boolean flag = true;
		while (flag) {
			try {

				System.out.println("--------------------------------------");
				System.out.println("Please enter the amount to withdraw");
				Scanner k1 = new Scanner(System.in);
				int userAmt = k1.nextInt();
				this.amount = userAmt;
				initAmount = userAmt;
			} catch (Exception e) {

				System.out.println("Per Transaction Limit is 20,000");
				continue;
			}

			dbConnect();// here u need to call the method which will fetch denomination from atm DB
			dbConnectForAccount();
			calcTotalCorpus();// function to determine amount of balance in ATM

			int modCheck = initAmount % 50;// to ensure that users enter denominations in the factors of 50

			if (initAmount <= atmBalance && initAmount <= userAccountBalance) {// condition to check withdrawal amount
																				// is
																				// within user's bank balance and also
																				// within cash present in ATM
				if (initAmount >= 50 && initAmount <= 20000 && modCheck == 0) {// condition to check whether the
																				// withdrawal
					System.out.println("--------------------------------------");
					// amount is within withdrawal amount or not

					System.out.println(
							"What would you like?\n1. Denomination of my choice.\n2. System generated denomination.");
					int choice = k.nextInt();
					// if(choice>2) {}
					if (choice == 1) {
						flag = false;
						boolean flag2 = true;
						System.out.println(
								"Please enter the denomination of your priority below, \nfrom the currency notes of\n");

						while (flag2) {
							getDenominationChoice();
							int priorityNote = k.nextInt();
							int numberPriNote = 0;
							System.out.println("--------------------------------------");
							switch (priorityNote) {
							case 2000:

								numberPriNote = amount / priorityNote;
								if (currNo[0] == 0) {
									/*
									 * System.out.println(
									 * "Not enough denomination of 2000 is present.\nPlease accept the system generated denomination."
									 * );
									 */
									continue;
									// withdrawdCash();
								}
								if (numberPriNote > currNo[0] && currNo[0] > 0) {

									count[0] = currNo[0];
									currNo[0] = currNo[0] - currNo[0];
									amount = amount - count[0] * currDenom[0];

									numberPriNote = 0;
									System.out.println(
											"The notes you require are insufficient for your current request, \n Please Accept System generated currency:\n");
									withdrawdCash();
								}
								amountSubtractor2k(numberPriNote);
								break;
							case 1000:
								if (currNo[1] == 0) {
									/*
									 * System.out.println(
									 * "Not enough denomination of 1000 is present.\nPlease accept the system generated denomination."
									 * ); withdrawdCash();
									 */
									continue;
								}
								numberPriNote = amount / priorityNote;
								if (numberPriNote > currNo[1] && currNo[1] > 0) {

									count[1] = currNo[1];
									currNo[1] = currNo[1] - currNo[1];
									amount = amount - count[1] * currDenom[1];

									numberPriNote = 0;
									System.out.println(
											"The notes you require are insufficient for your current request, \n Please Accept System generated currency:\n");
									withdrawdCash();
								}
								amountSubtractor1k(numberPriNote);
								break;
							case 500:
								if (currNo[2] == 0) {
									/*
									 * System.out.println(
									 * "Not enough denomination of 500 is present.\nPlease accept the system generated denomination."
									 * ); withdrawdCash();
									 */
									continue;
								}
								numberPriNote = amount / priorityNote;
								if (numberPriNote > currNo[2] && currNo[2] > 0) {
									count[2] = currNo[2];
									currNo[2] = currNo[2] - currNo[2];
									amount = amount - count[2] * currDenom[2];

									numberPriNote = 0;
									System.out.println(
											"The notes you require are insufficient for your current request, \n Please Accept System generated currency:\n");
									withdrawdCash();
								}
								amountSubtractor5h(numberPriNote);
								break;
							case 100:
								if (currNo[3] == 0) {
									/*
									 * System.out.println(
									 * "Not enough denomination of 100 is present.\nPlease accept the system generated denomination."
									 * ); withdrawdCash();
									 */
									continue;
								}
								numberPriNote = amount / priorityNote;
								if (numberPriNote > currNo[3] && currNo[3] > 0) {

									count[3] = currNo[3];
									currNo[3] = currNo[3] - currNo[3];
									amount = amount - count[3] * currDenom[3];

									numberPriNote = 0;
									System.out.println(
											"The notes you require are insufficient for your current request, \n Please Accept System generated currency:\n");
									withdrawdCash();
								}
								amountSubtractor1h(numberPriNote);
								break;
							case 50:
								if (currNo[4] == 0) {
									/*
									 * System.out.println(
									 * "Not enough denomination of 50 is present.\nPlease accept the system generated denomination."
									 * ); withdrawdCash();
									 */
									continue;
								}
								numberPriNote = amount / priorityNote;
								if (numberPriNote > currNo[4] && currNo[4] > 0) {

									count[4] = currNo[4];
									currNo[4] = currNo[4] - currNo[4];
									int temporary;
									temporary = amount;
									// System.out.println("copied to temporary");
									amount = amount - count[4] * currDenom[4];
									// System.out.println("amount calculated");
									if (amount % 50 == 0) {
										// System.out.println("inside if");
										currNo[4] = count[4];
										count[4] = 0;
										amount = temporary;
										// System.out.println("reverted amount");
										System.out.println(
												"The notes you require are insufficient for your current request, \n Please Accept System generated denomination:\n");
										withdrawdCash();
									}
									// numberPriNote = 0;
								}
								amountSubtractor50(numberPriNote);
								break;
							default:
								logger.info(actNo + " Invalid denomination entered");
								System.out.println("Invalid denomination entered");
								withdrawal();
								break;

							}
						}
					} else if (choice == 2) {
						flag = false;
						withdrawdCash();
					} else {
						System.out.println("Invalid choice");
						continue;
						// goto a;

					}
				} else {
					logger.info(actNo + " Invalid denomination entered");
					System.out.println("Please enter the amount upto Rs 200000 in the multiples of 50");
					continue;
					// new withdrawCash(actNo);
				}

			} else {
				logger.info(actNo + " Not Enough balance To dispense");
				System.out.println("Not enough balance to dispense cash at the moment.");
				Receipt r23 = new Receipt();
				r23.receipt(0000, 1, actNo);

//			System.out.println("Do You want to continue with transaction:\n" + "0: CONTINUE 1: EXIT");
//			
//			int choice = k.nextInt();
//			if (choice == 0)
//
//			{
//				ATM_Machine atm=new ATM_Machine();
//				atm.menu();
//
//			} else {
//				
//				Log_Out l1 = new Log_Out();
//				l1.logout();
//
//			}
//			

			}
		}
	}

	public void amountSubtractor2k(int q) {
		int p = q;
		currNo[0] = currNo[0] - p;
		count[0] = p;
		amount = amount - currDenom[0] * p;
		withdrawdCash();
	}

	public void amountSubtractor1k(int q) {
		int p = q;
		currNo[1] = currNo[1] - p;
		count[1] = p;
		amount = amount - currDenom[1] * p;
		withdrawdCash();
	}

	public void amountSubtractor5h(int q) {
		int p = q;
		currNo[2] = currNo[2] - p;
		count[2] = p;
		amount = amount - currDenom[2] * p;
		withdrawdCash();
	}

	public void amountSubtractor1h(int q) {
		int p = q;
		currNo[3] = currNo[3] - p;
		count[3] = p;
		amount = amount - currDenom[3] * p;
		withdrawdCash();
	}

	public void amountSubtractor50(int q) {
		int p = q;
		currNo[4] = currNo[4] - p;
		count[4] = p;
		amount = amount - currDenom[4] * p;
		withdrawdCash();
	}

	// function to update the denomination in the ATM database
	/*
	 * public void atmDbUpdate() {//Method to fetch available denomination from DB
	 * String url = "jdbc:oracle:thin:@OSCTrain1DB01.oneshield.com:1521:train1";
	 * String user = "amdias"; String pass = "password";
	 * 
	 * 
	 * String sql1 =
	 * "Update ATM_MACHINE set D_2000="+currNo[0]+",D_1000="+currNo[1]+",D_500="+
	 * currNo[2]+",D_100="+currNo[3]+",D_50="+currNo[4]+" where ATM_MACHINE_ID=1 ";
	 * //String
	 * sql2="Update ACCOUNT set ACCOUNT_BALANCE=ACCOUNT_BALANCE-500 where ACCOUNT_NO=1212123456"
	 * ; Connection con=null; try {
	 * 
	 * Class.forName("oracle.jdbc.driver.OracleDriver"); //Reference to connection
	 * interface con = DriverManager.getConnection(url,user,pass);
	 * 
	 * Statement st = con.createStatement(); //Statement st1 =
	 * con.createStatement(); st.executeUpdate(sql1); //st1.executeUpdate(sql2);
	 * 
	 * con.close();
	 * 
	 * } catch(Exception ex) { System.err.println(ex); }
	 * 
	 * 
	 * }
	 */

	public void actDbUpdate() {// Method to fetch available denomination from DB

		// String sql1 = "Update ATM_MACHINE set
		// D_2000="+currNo[0]+",D_1000="+currNo[1]+",D_500="+currNo[2]+",D_100="+currNo[3]+",D_50="+currNo[4]+"
		// where ATM_MACHINE_ID=1 ";
		String sql1 = "Update ATM_MACHINE set D_2000=" + currNo[0] + ",D_1000=" + currNo[1] + ",D_500=" + currNo[2]
				+ ",D_100=" + currNo[3] + ",D_50=" + currNo[4] + " where ATM_MACHINE_ID=1 ";
		String sql2 = "Update ACCOUNT set ACCOUNT_BALANCE=ACCOUNT_BALANCE-" + dispensedAmount + " where ACCOUNT_NO="
				+ this.actNo + "";
		String sql3 = "INSERT INTO TRANSACTION(TRANSACTION_TYPE,ACCOUNT_NO,TRANSACTION_AMT) VALUES('WITHDRAWAL',"
				+ this.actNo + "," + dispensedAmount + ")";
		String sql4 = "select max(transaction_id) from transaction";

		try {

			Connection con = DBConnection.getConnection();
			// Statement st = con.createStatement();
			Statement st1 = con.createStatement();

			st1.executeUpdate(sql1);
			// st.executeUpdate(sql1);
			st1.executeUpdate(sql2);
			// update to database table transaction
			logger.info(actNo + " Withdrew " + dispensedAmount);
			st1.executeUpdate(sql3);

			ResultSet abcd = st1.executeQuery(sql4);
			abcd.next();
			this.receiptnumber = abcd.getInt(1);
//			con.close();

		} catch (Exception ex) {
			logger.debug("Error!!");
			System.out.println(ex);
		}

	}

	/*
	 * public void sd() {
	 * 
	 * System.out.println("Please enter the denomination of your choice below");
	 * System.out.println("2000: "); int d1 = k.nextInt();
	 * System.out.println("1000: "); int d2= k.nextInt();
	 * System.out.println("500: "); int d3 = k.nextInt( );
	 * System.out.println("100: "); int d4 = k.nextInt();
	 * System.out.println("50: "); int d5 = k.nextInt();
	 * 
	 * 
	 * int enteredAmt=(d1*2000)+(d2*1000)+(d1*500)+(d1*100)+(d1*50);
	 * 
	 * if(enteredAmt==amount) { //to check whether the value of denomination entered
	 * corresponds to the actual amount user wanted
	 * 
	 * //dispenses money as per the users requirement
	 * if(d1<=currNo[0]&&d2<=currNo[1]&&d3<=currNo[2]&&d4<=currNo[3]&&d5<=currNo[4])
	 * { count[0]=d1;count[1]=d2;count[2]=d3;count[3]=d4;count[4]=d5;
	 * 
	 * currNo[0]=currNo[0]-d1; currNo[1]=currNo[1]-d2; currNo[2]=currNo[2]-d3;
	 * currNo[3]=currNo[3]-d4; currNo[4]=currNo[4]-d5;
	 * 
	 * System.out.println("The amount dispensed is: "+this.amount);
	 * 
	 * } else {
	 * System.out.println("Sorry the denomination you entered is not available");
	 * System.out.
	 * println("Would you like to accept amount in available denominations? Reply Y/N"
	 * ); String response =k.nextLine(); if(response.contentEquals("Y"))
	 * withdrawdCash(); else { //call-logout } }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

	/*
	 * this is the function to take care of the amount to be dispensed after choice
	 */
	public void withdrawdCash() {
		if (amount <= totalCorpus) {
			for (int i = 0; i < currDenom.length; i++) {
				if (currDenom[i] <= amount) {// If the amount is less than the currDenom[i] then that particular
												// denomination cannot be dispensed
					int noteCount = amount / currDenom[i];
					if (currNo[i] > 0) {// To check whether the ATM Vault is left with the currency denomination under
										// iteration
						// If the Note Count is greater than the number of notes in ATM vault for that
						// particular denomination then utilize all of them
						count[i] = noteCount >= currNo[i] ? currNo[i] : noteCount;
						currNo[i] = noteCount >= currNo[i] ? 0 : currNo[i] - noteCount;
						// Deduct the total corpus left in the ATM Vault with the cash being dispensed
						// in this iteration
						totalCorpus = totalCorpus - (count[i] * currDenom[i]);
						// Calculate the amount that need to be addressed in the next iterations
						amount = amount - (count[i] * currDenom[i]);
						// no atm update function calling here

					}
				}
			}
			dispensedAmount = amountCheck();

			if (initAmount == dispensedAmount) {
				System.out.println("The amount dispensed is: ₹" + dispensedAmount);

				// Functions not to be used if not required
				displayNotes();
				// displayLeftNotes();

				// database update of atm and bank account
				// atmDbUpdate();
				actDbUpdate();

				System.out.println("\tYour transaction is complete\n\t please collect your card");
				System.out.println("--------------------------------------");
				System.out.println(" ");
				Receipt r1 = new Receipt();
				r1.receipt(receiptnumber, 2, actNo);
			} else {
				logger.info(actNo + " Not enough balance to dispense cash");
				System.out.println("Not enough balance to dispense cash at the moment.");
				Receipt r23 = new Receipt();
				r23.receipt(receiptnumber, 1, actNo);

			}

		} else {
			logger.info(actNo + " Not enough balance to dispense cash");
			System.out.println("Unable to dispense cash at this moment for this amount");
		}

	}

	public int amountCheck() {
		int dispensedAmnt = 0;
		for (int i = 0; i < currDenom.length; i++) {
			dispensedAmnt = dispensedAmnt + (count[i] * currDenom[i]);
		}

		return dispensedAmnt;

	}

	/**
	 * Display notes.
	 */

	private void displayNotes() {
		for (int i = 0; i < count.length; i++) {
			if (count[i] != 0) {
				System.out.println(currDenom[i] + " * " + count[i] + " = " + (currDenom[i] * count[i]));

			}
		}
		System.out.println("--------------------------------------");

	}

	/*
	 * * Display left notes.
	 *//*
		 * private void displayLeftNotes(){ for(int i = 0; i < currDenom.length; i++){
		 * System.out.println("Notes of "+currDenom[i]+" left are "+currNo[i]); }
		 * 
		 * }
		 */

} // main brace