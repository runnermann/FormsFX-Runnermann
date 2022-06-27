package uicontrols;

import fmannotations.FMAnnotations;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import uicontrols.api.FMNotifications;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Stage;

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

      public static synchronized void notificationWarning(String title, String msg, Pos pos, int duration, String iconPath, Stage owner, int padding) {
            if (notificationBuilder == null || !notificationBuilder.isShowing()) {
                  Node graphic = new ImageView(iconPath);
                  notificationBuilder = FMNotifications.create()
                      .title(title)
                      .text(msg)
                      .graphic(graphic)
                      .hideAfter(Duration.seconds(duration))
                      .padding(padding)
                      .position(pos);

                  notificationBuilder.owner(owner);
                  notificationBuilder.warningStyle();
                  notificationBuilder.show();
            }
      }

      public static synchronized void notificationBlue(String title, String msg, Pos pos, int duration, String iconPath, Stage owner) {
            if (notificationBuilder == null || !notificationBuilder.isShowing()) {
                  Image img = new Image(iconPath);
                  Node graphic = new ImageView(img);
                  notificationBuilder = FMNotifications.create()
                      .title(title)
                      .text(msg)
                      .graphic(graphic)
                      .hideAfter(Duration.seconds(duration))
                      .position(pos);

                  notificationBuilder.owner(owner);
                  notificationBuilder.blueStyle();
                  notificationBuilder.show();
            }
      }

      public static synchronized void notificationDark(String title, String msg, Pos pos, int durationSeconds, String iconPath, Stage owner) {
            if (notificationBuilder == null || !notificationBuilder.isShowing()) {
                  Node graphic = new ImageView(iconPath);
                  notificationBuilder = FMNotifications.create()
                      .title(title)
                      .text(msg)
                      .graphic(graphic)
                      .hideAfter(Duration.seconds(durationSeconds))
                      .position(pos);

                  notificationBuilder.owner(owner);
                  notificationBuilder.darkStyle();
                  notificationBuilder.show();
            }
      }

      @FMAnnotations.DoNotDeployMethod
      public static boolean notificationIsShowing() {
            return notificationBuilder != null && notificationBuilder.isShowing();
      }

      @FMAnnotations.DoNotDeployMethod
      public static boolean builderIsNull() {
            return null == notificationBuilder;
      }
}
