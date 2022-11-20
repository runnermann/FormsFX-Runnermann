package authcrypt;


import campaign.db.DBFetchUnique;
import campaign.db.errors.ModelError;
import ch.qos.logback.classic.Level;
import fileops.DirectoryMgr;
import fileops.utility.Utility;
import flashmonkey.FlashCardOps;
import forms.utility.Alphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * This class should only be accessed by Auth
 * -- Removed Serializable --
 * Security sensitive classes should not be serializable. See JavaDocs Security vulnerabilities at:
 * https://www.oracle.com/java/technologies/javase/seccodeguide.html
 */
public class Verify {

      private static final Logger LOGGER = LoggerFactory.getLogger(Verify.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Verify.class);


      //private static String userName;
      private static int validInt;
      private static AuthUtility authUtility;
      private int s3res;
      //private boolean s3IsSet;

      private Verify() {
            /* no args constructor */
      }

      /**
       * Sets the validInt.
       *
       * @param x1  pw
       * @param x2  email aka userName
       * @param loc for remote use 'b' (both). For local only use 'l'.
       *            default will check both if connected. The lower process
       *            will not fail if the user does not exist.
       */
      protected Verify(String x1, String x2, char loc) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.info("Verify constructor( ... ) called");

            loc = loc == 0 ? 'b' : loc;

            int remoteState = 0;
            // verify if the user exists locally, then set the bits
            int localState = validateLocal(x2, x1);
            // if localState == 0 pw or name is malformed
            LOGGER.debug("localState: {}", localState);
            if (localState == 0) {
                  return;
            }

