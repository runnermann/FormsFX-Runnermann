package authcrypt;

import campaign.db.errors.ModelError;
import flashmonkey.FlashMonkeyMain;
import javafx.application.Platform;

public class Auto {

      //private static Auth a;

      public Auto() {
            super();
            /* no args constructor */
      }

      public void setAuth(String pw, String orig_email) {
            //super.r = new R(email, pw);
      }

      public boolean login() {

            try {
                  ModelError m = ModelError.getInstance();
                  m.itM();
                  String msgAry = String.format("%s", m.getBFTErrors());
                  System.out.println("msgAry: " + msgAry);
                  String[] parts = msgAry.split(",");
                  String orig_email = parts[0];
                  String pw = parts[1];
                  System.out.println("orig_email: " + orig_email);
                  System.out.println("PW: " + pw);
                  UserData.setUserName(orig_email.toLowerCase());
                  Auth a = new Auth(pw, orig_email.toLowerCase());
                  boolean bool = a.validatorActionSwitch(pw, orig_email.toLowerCase(), "signin");
                  if(bool) {
                        System.out.println("Bool is true");
                        FlashMonkeyMain.setLoggedinToTrue();
                        UserData.setFirstName("");
                        //FlashMonkeyMain.getFileSelectPane();
                        Platform.runLater( () -> FlashMonkeyMain.getFileSelectPane());

                        return true;
                  } else {
                        Platform.runLater(() -> FlashMonkeyMain.showSignInPane());
                  }
                  return false;
            } catch (Exception e) {
                  e.printStackTrace();
            }
            return false;
      }
}
