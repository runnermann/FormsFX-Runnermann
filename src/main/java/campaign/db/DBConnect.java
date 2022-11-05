package campaign.db;

import campaign.db.errors.ModelError;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public final class DBConnect {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DBConnect.class);
      //private static final Logger LOGGER = LoggerFactory.getLogger(DBConnect.class);

      private static DBConnect CLASS_INSTANCE;
      private static Connection connection;
      private static boolean dbIsConnected;
      private static long connectTime;


      /*no args constructor */
      private DBConnect() {
            long now = System.currentTimeMillis();
            long result = now - connectTime;
            if (!dbIsConnected || result > 6000) {
                  connectTime = System.currentTimeMillis();
                  init();
            }
      }

      public static synchronized DBConnect getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new DBConnect();
            }
            return CLASS_INSTANCE;
      }

      private static void init() {
            //LOGGER.setLevel(Level.DEBUG);
            //LOGGER.debug("init() called");
            try {
                  // Create a signed JWT or JWTS token
                  // S3 access key related.
                  ModelError dang = ModelError.getInstance();
                  dang.it();

                  // s3 accessKey = errors[0] and s3 secretKey = errors[1]
                  final String[] errors = dang.getS3Errors();

                  connection = PostgreSQLConnectionBuilder
                      .createConnectionPool(errors[0]);
            } catch (Exception e) {
                  //LOGGER.warn("Database Connection Error. Could not connect to the database.");
            }
            //LOGGER.debug("init() completed");
      }


      public Connection getConnection() {
            // LOGGER.debug("getting DB connection.");
            if (connection == null) {
                  init();
            }
            if (connection.isConnected()) {
                  return connection;
            } else {
                  //LOGGER.warn("getConnection(): there is no network connection. Returning null.");
                  return null;
            }
      }
}
