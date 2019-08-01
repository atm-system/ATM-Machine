package atmmachine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import atmmachine.ATM_Machine;

public class Log_Out {
	static final Logger logger = LogManager.getLogger(DBConnection.class.getName());

	public void logout()
	{
		System.out.println("Logged Out");
		ATM_Machine a1= new ATM_Machine();
		a1.Card_number=null;
		a1.tempcardnum=null;
		a1.pin=null;
		a1.Validation();
		logger.info("USER logged out");
	}
	
}
