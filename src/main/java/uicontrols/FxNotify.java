package uicontrols;

import javafx.scene.image.ImageView;
import javafx.util.Duration;
import uicontrols.api.FMNotifications;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Stage;


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
			
				//.onAction(e -> System.out.println("Notification clicked on!"))
				//.threshold((int) thresholdSlider.getValue(),
				//		Notifications.create().title("Threshold Notification"));
		
			notificationBuilder.owner(owner);
			notificationBuilder.purpleStyle();
			notificationBuilder.show();
	}

	public static synchronized void notificationBlue(String title, String msg, Pos pos, int duration, String iconPath, Stage owner) {

		Node graphic = new ImageView(iconPath);
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

	public static synchronized void notificationDark(String title, String msg, Pos pos, int duration, String iconPath, Stage owner) {

		Node graphic = new ImageView(iconPath);
		FMNotifications notificationBuilder = FMNotifications.create()
				.title(title)
				.text(msg)
				.graphic(graphic)
				.hideAfter(Duration.seconds(duration))
				.position(pos);

		notificationBuilder.owner(owner);
		notificationBuilder.darkStyle();
		notificationBuilder.show();
	}
}
