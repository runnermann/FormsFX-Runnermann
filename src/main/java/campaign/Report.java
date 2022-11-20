package campaign;

import authcrypt.UserData;
import campaign.db.DBConnect;
import campaign.db.DBFetchUnique;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import fileops.utility.Utility;
import flashmonkey.EndGame;
import flashmonkey.FlashCardOps;
import flashmonkey.Timer;
import forms.utility.Alphabet;
import metadata.DeckMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NOTE: Information about users, ie names and email address are stored in an encrypted state.
 * When users information is compared from the DB, it is expected to be encrypted. Therefore
 * always ensure, that users information is encrypted when sent to the DB, and decrypted when
 * used by the current session.
 */
public final class Report {

      private static final Logger LOGGER = LoggerFactory.getLogger(Report.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Report.class);

      private static Connection connect;
      private Timer fmTimer;
      private static Report CLASS_INSTANCE;

      private Report() {
            init();
      }

      public static synchronized Report getInstance() {
            //LOGGER.setLevel(Level.DEBUG);
            //LOGGER.info("getInstance called");

            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new Report();
            }
            return CLASS_INSTANCE;
      }

      /**
       * Initialization, sets up the
       * connection.
       */
      private void init() {
            DBConnect db = DBConnect.getInstance();
            fmTimer = Timer.getClassInstance();
            connect = null;
            try {
                  //LOGGER.debug("@init() attempting connection");
                  connect = db.getConnection();
                  fmTimer = Timer.getClassInstance();
            } catch (Exception e) {
                  connect = null;
                  e.printStackTrace();
            }
      }

      /**
       * Call sessionStart() at the beginning of the session
       * Then call sessionUseTime() at the end.
       */
      public void sessionStart() {
            fmTimer.begin();
            //LOGGER.info("sessionStart called at: {}" + fmTimer.getBeginTime());
      }

      /**
       * Call sessionUseTime() at the end of a session. Reports to
       * the table object set in setTable();
       */
      public void endSessionTime() {
            if (connect != null && Utility.isConnected()) {
                  LOGGER.info("called endSessionTime()");

                  DBConnect db = DBConnect.getInstance();
                  fmTimer.end();
                  Long useTime = fmTimer.getTotalTime();

                  //Verify v = new Verify();

                  try {
                        String userName = UserData.getUserName();
                        CompletableFuture<QueryResult> future = db.getConnection()
                            .sendPreparedStatement("INSERT INTO sessions (" +
                                "uhash, " +
                                "event_localtime, " +
                                "createnotes_time, " +
                                "createtest_time, " +
                                "qna_usetime, " +
                                "test_usetime, " +
                                "total_usetime, " +
                                "session_score) VALUES ('"
                                + Alphabet.encrypt(userName != null ? userName : "name was null") + "', '"
                                + fmTimer.getBeginTime() + "', '"
                                + fmTimer.getCreateNotesTime() + "', '"
                                + fmTimer.getCreateTestsTime() + "', '"
                                + fmTimer.getQnATime() + "', '"
                                + fmTimer.getTakeTestTime() + "', '"
                                + useTime + "', '"
                                + EndGame.getPercentScore() + "')");
                        future.get();
                  } catch (ExecutionException e) {
                        LOGGER.warn("WARNING: I cannot connect! : ExecutionException, DBConnection ERROR, {}" + e.getMessage());
                        e.printStackTrace();
                  } catch (InterruptedException e) {
                        LOGGER.warn("WARNING: InterruptedException, DBConnection ERROR, {}" + e.getMessage());
                        e.printStackTrace();
                  } finally {
                        //connect.disconnect();
                  }

            } else {
                  LOGGER.info("endSessionTime(). We are not connected to the DB.  Check network connection.");
            }
      }

      /**
       * Sets metadata to the database. Depending on if the deck exists, this method will
     * conduct an insert or an update. Returns a long. Negative numbers are an error.
       * @param metaData ..
     * @return Returns the deck_id for insert or update and was successful, otherwise returns -99 if the insert failed,
     * -2 if the update failed.
       */
      public long reportDeckMetadata(DeckMetaData metaData) {
            //query DB for username and deckname, get ID, and do update
            long id = queryGetDeckID();

            LOGGER.debug("id: " + id);

            if (id == -99) {
                  // item does not exist, do insert
                  LOGGER.debug("result was -99. inserting new row");
                  return insertDeckMetadata(metaData);
            } else {
                  // item exists, do update
                  LOGGER.debug("result was " + id + ". updating!");
                  return updateDeckMetadata(metaData, id) ? id : -2;
            }
      }

      /**
       * Updates test metaData, uses same array as
       * reportDeckMetadata
       * @param metaDataAry ..
       */
      public void reportTestMetaData(HashMap<String, String> metaDataAry) {
            //query DB for username and deckname, get ID, and do update
            long id = queryGetDeckID();
            LOGGER.debug("id: " + id);
            updateTestMetaData(metaDataAry, id);
      }


      /**
       * The initial insert of this decks metadata into the DB. If the deck was originally
       * created by this user it will set the name of the creator to this user. Otherwise
       * it will query the DB for the correct user.
       * @param metaObj ..
       * @return ..
       */
       private long insertDeckMetadata(DeckMetaData metaObj) {
            if (connect != null && Utility.isConnected()) {
                  //UserData user = new UserData();
                  String sep = "', '";
                  LOGGER.info("inserting data into deckMetadata.");

                  StringBuffer sbuffer = new StringBuffer();
                  String insertQuery = "INSERT INTO deckMetadata(" +
                      "last_date, " +     // 0 lastdate
                      "deck_descript, " + // 1 descript
                      "user_email, " +    // 2 userName
                      "creator_email, " + // 3 origAuthor
                      "deck_school, " +   // 4 school
                      "deck_book, " +     // 5 book
                      "deck_class, " +    // 6 class
                      "deck_prof, " +     // 7 prof
                      "subj, " +          // 8 ie physics
                      "section, " +       // 9 ie mechanics // previously cat
                      "deck_language, " + // 10 lang
                      "deck_name, " +     // 11 deckName
                      "test_types, " +    // 12 testtypes
                      "num_cards, " +     // 13 numCards
                      "num_imgs, " +      // 14 numImg
                      "num_video, " +     // 15 numVid
                      "num_audio, " +     // 16 numAud
                      "course_code, " +   // 17 courseCode
                      "session_count, " + // 18 add to the session count
                      "share_distro, " +  // 19 will share distro
                      "sell_deck, " +     // 20 will share deck
                      "full_name, " +     // 21 The deck file full name
                      "user_hash, " +     // 22 This user's md5Hash
                    "price, " +         // 23 requested price
                    "deck_numstars, " +   // 24 num
                    "deck_photo) VALUES ('"; // 25 photo
                  sbuffer.append(insertQuery);
                  // subtract the last element from the array. We are
                  // not reporting the session score here.
                  sbuffer.append(metaObj.getLastDate() + sep);
                  sbuffer.append(metaObj.getDescript() + sep);
                  sbuffer.append(Alphabet.encrypt(authcrypt.UserData.getUserName()) + sep);
                  sbuffer.append(getOriginalCreator(authcrypt.UserData.getUserName()) + sep);
                  sbuffer.append(metaObj.getDeckSchool() + sep);
                  sbuffer.append(metaObj.getDeckBook() + sep);
                  sbuffer.append(metaObj.getDeckClass() + sep);
                  sbuffer.append(metaObj.getDeckProf() + sep);
                  sbuffer.append(metaObj.getSubj() + sep);
                  sbuffer.append(metaObj.getCat() + sep);
                  sbuffer.append(metaObj.getLang() + sep);
                  sbuffer.append(FlashCardOps.getInstance().getDeckLabelName() + sep);
                  sbuffer.append(metaObj.getTestTypes() + sep);
                  sbuffer.append(metaObj.getNumCard() + sep);
                  sbuffer.append(metaObj.getNumImg() + sep);
                  sbuffer.append(metaObj.getNumVideo() + sep);
                  sbuffer.append(metaObj.getNumAudio() + sep);
                  sbuffer.append(metaObj.getCourseCode() + sep);
                  sbuffer.append(1 + sep);
                  sbuffer.append(metaObj.isShareDistro() + sep);
                  sbuffer.append(metaObj.isSellDeck() + sep);
                  sbuffer.append(FlashCardOps.getInstance().getDeckFileName() + sep);
                  sbuffer.append(authcrypt.UserData.getUserMD5Hash() + sep);
                  sbuffer.append(metaObj.getPrice() + sep);
//            sbuffer.append(metaObj.getNumSessions() + sep);
                  sbuffer.append(metaObj.getNumStars() + sep);
                  sbuffer.append(metaObj.getDeckImgName() + "')" +
                    " RETURNING deck_id;");
                  DBConnect db = DBConnect.getInstance();

                  LOGGER.debug(sbuffer.toString());

                  try {
                        CompletableFuture<QueryResult> future = db.getConnection()
                              .sendPreparedStatement(sbuffer.toString());
                        QueryResult queryResult = future.get();
                        if (queryResult.getRows().size() == 0) {
                              return -99;
                        } else {
                              String num = Arrays.toString(((ArrayRowData) (queryResult.getRows().get(0))).getColumns());
                              num = num.replaceAll("\\D", "");
                              return Long.valueOf(num);
                        }
                  } catch (NullPointerException e) {
                        LOGGER.warn("INFO: Null pointer exception at getDeckID. Deck may not exist. ");
                  } catch (ExecutionException e) {
                        LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage());//, e.getStackTrace());
                  } catch (InterruptedException e) {
                        LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage());//, e.getStackTrace());
                  }
                  // failed return -99
                  return -99;
            } else {
                  LOGGER.info(" reportDeckMetaData() DB is not connected. Check network connection.");
                  return -99;
            }
      }

      /**
       * Returns this user if they are the original creator
       * of the deck. Otherwise sends a query to the DB to
       * request based on the Hash provided in the deckName.
       *
       * @param thisUserText ..
       * @return the oriignal creator
       */
      private String getOriginalCreator(String thisUserText) {
            String name = FlashCardOps.getInstance().getDeckFileName();
            String firstHash = name.substring(0, name.indexOf("_"));

            if (authcrypt.UserData.getUserMD5Hash().equals(firstHash)) {
                  return Alphabet.encrypt(thisUserText);
            } else {
                  String[] creatorEncrypted = DBFetchUnique.CREATOR_HASH.query(firstHash);
                  return creatorEncrypted[0];
            }
      }

      /**
       * Most frequently, MetaData will come from an update.
       * Sends data from the MetaDataAry to the MetaData Table
       * receives data from updateDataAry in DeckMetaData
       *
       * @param metaObj ..
       * @param id      ..
       * @return ..true if successful
       */
      private boolean updateDeckMetadata(DeckMetaData metaObj, long id) {
            boolean bool = false;
            LOGGER.info("updateDeckMetadata sending, ID: {}" + id);

            if (connect != null) {
                  int i = 0;
                  String updateDB = "UPDATE deckMetadata SET" +
                      " last_date ='" + metaObj.getLastDate() + "'" + // lastDate
                      ", deck_descript ='" + metaObj.getDescript() + "'" + // deckDescript
                      ", course_code ='" + metaObj.getCourseCode() + "'" +
                      ", deck_school ='" + metaObj.getDeckSchool() + "'" +
                      ", deck_book ='" + metaObj.getDeckBook() + "'" +
                      ", deck_class ='" + metaObj.getDeckClass() + "'" +
                      ", deck_prof ='" + metaObj.getDeckProf() + "'" +
                      ", subj ='" + metaObj.getSubj() + "'" +
                      ", section ='" + metaObj.getCat() + "'" +
                      ", deck_language ='" + metaObj.getLang() + "'" +
                      ", test_types ='" + metaObj.getTestTypes() + "'" +
                      ", num_cards ='" + metaObj.getNumCard() + "'" +
                      ", num_imgs ='" + metaObj.getNumImg() + "'" +
                      ", num_video ='" + metaObj.getNumVideo() + "'" +
                      ", num_audio ='" + metaObj.getNumAudio() + "'" +
                      ", session_score ='" + metaObj.getScores().toString() + "'" +
                      ", share_distro = '" + metaObj.isShareDistro() + "'" +
                      ", sell_deck = '" + metaObj.isSellDeck() + "'" +
                      ", price = '" + metaObj.getPrice() + "'" +
                      ", deck_numstars = '" +metaObj.getNumStars() + "'" +
                      ", deck_photo = '" +   metaObj.getDeckImgName() + "'" +
                      ", session_count = session_count+1 " +
                      " WHERE deck_id = " + id + ";";

                  LOGGER.debug("query: " + updateDB);

                  DBConnect db = DBConnect.getInstance();
                  try {
                        CompletableFuture<QueryResult> future = db.getConnection()
                            .sendQuery(updateDB);
                        future.get();
                        bool = true;

                  } catch (ExecutionException e) {
                        LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage()); //, e.getStackTrace());
                        e.printStackTrace();
                  } catch (InterruptedException e) {
                        LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage()); //, e.getStackTrace());
                        e.printStackTrace();
                  }
            } else {
                  LOGGER.info(" updateDeckMetadata() DB is not connected. Check network connection.");
            }
            return bool;
      }


      /**
       * Updates DeckMetaData with the Test Score.
       * Only updates testScore, last date, and increments
       * session_count to DB
       *
       * @param map Uses same string as insertDeckMetadata
       * @param id  ..
       * @return true if successful
       */
      private boolean updateTestMetaData(HashMap<String, String> map, long id) {
            AtomicBoolean bool = new AtomicBoolean(false);

            LOGGER.info("updateDeckMetadata sending, ID: {}" + id);
            if (connect != null && Utility.isConnected()) {

                  String updateDB = "UPDATE deckMetadata SET" +
                      " last_date = '" + map.get("last_date") + "'" +
                      ", session_score = '" + map.get("session_score") + "'" +
                      ", session_count = session_count+1 " +
                      " WHERE deck_id = " + id + ";";

                  LOGGER.debug("query: " + updateDB);

                  DBConnect db = DBConnect.getInstance();


                  ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
                  Runnable task = () -> {
                        CompletableFuture<QueryResult> future = db.getConnection()
                            .sendPreparedStatement(updateDB);
                        try {
                              future.get();
                        } catch (InterruptedException e) {
                              LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage()); //, e.getStackTrace());
                              e.printStackTrace();
                        } catch (ExecutionException e) {
                              LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage()); //, e.getStackTrace());
                              e.printStackTrace();
                        }
                        bool.set(true);
                        scheduledExecutor.shutdown();
                  };
                  scheduledExecutor.execute(task);

            } else {
                  LOGGER.info(" updateDeckMetadata() DB is not connected. Check network connection.");
            }
            return bool.get();
      }

      /**
       * returns the deck_id if successful. If not returns
       * -99l;
       *
       * @return ..
       */
      public long queryGetDeckID() {
            long id = -99;
            String idQuery = "SELECT deck_id FROM deckMetadata"
                + " WHERE user_email = " + "'" + Alphabet.encrypt(UserData.getUserName()) + "'"
                + " AND deck_name = " + "'" + FlashCardOps.getInstance().getDeckLabelName() + "'"
                + ";";


            LOGGER.info("Report query request: " + idQuery);

            DBConnect db = DBConnect.getInstance();

            try {
                  CompletableFuture<QueryResult> future = db.getConnection()
                      .sendPreparedStatement(idQuery);
                  QueryResult queryResult = future.get();
                  if (queryResult.getRows().size() == 0) {
                        //id = -99;
                  } else {
                        String num = Arrays.toString(((ArrayRowData) (queryResult.getRows().get(0))).getColumns());
                        num = num.replaceAll("\\D", "");
                        id = Long.valueOf(num);
                  }
                  //id = queryResult.getRows().get(0).
            } catch (NullPointerException e) {
                  LOGGER.warn("INFO: Null pointer exception at getDeckID. Deck may not exist. ");
            } catch (ExecutionException e) {
                  LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage());//, e.getStackTrace());
            } catch (InterruptedException e) {
                  LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage());//, e.getStackTrace());
            }

            return id;
      }
}
