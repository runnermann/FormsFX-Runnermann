package uicontrols;

import flashmonkey.FlashMonkeyMain;
import fmannotations.FMAnnotations;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Popup with a user response. Waits until the user has responded.
 */
public class FMAlerts {

      private static Alert alert;
      private static Button okBtn;
      private static Button cancelBtn;

      public final static String MISSING_DELETE = "It appears that the question or answer\n"
          + "area's are incomplete. If you\n"
          + "continue this card will be deleted.\n\n"
          + "Are you sure you want to delete this card?";

      public final static String MISSING_ANSWER = "This card is missing content in the answer area.\n"
          + " Do you wish to save it and return later?\n\n"
          + "To go back and edit just close this message.";

      public final static String MISSING_QUESTION = "This card is missing content in the question area.\n"
          + " Do you wish to save it and return later?\n\n"
          + "To go back and edit just close this message.";

      public final static String MISSING_TESTTYPE = "Ooooph!\nThe Test Type has not been selected. "
          + "\n  To go back and set the test type, press \"OK\"."
          + "\n  To delete this card and continue, press \"Cancel\". ";


      public final static String NO_DATA = "There is no data to save.\n"
          + " If you are finished, press quit.";

      public final static String CREATE_ONLINE = "Hey! \nYou are about to create an online account. "
          + "This will allow you to:  synchronize your learning materials between devices, share with friends, " +
          "collaborate with others, become a creator, and find resources created by your classmates. ";

      public final static String JOIN_OR_FAIL = "Selling decks is a part of FlashMonkey Advanced. To subscribe and " +
          " have payments sent to your bank, select 'OK'.";


      public FMAlerts() {
            /* empty constructor */
      }

      //boolean bool;

      /**
       * Single button choice popup message box.
       *
       * @param alertMsg  The message to the user.
       * @param btnString The title of the button
       * @param emojiPath The path to the emoji for this popup
       * @return True if the user clicks ok. flase otherwise.
       */
      public boolean saveAlertAction(String alertMsg, String btnString, String emojiPath) {
            AtomicBoolean bool = new AtomicBoolean(false);

            alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("ALERT");
            // null as a value will cause the section to not be displayed
            alert.setHeaderText(null);
            alert.setContentText(alertMsg);
            Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            okBtn.setText(btnString);
            ImageView emojiView = new ImageView(emojiPath);
            alert.setGraphic(emojiView);
            alert.setHeight(190);
            alert.setWidth(200);
            alert.getDialogPane().contentTextProperty().toString();//   setStyle(" -fx-text-fill: #FFFFFF");
            alert.getDialogPane().setStyle(" -fx-background-color: " + UIColors.BACKGROUND_BLUE);

            alert.showAndWait().ifPresent((btnType) -> {
                  // if user clicks ok button
                  if (btnType == ButtonType.OK) {
                        bool.set(false);

                  } else if (btnType == ButtonType.CANCEL) {
                        // else user cancels
                        alert.close();
                        bool.set(true);
                  }
            });
            return bool.get();
      }


      public void noDataAlert(String alertMsg) {
            alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("No data to save.");

            alert.setHeaderText(null);
            alert.setContentText(alertMsg);

            ImageView sunglasses = new ImageView("emojis/Flash_hmm_75.png");
            alert.setGraphic(sunglasses);
            alert.setHeight(190);
            alert.setWidth(200);
            alert.getDialogPane().setStyle("-fx-background-color: " + UIColors.BACKGROUND_ORANGE);
            alert.show();
      }

      public void sessionRestartPopup() {
            AtomicBoolean bool = new AtomicBoolean(false);
            alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Continue Session");
            alert.setHeaderText(null);
            alert.setContentText("To continue an online session please sign in again." +
                "\n You may also continue if you are offline." +
                "\n sign in?");

            ImageView sunglasses = new ImageView("emojis/flashFaces_sunglasses_60.png");
            alert.setGraphic(sunglasses);
            alert.setHeight(190);
            alert.setWidth(200);
            alert.getDialogPane().contentTextProperty().toString();//   setStyle(" -fx-text-fill: #FFFFFF");
            alert.getDialogPane().setStyle(" -fx-background-color: " + UIColors.FM_WHITE);

            alert.showAndWait().ifPresent((btnType) -> {
                  // if user clicks ok button
                  if (btnType == ButtonType.OK) {
                        //bool = true;
                        FlashMonkeyMain.showSignInPane();

                  } else if (btnType == ButtonType.CANCEL) {
                        // else user cancels
                        alert.close();
                        bool.set(false);
                        FlashMonkeyMain.showSignInPane();
                  }
            });
            //return bool;
      }

      public boolean yesOrRedirectToSignInPopup(String title, String alertMsg, String emojiPath, String uiColor) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/image/logo/blue_flash_128.png"));
            AtomicBoolean bool = new AtomicBoolean(false);

            alert.setTitle(title);
            // null as a value will cause the section to not be displayed
            alert.setHeaderText(null);
            alert.setContentText(alertMsg);

            //ImageView sunglasses = new ImageView("emojis/flashFaces_sunglasses_60.png");
            ImageView emojiView = new ImageView(emojiPath);
            emojiView.setFitWidth(256);
            emojiView.setPreserveRatio(true);
            emojiView.setSmooth(true);
            alert.setGraphic(emojiView);
            //alert.setHeight(290);
            alert.setWidth(180);
            alert.getDialogPane().contentTextProperty().toString();//   setStyle(" -fx-text-fill: #FFFFFF");
            alert.getDialogPane().setId("alert-dialog"); //setStyle(" -fx-background-color: " + uiColor);

