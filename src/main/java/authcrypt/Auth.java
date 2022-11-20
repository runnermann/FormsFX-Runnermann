package authcrypt;

import fileops.CloudOps;
import fileops.DirectoryMgr;
import fileops.S3ListObjs;
import flashmonkey.FlashMonkeyMain;
import javafx.application.Platform;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FMAlerts;
import uicontrols.FxNotify;
import uicontrols.UIColors;

import java.net.http.HttpConnectTimeoutException;

/**
 * This class provides the actions neccessary to coordinate
 * between remote and local authorizations.
 */
public class Auth {

      //private static final Logger LOGGER = LoggerFactory.getLogger(Auth.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Auth.class);

      // Authorized locally only = 8675309
      // Authorized in local and remote = 8675311
      private int state = 0;
      // must be set before resetLocal can be used
      // outside of this class
      private String resetLocal = "";
      private R r;
      private Verify v;
      private boolean s3HasSet;


      private Auth() {
            // no args
      }

      /**
       * Call when resetting PW.
       * @param pw
       * @param email
       * @param notUsed
       */
      public Auth(String pw, String email, int notUsed) {
            // Used when resetting the password.
            r = new R(email, pw);
      }

      /*
      * When used by SignUp Model, we must check if the user
      * exists at remote before creating the user locally.
       */
      public Auth(String pw, String email) {
            // check both local and remote
            r = new R(email, pw);
            v = new Verify(pw, email, 'b');
      }

      public Auth(String pw, String email, char location) {
            // check at the location
            r = new R(email, pw);
            v = new Verify(pw, email, location);
            this.state = actionSwitchLocal(v);
      }

      // Verify if user is authorized for (local only) remote, or not authorized.
      public final int authState() {
            int s = state;
            return s;
      }

      private R getR() {
            return r;
      }

      // Sets the lock variable. If not
      // set, resetLocalPW is not possible.
      public void setResetLocal(String s) {
            this.resetLocal = s;
      }

      /**
       * Show sign-up pane.
       */
      protected void showCreateUserLocal() {
            FlashMonkeyMain.showSignUpPane();
      }

      /**
       * Create LOCAL user programatically
       * User logged-in and is correct for remote. User does
       * not exist locally.
       * if user does not exist at local,
       * and user correctly logged in at remote,
       * then set user PW and UserName
       * locally.
       * @param x1 pw
       * @param x2 Name
       */
      private void createUserLocalConnected(String x1, String x2) {
            // Download the decks from S3. also sends a token
            S3ListObjs s3ListObjs = new S3ListObjs();
            try {
                  int res = s3ListObjs.listDecks(x2, x1);
                  if (res == 1) {
                        // set userData to the entered data
                        saveAction(x1, x2);
                  }
            } catch (HttpConnectTimeoutException e) {
 //                 LOGGER.warn(e.getMessage());
            }
            // else
            // it's an error. Do nothing.
      }

      /**
       * No checks are made at this level. Some checks are conducted at the lower levels.
       * Saves the user to file locally but is not synchronized with remote. Use when not
       * connected for the first time and to save a user.
       *
       * @param x1 pw
       * @param x2 name
       */
      private void createUserLocalNot(String x1, String x2) {
            saveAction(x1, x2);
      }

      /**
       * Create REMOTE user step 1
       *
       * @param email ..
       */
      protected void createUserRemote(String email) {
            // Inform the user that they need to create
            // an account online. And make it optional.
            // They are directed to the sign-in pane if they cancel.
            FMAlerts alerts = new FMAlerts();
            boolean b = alerts.yesOrRedirectToSignInPopup(" ALERT ", FMAlerts.CREATE_ONLINE, "image/logo/vertical_logo_blue_480.png",
                UIColors.ICON_ELEC_BLUE);
            if (b) {
                  // Send request to remote server
                  //CloudOps co = new CloudOps();
                  int res = CloudOps.requestCreateUserCode(email);
                  switch (res) {
                        case -1: {
                              String msg = "That didn't work.... Try resetting your password.";
                              FxNotify.notification("", " Hmmmm! " + msg, Pos.CENTER, 8,
                                  "image/Flash_hmm_75.png", FlashMonkeyMain.getPrimaryWindow());
                              FlashMonkeyMain.showResetOnePane();
                              break;
                        }
                        case 0: {
                              String msg = "I cannot connect with the server. Please check the connection to the internet";
                              FxNotify.notification("", " Oooph! " + msg, Pos.CENTER, 8,
                                  "image/flashFaces_smirking_75.png", FlashMonkeyMain.getPrimaryWindow());
                              break;
                        }
                        case 1: {
                              // Succeeded, email has been sent
                              FlashMonkeyMain.showConfirmPane();
                              break;
                        }
                  }
            }
      }

