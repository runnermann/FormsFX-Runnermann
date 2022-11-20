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
                  String[] parts = msgAry.split(",");
                  String pw = parts[0];
                  String orig_email = parts[1];

                  UserData.setUserName(orig_email.toLowerCase());
                  Auth a = new Auth(pw, orig_email.toLowerCase());
                  boolean bool = a.validatorActionSwitch(pw, orig_email.toLowerCase(), "signin");
                  if(bool) {
                        FlashMonkeyMain.setLoggedinToTrue();
                        UserData.setFirstName("");
                        Platform.runLater( () -> {
                              FlashMonkeyMain.getFileSelectPane();
                              FlashMonkeyMain.setTopPane();
                        });

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
