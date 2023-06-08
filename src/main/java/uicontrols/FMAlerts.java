package uicontrols;

import flashmonkey.FlashMonkeyMain;
import fmannotations.FMAnnotations;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import media.sound.SoundEffects;


import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Popup with a user response. Waits until the user has responded.
 */
public class FMAlerts {

      private static Alert alert;


      public final static String MISSING_ANSWER = "There is no content in the answer.\n\n"
          + "To save it anyway, click save.\n"
          + "To go back, close this message X";

      public final static String MISSING_QUESTION = "There is no content in the question.\n\n"
          + "To save it anyway, click save.\n"
          + "To go back, close this message";

      public final static String MISSING_TESTTYPE = "\nThe CARD TYPE has not been set. "
          + "\n\nDELETE THIS CARD?" +
          "\n\n \"Cancel\" to go back and set it.";

      public final static String CREATE_ONLINE = "Hey! \nYou are about to create an online account. "
          + "This will allow you to:  synchronize your learning materials, share with friends, " +
          "become a creator, and find resources created by your classmates. ";

      public final static String JOIN_OR_FAIL =
          "\nSTART EARNING FROM YOUR STUDY MATERIALS" +
          "\n\n - Earn knowledge credibility to show to job recruiters" +
          "\n - Earn pay" +
              "\n\t - get 100% of what you earn" +
              "\n\t - only minor credit card fees and taxes are subtracted" +
          "\n\n - Click OK to begin";


      public FMAlerts() {
            /* empty constructor */
      }


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
            alert.getDialogPane().contentTextProperty().toString();
            alert.getDialogPane().setStyle(" -fx-background-color: " + UIColors.BACKGROUND_BLUE);
            SoundEffects.ATTENTION.play();

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

      public void mathErrorAlert(String title, Node node) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            // Set a pane into the dialogue
            alert.getDialogPane().setContent(node);

            alert.setWidth(180);
            SoundEffects.ERROR.play();

            Optional<ButtonType> option = alert.showAndWait();
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
            SoundEffects.ATTENTION.play();
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
            SoundEffects.ATTENTION.play();
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



      public static final String COLOR_NORMAL = "alert-dialog";
      public static final String COLOR_WARNING = "warning-dialog";

      /**
       * Provides a popup alert with two button options. If the user clicks ok, returns a true
       * "cancel" returns false. An emoji or image is displayed. To display on top either provide text
       * or a blank string. To display the image to the left provide null in the headerText
       *
       * @param title
       * @param headerText provide null to display to the left, and a blank string to display on top.
       *                   Optoinally provide a string.
       * @param alertMsg
       * @param emojiPath
       * @param uiColor
       * @param effects SoundEffect, provide your choice of SoundEffect. If left null the default will be played.
       * @return
       */
      public boolean choiceOnlyActionPopup(String title, String headerText, String alertMsg, String emojiPath, String uiColor, SoundEffects effects) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            // set css
            DialogPane dialogPane = alert.getDialogPane();
            //dialogPane.getStylesheets().addAll("css/buttons.css", "css/fxformStyle.css");
            dialogPane.getStylesheets().add(getClass().getResource("/css/alert-nice.css").toExternalForm());
            dialogPane.getStyleClass().add("alert-nice");

            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/image/logo/blue_flash_128.png"));
            ((Stage) alert.getDialogPane().getScene().getWindow()).initOwner(FlashMonkeyMain.getPrimaryWindow());
            AtomicBoolean bool = new AtomicBoolean(false);
            alert.setTitle(title);
            // null as a value will cause the section to not be displayed
            alert.setHeaderText(headerText);
            alert.setContentText(alertMsg);
            if(null == effects) {
                  SoundEffects.ERROR.play();
            } else {
                  effects.play();
            }
            //ImageView sunglasses = new ImageView("emojis/flashFaces_sunglasses_60.png");
            ImageView emojiView = new ImageView(emojiPath);
            emojiView.setFitWidth(emojiView.getFitWidth());
            emojiView.setPreserveRatio(true);
            emojiView.setSmooth(true);
            alert.setGraphic(emojiView);
            //alert.setHeight(290);
            alert.setWidth(180);
            //String idk = alert.getDialogPane().contentTextProperty().toString();

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


      public boolean choicePanePopup(String title, String headerText, Node node, String imagePath, SoundEffects effects) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().addAll("css/alert-nice.css", "css/mainStyle.css");

