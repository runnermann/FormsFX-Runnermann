package campaign.db;

import ch.qos.logback.classic.Level;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import forms.utility.Alphabet;
//import javafx.scene.image.Image;
//import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
//import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public enum DBFetchToMapAry {
      DECK_METADATA_SINGLE() {
            @Override
            public ArrayList<HashMap<String, String>> query(String... whereStatement) {
                  ArrayList<HashMap<String, String>> map;
                  String strQuery = "SELECT " + formatColumns(DECK_COLUMNS) +
                      " FROM deckMetadata " + whereStatement[0];
                  map = fetchSingleResult(strQuery, DECK_COLUMNS);

                  return map;
            }
      },

      DECK_METADATA_MULTIPLE() {
            @Override
            public ArrayList<HashMap<String, String>> query(String... whereStatement) {
                  ArrayList<HashMap<String, String>> multiMap;
                  String strQuery = "SELECT " + formatColumns(DECK_COLUMNS) +
                      " FROM deckMetadata " + whereStatement[0];
                  multiMap = DBFetchToMapAry.fetchMultiResultSerial(strQuery, DECK_COLUMNS);

                  return multiMap;
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
                  String[] columns = {"account_id", "account_status", "catagory",
                      "paid_date", "currency", "fee"};

                  String strQuery = "SELECT " + formatColumns(columns) +
                      "FROM Account JOIN PurchaseFeeSched USING(catagory, currency) " +
                      " WHERE orig_email = '" + Alphabet.encrypt(args[0]) + "';";
                  map = fetchSingleResult(strQuery, columns);
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
      //                          Common fields
      // --------------------------------- --------------------------------- //

      // LOGGING
      private static final Logger LOGGER = LoggerFactory.getLogger(DBFetchToMapAry.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DBFetchToMapAry.class);
      // the columns requested and to be mapped in the return data.
      private final static String[] DECK_COLUMNS = {
          "deck_id",
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
          "course_code",
          "deck_school",
          "deck_language",
          "course_code",
          "share_distro",
          "sell_deck",
          "full_name"
      };

      /**
       * No args constructor
       */
      DBFetchToMapAry() { /* no args constructor */ }

      // the query
      public abstract ArrayList<HashMap<String, String>> query(String... whereStatement);


      /**
       * Returns an array of hashmaps with the first array-element
       * that contains the desired map.
       *
       * @param strQuery
       * @param columns  The columns requested in the query
       * @return the result
       */
      private static ArrayList<HashMap<String, String>> fetchSingleResult(String strQuery, String[] columns) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("strQuery: {}", strQuery);

            ArrayList<HashMap<String, String>> mapArray = new ArrayList<>();
            HashMap<String, String> map = new HashMap<>();

            try {
                  DBConnect db = DBConnect.getInstance();
                  CompletableFuture<QueryResult> future = db.getConnection().sendPreparedStatement(strQuery);
                  QueryResult queryResult = future.get();

                  if (queryResult.getRows().size() == 0) {
                        LOGGER.warn("query result has 0 rows");
                        map.put("empty", "true");
                  } else {
                        LOGGER.debug("queryResult num columns: {}", ((ArrayRowData) queryResult.getRows().get(0)).getColumns().length);

                        map.put("empty", "false");
                        Object[] res = ((ArrayRowData) (queryResult.getRows().get(0))).getColumns();
                        // create the map array with the results
                        for (int i = 0; i < res.length; i++) {
                              map.put(columns[i], res[i] != null ? res[i].toString() : "0");
                        }
                  }
            } catch (NullPointerException e) {
                  LOGGER.warn("WARNING: Null pointer exception at DBFetchToMapAry.fetchSingleResult(): may mean nothing");
            } catch (ExecutionException e) {
                  LOGGER.warn("WARNING: {}\n{}" + e.getMessage(), e.getStackTrace());
            } catch (InterruptedException e) {
                  LOGGER.warn("WARNING: {}\n{}" + e.getMessage(), e.getStackTrace());
            }
            mapArray.add(map);
            return mapArray;
      }

      private static ArrayList<HashMap<String, String>> fetchMultiResultSerial(String strQuery, String[] columns) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("strQuery: {}", strQuery);

            ArrayList<HashMap<String, String>> mapArray = new ArrayList<>();
            HashMap<String, String> map;// = new HashMap<>();

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
                                    map.put(columns[i], res[i] != null ? res[i].toString() : "0");
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

      private static HashMap<String, String> decryptNames(HashMap<String, String> map) {
            String encryptedFirst = map.get("first_name");
            String encryptedLast = map.get("last_name");
            map.replace("first_name", Alphabet.decrypt(encryptedFirst));
            map.replace("last_name", Alphabet.decrypt(encryptedLast));
            return map;
      }
}
