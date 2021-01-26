package campaign.db;

import ch.qos.logback.classic.Level;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import forms.utility.Alphabet;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This class is for queries where it is expected
 * to return a single unique result. If there is
 * a potential for multiple results, use DBFecthMulti.
 */
public enum DBFetchUnique {
	
	/**
	 * Returns the account status of the Student or Professor. Takes
	 * two args arg[0] is the person_id if it exists, otherwise it expects
	 * arg[1] to provide the users encrypted orig_email. If the person_id has
	 * not been returned by another query, then person_id should be less than
	 * 0.
	 * <p>Expects user email in plain text</p>
	 */

	//XXXXXXx this is wrong. MembershipFee is a transaction table. the user email is not unique.
	// the user email is repeated for each transaction
	//	get ACCT_DATA and calculate
	/*ACCT_STATUS() {
		@Override
		public String[] query(String... args) {
			String[] res;// = new String[];
			long num = Long.parseLong(args[0]);
			if (num > 0) {
				String strQuery = "SELECT account_status " +
						"FROM membershipfee " +
						"WHERE person_id = '" + num + "';";
				res = fetchUniqueResult(strQuery);
			} else {
				String strQuery = "SELECT pId account_status " +
						"FROM membershipfee " +
						"WHERE payer_id = (SELECT person_id as pId" +
												"FROM Person " +
						" WHERE orig_email = '" + Alphabet.encrypt(args[1]) + "';";
				res = fetchUniqueResult(strQuery);
			}
			if(res == null || res.length < 7) {
				res[0] = "EMPTY";
			}
			return res;
		}
	},
	ACCT_TYPE() {
		@Override
		public String[] query(String... args) {
			return null;
		}
	},
	ACCT_PAID_DATE() {
		@Override
		public String[] query(String... args) {
			return null;
		}
	},

	 */
	PERSON_ID() {
		@Override
		public String[] query(String ...args) {
			String strQuery = "SELECT person_id " +
					"FROM person " +
					"WHERE orig_email = '" + Alphabet.encrypt(args[0]) + "';";
			return fetchUniqueResult(strQuery);
		}
	},
	STUDENT_ID() {
		@Override
		public String[] query(String ...args) {
			String strQuery = "SELECT person_id " +
					"FROM student " +
					"WHERE orig_email = '" + Alphabet.encrypt(args[0]) + "';";
			return fetchUniqueResult(strQuery);
		}
	},
	STUDENTS_UUID {
		@Override
		public String[] query(String ...args) {
			String strQuery = "SELECT uuid " +
					"FROM students " +
					"WHERE email = '" + args[0] + "';";
			return fetchUniqueResult(strQuery);
		}
	},
	/**
	 * Queries the table provided in the param for the users encrypted data if it exists.
	 *
	 * @param args args[0] = encrypted email: The encrypted original_username, usually their original email address.
	 * @return A string containing the encrypted query.
	 */
	STUDENT_ENCRYPTED_DATA() {
		@Override
		public String[] query(String ... args) {
			// photo link is not currently used.
			String strQuery = "SELECT first_name, last_name, middle_name, phone, age, institution, descript, photo_link," +
					" education_level, major, minor, cv_link, person_id" +
					" FROM Person JOIN Student USING(orig_email)" +
					//" FROM Student" +
					" WHERE orig_email = '" + Alphabet.encrypt(args[0]) + "';";
			return fetchUniqueResult(strQuery);
		}
	};
	
	
	
	
	
	
	// --------------------------------- --------------------------------- //
	// Common fields
	// --------------------------------- --------------------------------- //
	
	// LOGGING
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DBFetchUnique.class);
	
	
	// Columns
	String[] args;

	
	DBFetchUnique() { /* NO ARGS CONSTRUCTOR */}
	
	/**
	 * The query
	 * @param args The args for the query.
	 * @return
	 */
	public abstract String[] query(String ... args);
	
	/**
	 * Sends a query statement to the DB and returns the response.
	 * Use this query when the response should be for unique results.
	 * @param strQuery
	 * @return The response if successful or an empty string if not.
	 */
	private static String[] fetchUniqueResult(String strQuery) {
		
		LOGGER.setLevel(Level.DEBUG);
		LOGGER.debug("strQuery: {}", strQuery );
		
		DBConnect db = DBConnect.getInstance();
		String[] columnData = {"EMPTY"};

		try {
			CompletableFuture<QueryResult> future = db.getConnection()
					.sendPreparedStatement(strQuery);
			QueryResult queryResult = future.get();
			if (queryResult.getRows().size() > 0) {

				LOGGER.debug("query result has rows of data");

				ArrayRowData row = (ArrayRowData) (queryResult.getRows().get(0));
				columnData = new String[row.size()];

				for(int i = 0; i < row.size(); i++) {
					columnData[i] = row.get(i).toString();
				}
				//return columnData;
				//num = num.replaceAll("\\D", "");
				//id = Long.valueOf(num);
			}
		} catch (NullPointerException e) {
			LOGGER.warn("WARNING: Null pointer exception at DBFetch.fetchUniqueResult: Deck may not exist. ");
		} catch (ExecutionException e) {
			LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
		} catch (InterruptedException e) {
			LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
		}

		return columnData;
	}
	
	
}

