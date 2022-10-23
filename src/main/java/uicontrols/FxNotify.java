package uicontrols;

import flashmonkey.FlashMonkeyMain;
import fmannotations.FMAnnotations;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import media.sound.SoundEffects;
import org.controlsfx.control.Notifications;
import uicontrols.api.FMNotifications;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.awt.*;

/**
 * <p>Provides a pop-up message without a button.</p>
 * <pre>
 *     Usage:
 *     String msg = "That didn't work.... Try resetting your password.";
 *                     FxNotify.notificationDark("", " Hmmmm! " + msg, Pos.CENTER, 8,
 *                             "image/Flash_hmm_75.png", FlashMonkeyMain.getWindow());
 *                     FlashMonkeyMain.showResetOnePane();
 * </pre>
 */
public class FxNotify {

      public FxNotify() {
            /* no args constructor */
      }

      private static FMNotifications notificationBuilder;

      /**
       * The non-blocking notification pop-up. Should appear within the window of the owner stage.
       * @param title Title if needed
       * @param msg message
       * @param pos position in the window
       * @param seconds amount of time it is displayed
       * @param graphicPath The path to the image to be displayed if any
       * @param owner The stage/window that created the alert notification.
       */
      public static synchronized void notification(String title, String msg, Pos pos, int seconds, String graphicPath, Stage owner) {
 //           if (notificationBuilder == null || !notificationBuilder.isShowing()) {

                  Node graphic = new ImageView(graphicPath);
                  Notifications.create()
                      .title(title)
                      .graphic(graphic)
                      .text(msg)
                      .threshold(1, Notifications.create().title("Collapsed Notification"))
                      .hideAfter(Duration.seconds(seconds))
                      .position(pos)
                      .owner(owner)
                      .showWarning();

                  SoundEffects.NOTIFICATION_NORM.play();
//                  notificationBuilder.owner(owner);
//                  notificationBuilder.warningStyle();
//                  notificationBuilder.show();
//            }
      }

//      public static synchronized void notificationBlue(String title, String msg, Pos pos, int duration, String iconPath, Stage owner) {
//            if (notificationBuilder == null || !notificationBuilder.isShowing()) {
//                  Image img = new Image(iconPath);
//                  Node graphic = new ImageView(img);
//
//
//
//                  //Toolkit.getDefaultToolkit().beep();
//                  SoundEffects.NOTIFICATION_NORM.play();
//                  notificationBuilder = FMNotifications.create()
//                      .title(title)
//                      .text(msg)
//                      .graphic(graphic)
//                      .hideAfter(Duration.seconds(duration))
//                      .position(pos);
//
//                  notificationBuilder.owner(owner);
//                  notificationBuilder.blueStyle();
//                  notificationBuilder.show();
//            }
//      }

//      public static synchronized void notificationDark(String title, String msg, Pos pos, int durationSeconds, String iconPath, Stage owner) {
//            if (notificationBuilder == null || !notificationBuilder.isShowing()) {
//                  Node graphic = new ImageView(iconPath);
//                  SoundEffects.NOTIFICATION_NORM.play();
//                  notificationBuilder = FMNotifications.create()
//                      .title(title)
//                      .text(msg)
//                      .graphic(graphic)
//                      .hideAfter(Duration.seconds(durationSeconds))
//                      .position(pos);
//
//                  Toolkit.getDefaultToolkit().beep();
//                  notificationBuilder.owner(owner);
//                  notificationBuilder.darkStyle();
//                  notificationBuilder.show();
//            }
//      }

      @FMAnnotations.DoNotDeployMethod
      public static boolean notificationIsShowing() {
            return notificationBuilder != null && notificationBuilder.isShowing();
      }

      @FMAnnotations.DoNotDeployMethod
      public static boolean builderIsNull() {
            return null == notificationBuilder;
      }
}