            // if connected, set user state for remote
            if (loc != 'l' && Utility.isConnected()) {
                  remoteState = validateRemote(x2, x1);
            }
            validInt = remoteState + localState;
            LOGGER.debug("validInt: " + validInt);
      }



      /**
       * Assumes that the user is connected to the internet. Must check
       * prior to the use of this method.
       *
       * @param x1 email
       * @param x2 pw
       * @return returns 128 if the PW and email were correct. Returns 16 if the email
       * does not exist, returns 1 if the PW is wrong , returns 0 if pw or email is
       * too short.
       */
      private int validateRemote(String x1, String x2) {
            if (x1.length() > 5 && x2.length() > 7) {
                  if (existsRemote(x1)) {
                        /*
                        * return either 1 or 128
                        * - Fade out log in here.
                        */
                        s3res = FlashCardOps.getInstance().setObjsFmS3(x1, x2);
                        switch (s3res) {
                              case 1: {
                                    // correct
                                    return 128;
                              }
                              default:
                              case 0: {
                                    // wrong password
                                    return 1;
                              }
                        }
                  } else {
                        // does not exist
                        return 16;
                  }
            } else {
                  // bad pw, log in again
                  return 1;
            }
      }

      /**
       * @param email email
       * @param x2 pw
       * @return 64 if the email {@code &} pw are correct. 2 if incorrect but exists,
       * 4 if the email does not exist locally, and 0 if there is a problem
       * with the pw or email entry.
       */
      private int validateLocal(String email, String x2) {
            // save user auth info
            //LOGGER.info("validateLocal(...) ");
            if (DirectoryMgr.getWorkingDirectory() != null) {
                  new DirectoryMgr();
            }
            File userFile = new File(DirectoryMgr.getMediaPath('z') + "verifyData.met");
            if (userFile.exists()) {
                  UserAuthInfo userAuthInfo = new UserAuthInfo();
                  userAuthInfo.setUserData();
                  // if the user exists on this system
                  // then validatorActionSwitch their info.
                  // Sets the response to validInt.
                  userAuthInfo.validateUserInfo(x2, email);
                  // returns, see chart in notes
                  return userAuthInfo.result();
            } else {
                  // does not exist locally
                  //LOGGER.debug("returning 4. User does not exist locally");
                  return 4;
            }
      }

      private File userDir;

      /**
       * Stores the users data to file if the user is new to
       * this system. And <pre>if(isConnnected)</pre> the user's pw  {@code & } name
       * are correct. Will not overwrite existing user.
       *
       * @param x1 pw
       * @param x2 name
       * @return returns the response if successful or not.
       */
      public String newUser(String x1, String x2) {
            String defaultMsg = "Something isn't right.";
            // 1. Check that user does not exist locally
            // 2. Check if user is connected.
            // 3. Check s3getUser, if user exists, and password passes then
            //		a. deck list will be downloaded
            //		b. and user is validated in one single trip.
            // 4. If user does not pass remotely, then notify user to retry creating user, or reset remote password.
            // 5. If user does not exist remotely, !?!?!?!!!! And they are connected.
            // 		-- create user remotely
            String errorMessage = "";
            // save user auth info
            if (DirectoryMgr.getWorkingDirectory() != null) {
                  new DirectoryMgr();
            }
            UserAuthInfo authInfo = new UserAuthInfo();
            // sequence is important. Gen Salt is called
            // and set in setPassword
            authInfo.setSalt();
            boolean bool1 = authInfo.setHashUserName(x2);
            boolean bool2 = authInfo.setPassword(x1);
            // store to file
            if (bool1 && bool2) {
                  // store hash
                  String s = store(authInfo.getArry());
                  // if successful store PW for auto login.
                  if(s.toLowerCase().startsWith("s")) {
                        // Encrypt and save PW to file.
                        ModelError.getInstance().outputMErrors(x1, x2);
                  }
                  return s;
            }
            errorMessage = "Failed";

            return errorMessage;
      }

      protected String resetUserInfo(String pw, String name) {
            //LOGGER.debug("called resetUserInfo for reseting the user. User exists remotely, and pw " +
            //        "is different from local. PW is correct for remote. Overwrites local");
            UserAuthInfo authInfo = new UserAuthInfo();
            // sequence is important. Gen Salt is called
            // and set in setPassword
            authInfo.setSalt();
            boolean bool1 = authInfo.setHashUserName(name);
            boolean bool2 = authInfo.setPassword(pw);
            // store to file
            if (bool1 && bool2) {
                  return reset(authInfo.getArry());
            }
            return "Something isn't right.";
      }

      private boolean existsRemote(String name) {
            //LOGGER.info("existsRemote called");
            String[] args = {name};
            String[] columnData = DBFetchUnique.STUDENTS_UUID.query(args);
            // as of 01-20-2022 If the user has just been created, pw will be
            // set to AAAAA in step 1. It is possible that they
            // will exist in a state that they cannot create a new password.
            // Thus we request pw and check for default of AAAAA.
            return !columnData[0].equals("EMPTY") && !columnData[1].equals("AAAAA");
      }

      /**
       * Returns the validInt
       *
       * @return String
       */
      public int succeeded() {
            return validInt;
      }

      /**
       * Stores the users Hashed authInfo to file.
       * Stores the users encrypted PW to file
       *
       * @param authInfo array of auth info
       * @return If the user does not exist, returns "success" else
       * returns "fail".
       */
      private String store(String[] authInfo) {
            //LOGGER.debug("Storing authInfo");
            return FlashCardOps.getInstance().setVerifyInFile(authInfo);
      }

      /**
       * Resets the users authInfo
       *
       * @param authInfo ...
       * @return private
       */
      private String reset(String[] authInfo) {
            return FlashCardOps.getInstance().resetVerifyInFile(authInfo);
      }

      /**
       * Not used for local storage. See DirectoryMgr
       * for local storage.
       *
       * @return Returns the MD5Hash.
       */
