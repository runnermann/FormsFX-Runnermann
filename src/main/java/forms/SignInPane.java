package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ViewMixin;

import flashmonkey.FlashMonkeyMain;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SignInPane extends Pane implements ViewMixin {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SignInPane.class);
	
	private GridPane signInPane;
	private Pane spacer;
	private HBox newActHBox;
	private HBox forgotHBox;
	private HBox signInHBox;
	private static VBox msgVBox;
	private Button signInBtn;
	private Hyperlink signUpLink;
	private Hyperlink forgotLink;
	
	// FxForm related
	private FormRenderer displayForm;
	private SignInModel model;
	
	public SignInPane(SignInModel signInModel) {
		LOGGER.info("SignInPane constructor called");
		this.model = signInModel;
		init();
	}
	
	/**
	 * This method is used to set up the stylesheets.
	 */
	@Override
	public void initializeSelf() {
		LOGGER.info("initializeSelf called and it does nothing");
	}
	
	/**
	 * This method initializes all nodes and regions.
	 */
	@Override
	public void initializeParts() {
		
		LOGGER.info("initializeParts called");
		
		signInPane = new GridPane();
		spacer = new Pane();
		newActHBox = new HBox();
		forgotHBox = new HBox();
		msgVBox = new VBox();
		signInHBox = new HBox();
		signInBtn = new Button("Sign in");
		signUpLink = new Hyperlink("Join now");;
		forgotLink = new Hyperlink("Forgot password?");
		
		displayForm = new FormRenderer(model.getFormInstance());
	}
	
	/**
	 * This method sets up the necessary bindings for the logic of the
	 * application.
	 */
	@Override
	public void setupBindings() {
		
		LOGGER.info("setupBindings() called");
		
		signInBtn.disableProperty().bind(model.getFormInstance().persistableProperty().not());
		//reset.disableProperty().bind(model.getFormInstance().changedProperty().not());
		displayForm.prefWidthProperty().bind(signInPane.prefWidthProperty());
	}
	
	/**
	 * This method sets up listeners and sets the text of the state change
	 * labels.
	 */
	@Override
	public void setupValueChangedListeners() {
		
		LOGGER.info("setupValueChangedListeners called");
		
		//model.getFormInstance().changedProperty().addListener((observable, oldValue, newValue) -> changedLabel.setText("The form has " + (newValue ? "" : "not ") + "changed."));
		//model.getFormInstance().validProperty().addListener((observable, oldValue, newValue) -> validLabel.setText("The form is " + (newValue ? "" : "not ") + "valid."));
		//model.getFormInstance().persistableProperty().addListener((observable, oldValue, newValue) -> persistableLabel.setText("The form is " + (newValue ? "" : "not ") + "persistable."));
		
		//model.getCountry().nameProperty().addListener((observable, oldValue, newValue) -> countryLabel.setText("Country: " + newValue));
		//model.getCountry().currencyShortProperty().addListener((observable, oldValue, newValue) -> currencyLabel.setText("Currency: " + newValue));
		//model.getCountry().populationProperty().addListener((observable, oldValue, newValue) -> populationLabel.setText("Population: " + newValue));
	}
	
	/**
	 * This method sets up the handling for all the button clicks.
	 */
	@Override
	public void setupEventHandlers() {
		
		signInBtn.setOnAction(e -> {
			model.formAction();
		});
		
		forgotLink.setOnAction(e -> {
			
			// TODO forgotLink Action
		});
		
		signUpLink.setOnAction(e -> {
			LOGGER.info("signUpLink clicked.");
			FlashMonkeyMain.getSignUpPane();
		});
		
	/*	languageDE.setOnAction(event -> {
			model.translate("DE");
			languageDE.setDisable(true);
			languageEN.setDisable(false);
		});
		
		languageEN.setOnAction(event -> {
			model.translate("EN");
			languageEN.setDisable(true);
			languageDE.setDisable(false);
		});
		
	 */
		
	}
	
	
	/**
	 * This method is used to layout the nodes and regions properly.
	 */
	@Override
	public void layoutParts() {
		
		LOGGER.info("*** layoutParts called ***");
		
		displayForm.setMaxWidth(260);
		
		signInPane.setAlignment(Pos.CENTER);
		signInPane.setHgap(10);
		signInPane.setVgap(12);
		signInPane.setId("fileSelectPane");
		signInPane.setPrefSize(325, 400);
		signInPane.setOnKeyPressed(f -> {
			if(f.getCode() == KeyCode.ENTER) {
				model.formAction();
			}
		});
		
		spacer.setPrefHeight(20);
		
		Label msgLabel = new Label("Log in");
		msgLabel.setId("label24White");
		Label signUpLabel = new Label("New to FlashMonkey?");
		signUpLabel.setId("signUpLabel");
		//signUpLabel.setStyle("-fx-font-size: 12");
		
		signInBtn.setMaxWidth(240);
		signInBtn.setMinWidth(240);
		signInBtn.setId("signInButton");
		
		signUpLink.setId("signInHyp");
		forgotLink.setId("signInHyp");
		
		msgVBox.setAlignment(Pos.CENTER);
		forgotHBox.setAlignment(Pos.CENTER);
		forgotHBox.getChildren().add(forgotLink);
		
		msgVBox.getChildren().add(msgLabel);
		newActHBox.getChildren().addAll(signUpLabel, signUpLink);
		newActHBox.setAlignment(Pos.CENTER);
		
		signInHBox.setAlignment(Pos.CENTER);
		signInHBox.getChildren().add(signInBtn);
		
		signInPane.addRow(0, msgVBox);
		signInPane.addRow(1, spacer);
		signInPane.addRow(2, displayForm);
		signInPane.addRow(3, signInHBox);
		signInPane.addRow(4, forgotHBox);
		signInPane.addRow(5, newActHBox);
	}
	
	
	public GridPane getSignInPane() {
		return this.signInPane;
	}
	
	/**
	 * Uses A working replacement for Label.wrapText() method.
	 * Called by model sets errorMsg in msgVBox
	 * @param msg
	 */
	public static void setErrorMsg(String msg) {
		
		msgVBox.getChildren().clear();
		int lineLength = 30;
		FormsUtility.setErrorMsg(msg, msgVBox, lineLength);
	}
}
