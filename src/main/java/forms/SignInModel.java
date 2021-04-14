package forms;

import authcrypt.UserData;
import authcrypt.Verify;
import ch.qos.logback.classic.Level;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import fileops.Utility;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import forms.utility.Alphabet;
import forms.utility.FirstDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class is used to create the form and holds all the necessary data. This
 * class acts as a singleton where the current instance is available using
 * {@code getInstance}.
 *
 * @author Lowell Stadelman
 */
public class SignInModel {
	
	// private static final Logger LOGGER = LoggerFactory.getLogger(SignInModel.class);
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SignInModel.class);
	private StringProperty field1 = new SimpleStringProperty("");
	private FirstDescriptor descriptor = new FirstDescriptor();
	
	/**
	 * These are the resource bundles for german and english.
	 */
	private ResourceBundle rbDE = ResourceBundle.getBundle("demo-locale", new Locale("de", "CH"));
	private ResourceBundle rbEN = ResourceBundle.getBundle("demo-locale", new Locale("en", "UK"));
	
	/**
	 * The default locale is English, thus the {@code ResourceBundleService} is
	 * initialised with it.
	 */
	private ResourceBundleService rbs = new ResourceBundleService(rbEN);

	private Form formInstance;
	/**
	 * Creates or simply returns to form singleton instance.
	 * @return Returns the form instance.
	 */
	public Form getFormInstance() {
		LOGGER.setLevel(Level.DEBUG);
		if (formInstance == null) {
			createForm();
		}
		return formInstance;
	}
	
	/**
	 * Creates a new form instance with the required information.
	 */
	private void createForm() {
		//LOGGER.info("createForm called");
		formInstance = Form.of(
				Group.of(
						Field.ofStringType(descriptor.siOrigEmailProperty())
								.id("form-field")
								.required("required_error_message")
								.placeholder("email_placeholder")
		  						.validate(StringLengthValidator.between(6, 40,"email_error_message")),
						Field.ofPasswordType(field1)
								.placeholder("password_first")
								.required("required_error_message")
								.validate(StringLengthValidator.between(3, 40,"non_error_message"))
				)
		).title("form_label")
				.i18n(rbs);
	}
	

	protected void formAction() {
		getFormInstance().persist();
		UserData.setUserName(descriptor.getSiOrigEmail());

		LOGGER.debug("formAction() userName from form: {}", descriptor.getSiOrigEmail());

		if(validate()) {
			UserData.setFirstName(descriptor.getSiFirstName());
			FlashMonkeyMain.getFileSelectPane();
			FlashMonkeyMain.setTopPane();
		}
		else {
			getFormInstance().reset();
			field1.setValue("");
		}
	}

	// validate user information, if successful then return true
	// else we create a popup and return false.
	// returns true if not connected and pw username passes. If connected, returns
	// the response from Vert.x if the pw and username passes.
	// ifConnected & ifSuccessful, gets deckList from s3 and synchronizes with local list if exists.
	private boolean validate() {
		LOGGER.info("validate() called");
		String errorMessage = "Error";

		// We interface with s3resources here in order to prevent saving unencrypted passwords
		// in a file.
		// validate the forms input
		Verify v = new Verify(field1.get(), descriptor.getSiOrigEmail().toLowerCase());
		//System.out.println(" didSucceed: " + v.succeeded());
		if(v.succeeded() == 8675309) {
			if(! Utility.isConnected()) {
				return true;
			}
			else {
				LOGGER.info("App is connected");
				// if unable to login at remote returns false and cannot download any decks
				int res = FlashCardOps.getInstance().setObjsFmS3(descriptor.getSiOrigEmail(), field1.get());
				switch(res) {
						case 1: {
							return true;
						}
						case 0: {
							LOGGER.debug("Password did not pass remote login.");

							UserData.clear();
							errorMessage = " There is a problem with your email-password combination in the cloud.\n" +
									" Use \"Forgot password\" to change the password in the cloud to the password \n" +
									" on this system and to synchronize your work.";

							break;
						}
						default: {
							LOGGER.warn("ConnectException: <{}> is unable to connect: Server not found", Alphabet.encrypt(UserData.getUserName()));
							errorMessage = "Wow! That's unusual. The network may be down. " +
									"\nWait a few minutes and try again.";

						}
				}
			}
		}
		else {
			UserData.clear();
			errorMessage = " That email-password combination does not match. " +
					"If your user already exists here, try \"Forgot password\"." +
					" Or, try \"Join now\".";

		}
		FxNotify.notificationPurple("Ooops!", errorMessage, Pos.CENTER, 5,
				"image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getWindow());
		return false;
	}

	
	/**
	 * Sets the locale of the form.
	 *
	 * @param language The language identifier for the new locale. Either DE or EN.
	 */
	public void translate(String language) {
		switch (language) {
			case "EN":
				rbs.changeLocale(rbEN);
				break;
			case "DE":
				rbs.changeLocale(rbDE);
				break;
			default:
				throw new IllegalArgumentException("Not a valid locale");
		}
	}
	
	public FirstDescriptor getDataModel() {
		return this.descriptor;
	}
}
