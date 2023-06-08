package campaign.db;

import ch.qos.logback.classic.Level;
import com.github.jasync.sql.db.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public enum DBDelete {
    PERSON_BY_ID() {

        @Override
        public boolean query(String ...args) {
            String strQuery = "DELETE FROM Person WHERE person_id =" + args[0];
            return doDelete(strQuery);
        }
    };

    // --------------------------------- --------------------------------- //
    // Common fields
    // --------------------------------- --------------------------------- //

    // LOGGING
    private static final Logger LOGGER = LoggerFactory.getLogger(DBDelete.class);
    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DBDelete.class);

    DBDelete() { /* NO ARGS CONSTRUCTOR */ }

    /**
     * The query
       *
     * @param args The args for the query.
     * @return
     */
    public abstract boolean query(String ... args);

    public static boolean doDelete(String strQuery) {

        //LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("strQuery: {}", strQuery );

        DBConnect db = DBConnect.getInstance();
        String[] columnData = {"EMPTY"};

        try {
            CompletableFuture<QueryResult> future = db.getConnection()
                    .sendPreparedStatement(strQuery);
            QueryResult queryResult = future.get();
            LOGGER.debug("returned: {}", queryResult.getStatusMessage());

            return queryResult.getStatusMessage().equals("200");

        } catch (NullPointerException e) {
            LOGGER.warn("WARNING: Null pointer exception at DBFetch.fetchUniqueResult: Deck may not exist. ");
        } catch (ExecutionException e) {
            LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
        } catch (InterruptedException e) {
            LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
        }
        return false;
    }
}
