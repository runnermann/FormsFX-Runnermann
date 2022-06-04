package campaign;

import authcrypt.Auth;
import authcrypt.UserData;
import authcrypt.Verify;

import campaign.db.DBConnect;
import campaign.db.Table;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;

import flashmonkey.FlashMonkeyMain;
import forms.utility.Alphabet;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ReportError extends AppenderBase<ILoggingEvent> {
	// Haaaa Haaaa!!! cannot use logger here. Causes an initialization error.
	//private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ReportError.class);
	
	private static Connection connect;
	static int DEFAULT_LIMIT = 10;
	int counter = 0;
	int limit = DEFAULT_LIMIT;

	boolean connectedAlready;
	long connectedTime;
	
	private void init() {
		if(! isConnected()) {
			return;
		}
		
		DBConnect db = DBConnect.getInstance();
		connect = null;
		try {
			//System.out.println("ReportError.init() attempting connection");
			connect = db.getConnection();
		}
		catch (Exception e) {
			//System.out.println("ReportError.init() EXCEPTION: @init() did not connect to DB");
			connect = null;
			e.printStackTrace();
		}
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	
	@Override
	public void append(ILoggingEvent event) {
		/* Send event to database error log */
		/*init();
		
		if (counter >= limit) {
			return;
		}
		if(connect == null) {
			//System.out.println(" We are not connected to the DB.  Check network connection.");
			return;
		}
			//System.out.println("called endSessionTime()");
			Table table = Table.ERROR_WARNING;
			
			if (table == null) {
				//System.out.println(table.getDefaultErrorMsg());
			}
			//System.out.println("Table = {}" + table.toString());
			
			DBConnect db = DBConnect.getInstance();

			StackTraceElement callerData = event.getCallerData()[0];
			
			try {
				CompletableFuture<QueryResult> future = db.getConnection()
						.sendPreparedStatement("INSERT INTO error_warning (" +
								"uhash, " +
								"event_localtime, " +
								"message, " +
								"logger_name, " +
								"level, " +
								"version, " +
								//"caller_class, " +
								"caller_method, " +
								"caller_line ) VALUES ('"
								+ Alphabet.encrypt(UserData.getUserName()) + "', '"
								+ System.currentTimeMillis() + "', '"
								+ event.getFormattedMessage() + "', '"
								+ event.getLoggerName() + "', '"
								+ event.getLevel() + "', '"
								+ FlashMonkeyMain.VERSION + "', '"
								//+ event.getLoggerContextVO().getName() + "', '"
								+ callerData.getMethodName() + "', '"
								+ callerData.getLineNumber() + "')");
				future.get();
			} catch (ExecutionException e) {
				//LOGGER.warn("WARNING: I cannot connect! : ExecutionException, DBConnection ERROR, {}", e.getMessage());
				e.printStackTrace();
			} catch (InterruptedException e) {
				//LOGGER.warn("WARNING: InterruptedException, DBConnection ERROR, {}", e.getMessage());
				e.printStackTrace();
			} finally {
				//connect.disconnect();
			}*/
		
		//System.out.println("callerData.getMethodName() " + callerData.getMethodName());
		//System.out.println("callerData.getLineNumber() " + callerData.getLineNumber());
		
	}
	
	private boolean isConnected() {
		long now = System.currentTimeMillis();
		long result = now - connectedTime;
		if(connectedAlready && result < 5000) {
			return true;
		} else {
			connectedAlready = false;
			try {
				String url = "https://www.flashmonkey.xyz";
				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				connection.setRequestMethod("HEAD");
				int responseCode = connection.getResponseCode();

				if (responseCode != 200) {
					return false;
				}
				connectedAlready = true;
				connectedTime = System.currentTimeMillis();
				return true;

			} catch (ProtocolException e) {
				//LOGGER.warn(e.getMessage());
			} catch (IOException e) {
				//LOGGER.warn(e.getMessage());
			}
			return false;
		}
	}
}
