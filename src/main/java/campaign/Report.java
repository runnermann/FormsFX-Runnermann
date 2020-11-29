package campaign;

import authcrypt.UserData;
import authcrypt.Verify;
import campaign.db.DBConnect;
import ch.qos.logback.classic.Level;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import fileops.Utility;
import flashmonkey.EndGame;
import flashmonkey.ReadFlash;
import flashmonkey.Timer;
import forms.utility.Alphabet;
import metadata.DeckMetaData;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * NOTE: Information about users, ie names and email address are stored in an encrypted state.
 * When users information is compared from the DB, it is expected to be encrypted. Therefore
 * always ensure, that users information is encrypted when sent to the DB, and decrypted when
 * used by the current session.
 */
public final class Report {

    // private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Report.class);
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Report.class);
    
    
    //@TODO Create authcrypt.user hash and replace in Report.
    private static Connection connect;
    private Timer fmTimer;
    private static Report CLASS_INSTANCE;

    private Report() {
        init();
    }

    public static synchronized Report getInstance() {
        LOGGER.info("getInstance called");
    // @TODO remove setLevel
        LOGGER.setLevel(Level.DEBUG);
        
        if(CLASS_INSTANCE == null) {
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
            System.out.println("@init() attempting connection");
            connect = db.getConnection();
            fmTimer = Timer.getClassInstance();
        }
        catch (Exception e) {
            System.out.println("EXCEPTION: @init() did not connect to DB");
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
        LOGGER.info("sessionStart called at: {}" + fmTimer.getBeginTime());
    }

    /**
     * Call sessionUseTime() at the end of a session. Reports to
     * the table object set in setTable();
     */
    public void endSessionTime() {
        if(connect != null && fileops.Utility.isConnected()) {
            LOGGER.info("called endSessionTime()");

            DBConnect db = DBConnect.getInstance();
            fmTimer.end();
            Long useTime = fmTimer.getTotalTime();
    
            Verify v = new Verify();
            
            try {
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
                                + v.getUserHash() + "', '"
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
     * Sets metadata to the database. Returns true if successful
     * false otherwise.
     * @param metaData
     * @return true if successful, false otherwise.
     */
    public boolean reportDeckMetadata(DeckMetaData metaData) {
        //query DB for username and deckname, get ID, and do update
        long id = queryGetDeckID();
    
        LOGGER.debug("id: " + id);
        
        if(id == -99) {
            // item does not exist, do insert
            LOGGER.debug("result was -99. inserting new row");
            return insertDeckMetadata(metaData);
        } else {
            // item exists, do update
            LOGGER.debug("result was " + id + ". updating!");
            return updateDeckMetadata(metaData, id);
        }
    }

    /**
     * Updates test metaData, uses same array as
     * reportDeckMetadata
     * @param metaDataAry
     */
    public void reportTestMetaData(HashMap<String, String> metaDataAry) {
        //query DB for username and deckname, get ID, and do update
        long id = queryGetDeckID();
        LOGGER.debug("id: " + id);
        updateTestMetaData(metaDataAry, id);
    }
    
    
    /**
     * The initial insert of this decks metadata into the DB
     * @param metaObj
     * @return
     */
    private boolean insertDeckMetadata(DeckMetaData metaObj) {
        if (connect != null && Utility.isConnected()) {
            //UserData user = new UserData();
            
            LOGGER.info("inserting data into deckMetadata.");
            System.out.println("UserName is: " + authcrypt.UserData.getUserName());

            StringBuffer sbuffer = new StringBuffer();
            String insertQuery = "INSERT INTO deckMetadata(" +
                    "last_date, " +     // 0 lastdate
        //            "create_date, " +
                    "deck_descript, " + // 1 descript
                    "user_email, " +    // 2 userName
                    "creator_email, " + // 3 origAuthor
                    "deck_school, " +   // 4 school
                    "deck_book, " +     // 5 book
                    "deck_class, " +    // 6 class
                    "deck_prof, " +     // 7 prof
                    "subj, " +          // 8 ie physics
                    "section, " +       // 9 ie mechanics // previously cat
                    //"sub_section, " +   // previously Cat
                    "deck_language, " + // 10 lang
                    "deck_name, " +     // 11 deckName
                    "test_types, " +    // 12 testtypes
                    "num_cards, " +     // 13 numCards
                    "num_imgs, " +      // 14 numImg
                    "num_video, " +     // 15 numVid
                    "num_audio, " +     // 16 numAud
                    "course_code, " +     // 17 courseCode
		     //       "session_score, " +
                    "session_count) VALUES ('";
            sbuffer.append(insertQuery);
            // subtract the last element from the array. We are
            // not reporting the session score here.
            sbuffer.append(metaObj.getLastDate() + "', '");
            sbuffer.append(metaObj.getDescript() + "', '");
            sbuffer.append(Alphabet.encrypt(authcrypt.UserData.getUserName()) + "', '");
            sbuffer.append(Alphabet.encrypt(authcrypt.UserData.getUserName())  + "', '");
            sbuffer.append(metaObj.getDeckSchool() + "', '");
            sbuffer.append(metaObj.getDeckBook() + "', '");
            sbuffer.append(metaObj.getDeckClass() + "', '");
            sbuffer.append(metaObj.getDeckProf() + "', '");
            sbuffer.append(metaObj.getSubj() + "', '");
            sbuffer.append(metaObj.getCat() + "', '");
            sbuffer.append(metaObj.getLang() + "', '");
            sbuffer.append(ReadFlash.getInstance().getDeckName() + "', '");
            sbuffer.append(metaObj.getTestTypes() + "', '");
            sbuffer.append(metaObj.getNumCard() + "', '");
            sbuffer.append(metaObj.getNumImg() + "', '");
            sbuffer.append(metaObj.getNumVideo() + "', '");
            sbuffer.append(metaObj.getNumAudio() + "', '");
            sbuffer.append(metaObj.getCourseCode() + "', '");
            //sbuffer.append(0 + "' ");
            //sbuffer.append(0 + "' ");
            
            // add one to session_count
            sbuffer.append( 1 + "')");
            
            DBConnect db = DBConnect.getInstance();

            LOGGER.debug(sbuffer.toString());

            try {
                CompletableFuture<QueryResult> future = db.getConnection()
                        .sendPreparedStatement(sbuffer.toString());
                future.get();
            } catch (ExecutionException e) {
                LOGGER.warn("WARNING: DBConnection ERROR: {}", e.getMessage() ); //+ " " + e.getStackTrace());
                e.printStackTrace();
                return false;
            } catch (InterruptedException e) {
                LOGGER.warn("WARNING: DBConnection ERROR: {}", e.getMessage() ); //, e.getStackTrace());
                e.printStackTrace();
                return false;
            }
        } else {
            LOGGER.info(" reportDeckMetaData() DB is not connected. Check network connection.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Most frequently, MetaData will come from an update.
     * Sends data from the MetaDataAry to the MetaData Table
     * recieves data from updateDataAry in DeckMetaData
     * @param metaObj
     * @param id
     * @return
     */
    private boolean updateDeckMetadata(DeckMetaData metaObj, long id) {
        boolean bool = false;
    
        LOGGER.info("updateDeckMetadata sending, ID: {}" + id);
        System.out.println("updateing DeckMetaDataDB in Report");
    
        if (connect != null) {
            int i = 0;
            String updateDB = "UPDATE deckMetadata SET" +
                    " last_date ='" +      metaObj.getLastDate() + "'" + // lastDate
                    ", deck_descript ='" + metaObj.getDescript() + "'" + // deckDescript
                    ", course_code ='"     + metaObj.getCourseCode() + "'" +
                    ", deck_school ='" +   metaObj.getDeckSchool() + "'" +
                    ", deck_book ='" +     metaObj.getDeckBook() + "'" +
                    ", deck_class ='" +    metaObj.getDeckClass() + "'" +
                    ", deck_prof ='" +     metaObj.getDeckProf() + "'" +
                    ", subj ='" +          metaObj.getSubj() + "'" +
                    ", section ='" +       metaObj.getCat() + "'" +     // formerly cat
                    ", deck_language ='" + metaObj.getLang() + "'" +
                    ", test_types ='" +    metaObj.getTestTypes() + "'" +
                    ", num_cards ='" +     metaObj.getNumCard() + "'" +
                    ", num_imgs ='" +      metaObj.getNumImg() + "'" +
                    ", num_video ='" +     metaObj.getNumVideo() + "'" +
                    ", num_audio ='" +     metaObj.getNumAudio() + "'" +
                    ", session_score ='" + metaObj.getScores().toString() + "'" +
                    ", session_count = session_count+1 " +
                    " WHERE deck_id =" + id + ";";
    
            LOGGER.debug("query: " + updateDB);
    
            DBConnect db = DBConnect.getInstance();
    
            try {
                CompletableFuture<QueryResult> future = db.getConnection()
                        .sendPreparedStatement(updateDB);
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
     * @param map Uses same string as insertDeckMetadata
     * @param id
     * @return
     */
    private boolean updateTestMetaData(HashMap<String, String> map, long id) {
        boolean bool = false;

        LOGGER.info("updateDeckMetadata sending, ID: {}" + id);
        if (connect != null && fileops.Utility.isConnected()) {

            String updateDB = "UPDATE deckMetadata SET" +
                    " last_date = '" + map.get("last_date") + "'" +
                    ", session_score = '" + map.get("session_score") + "'" +
                    ", session_count = session_count+1 " +
                    " WHERE deck_id = " + id + ";";

            LOGGER.debug("query: " + updateDB);

            DBConnect db = DBConnect.getInstance();

            try {
                CompletableFuture<QueryResult> future = db.getConnection()
                        .sendPreparedStatement(updateDB);
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
     * returns the deck_id if successful. If not returns
     * -99l;
     * @return
     */
    private long queryGetDeckID() {
        long id = -99;
        UserData user = new UserData();
        String idQuery = "SELECT deck_id FROM deckMetadata"
                + " WHERE user_email = " + "'" + Alphabet.encrypt(user.getUserName()) + "'"
                + " AND deck_name = " + "'" + ReadFlash.getInstance().getDeckName() + "'"
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
    
    
    /**
     * Queries the MetaData DB for this decks metaData if it exists
     * @return A string containing the returned query.
     */
    /*
    public String queryGetDeckMetaData(String userName, String deckName) {
        
        String strQuery = "SELECT * FROM deckMetadata"
                            + " WHERE user_email = " + "'" + userName + "'"
                            + " AND deck_name = " + "'" + deckName + "'"
                        + ";";
        
        return query(strQuery);
    }
     */
    
    
    /**
     * Queries the table provided in the param for the users encrypted data if it exists.
     * Use constants for PROFFESSOR or STUDENT tables
     * @param studentHash: The hashed original username, usually their original email address.
     * @return A string containing the encrypted query.
     */
    /*public String queryGetUserEcryptedData(String studentHash, String table) {
        String strQuery = "SELECT * FROM " + table +
                " WHERE student_hash = " + studentHash +
                ";";
        
        return query(strQuery);
    }
     */
    
    
    /**
     * Sends a query statement to the DB and returns the response.
     * @param strQuery
     * @return The response if successful or an empty string if not.
     */
    /*
    private String query(String strQuery) {
    
        DBConnect db = DBConnect.getInstance();
        try {
            CompletableFuture<QueryResult> future = db.getConnection()
                    .sendPreparedStatement(strQuery);
            QueryResult queryResult = future.get();
            if (queryResult.getRows().size() == 0) {
                LOGGER.debug("query result has 0 rows");
                return "";
            } else {
                LOGGER.debug("query result has rows of data");
                return Arrays.toString(((ArrayRowData) (queryResult.getRows().get(0))).getColumns());
                //num = num.replaceAll("\\D", "");
                //id = Long.valueOf(num);
            }
        } catch (NullPointerException e) {
            LOGGER.warn("WARNING: Null pointer exception at getDeckMetaData. Deck may not exist. ");
        } catch (ExecutionException e) {
            LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
        } catch (InterruptedException e) {
            LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
        }
        LOGGER.debug("returning an empty string");
        return "";
    }
     */
    
    
    
    /*
    public static void main(String[] args) throws ExecutionException, InterruptedException {
    
        
        
        String[] str = new String[18];
        str[0] = Long.toString(System.currentTimeMillis());//last_date: system.now()
        str[1] = Long.toString(System.currentTimeMillis());//create_date system.file().getDate()
        str[2] = "Essential database knowledge for entering, manipulating and maintaining data in databases, Course " +
                "is primarily the Stanford First Course in Databases";
        str[3] = "lowell.stadelman@gmail.com";//user_email EncryptedUser.EncryptedUser.getEmail()
        str[4] = "lowell.stadelman@gmail.com";//null;//creator_email if(oringalAuthor) EncryptedUser.EncryptedUser.getEmail()
        str[5] = "College of San Mateo"; //authcrypt.user.getSchool();//deck_school
        str[6] = "Database Systems, The Complete Book, Second Edition"; //deck_book
        str[7] = "CIS-132-OLH-Introduction to Databases"; //deck_class
        str[8] = "Mounjed Moussalem";//deck_prof
        str[9] = "Comp Sci";//subj_cat
        str[10] = "english"; //null;//deck_language EncryptedUser.EncryptedUser.getLang()
        str[11] = "Small cats pur";//deck_name getDeckName()
        str[12] = "3226";//test_types account for testTypes
        str[13] = "19";//num_cards flashList.size()
        str[14] = "21";//num_imgs count number of images in filesystem
        str[15] = "1";//num_video count number of videos in filesystem
        str[16] = "2";//num_audio count number of audio files in filesystem
        str[17] = "97.3";
        //str[17] = "1";//session_count increment this number
        
        String[] metaDataAry = str;
    

        LOGGER.debug("Sending to DB: " );
        for(String s : str) {
            System.out.print(s + ", ");
        }
    
        getInstance().reportDeckMetadata(metaDataAry);
    }
    
     */
}