            alert.showAndWait().ifPresent((btnType) -> {
                  // if user clicks ok button
                  if (btnType == ButtonType.OK) {
                        bool.set(true);
                  } else if (btnType == ButtonType.CANCEL) {
                        // else user cancels
                        alert.close();
                        bool.set(false);
                        FlashMonkeyMain.showSignInPane();
                  }
            });

            return bool.get();
      }

      public static String COLOR_NORMAL = "alert-dialog";
      public static String COLOR_WARNING = "warning-dialog";

      /**
       * PRovides a choice popup alert. The if the user clicks ok, returns a true
       * "cancel" returns false.
       *
       * @param title
       * @param alertMsg
       * @param emojiPath
       * @param uiColor
       * @return
       */
      public boolean choiceOnlyActionPopup(String title, String alertMsg, String emojiPath, String uiColor) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/image/logo/blue_flash_128.png"));
            AtomicBoolean bool = new AtomicBoolean(false);

            alert.setTitle(title);
            // null as a value will cause the section to not be displayed
            alert.setHeaderText(null);
            alert.setContentText(alertMsg);

            //ImageView sunglasses = new ImageView("emojis/flashFaces_sunglasses_60.png");
            ImageView emojiView = new ImageView(emojiPath);
            emojiView.setFitWidth(emojiView.getFitWidth());
            emojiView.setPreserveRatio(true);
            emojiView.setSmooth(true);
            alert.setGraphic(emojiView);
            //alert.setHeight(290);
            alert.setWidth(180);
            alert.getDialogPane().contentTextProperty().toString();//   setStyle(" -fx-text-fill: #FFFFFF");
            //alert.getDialogPane().setId("alert-dialog"); //setStyle(" -fx-background-color: " + uiColor);
            alert.getDialogPane().setStyle("-fx-font-size: 12");

            alert.showAndWait().ifPresent((btnType) -> {
                  // if user clicks ok button
                  if (btnType == ButtonType.OK) {
                        bool.set(true);
                  } else if (btnType == ButtonType.CANCEL) {
                        // else user cancels
                        alert.close();
                        bool.set(false);
                  }
            });

            return bool.get();
      }

      /**
       * Dialogue popup with 2 buttons. This box allows you to set the names of the 2 buttons as well as
       * the colors of the background. For the standard choice box, chose the choiceOnlyActionPopup.
       *
       * @param title
       * @param alertMsg
       * @param emojiPath
       * @param uiColor     Color provided from UIColor
       * @param confirmName The name for the true button
       * @param denyName    the name for the false button
       * @return 1 if the user clicks yes. -2 if the user clicks cancel, and -1
       * if they close the window.
       */
      public int choiceOptionActionPopup(String title, String alertMsg, String emojiPath, String uiColor, String confirmName, String denyName) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            //AtomicInteger res = new AtomicInteger(0);

            alert.setTitle(title);
            // null as a value will cause the section to not be displayed
            alert.setHeaderText(null);
            alert.setContentText(alertMsg);
            // okButton Name

            alert.getButtonTypes().clear();

		ButtonType bt1 = new ButtonType(confirmName);
            ButtonType bt2 = new ButtonType(denyName);
            ButtonType escBtn = ButtonType.CANCEL;

            alert.getButtonTypes().addAll(bt1, bt2, escBtn);

            Node cancelNode = alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancelNode.managedProperty().bind(cancelNode.visibleProperty());
            cancelNode.setVisible(false);

            // set alert to trigger an escape on the escape key press.
            alert.getDialogPane().setOnKeyPressed(e -> {
                  if (e.getCode() == KeyCode.ESCAPE) {
                        alert.setResult(escBtn);
                  }
            });

            // Set btn1 as the default so that enter will trigger its action.
            Button enterBtn = (Button) alert.getDialogPane().lookupButton(bt1);
            enterBtn.setDefaultButton(true);


            //ImageView sunglasses = new ImageView("emojis/flashFaces_sunglasses_60.png");
            Image img = new Image(emojiPath);
            ImageView emojiView = new ImageView(img);
            emojiView.setFitWidth(emojiView.getFitWidth());
            emojiView.setPreserveRatio(true);
            emojiView.setSmooth(true);
            alert.setGraphic(emojiView);
            alert.setHeight(290);
            alert.setWidth(180);
            //alert.getDialogPane().contentTextProperty().toString();//   setStyle(" -fx-text-fill: #FFFFFF");
            alert.getDialogPane().setStyle("-fx-font-size: 12");


            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.CANCEL) {
                  //res.set(-1);
                  return -1;
                  //res = -1;
            } else if (option.get() == bt1) {
                  // res.set(1);
                  //res = 1;
                  return 1;
            } else {
                  // res.set(-2);
                  //res = -2;
                  return -2;
            }
            // return res.get();
      }

      @FMAnnotations.DoNotDeployMethod
      public static boolean notificationIsShowing() {
            return alert != null && alert.isShowing();
      }

      @FMAnnotations.DoNotDeployMethod
      public static boolean notificationIsNull() {
            return null == alert;
      }

      @FMAnnotations.DoNotDeployMethod
      public static Point2D getOKBtnXY() {
            return new Point2D(alert.getX() + 200, alert.getY() + 134);
      }

      @FMAnnotations.DoNotDeployMethod
      public static Point2D getCancelBtnXY() {
            return new Point2D(alert.getX() + 120, alert.getY() + 60);
      }

      @FMAnnotations.DoNotDeployMethod
      public static Point2D getXBtnXY() {
            return new Point2D(alert.getX() + 16, alert.getY() + 16);
      }


}
