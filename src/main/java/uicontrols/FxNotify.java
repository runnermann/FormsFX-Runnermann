package uicontrols;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import uicontrols.api.FMNotifications;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Popup message.
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
	
	public static synchronized void notificationPurple(String title, String msg, Pos pos, int duration, String iconPath, Stage owner) {

		Node graphic = new ImageView(iconPath);
		
		FMNotifications notificationBuilder = FMNotifications.create()
				.title(title)
				.text(msg)
				.graphic(graphic)
				.hideAfter(Duration.seconds(duration))
				.position(pos);
		
			notificationBuilder.owner(owner);
			notificationBuilder.purpleStyle();
			notificationBuilder.show();
	}

	public static synchronized void notificationBlue(String title, String msg, Pos pos, int duration, String iconPath, Stage owner) {
		Image img = new Image(iconPath);
		Node graphic = new ImageView(img);
		FMNotifications notificationBuilder = FMNotifications.create()
				.title(title)
				.text(msg)
				.graphic(graphic)
				.hideAfter(Duration.seconds(duration))
				.position(pos);

		notificationBuilder.owner(owner);
		notificationBuilder.blueStyle();
		notificationBuilder.show();
	}

	public static synchronized void notificationDark(String title, String msg, Pos pos, int durationSeconds, String iconPath, Stage owner) {

		Node graphic = new ImageView(iconPath);
		FMNotifications notificationBuilder = FMNotifications.create()
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
