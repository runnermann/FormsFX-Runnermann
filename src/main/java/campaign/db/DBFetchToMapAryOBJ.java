package campaign.db;

import authcrypt.UserData;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public enum DBFetchToMapAryOBJ {
    USER_STATS_MULTIPLE() {
        @Override
        public ArrayList<HashMap<String, Object>> query(String... notUsed) {
            ArrayList<HashMap<String, Object>> multiMap;
            String hexName = UserData.getUserMD5Hash();
            String strQuery = "SELECT DISTINCT " + formatColumns(STATS_COlUMNS) +
                    " FROM userdeckstats t1 JOIN deckmetadata t2 ON t1.deck_id = t2.deck_id" +
                    " WHERE t1.user_hash = '" + hexName + "';";

            multiMap = fetchMultiObjectsSerial(strQuery, STATS_COlUMNS);

            return multiMap;
        }
    };


    // ******* COMMON ********

    // LOGGING
    private static final Logger LOGGER = LoggerFactory.getLogger(DBFetchToMapAryOBJ.class);

    /**
     * No args constructor
     */
    DBFetchToMapAryOBJ() { /* No args constructor */ }

    // the query
    public abstract ArrayList<HashMap<String, Object>> query(String... whereStatement);

    private static final String[] STATS_COlUMNS = {
            "t2.subj",
            "amount",
            "create_time",
            "review_test_time"
    };

    /**
     * Used by stats gauges.
     * @param strQuery
     * @param columns
     * @return
     */
    private static ArrayList<HashMap<String, Object>> fetchMultiObjectsSerial(String strQuery, String[] columns) {
        //LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("strQuery: {}", strQuery);

        ArrayList<HashMap<String, Object>> mapArray = new ArrayList<>();
        HashMap<String, Object> map;// = new HashMap<>();

        try {
            DBConnect db = DBConnect.getInstance();
            CompletableFuture<QueryResult> future = db.getConnection().sendPreparedStatement(strQuery);
            QueryResult queryResult = future.get();
            int size = queryResult.getRows().size();

            if (size == 0) {
                LOGGER.debug("query result has 0 rows");
                map = new HashMap<>();
                map.put("empty", "true");
                mapArray.add(map);
            } else {
                LOGGER.debug("queryResult num columns: {}", ((ArrayRowData) queryResult.getRows().get(0)).getColumns().length);

                for (int j = 0; j < size; j++) {
                    map = new HashMap<>();
                    map.put("empty", "false");
                    Object[] res = ((ArrayRowData) (queryResult.getRows().get(j))).getColumns();
                    // create the map array with the results
                    for (int i = 0; i < res.length; i++) {
                        map.put(columns[i], res[i] != null ? res[i] : "0");
                    }
                    mapArray.add(map);
                }
            }
        } catch (NullPointerException e) {
            LOGGER.warn("WARNING: Null pointer exception at DBFetchToMapAry.fetchSingleResult(): may mean nothing");
        } catch (ExecutionException e) {
            LOGGER.warn("WARNING: {}\n{}" + e.getMessage(), e.getStackTrace());
        } catch (InterruptedException e) {
            LOGGER.warn("WARNING: {}\n{}" + e.getMessage(), e.getStackTrace());
        }

        return mapArray;
    }



    // Returns the array of columns formatted correctly
    // for the query.
    private static String formatColumns(String[] elements) {
        StringBuilder sb = new StringBuilder();
        // append all but the last element
        for (int i = 0; i < elements.length - 1; i++) {
            sb.append(elements[i] + ", ");
        }
        // add the last element without the comma
        sb.append(elements[elements.length - 1] + " ");
        return sb.toString();
    }

}
