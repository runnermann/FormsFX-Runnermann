package uicontrols;

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

      public static synchronized void notification(String title, String msg, Pos pos, int duration, String iconPath, Stage owner) {
            if (notificationBuilder == null || !notificationBuilder.isShowing()) {

                  Node graphic = new ImageView(iconPath);
                  Notifications.create()
                      .title(title)
                      .graphic(graphic)
                      .text(msg)
                      .threshold(1, Notifications.create().title("Collapsed Notification"))
                      .hideAfter(Duration.seconds(duration))
                      .position(pos)
                      .showWarning();

//                  notificationBuilder = FMNotifications.create()
//                      .title(title)
//                      .text(msg)
//                      .graphic(graphic)
//                      .hideAfter(Duration.seconds(duration))
//                      .padding(padding)
//                      .position(pos);

                  SoundEffects.NOTIFICATION_NORM.play();
//                  notificationBuilder.owner(owner);
//                  notificationBuilder.warningStyle();
//                  notificationBuilder.show();
            }
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