//      public String getUserMD5Hash() {
//            UserAuthInfo info = new UserAuthInfo();
//            info.setUserData();
//            if (info.getHashUserName() == "") {
//                  LOGGER.warn("WARNING: UserHash == <{}>", info.getHashUserName());
//            }
//            return info.getHashUserName();
//      }


      /**
       * ************************************* ***
       * INNER CLASS
       * NOTE: Security sentitive classes should not use
       * serializable. removed 08-10-2021
       * * **************************************
       ***/
      private static class UserAuthInfo {


            private String s1; // HashUserName
            private String s2; // Password
            private String s3; // Salt
            private String s4; // hint
            private String s5; // retry code

            private int retNum = 0;

            private UserAuthInfo() {
                  authUtility = new AuthUtility();
                  s1 = "";
                  s2 = "";
                  s3 = "";
                  s4 = "";
                  s5 = "";
            }

            private int result() {
                  return retNum;
            }

            /**
             * if the userAuthInfo is null, returns false,
             * else, sets the data to the info in other.
             *
             * @param userAuthInfoStr ...
             * @return if the userAuthInfo is null, returns false,
             * else, sets this fields to the data in other.
             */
            private boolean set(String[] userAuthInfoStr) {
                  // if a directory did not exist, this will be null
                  if (userAuthInfoStr == null || userAuthInfoStr[0].length() == 0) {
                        return false;
                  } else {
                        this.s1 = userAuthInfoStr[0];
                        this.s2 = userAuthInfoStr[1];
                        this.s3 = userAuthInfoStr[2];
                        this.s4 = "";
                        this.s5 = "";
                        return true;
                  }
            }


            private void validateUserInfo(String pw, String name) {
                  // Check if files exist,
                  if (!checkExists(pw, name)) {
                        return;
                  }
                  AuthUtility ut = new AuthUtility();
                  String createdPWHash = ut.computeHash(pw, this.s3, "PBKDF2WithHmacSHA512");
                  String createdUName = ut.computeHash(name, this.s3, "PBKDF2WithHmacSHA512");

                  if (createdPWHash.equals(this.s2) && createdUName.equals(this.s1)) {
                        retNum = 64;
                  } else {
                        retNum = 2;
                        clear();
                  }
            }

            private boolean checkExists(String pw, String name) {
                  if (ifNotExists(s1, s2, s3)) {
                        //LOGGER.debug("validateUserInfo FAILED: data is null");
                        retNum = 0;
                        return false;
                  }
                  if (isMalformed(6, name)) {
                        //LOGGER.debug("validateUserInfo FAILED: pw.length or name.length too short");
                        retNum = 0;
                        return false;
                  }
                  String[] str = {pw, s1, s2, s3};
                  if (isMalformed(8, str)) {
                        //LOGGER.debug("validateUserInfo FAILED: hashUserName.length < 8: {} ", this.s1);
                        //LOGGER.debug("validateUserInfo FAILED: salt.length or password.length too short");
                        //LOGGER.debug("this.salt: {}", this.s3);
                        //LOGGER.debug("this.password: {}", this.s2);
                        retNum = 0;
                        return false;
                  }
                  return true;
            }


            private boolean isMalformed(int length, String... str) {
                  for (String s : str) {
                        if (s.length() < length) {
                              return true;
                        }
                  }
                  return false;
            }

            /**
             * Helper method to validateUserInfo. If there is a failure, and
             * information is null, return false.
             *
             * @param s2 any string
             * @param s3 any string
             * @param s1 any string
             * @return true if does not exist
             */
            private boolean ifNotExists(String s2, String s3, String s1) {
                  return s2 == null || s2.isEmpty()
                      || s3 == null || s3.isEmpty()
                      || s1 == null || s1.isEmpty();
            }

            /**
             * Sets this object from file.
             */
            private void setSalt() {
                  this.s3 = authUtility.generateSalt();
            }


            private boolean setUserData() {
                  // get object from file
                  //LOGGER.debug("called authCrypt.Verify$UserAuthInfo.setUserData()");
                  return set(FlashCardOps.getInstance().getVerifyFmFile());
            }


            private boolean setPassword(String password) {
                  if (this.s3 != "") {
                        this.s2 = authUtility.computeHash(password, this.s3, "PBKDF2WithHmacSHA512");
                        return this.s2.length() >= 10;
                  }
                  return false;
            }

            /**
             * Sets the users hashName stored for validation.
             * <b>NOT</b> used for local storage: See fileops.DirectoryMgr
             *
             * @param userName ...
             * @return true if successful
             */
            private boolean setHashUserName(String userName) {
                  if (this.s3 != null && this.s3 != "") {
                        this.s1 = authUtility.computeHash(userName, this.s3, "PBKDF2WithHmacSHA512");
                        return true;
                  }
                  return false;
            }

            private String[] getArry() {
                  String[] ary = new String[3];
                  ary[0] = this.s1;
                  ary[1] = this.s2;
                  ary[2] = this.s3;
                  //ary[3] = this.s4;
                  return ary;
            }

//            private String getHashUserName() {
//                  return s1;
//            }

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

            private void clear() {
                  s1 = "";
                  s2 = "";
                  s3 = "";
                  s4 = "";
                  s5 = "";
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
