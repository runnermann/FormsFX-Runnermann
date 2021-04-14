package authcrypt;


import campaign.db.DBFetchUnique;
import fileops.DirectoryMgr;
import fileops.Utility;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.io.File;
import java.io.Serializable;


public class Verify implements Serializable {

    private static final long serialVersionUID = FlashMonkeyMain.VERSION;
    private static final Logger LOGGER = LoggerFactory.getLogger(Verify.class);

    //private static String userName;
    private static int validInt;
    private static AuthUtility authUtility;

    public Verify() { /* no args constructor */ }

    public Verify(String pw, String userName) {
        UserAuthInfo userAuthInfo = new UserAuthInfo();

        LOGGER.info("Verify constructor( ... ) called");
        // set data from file if exists.
        // verify userPW and userName
        if(pw == null || pw.length() < 8 || userName == null || userName.length() < 6) {
            LOGGER.warn("verification failed pw or username is malformed");
        } else {
            // if the user exists on this system
            // then validate thier info
            if (userAuthInfo.setUserData()) {
                userAuthInfo.validateUserInfo(pw, userName);
            }
            // else check if they exist in the cloud
            // @TODO checkUserCloudExists()
   /*         else if (checkUserCloudExists()){
                // if they exist in the cloud, and the data is correct
                // create locally
                LOGGER.info("verification Failed");

                // if not have them reset their password by email

            } */
            // else notify them to create a new user on this
            // system and in the cloud.
            else {
                LOGGER.info("user does not exist locally or in the cloud");
            }
        }
    }

    // @TODO finish checkUserCloudExists
    private boolean checkUserCloudExists() {
        return false;
    }

    /**
     * @TODO create syncronization for users pw and userNames.
     * Stores the users data to file if the user is new to
     * this system. And if(isConnnected) the user's pw & name
     * are correct.
     * @param pw
     * @param name
     * @return returns the response if successful or not.
     */
    private File userDir;


    public String newUser(String pw, String name) {
        String defaultMsg = "Something isn't right.";
        // 1. Check that user does not exist locally
        // 2. Check if user is connected. For this version, user must be connected to initially create the account.
        // 3. Check s3getUser, if user exists, and password passes then
        //		a. deck list will be downloaded
        //		b. and user is validated in one single trip.
        // 4. If user does not pass remotely, then notify user to retry creating user, or rest remote password.
        // 5. If user does not exist remotely, !?!?!?!!!! And they are connected.
        // 		-- create user remotely???
        String errorMessage = "";
        // save user auth info
        LOGGER.info("Verify.newUser(...) ");
        if(DirectoryMgr.getWorkingDirectory() != null) {
            new DirectoryMgr();
        }
        // verify if user's directory already exists, if not
        // return to caller
        userDir = new File(DirectoryMgr.getMediaPath('z'));
        if(userDir.exists()) {
            return "Sorry that email exists. Try resetting the password or create a new user.";
        }

        if(Utility.isConnected()) {
            if(existsRemote(name)) {
                // create local user if local & remote password
                // are the same.
                int res = FlashCardOps.getInstance().setObjsFmS3(name, pw);
                switch (res) {
                    case 1: {
                        // successful
                        UserAuthInfo authInfo = new UserAuthInfo();
                        // sequence is important. Gen Salt is called
                        // and set in setPassword
                        authInfo.setSalt();
                        boolean bool1 = authInfo.setHashUserName(name);
                        boolean bool2 = authInfo.setPassword(pw);
                        // store to file
                        if (bool1 && bool2) {
                            return store(authInfo);
                        }
                        errorMessage = defaultMsg;
                        break;
                    }
                    case 0: {
                        UserData.clear();
                        errorMessage = "There is a problem with your email-password combination in the cloud.\n" +
                                " Retry your password or use \"reset password\" to reset the cloud password.";
                        break;
                    }
                    case -1: {
                        errorMessage = "Wow! That's unusual. The network may be down. " +
                                "\nWait a few minutes and try again.";
                        break;
                    }
                    default: {
                        errorMessage = defaultMsg;
                    }
                }
            }
            else {
                // User's name does not exist in the cloud
                // errorMessage user that they need to download
                // the app.
                UserData.clear();
                errorMessage = "Ooops!! We need to check if your real. \nPlease go to https://www.flashmonkey.xyz\n and download the app.\nThanks";
            }
        }
        else {
            // errorMessage user that they must be
            // connected to create a local account.
            UserData.clear();
            errorMessage = "The initial configuration requires that you are online when you first install on a new computer.";
        }

        return errorMessage;
    }


    private boolean existsRemote( String name) {
        LOGGER.info("App is connected");
        String[] args = {name};
        String[] columnData = DBFetchUnique.STUDENTS_UUID.query(args);
        if(columnData[0].equals("EMPTY")) {
            return false;
        }
        return true;
    }

    /**
     * If successful in creating new user, validInt
     * is set to correct number.
     * @return String
     */
    public int succeeded() {
        return validInt;
    }

    /**
     * Stores the users authInfo to file.
     * @param authInfo
     * @return private
     */
    private String store(UserAuthInfo authInfo) {
        return FlashCardOps.getInstance().getFO().setVerifyInFile(authInfo);
    }
    
    public String getUserHash() {
        UserAuthInfo info = new UserAuthInfo();
        info.setUserData();
        if(info.getHashUserName() == "") {
            LOGGER.warn("WARNING: UserHash == <{}>", info.getHashUserName());
        }
        return info.getHashUserName();
    }
    
    /** ************************************** ***
     *                 INNER CLASS
     ** ************************************** ***/
    private static class UserAuthInfo implements Serializable {

        private static final long serialVersionUID = FlashMonkeyMain.VERSION;