      /**
       * Create remote user step 2
       *
       * @param field0 code
       * @param field1 password
       */
      public static void finalizeUserRemote(String field0, String field1) {
            //LOGGER.debug("finalizeUserRemote called");
            //CloudOps co = new CloudOps();
            int res = CloudOps.requestFinalizeUserRemote(field0, UserData.getUserName(), UserData.getFirstName(), field1);
            switch (res) {
                  case -1: {
                        String msg = "That didn't work.";
                        FxNotify.notification("", " Hmmmm! " + msg, Pos.CENTER, 4,
                            "image/Flash_hmm_75.png", FlashMonkeyMain.getPrimaryWindow());
                        break;
                  }
                  case 0: {
                        String msg = " Please check your connection to the internet";
                        FxNotify.notification("", " Hmmmm! " + msg, Pos.CENTER, 4,
                            "image/flashFaces_smirking_75.png", FlashMonkeyMain.getPrimaryWindow());

                        break;
                  }
                  case 1: {
                        String msg = " Congratulations! \n Welcome to FlashMonkey. The learning platform where" +
                            " you get more than a grade. \n1. You can Learn Smarter\n" +
                            "2. Earn Cash\n3. Organize Everything\n4. Find Faster\n5. and Create on the Fly";
                        FxNotify.notification("", " Awesomeness! " + msg, Pos.CENTER, 4,
                            "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getPrimaryWindow());
                        // set flashmonkey main to filesPane()
                        FlashMonkeyMain.getFileSelectPane();
                        break;
                  }
            }

      }

      /**
       * Request for password reset code. If the response == true,
       * then we switch the pane in the main window to
       * the new reset02Pane.
       *
       * @param email ..
       * @return true if a 200 is received from vertx
       */
      protected int callRemoteReset(String email) {
            return CloudOps.requestCreateUserCode(email);
      }


      private boolean resetPasswordRemote() {
            // ?????
            //LOGGER.debug("resetPasswordRemote called");
            String errorMessage = "There is a problem with your password .";
            notifyError(errorMessage);
            UserData.clear();

            return false;
      }

      // Used by forms.AccountModel
      public boolean validateLocalOnly(String pw, String email) {
            Verify vi = new Verify(pw, email, 'l');
            return vi.succeeded() == 64;
      }


      /**
       * Sets the state field based on the responses from local and remote. The responses
       * are from: if the users email exists, and if their pw is correct. See chart in the
       * Security, Privacy, Account Create, and Log-in section of the FlashMonkey Developer
       * Document.
       *
       * @param pw       ..
       * @param email    ..
       * @param formName ..
       * @return true if succeeded
       */
      public boolean validatorActionSwitch(String pw, String email, String formName) {
            //LOGGER.setLevel(Level.DEBUG);
            //LOGGER.debug("Auth.validatorActionSwitch called. v.succeeded?: <{}>", v.succeeded());

            switch (v.succeeded()) {
                  case 0: {
                        String errorMessage = "There is a problem with your password.";
                        notifyError(errorMessage);
                        return false;
                  }
                  case 4: {
                        // User does not exist locally, and user is not connected.
                        // Create new local user.
                        if (formName.equals("signup")) {
                              // creating user locally when not connected.
                              createUserLocalNot(pw, email);
                              return true;
                        } else {
                              String errorMessage = "Your user does not exist. Please create an account.";
                              notifyError(errorMessage);
                              showCreateUserLocal();
                              return false;
                        }
                  }
                  // Handles User does not exist on this machine nor
                  // at remote.
                  case 20: {
                        if (formName.equals("signup")) {
                              saveAction(pw, email);
                              // Sets userCreated
                              createUserRemote(email);
                              return false;
                        } else {
                              // Attempted to sign-in and user does
                              // not exist.
                              String errorMessage = "We didn't find your user. Please create an account.";
                              notifyError(errorMessage);
                              showCreateUserLocal();
                              return false;
                        }
                  }
                  case 64: {
                        // Disconnected state: PW amd USER combo Passed: Local operations enabled
                        this.state = 8675309;
                        return true;
                  }
                  case 80: {
                        // request to create user at remote. Sets userCreated
                        createUserRemote(email);
                        return false;
                  }
                  case 130: {
                        String errorMessage = r.resetPWLocal(pw, email);
                        // @todo email user that their PW has changed at the computer name.
                  }
                  case 132: {
                        createUserLocalConnected(pw, email);
                        return true;
                  }
                  case 192: {
                        if(!DirectoryMgr.resuExists()) {
                              saveAction(pw, email);
                        }
                        // Connected state: PW and USER combo passed on local and remote. Remote and local operations enabled.
                        this.state = 8675311;
                        return true;
                  }
                  case 65: {
                        // Moved to here from 80 on 12 Jan 2022
                        if (formName.equals("signup")) {
                              // request to create user at remote. Sets userCreated
                              createUserRemote(email);
                              return false;
                        } else {
                              Platform.runLater(this::resetPasswordRemote);
                              return false;
                        }
                  }
                  default: {
                        resetPasswordRemote();
                        return false;
                  }
            }
      }

      // **** HELPER METHODS **** //
      private static void notifyError(String errorMessage) {
            FxNotify.notification("", " Ooops! " + errorMessage, Pos.CENTER, 10,
                "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getPrimaryWindow());
      }

//      /**
//       * @param x1      pw
//       * @param x2      email
//       * @param notUsed ..
//       * @return true if succeeded
//       */
//      public boolean saveAction(String x1, String x2, int notUsed) {
//            if (x1.length() == 0 || x1.isBlank() || x2.length() == 0 || x2.isBlank()) {
//                  return false;
//            }
//            return saveAction(x1, x2);
//      }

      /**
       * Saves user data to file
       * Prints a message if successful or not.
       *
       * @param x1 pw
       * @param x2 email
       * @return returns true if a new user is saved to file, false otherwise.
       */
      private boolean saveAction(String x1, String x2) {
            //LOGGER.info("In save action and pw is: " + x1 + " getUserName is: " + x2);
            // Create userFile data
            String msg = v.newUser(x1, x2);
            //LOGGER.info("saveAction() create newUser : {}",msg);
            if (msg.startsWith("Success")) {
                  UserData.setUserName(x2);
                  //LOGGER.info("success: " + msg);
                  return true;
            } else {
                  // Prevent bug issue #0001
                  // LOGGER.debug("Clearing user data");
                  UserData.clear();
                  notifyError(msg);
                  return false;
            }
      }

      /**
       * Use with reset Password for local.
       *
       * @throws NullPointerException
       */
      public void execute() throws NullPointerException {
            r.resetPWLocal();
      }

      /**
       * Isolated class save users PW. To ensure privacy and prevent
       * leaks or adhok use of saving passwords. Password file saving
       * is completed through Auth. To save a PW from outside of the
       * class requires setting the fields of this class in the parent
       * constructor and setting the resetLocal field to an allowable
       * string.
       */
      private class R {
            //  userName
            String x1;
            // pw
            String x2;

            R(String email, String x2) {
                  this.x1 = email;
                  this.x2 = x2;
            }

            private boolean resetPWLocal() {
                  // Sent as x3, used in tkm.digest(...) in Vert.X
                  if (resetLocal.equals("TexasA&M")) {
                        UserData.setUserName(x1);
                        resetPWLocal(x2, x1);
                        resetLocal = "";
                        return true;
                  }
                  return false;
            }

            private String resetPWLocal(String pw, String name) {
                  Verify v = new Verify(pw, name, 'l');
                  return v.resetUserInfo(pw, name);
            }
      }

      private int actionSwitchLocal(Verify v) {
            switch (v.succeeded()) {
                  case 64: {
                        // correct
                        return 8675309;
                  }
                  default: {
                        return 0;
                  }
            }
      }
}
