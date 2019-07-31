package atmmachine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLog {static final Logger logger = LogManager.getLogger(MyLog.class.getName());

	public static Logger getLogger() {
		if (logger == null) {
			try {
				new MyLog();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return logger;
	}

	

}
