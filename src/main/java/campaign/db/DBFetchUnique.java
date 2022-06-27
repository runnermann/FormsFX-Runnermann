package campaign.db;

import ch.qos.logback.classic.Level;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import forms.utility.Alphabet;
import org.slf4j.Logger;
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

      CREATOR_HASH() {
            @Override
            public String[] query(String... args) {
                  String strQuery = "SELECT email " +
                      "FROM EmailHash " +
                      "WHERE hash = '" + args[0] + "';";
                  return fetchUniqueResult(strQuery);
            }
      },
      PERSON_ID() {
            @Override
            public String[] query(String... args) {
                  String strQuery = "SELECT person_id " +
                      "FROM person " +
                      "WHERE orig_email = '" + Alphabet.encrypt(args[0]) + "';";
                  return fetchUniqueResult(strQuery);
            }
      },

      STUDENTS_UUID {
            @Override
            public String[] query(String... args) {
                  String strQuery = "SELECT uuid, password " +
                      "FROM students " +
                      "WHERE email = '" + args[0] + "';";
                  return fetchUniqueResult(strQuery);
            }
      },

      STUDENT_ENCRYPTED_DATA() {
            /**
             * Queries the table provided in the param for the users encrypted data if it exists.
             *
             * @param args args[0] = encrypted email: The encrypted original_username, usually their original email address.
             * @return A string containing the encrypted query.
             */
            @Override
            public String[] query(String... args) {
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
      private static final Logger LOGGER = LoggerFactory.getLogger(DBFetchUnique.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DBFetchUnique.class);


      // Columns
      String[] args;


      DBFetchUnique() { /* NO ARGS CONSTRUCTOR */}

      /**
       * The query
       *
       * @param args The args for the query.
       * @return
       */
      public abstract String[] query(String... args);

      /**
       * Sends a query statement to the DB and returns the response.
       * Use this query when the response should be for unique results.
       *
       * @param strQuery
       * @return The response if successful or an empty string if not.
       */
      private static String[] fetchUniqueResult(String strQuery) {

            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("strQuery: {}", strQuery);

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

                        for (int i = 0; i < row.size(); i++) {
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