        private String s1; // HashUserName
        private String s2; // Password
        private String s3; // Salt
        private String s4; // hint
        private String s5; // retry code
        private long num1; // valid retry time

        private UserAuthInfo() {
            authUtility = new AuthUtility();
            s1 = "";
            s2 = "";
            s3 = "";
            s4 = "";
            s5 = "";
            num1 = 100;
        }

        /**
         * if the userAuthInfo is null, returns false,
         * else, sets the data to the info in other.
         * @param other
         * @return if the userAuthInfo is null, returns false,
         *          else, sets this fields to the data in other.
         */
        private boolean set(UserAuthInfo other) {
            // if a directory did not exist, this will be null
            if(other == null) {
                //LOGGER.info("in set(...), other is null");
                return false;
            } else {
                //LOGGER.info("set other.hashUserName is: {} submitted hashUserName: {}" + other.hashUserName);
                s1 = other.s1;
                //LOGGER.info("in set and other.pwHash: {} submitted pwHash: {}", other.password);
                this.s2 = other.s2;
                this.s3 = other.s3;
                this.s4 = other.s4;
                this.s5 = "";
                this.num1 = 0L;
                return true;
            }
        }


        private void validateUserInfo(String pw, String name) {
            
            // Check if files exist,
            if(ifNotExists(s1, s2, s3)) {
                LOGGER.debug("validateUserInfo FAILED: data does is null");
                validInt = 0;
                return;
            }
            
            if(pw.length() < 8 || name.length() < 6) {
                LOGGER.debug("validateUserInfo FAILED: pw.length or name.length too short");
                validInt = 0;
                return;
            }
            // s3 = salt, s2 = password
            if(this.s3.length() < 8 || this.s2.length() < 8) {
                LOGGER.debug("validateUserInfo FAILED: salt.length or password.length too short");
                LOGGER.debug("this.salt: {}", this.s3);
                LOGGER.debug("this.password: {}", this.s2);
                validInt = 0;
                return;
            }
            // s1 = userHashName
            if(this.s1.length() < 8 ) {
                LOGGER.debug("validateUserInfo FAILED: hashUserName.length < 8");
            }

            AuthUtility ut = new AuthUtility();
            String createdPWHash = ut.computeHash(pw, this.s3, "PBKDF2WithHmacSHA512");
            String createdUName =  ut.computeHash(name, this.s3, "PBKDF2WithHmacSHA512");


            if(createdPWHash.equals(this.s2) && createdUName.equals(this.s1)) {
                //userName = name;
                LOGGER.debug("validateUserInfo succeeded returning correct int");
                validInt = 8675309;
            } else {
                validInt = 0;
                clear();
                LOGGER.debug("validateUserInfo({} {}) failed ", pw, name);
            }
        }
    
        /**
         * Helper method to validateUserInfo. If there is a failure, and
         * information is null, return false.
         * @param s2
         * @param s3
         * @param s1
         * @return
         */
        private boolean ifNotExists(String s2, String s3, String s1) {
            if(s2 == null || s2.isEmpty()
                    || s3 == null || s3.isEmpty()
                    || s1 == null || s1.isEmpty()) {
                return true;
            }
            return false;
        }

        /**
         * Sets this object from file.
         */
        private void setSalt() {
            this.s3 = authUtility.generateSalt();
        }


        private boolean setUserData() {
            // get object from file
            LOGGER.debug("called authCrypt.Verify$UserAuthInfo.setUserData()");
            return set( (UserAuthInfo) FlashCardOps.getInstance().getFO().getVerifyFmFile());
        }
        
        
        private boolean setPassword(String password) {
            if(this.s3 != "") {
                this.s2 = authUtility.computeHash(password, this.s3, "PBKDF2WithHmacSHA512");
                if(this.s2.length() < 10) {
                    return false;
                }
                return true;
            }
            return false;
        }

        private String getHashUserName() {
            return s1;
        }

        /**
         * Sets the users hashName stored for validation.
         * <b>NOT</b> used for local storage: See fileops.DirectoryMgr
         * @param userName
         * @return
         */
        private boolean setHashUserName(String userName) {
            if(this.s3 != null && this.s3 != "") {
                this.s1 = authUtility.computeHash(userName, this.s3, "PBKDF2WithHmacSHA512");
                return true;
            }
            
            return false;
        }



        private String getS4() {
            return s4;
        }

        private void setS4(String s4) {
            this.s4 = s4;
        }

        private String getS5() {
            return s5;
        }

        private void setS5(String s5) {
            this.s5 = s5;
        }

        private long getRetryValidtime() {
            return num1;
        }

        private void setNum1(long num1) {
            this.num1 = num1;
        }
        
        private void clear() {
            s1 = "";
            s2 = "";
            s3 = "";
            s4 = "";
            s5 = "";
            num1 = 100;
        }
    }


    // Testing
/*
    static final UserAuthInfo authInfo = new UserAuthInfo();

    public static void main(String ... args) {
        // Set password and user
        authInfo.setPassword("AGoodPassword#01");
        authInfo.setHashUserName("GoodUser");

        // print name and password strings
        System.out.println("hashed UserName: " + authInfo.hashUserName);
        System.out.println("hashed Password: " + authInfo.password);

        String msg = "validateUserInfo: returned correctInfo: ";
        // Test good password and good user
        int num = authInfo.validateUserInfo("AGoodPassword#01", "GoodUser");
        System.out.println(msg + (num == 8675309));
        // Bad password good user
        num = authInfo.validateUserInfo("aBadPassword#01", "GoodUser");
        System.out.println(msg + (num == 8675309));
        // Good password bad user
        num = authInfo.validateUserInfo("AGoodPassword#01", "BadUser");
        System.out.println(msg + (num == 8675309));
    }
 */
}
