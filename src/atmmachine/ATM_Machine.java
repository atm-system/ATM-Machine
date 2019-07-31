/**
 * 
 */
/**
 * @author yparmar
 *
 */
package atmmachine;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import java.util.*;
import java.util.Date;


public class ATM_Machine {
	static final Logger logger = LogManager.getLogger(ATM_Machine.class.getName());

	String Card_number;//Card number
	int validcard=0;
	String tocheckcount;
	int Receipt_no;
	int softlock;
	String temppin;
	String tempcardnum;
	String StringAccount_no;
	Integer Account_no;
	final int countvalue=3;
	Date todayDate =  new Date();
	String pin;//pin entered
	String encryptednumber; //encrypted number
	int[] acno_array; //used for conversion of integer into digits
	Integer count=0;            //counts number of time user has input invalid entries
	
	
	
	public ATM_Machine(int acntno,int rcpt) {
		this.Account_no=acntno;
		this.Receipt_no=rcpt;
		// TODO Auto-generated constructor stub
	}
		
	public ATM_Machine() {
		// TODO Auto-generated constructor stub
	}

	public static boolean validateJavaDate(String strDate)
	   {
        //System.out.println("Retrieved from database1: "+strDate);
		//System.out.println("\n\n\tWELCOME\n\n");


		    SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM-dd");

		    try
		    {
		        Date javaDate = sdfrmt.parse(strDate);
		       // System.out.println("Retrieved from database: "+javaDate);
		        Date todayDate = sdfrmt.parse(sdfrmt.format(new Date()));
		      //  System.out.println("Todays date: "+todayDate);
		        if(javaDate.compareTo(todayDate)>=0)
		        {
		        	return true;
		        }
		        else
		        	return false;
		    }
		    /* Date format is invalid */
		    catch (ParseException e)
		    {
		        System.out.println(strDate+" is Invalid Date format");
		        
		        return false;
		    }
		    
		}
	
	
//	public void Encryption()
//	{
//	    Long cardnum;
//	    Integer pinofcard;
//	    int key=3;
//	    int cardarray[] = new int[Card_number.length()], pinarray[] = new int[pin.length()];
//	    int temp,i=0;
//	    Long tempp=(long) 0;
//        cardnum = Long.parseLong(Card_number);
//	    while(cardnum!=0)
//	    {
//	    	temp=(int) (cardnum%10);
//	    	cardnum/=10;
//	    	temp+=key;
//	    	temp%=10;
//	    	cardarray[i]=temp;
//	    	i++;
//	    }
//	    for(int k=Card_number.length()-1;k>=0;k--)
//	    {
//	    	tempp = tempp* 10 + cardarray[k];
//	    }
//	    tempcardnum= Long.toString(tempp);
//	    i=0;
//	    pinofcard = Integer.parseInt(pin);
//	    while(pinofcard!=0)
//	    {
//	    	temp=pinofcard%10;
//	    	pinofcard/=10;
//	    	temp+=key;
//	    	temp%=10;
//	    	pinarray[i]=temp;
//	    	i++;
//	    }
//	    tempp = (long) 0;
//	    for(int k=pin.length()-1;k>=0;k--)
//	    {
//	    	tempp = tempp* 10 + pinarray[k];
//	    }
//	    
//	    pin= Long.toString(tempp);
//	}
	public void Validation()
	{
		
		try
		{Connection con= DBConnection.getConnection();
		
		Statement st = con.createStatement();
		System.out.println("--------------------------------------");
		System.out.print("Enter card number:  ");
		Scanner myObj = new Scanner(System.in);
		logger.info("Card number Entered");
		Card_number= myObj.nextLine();
		System.out.println("--------------------------------------");
		System.out.print("\nEnter pin:  ");
		pin= myObj.nextLine();
		logger.info("User Entered Pin");
		System.out.println("--------------------------------------\n");
		
		//new code
		String sql11= "Select pkg_app_security.fn_encrypt_value ('"+Card_number+"') rh from dual";
		String sql12= "Select pkg_app_security.fn_encrypt_value ('"+pin+"') rh from dual";
		ResultSet u= st.executeQuery(sql11);
		//System.out.println("query 11 working");
		u.next();
		tempcardnum=u.getString(1);
		//System.out.println(tempcardnum);

		ResultSet v= st.executeQuery(sql12);
		//System.out.println("query 12 working");
		v.next();
		temppin=v.getString(1);
		//System.out.println(temppin);

		
		//new code end
		//Encryption();
		String sql = "select * from acc_pin where CARD_PIN_E='"+tempcardnum+"'";
		
		ResultSet m= st.executeQuery(sql);
		//System.out.println("query sql working");
		m.next();
		if(tempcardnum.equals(m.getString(2)) && temppin.equals(m.getString(4)))//verification using database 
		{
			String sql2 = "select * from atm_card where CARD_NO="+Card_number;
			ResultSet n= st.executeQuery(sql2);
			//System.out.println("query sql2 working");
			n.next();
			
			if(validateJavaDate(n.getString(5)))
			{
				if(n.getString(6).equals("1"))
				{
					String sql5="update card_pin set INVALID_ENTRIES ="+0+" where CARD_NO="+this.Card_number;
					ResultSet p= st.executeQuery(sql5);
					//System.out.println("query sql5 working");
					String sql6 ="select account_no from account where card_no="+this.Card_number; 
					ResultSet q=st.executeQuery(sql6); 
					//System.out.println("query sql6 working");
					q.next();
					StringAccount_no=q.getString(1);
				    Account_no = Integer.parseInt(StringAccount_no);
					this.count=0;
					//System.out.println(Account_no);		
					String sql7 ="select max(transaction_id) from transaction"; 
					ResultSet r=st.executeQuery(sql7); 
					//System.out.println("query sql7 working");
					r.next();
					this.Receipt_no=r.getInt(1);
					//System.out.println(Receipt_no);
					
					String sql8 ="select soft_lock from account where account_no="+Account_no;
					ResultSet s=st.executeQuery(sql8);
					//System.out.println("query sql8 working");
					s.next();
					this.softlock=s.getInt(1);
					//System.out.println(this.softlock);
					if(this.softlock==1)
					{logger.info(Account_no+" Logged in");
						System.out.println("\n\n\tWELCOME\n\n");
						String sql9="update account set soft_lock=0 where account_no="+Account_no;
						ResultSet t=st.executeQuery(sql9);
						//System.out.println("query sql9 working");
						//t.next();
						//this.softlock=0;
						menu();
					}
					else
					{
						System.out.println("Someone else is still using this account.\nIf it is a suspicious activity please report that to nearby bank");
						Log_Out l2= new Log_Out();
						l2.logout();
						
					}
				}
				else
				{logger.info(Account_no+" Logged in");
					System.out.println("Card Blocked");
					Validation();
				}
			}
			else
			{
				System.out.println("Expired Card");
			}
				
		}
		else
		{
			if(Card_number.equals(m.getString(1)))
			{
				int temp;
				tocheckcount=m.getString(3);
				this.count = Integer.parseInt(tocheckcount);
				this.count++;
				temp=countvalue-this.count;
				if(this.count<=3)
				{
					System.out.println("Invalid Pin");
					System.out.println("Chances left :"+ temp);
					String sql3="update card_pin set INVALID_ENTRIES ="+this.count+" where CARD_NO="+this.Card_number;
					ResultSet o= st.executeQuery(sql3);
					//System.out.println("query sql3 working");
					Validation();
				}
				else
				{
					System.out.println("Your card has been blocked..Please contact your bank");
					String sql4="update atm_card set CARD_STATUS = "+ 0 +" where CARD_NO="+this.Card_number;
					ResultSet p = st.executeQuery(sql4);
					Validation();
				}
			}
		}
		}
		
		catch(Exception x)
		{logger.debug(Account_no+" Invalid card"+x);
			System.out.println("\nInvalid card");
			//x.printStackTrace();
			Validation();
		}
		
		
	}
	
