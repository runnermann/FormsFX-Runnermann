package forms;

import authcrypt.UserData;
import authcrypt.user.EncryptedStud;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.sun.glass.ui.Screen;
import fileops.DirectoryMgr;
import fileops.FileSelector;
import fileops.VertxLink;
import fmannotations.FMAnnotations;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.slf4j.LoggerFactory;
import type.tools.imagery.ImageUploaderBox;
import uicontrols.FMHyperlink;
import uicontrols.SceneCntl;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Displays users data. 1) Ensure this form is not displayed unless the user
 * has passed login. 2) Ensure users data is not over-written unless the user
 * has passed login. 3) Ensure users data is not available unless the user
 * has passed login.
 */
public class StudentPane extends FormParentPane {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(StudentPane.class);

	// ****** CONSTANT ******
	private static final String vertxGet = VertxLink.USER_PAGE.getLink() + UserData.getUserMD5Hash();
	// ****** fields ******
	private HBox containerHBox;
	// Box to contain the imgView so we can swap the
	// img later!?!
	private HBox imgViewBox;
	private GridPane leftGPane;
	private GridPane rightGPane;
	private static Button qrButton;
	private static ImageView qrView;
	private static FMHyperlink deckLink;
	private static ImageUploaderBox imgUp;
	private StudentModel model;
	private EncryptedStud student;

	
	public StudentPane() {
		super();
		LOGGER.info("StudentPane called");
	}

	@Override
	public ScrollPane getMainPane() {
		return this.scrollPane;
	}
	
	/**
	 * This method is used to set up the stylesheets.
	 */
	@Override
	public void initializeSelf() {
		getStylesheets().add(getClass().getResource("/css/fxformStyle.css").toExternalForm());
		model = new StudentModel();
		student = new EncryptedStud();
		this.initialize(model, student);
	}
	
	/**
	 * This method initializes all nodes and regions.
	 */
	@Override
	public void initializeParts() {
		super.formRenderer = new FormRenderer(model.getFormInstance());

		leftGPane = getGrid();
		rightGPane = getGrid();
		imgUp = new ImageUploaderBox();
		imgUp.init(ImageUploaderBox.AVATAR_IMG, 270, 270);
		imgViewBox = new HBox();
		containerHBox = new HBox(4);
		deckLink = new FMHyperlink("Not yet available.", "");
		deckLink.setDisable(true);

		leftGPane.setId("formPane");
		rightGPane.setId("formPane");
		mainVBox.setId("infoPane");

		qrButton = new Button("SAVE TO DESKTOP");
		qrButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: #ffffff; -fx-font-size: 14;");
		qrButton.setMaxWidth(200);

		final String filePath = DirectoryMgr.getMediaPath('z') + model.getDescriptor().getFirstName() + "_QRCode.png";
		// if file exists, shows the decks qr code, else,
		// shows the standard FlashMonkey QR Code.
		File f = new File(filePath);
		setQrImage(f);

		// User save QR-File folder selector
		qrButton.setOnAction(e -> {
			if(f.exists()) {
				FileSelector.saveQRImagePopup(f, f.getName());
			}
		});
	}
	
	@Override
	public void setupValueChangedListeners() {
		// When the user clicks submit, listen for the QR link property for
		// a change. Display the QR code and the link.
		model.getQrFIlePathProperty().addListener((obs, old, changed) -> {
			model.getFormInstance().changedProperty().set(true);
			final File f = new File(changed);
				if(f.exists()) {
						setQrCode(f);
						imgViewBox.getChildren().clear();
						imgViewBox.getChildren().add(qrView);
						VBox linkBox = new VBox(10);
						linkBox.setId("linkBox");
						linkBox.getChildren().clear();
						linkBox.getChildren().addAll(deckLink, qrButton);
						leftGPane.add(linkBox, 1, 4, 1, 1);
				}
		});


		imgUp.getIsNewImgNameChangedProperty().addListener((obs, old, changed) -> {
			model.getFormInstance().changedProperty().setValue(true);
			if(model.getFormInstance().validProperty().get() == true) {
				model.getFormInstance().persistableProperty().set(true);
			};
		});
	}

	/**
	 * Actions to be performed before the model formAction method.
	 */
	@Override
	public void paneAction() {
		model.getDescriptor().photoLinkProperty().set(imgUp.getImgName());
		imgUp.snapShot('a');
	}

	// Other methods

	/**
	 * Call when the QR code has changed or to display in the pane.
	 * Sets the qrView
	 */
	private void setQrCode(File qrImgFile) {
		if (qrImgFile.exists()) {
			Image img = new Image("file:" + qrImgFile.getPath(), true);
			if(qrView == null) {
				qrView = new ImageView();
			}
			qrView.setImage(img);

			LOGGER.debug("vertxGet: " + vertxGet);
			deckLink = new FMHyperlink("Link to your profile & stats web-page. Right click to copy", vertxGet);
			deckLink.setOnMouseClicked(this::linkAction);
		} else {
			// set it to the default app QR image
			qrView = new ImageView(new Image(getClass().getResourceAsStream("/image/QR_Code_IndexPg.png")));
		}
	}

