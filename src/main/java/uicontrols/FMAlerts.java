package uicontrols;

import flashmonkey.FlashMonkeyMain;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Popup with a user response. Waits until the user has responded.
 */
public class FMAlerts {
	
	public final static String MISSING_DELETE = "It appears that the question or answer\n"
			+ "area's are incomplete. If you\n"
			+ "continue this card will be deleted.\n\n"
			+ "Are you sure you want to delete this card?";
	
	public final static String MISSING_SAVE = "This card appears incomplete.\n"
			+ " Do you wish to save it and return later?"
			+ "\n To save and continue press \"OK\"."
			+ "\n To go back and edit press \"Cancel\".";

	public final static String MISSING_TESTTYPE = "Ooooph!\nThe Test Type has not been selected. "
			+ "\n  To go back and set the test type, press \"OK\"."
			+ "\n  To delete this card and continue, press \"Cancel\". ";


	public final static String NO_DATA = "There is no data to save.\n"
			+ " If you are finished, press quit.";

	public final static String CREATE_ONLINE = "Hey there! \nYou are about to create an online account for your user. "
			+ "This will allow you to:  synchronize your learning materials between devices, share with friends, " +
			"collaborate with others, become a creator, and find resources created by your classmates. ";

	public final static String JOIN_OR_FAIL = "Selling decks is a part of FlashMonkey Advanced. To subscribe and " +
			" have payments sent to your bank, select 'OK'.";

	
	public FMAlerts() {
		/* empty constructor */
	}
	
	boolean bool;
	
	
	public boolean saveAlertAction(String alertMsg, String emojiPath) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		
		alert.setTitle("ALERT");
		// null as a value will cause the section to not be displayed
		alert.setHeaderText(null);
		alert.setContentText(alertMsg);
		
		//ImageView sunglasses = new ImageView("emojis/flashFaces_sunglasses_60.png");
		ImageView emojiView = new ImageView(emojiPath);
		alert.setGraphic(emojiView);
		alert.setHeight(190);
		alert.setWidth(200);
		alert.getDialogPane().contentTextProperty().toString();//   setStyle(" -fx-text-fill: #FFFFFF");
		alert.getDialogPane().setStyle(" -fx-background-color: " + UIColors.BACKGROUND_BLUE);

		
		alert.showAndWait().ifPresent((btnType) -> {
			// if user clicks ok button
			if (btnType == ButtonType.OK) {
				bool = false;
				
			} else if (btnType == ButtonType.CANCEL) {
				// else user cancels
				alert.close();
				bool = true;
			}
		});
		return bool;
	}
	

	public void noDataAlert(String alertMsg) {
		Alert alert = new Alert(Alert.AlertType.NONE);
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
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

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
				bool = false;
				FlashMonkeyMain.showSignInPane();
			}
		});
		//return bool;
	}

	public boolean yesOrRedirectToSignInPopup(String title, String alertMsg, String emojiPath, String uiColor) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/image/logo/blue_flash_128.png"));

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
				bool = true;
			} else if (btnType == ButtonType.CANCEL) {
				// else user cancels
				alert.close();
				bool = false;
				FlashMonkeyMain.showSignInPane();
			}
		});

		return bool;
	}

	public boolean choiceOnlyActionPopup(String title, String alertMsg, String emojiPath, String uiColor) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/image/logo/blue_flash_128.png"));

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
				bool = true;
			} else if (btnType == ButtonType.CANCEL) {
				// else user cancels
				alert.close();
				bool = false;
			}
		});

		return bool;
	}
}
