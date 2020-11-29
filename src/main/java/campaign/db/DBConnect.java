package campaign.db;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
//import com.github.jasync.sql.db.postgresql.pool.PostgreSQLConnectionFactory;
import forms.utility.StudentDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class DBConnect {

    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DBConnect.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnect.class);

    private static DBConnect  CLASS_INSTANCE;
    private static Connection connection;


    /*no args constructor */
    private DBConnect() {
        init();
    }

    public static synchronized DBConnect getInstance() {
        if(CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new DBConnect();
        }
        return CLASS_INSTANCE;
    }

    private static void init() {
        try {
            connection = PostgreSQLConnectionBuilder
                // AWS
                   .createConnectionPool("jdbc:postgresql://usa-conus.caws1d0xah4s.us-west-2.rds.amazonaws.com:5432/usa-conus?user=lowell&password=ochCtirAwddThcatemHQzi2002");
            
                // new-usa-conus
                //.createConnectionPool("jdbc:postgresql://localhost:5432/new-usa-conus?user=postgres&password=backSpace01");
                // old usa-conus
                //    .createConnectionPool("jdbc:postgresql://localhost:5432/usa-conus?user=postgres&password=backSpace01");
        } catch (Exception e) {
            LOGGER.warn("Database Connection Error. Could not connect to the database.");
        }
    }


    public Connection getConnection() {
        if(connection.isConnected()) {
            return this.connection;
        }
        else {
            LOGGER.warn("getConnection(): there is no network connection. Returning null.");
            return null;
        }
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        DBConnect db = DBConnect.getInstance();
        Connection connect = db.getConnection();
    //    insert deck metadata test
    //    CompletableFuture<QueryResult> future = connect.sendPreparedStatement("INSERT INTO deck_metadata (deck_name) VALUES ('test entry')");
    //    String[] queryArgs = {"idk@idk.com", "History 2054"};
    

    //  Test 1, for  insert Transaction for Student & Person
        /*CompletableFuture<QueryResult> future = connect.sendQuery("BEGIN;" +
                " INSERT INTO Person (first_name, last_name, middle_name, orig_email, current_email, phone, age, ip_connect, institution, descript, photo_link) " +
                " VALUES ('{27,117,223,113,197}', '{27,117,223,113,197}', 'A', '{139,218,453,1268,695,654,1057,2344,963,1730,163}', '{139,218,453,1268,695,654,1057,2344,963,1730,163}', '650.334.7654', 21, '101.110.1234/32', 'FlashMonkey Inc.', 'I am a happy monkey descript', 'my photo link');" +
                " INSERT INTO Student (orig_email, education_level, major, minor, cv_link ) VALUES ( '{139,218,453,1268,695,654,1057,2344,963,1730,163}', '3rd year bachelors student', 'Astro Physics', 'Mathmatics', 'cvLink here');" +
                " COMMIT;"
        );*/
        CompletableFuture<QueryResult> future = connect.sendQuery("INSERT INTO Student (first_name, last_name, middle_name, orig_email, current_email, phone, age, ip_connect, institution, descript, photo_link, education_level, major, minor, cv_link) " +
                " VALUES ('{27,117,223,113,197}', '{27,117,223,113,197}', 'A', '{139,218,453,1268,695,654,1057,2344,963,1730,163}', '{139,218,453,1268,695,654,1057,2344,963,1730,163}', '650.334.7654', 21, '101.110.1234/32', 'FlashMonkey Inc.', " +
                "'I am a happy monkey descript', 'my photo link', '3rd year bachelors student', 'Astro Physics', 'Mathmatics', 'cvLink here');"
        );
        future.get();
    
        connect.disconnect();
        // end test 1

    /*
        // test 2 check that data exists and is extracted/parsed correctly
        String strQuery = "SELECT first_name, last_name, middle_name, phone, age, institution, descript, photo_link" +
                " education_level, major, minor, cv_link" +
                " FROM " + "Person " + "JOIN " + "Student" + " USING(orig_email)" +
                " WHERE orig_email = '" + "{139,218,453,1268,695,654,1057,2344,963,1730,163}" + "'" +
                ";";
        
        
        CompletableFuture<QueryResult> future = db.getConnection()
                .sendPreparedStatement(strQuery);
    
    //    CompletableFuture<QueryResult> future = connect.
    //    CompletableFuture<QueryResult> future = connect.sendPreparedStatement(DBFetch.DECK_META_DATA.query(queryArgs));
        future.get();
    
        QueryResult queryResult = future.get();
        String returned = Arrays.toString(((ArrayRowData) (queryResult.getRows().get(0))).getColumns());
        System.out.println(Arrays.toString(((ArrayRowData) (queryResult.getRows().get(0))).getColumns()));

     
        connect.disconnect();
    
        StudentDescriptor des = new StudentDescriptor();
        des.parseResponse(returned);
        // end test 2

     */
    }

 
}
