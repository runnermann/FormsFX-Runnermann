package campaign.db;

import ch.qos.logback.classic.Level;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import forms.utility.Alphabet;
import javafx.scene.image.Image;
import lombok.Builder;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public enum DBFetchToMapAry {


   /* __DECKS_METADATA_QUERYssss() {
        @Override
        public ArrayList<HashMap<String, String>> query(String ... whereStatement) {
            String strQuery = "SELECT " +
                    "deck_id, " +
                    "deck_photoid, " +
                    "deck_descript, " +
                    "creator_email, " +
                    "last_date, " +
                    "create_date, " +
                    "subj, " +
                    "section, " +
                    "deck_book, " +
                    "deck_class, " +
                    "deck_prof, " +
                    "deck_name, " +
                    "test_types, " +
                    "num_cards, " +
                    "num_imgs, " +
                    "num_video, " +
                    "num_audio, " +
                    "deck_numstars, " +
                    "num_users, " +
                    "price " +
                    "course_id " +
                    "FROM deckMetadata " +
                    whereStatement[0];

            LOGGER.info("Report query request: " + strQuery);
            DBConnect db = DBConnect.getInstance();
            return fetchMetaDataMap(strQuery);
        }
    },
    */
    DECKS_METADATA_QUERY() {
        @Override
        public ArrayList<HashMap<String, String>> query(String... whereStatement) {
            ArrayList<HashMap<String, String>> map;
            // the columns requested and to be mapped in the return data.
            String[] columns = {"deck_id",
                    "deck_photo",
                    "deck_descript",
                    "creator_email",
                    "last_date",
                    "create_date",
                    "subj",
                    "section",
                    "deck_book",
                    "deck_class",
                    "deck_prof",
                    "deck_name",
                    "test_types",
                    "num_cards",
                    "num_imgs",
                    "num_video",
                    "num_audio",
                    "deck_numstars",
                    "num_users",
                    "price",
                    "course_code"
            };
            String strQuery = "SELECT " + formatColumns(columns) +
                    " FROM deckMetadata " + whereStatement[0];
            map = fetchSingleResult(strQuery, columns);
            return map;
        }
    },
    /**
     * <p>Returns the account data of the Student or Professor. Takes
     * two args arg[0] is the person_id if it exists, otherwise it expects
     * arg[1] to provide the users orig_email. If the person_id has
     * not been returned by another query, then person_id should be less than
     * 0.</p>
     * <p>Expects user email in plain text</p>
     */
    ACCT_DATA() {
        @Override
        public ArrayList<HashMap<String, String>> query(String... args) {
            ArrayList<HashMap<String, String>> map;
                // the columns requested and to be mapped in the return data.
                String[] columns = {"account_id", "orig_email", "account_status", "catagory",
                                        "paid_date", "currency", "period", "due_date", "fee"};

                String strQuery = "SELECT " + formatColumns(columns) +
                        "FROM Account JOIN PurchaseFeeSched USING(catagory, currency) " +
                        " WHERE orig_email = '" + Alphabet.encrypt(args[0]) + "';";
                map = fetchSingleResult(strQuery, columns);

                LOGGER.debug("ACCT_DATA query: {}", strQuery);
            return map;
        }
    },

    STUDENT_ENCRYPTED_DATA() {
        @Override
        public ArrayList<HashMap<String, String>> query(String... args) {
            ArrayList<HashMap<String, String>> map;
            // the columns requested and to be mapped in the return data.
            String[] columns = {"first_name", "last_name", "middle_name", "phone", "age", "institution", "descript", "photo_link",
                    "education_level", "major", "minor", "cv_link", "person_id"};

            String strQuery = "SELECT " + formatColumns(columns) +
                    " FROM Person JOIN Student USING(orig_email)" +
                    //" FROM Student" +
                    " WHERE orig_email = '" + Alphabet.encrypt(args[0]) + "';";

            map = fetchSingleResult(strQuery, columns);
            return map;
        }
    };

    // --------------------------------- --------------------------------- //
    // Common fields
    // --------------------------------- --------------------------------- //

    // LOGGING
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DBFetchToMapAry.class);

    /**
     * No args constructor
     */
    DBFetchToMapAry() { /* no args constructor */ }

    // the query
    public abstract ArrayList<HashMap<String, String>> query(String ... whereStatement);

    /**
     * Creates an arrayList of hashmaps for a developer friendly
     * means to access elements of a map without worrying about
     * what is where in the array. Will require greater use of
     * memory.
     * @param strQuery
     * @return
     */
    private static ArrayList<HashMap<String, String>> fetchMetaDataMap(String strQuery) {
        ArrayList<HashMap<String, String>> mapArray = new ArrayList<>();
        HashMap<String, String> mapEl;

        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("strQuery: {}", strQuery );


        try {
            DBConnect db = DBConnect.getInstance();
            CompletableFuture<QueryResult> future = db.getConnection().sendPreparedStatement(strQuery);
            QueryResult queryResult = future.get();

            if(queryResult.getRows().size() == 0) {
                LOGGER.debug("query result has 0 rows");
            } else {
                LOGGER.debug("query result has rows of data");
                for(int i = 0; i < queryResult.getRows().size(); i++) {
                    // create a new HashMap
                    mapEl = new HashMap<>(20);
                    // create the map
                    Object[] res = new Object[19];
                    res = ((ArrayRowData) (queryResult.getRows().get(i))).getColumns();
                    mapEl.put("deck_id", res[0].toString());
                    mapEl.put("deck_photo", res[1].toString());
                    mapEl.put("deck_descript", res[2].toString());
                    mapEl.put("creator_email", res[3].toString());
                    mapEl.put("last_date", res[4].toString());
                    mapEl.put("create_date", res[5].toString());
                    mapEl.put("subj", res[6].toString());
                    mapEl.put("section", res[7].toString());
                    mapEl.put("deck_book", res[8].toString());
                    mapEl.put("deck_class", res[9].toString());
                    mapEl.put("deck_prof", res[10].toString());
                    mapEl.put("deck_name", res[11].toString());
                    mapEl.put("test_types", res[12].toString());
                    mapEl.put("num_cards", res[13].toString());
                    mapEl.put("num_imgs", res[14].toString());
                    mapEl.put("num_video", res[15].toString());
                    mapEl.put("num_audio", res[16].toString());
                    mapEl.put("deck_numstars", res[17].toString());
                    mapEl.put("num_users", res[18].toString());
                    mapEl.put("price", res[19].toString());
                    mapEl.put("course_id", res[20].toString());
                    // insert the map into the arrayList
                    mapArray.add(mapEl);
                }
            }
        } catch (NullPointerException e) {
            LOGGER.warn("WARNING: Null pointer exception at DBFetchToMapAry.fetchMap: Deck may not exist. ");
        } catch (ExecutionException e) {
            LOGGER.warn("WARNING: {}\n{}" + e.getMessage(), e.getStackTrace());
        } catch (InterruptedException e) {
            LOGGER.warn("WARNING: {}\n{}" + e.getMessage(), e.getStackTrace());
        }

        return mapArray;
    }

    /**
     * Returns an array of hashmaps with the first array-element
     * that contains the desired map.
     * @param strQuery
     * @param columns The columns requested in the query
     * @return the result
     */
    private static ArrayList<HashMap<String, String>> fetchSingleResult(String strQuery, String[] columns) {
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("strQuery: {}", strQuery );

        ArrayList<HashMap<String, String>> mapArray = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        try {
            DBConnect db = DBConnect.getInstance();
            CompletableFuture<QueryResult> future = db.getConnection().sendPreparedStatement(strQuery);
            QueryResult queryResult = future.get();

            LOGGER.debug("queryResult size: {}", ((ArrayRowData)queryResult.getRows().get(0)).getColumns().length);

            if (queryResult.getRows().size() == 0) {
                LOGGER.debug("query result has 0 rows");
                map.put("empty", "true");
            } else {
                map.put("empty", "false");
                Object[] res = ((ArrayRowData) (queryResult.getRows().get(0))).getColumns();
                // create the map array with the results
                for(int i = 0; i < res.length; i++) {
                    map.put(columns[i], res[i].toString());
                    //System.out.println("column " + i + " " + res[i]);
                }
            }
        } catch (NullPointerException e) {
            LOGGER.warn("WARNING: Null pointer exception at DBFetchToMapAry.fetchSingleResult(): Deck may not exist. ");
        } catch (ExecutionException e) {
            LOGGER.warn("WARNING: {}\n{}" + e.getMessage(), e.getStackTrace());
        } catch (InterruptedException e) {
            LOGGER.warn("WARNING: {}\n{}" + e.getMessage(), e.getStackTrace());
        }
        mapArray.add(map);
        return mapArray;
    }

    // Returns the array of columns formatted correctly
    // for the query.
    private static String formatColumns(String[] elements) {

        StringBuilder sb = new StringBuilder();
        // append all but the last element
        for(int i = 0; i < elements.length -1; i++) {
            sb.append(elements[i] + ", ");
        }
        // add the last element without the comma
        sb.append(elements[elements.length -1] + " ");
        return sb.toString();
    }
}