	public void menu()
	{
		int choice;
		Receipt R = new Receipt();
		Deposit d = new Deposit();
		withdrawCash w = new withdrawCash(Account_no);
		MoneyTransfer m=new MoneyTransfer();
		System.out.println("---------------------");
		System.out.println("\tMenu");
		System.out.println("---------------------");
		System.out.println("1: Statement \n2: Withdraw \n3: Transfer\n4: Deposit \n5: Exit");
		System.out.println("---------------------\n");
		Scanner myObj = new Scanner(System.in);
		choice= myObj.nextInt();
		switch(choice)
		{
			case 1: //Statement
				//	Receipt_no++;
				logger.info(Account_no+" selected Enquiry");
					R.receipt(Receipt_no, 1, Account_no);
					break;
			case 2: 
				logger.info(Account_no+" selected Withdrawal");
				w.withdrawal();
				break;
			case 3: 
				logger.info(Account_no+" selected transfer");
				m.transfer(Account_no);
				break;
			case 4: 
				logger.info(Account_no+" selected Deposit");
				d.deposit1(Account_no);
				break;
			case 5: 
				
				try
				{
					Connection con= DBConnection.getConnection();
				Statement st = con.createStatement();
				String sql10="update account set soft_lock=1 where account_no="+Account_no;
				ResultSet r= st.executeQuery(sql10);
				logger.info(Account_no+" logged out");
				Log_Out l= new Log_Out();
				l.logout();
				}
				catch(Exception e)
				{
					
				}
				break;
			default:
				logger.info(Account_no+":Invalid out");
				System.out.println("Invalid Entry");
				menu();
		}
	}
	
	
	
	public static void main(String[] jjjjjj) {
		ATM_Machine a1=new ATM_Machine();
		
		a1.Validation();
	
	}

}
