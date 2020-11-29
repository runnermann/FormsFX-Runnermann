package campaign.db;

import ch.qos.logback.classic.Level;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import org.apache.http.MethodNotSupportedException;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This class is for queries where it is expected
 * to return multiple results/rows. If results are unique
 * use DBFetchUnique.
 */
public enum DBFetchMulti  {
	
	DECK_SUBJECTS() {
		@Override
		public ArrayList<String> query(String ... notUsed) {
			String strQuery = "SELECT DISTINCT subj " +
					"FROM deckMetadata;";
			return fetchMultiResult(strQuery);
		}
	},
	DECK_PROFS() {
		@Override
		public ArrayList<String> query(String ... notUsed) {
			String strQuery = "SELECT deck_prof " +
					"FROM deckMetadata;";
			return fetchMultiResult(strQuery);
		}
	},
	COURSE_CODES() {
		@Override
		public ArrayList<String> query(String ... notUsed) {
			String strQuery = "SELECT course_code " +
					"FROM Course;";
			return fetchMultiResult(strQuery);
		}
	},
	DECK_SECTIONS() {
		@Override
		public ArrayList<String> query(String ... notUsed) {
			String strQuery = "SELECT section " +
					"FROM deckMetadata;";
			return fetchMultiResult(strQuery);
		}
	},
	DECK_SUB_SECTIONS() {
		@Override
		public ArrayList<String> query(String ... notUsed) {
			String strQuery = "SELECT sub_section " +
					"FROM deckMetadata;";
			return fetchMultiResult(strQuery);
		}
	},
	DECK_FRIENDS() {
		@Override
		public ArrayList<String> query(String ... deckID)  {
			// @TODO complete DECK_FRIENDS in DBFetchMulti
			return null;
		}
	},
	DECK_CLASSMATES() {
		@Override
		public ArrayList<String> query(String ... deckID)  {
			// @TODO complete DECK_CLASSMATES in DBFetchMulti
			return null;
		}
	};
	
	// --------------------------------- --------------------------------- //
	// Common fields
	// --------------------------------- --------------------------------- //
	
	// LOGGING
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DBFetchMulti.class);
	
	DBFetchMulti() { /* NO ARGS CONSTRUCTOR */ }
	
	/**
	 * The query
	 * @param args The args for the query.
	 * @return
	 */
	public abstract ArrayList<String> query(String ... args) ;
	
	private static ArrayList<String> fetchMultiResult(String strQuery) {
		
		LOGGER.setLevel(Level.DEBUG);
		LOGGER.debug("strQuery: {}", strQuery );
		ArrayList<String> returnAry = new ArrayList<>();
		DBConnect db = DBConnect.getInstance();
		try {
			CompletableFuture<QueryResult> future = db.getConnection().sendPreparedStatement(strQuery);
			QueryResult queryResult = future.get();
			if(queryResult.getRows().size() == 0) {
				LOGGER.debug("query result has 0 rows");
			} else {
				LOGGER.debug("query result has rows of data");
				
				//queryResult.getRows().stream()
				//		.forEach(e -> returnAry.add(e.));
				
				for(int i = 0; i < queryResult.getRows().size(); i++) {
					returnAry.add(Arrays.toString(((ArrayRowData) (queryResult.getRows().get(i))).getColumns()));
				}
			}
			
		} catch (NullPointerException e) {
			LOGGER.warn("WARNING: Null pointer exception at DBFetch.fetchMultiResult: Deck may not exist. ");
		} catch (ExecutionException e) {
			LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
		} catch (InterruptedException e) {
			LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
		}
		// If causing an error here, check if there
		// are courses in the Courses DB. 
		LOGGER.debug(returnAry.get(0));
		
		return returnAry;
	}
}
