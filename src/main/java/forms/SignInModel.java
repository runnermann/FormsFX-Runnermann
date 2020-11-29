package forms;

import authcrypt.UserData;
import authcrypt.Verify;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import fileops.Utility;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import forms.utility.FirstDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import org.slf4j.Logger;
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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SignInModel.class);
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
		LOGGER.info("*** getFormInstance called ***");
		// reset field1
//		field1 = new SimpleStringProperty("");
		if (formInstance == null) {
			createForm();
		}
		return formInstance;
	}
	
	/**
	 * Creates a new form instance with the required information.
	 */
	private void createForm() {
		LOGGER.info("createForm called");
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
	private boolean validate() {
		LOGGER.info("validate() called");
		// validate the forms input
		Verify v = new Verify(field1.get(), descriptor.getSiOrigEmail().toLowerCase());
		//System.out.println(" didSucceed: " + v.succeeded());
		if(v.succeeded() == 8675309){
			// We interface with s3resources here in order to prevent saving passwords
			// in memory.
			if(Utility.isConnected()) { FlashCardOps.getInstance().setObjsFmS3(descriptor.getSiOrigEmail(), field1.get()); }
			return true;
		}
		else {
			// prevent bug issue #0001
			UserData.clear();
			String message = " That email-password combination does not match. " +
					"If your user already exists here, try \"Forgot password\"." +
					" Or, try \"Join now\".";
			FxNotify.notificationPurple("Ooops!", message, Pos.CENTER, 10,
					"image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getWindow());
			return false;
		}
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