            //dialogPane.getStylesheets().add(getClass().getResource("/css/alert-nice.css").toExternalForm());
            //dialogPane.getStyleClass().add("alert-nice");

            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/image/logo/blue_flash_128.png"));
            ((Stage) alert.getDialogPane().getScene().getWindow()).initOwner(FlashMonkeyMain.getPrimaryWindow());
            AtomicBoolean bool = new AtomicBoolean(false);
            alert.setTitle(title);
            // null as a value will cause the section to not be displayed
            alert.setHeaderText(headerText);
            // Set a pane into the dialogue
            alert.getDialogPane().setContent(node);
            // set the sound effect.
            if(null == effects) {
                  SoundEffects.ERROR.play();
            } else {
                  effects.play();
            }
            //ImageView sunglasses = new ImageView("emojis/flashFaces_sunglasses_60.png");
            ImageView emojiView = new ImageView(imagePath);
            emojiView.setFitWidth(emojiView.getFitWidth());
            emojiView.setPreserveRatio(true);
            emojiView.setSmooth(true);
            alert.setGraphic(emojiView);
            //alert.setHeight(290);
            alert.setWidth(180);
            //String idk = alert.getDialogPane().contentTextProperty().toString();
            Optional<ButtonType> option = alert.showAndWait();

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
       * Dialogue popup with 2 buttons and allows 3 options. EG 1. close notice and correct the problem, 2. save the card
       * with the error, 3. delete the card. This box allows you to set the names of the 2 buttons as well as
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
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().addAll("css/alert-nice.css", "css/mainStyle.css");
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/image/logo/blue_flash_128.png"));
            ((Stage) alert.getDialogPane().getScene().getWindow()).initOwner(FlashMonkeyMain.getPrimaryWindow());
            dialogPane.getStylesheets().add(getClass().getResource("/css/alert-nice.css").toExternalForm());
            dialogPane.getStyleClass().add("alert-nice");

            alert.setTitle(title);
            // null as a value will cause the header section to not be displayed
            alert.setHeaderText(null);
            alert.setContentText(alertMsg);
            // okButton Name

            alert.initOwner(FlashMonkeyMain.getPrimaryWindow());
            // Set a pane into the dialogue
            VBox node = setAlertNode(alertMsg);
            alert.getDialogPane().setContent(node);
            // Buttons with names
            ButtonType bt1 = new ButtonType(confirmName);
            ButtonType bt2 = new ButtonType(denyName);
            ButtonType escBtn = ButtonType.CANCEL;
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(bt2, bt1, escBtn);

            Node cancelNode = alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancelNode.managedProperty().bind(cancelNode.visibleProperty());
            cancelNode.setVisible(false);

            SoundEffects.ERROR.play();

            // set alert to trigger an escape on the escape key press.
            alert.getDialogPane().setOnKeyPressed(e -> {
                  if (e.getCode() == KeyCode.ESCAPE) {
                        alert.setResult(escBtn);
                  }
            });

            // Set btn1 as the default so that enter will trigger its action.
            Button enterBtn = (Button) alert.getDialogPane().lookupButton(bt1);
            enterBtn.setDefaultButton(true);
            Image img = new Image(emojiPath);
            ImageView emojiView = new ImageView(img);
            emojiView.setFitWidth(emojiView.getFitWidth());
            emojiView.setPreserveRatio(true);
            emojiView.setSmooth(true);
            alert.setGraphic(emojiView);
            alert.setHeight(290);
            alert.setWidth(180);
            //alert.getDialogPane().contentTextProperty().toString();//   setStyle(" -fx-text-fill: #FFFFFF");
            // Sets style of buttons
  //          alert.getDialogPane().setStyle("-fx-font-family: Arial; -fx-font-size: 10pt");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.CANCEL) {
                  return -1;
            } else if (option.get() == bt1) {
                  return 1;
            } else {
                  return -2;
            }
      }

      private VBox setAlertNode(String message) {
            VBox box = new VBox();
//            box.setStyle("-fx-padding: 36, 0, 0, 0");
            Label center = new Label(message);
            center.setId("label16CtrBld");
            box.setAlignment(Pos.CENTER);
            box.getChildren().add(center);

            return box;
      }

      /**
       * <p>A pre formatted pane to use in alert messages, Not stand alone alerts.</p>
       * The Intro text string that has a heavier font weight. The
       * Center message with a normal font and weight. The
       * Call to action or lower message with a heavier font
       * and larger size.
       * @param topStr
       * @param message
       * @param btmString
       * @return
       */
      public VBox alertPane(String topStr, String message, String btmString) {
            //String topStyle = "-fx-font-family: Arial; -fx-font-size: 24px; -fx-font-weight: 800; -fx-padding: 0 0 0 0; -fx-text-alignment: CENTER;";
            VBox box = new VBox(20);

            Label top = new Label(topStr);
            //top.setStyle(topStyle);
            top.setId("label24BldCtr");

            Label center = new Label(message);
            center.setStyle("-fx-font-family: Arial; -fx-font-size: 15px; -fx-font-weight: 600;");
            Label bottom = new Label(btmString);
            bottom.setId("label18CtrBld");
            box.setAlignment(Pos.TOP_CENTER);
            box.getChildren().addAll(top, center, bottom);

            return box;
      }

      public VBox alertPane(String topStr, HBox expBox) {
            //String topStyle = "-fx-font-family: Arial; -fx-font-size: 24px; -fx-font-weight: 800; -fx-padding: 0 0 0 0; -fx-text-alignment: CENTER;";
            VBox box = new VBox(20);

            Label top = new Label(topStr);
            //top.setStyle(topStyle);
            top.setId("label24BldCtr");

            //Label center = new Label(message);
            //center.setStyle("-fx-font-family: Arial; -fx-font-size: 15px; -fx-font-weight: 600;");

            box.setAlignment(Pos.TOP_CENTER);
            box.getChildren().addAll(top, expBox);

            return box;
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
            return new Point2D(alert.getX() + alert.getWidth() - 20, alert.getY() + 16);
      }


}