	/**
	 * Called when the form is initially opened. If the image exists on disk it is displayed,
	 * otherwise it displays the blue QR code.
	 * @param qrImgFile
	 */
	private void setQrImage(File qrImgFile) {
		if (qrImgFile.exists()) {
			Image img = new Image("file:" + qrImgFile.getPath(), true);
			if(qrView == null) {
				qrView = new ImageView();
			}
			qrView.setImage(img);
			LOGGER.debug("vertxGet: " + vertxGet);
			deckLink = new FMHyperlink("Link to your profile & stats web-page. Right click to copy", vertxGet);
			deckLink.setOnMouseClicked(this::linkAction);
		} else {
			// set it to the default app QR image
			qrView = new ImageView(new Image(getClass().getResourceAsStream("/image/QR_Code_IndexPg.png")));
		}
	}

	private void linkAction(MouseEvent e) {
		if (e.getButton().equals(MouseButton.SECONDARY)) {
			final Clipboard cb = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(deckLink.getLink());
			cb.setContent(content);
		} else {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URI(deckLink.getLink()));
				} catch (IOException exc) {
					exc.printStackTrace();
				} catch (URISyntaxException ex) {
					ex.printStackTrace();
				}
			}
		}
	}


	/**
	 * Replaces ParentForm to allow us to set up a
	 * a two column form.
	 */
	private void setParentParts() {
		// Size the pane for users with small screens and
		// screen scale larger than 1:1.
		double scale = Screen.getMainScreen().getPlatformScaleY();
		if( scale != 1.0) {
			double ht = Screen.getMainScreen().getVisibleHeight() * (int) (scale / 2);
			// leave some room
			ht -= 40;
			if(ht > 800) {
				ht = 800;
			}
			mainVBox.setPrefHeight(ht);
		} else {
			mainVBox.setPrefHeight(800);
		}

		formRenderer.setId("formPane");

		submitButton.setMaxWidth(300);
		submitButton.setMinWidth(300);
		submitButton.setId("signInButton");

		buttonHBox.setAlignment(Pos.CENTER);
		buttonHBox.getChildren().add(submitButton);
		spacer.setMinHeight(2);
		spacer1.setMinHeight(20);
	}

	@Override
	public void layoutParts() {
		setParentParts();
		//super.layoutParts();
		super.submitButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: #ffffff; -fx-font-size: 14; -fx-font-weight: BOLD");
		formRenderer.setMaxWidth(SceneCntl.getFormBox().getWd() - 10);
		formRenderer.setPrefSize(SceneCntl.getFormBox().getWd(), SceneCntl.getFormBox().getHt());

		VBox linkBox = new VBox(10);
		linkBox.getChildren().addAll(deckLink, qrButton);
		linkBox.setId("linkBox");

		Pane spc = new Pane();
		spc.setMinHeight(20);
		leftGPane.add(spc, 0, 0, 2, 1 );

		// Temp QR code column 1, set in method.
		imgViewBox.getChildren().add(qrView);
		leftGPane.add(imgViewBox, 1, 1, 1, 3);

		leftGPane.add(linkBox, 1, 4, 1, 1);
		// Row 6 & 7 are empty
		// The students avatar image or user image???
		VBox imgUploaderVBox = imgUp.getVBox();
		imgUploaderVBox.setAlignment(Pos.CENTER);

		ImageView instructionView = new ImageView("image/avatar_upload_bkgnd.png");
		StackPane imgUploaderStack = new StackPane(instructionView);
		imgUploaderStack.getChildren().addAll(imgUploaderVBox);


		leftGPane.add(imgUploaderStack, 0, 8, 2,1);

		// Add the form to the right pane
		rightGPane.addRow(0, formRenderer);

		leftGPane.setPrefWidth(SceneCntl.getFormBox().getWd());
		rightGPane.setPrefWidth(SceneCntl.getFormBox().getWd());

		containerHBox.getChildren().addAll(leftGPane, rightGPane);
		mainVBox.getChildren().addAll(spacer, containerHBox, buttonHBox, spacer1);
		scrollPane.setContent(mainVBox);

		int wd = SceneCntl.getFormBox().getWd() * 2 + 12;
		scrollPane.setPrefWidth(wd);
		super.setFormPaneWidth(wd);

	}

	private GridPane getGrid() {
		GridPane gPane = new GridPane();
		gPane.setHgap(6);
		gPane.setVgap(10);
		gPane.setAlignment(Pos.CENTER);
		gPane.setPadding(new Insets(4, 2, 4, 2));
		return gPane;
	}

	/* *** FOR TESTING *** */
	@FMAnnotations.DoNotDeployMethod
	public StudentModel getModel() {
		return this.model;
	}

}
