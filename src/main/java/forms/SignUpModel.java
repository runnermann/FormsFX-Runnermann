package forms;

import authcrypt.UserData;
import authcrypt.Verify;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.CustomValidator;
import com.dlsc.formsfx.model.validators.RegexValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.model.validators.StringNumRangeValidator;
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
public class SignUpModel {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SignUpModel.class);
	private StringProperty field1 = new SimpleStringProperty("");
	private StringProperty field2 = new SimpleStringProperty("");
	private FirstDescriptor descriptor = new FirstDescriptor();
	
	/**
	 * These are the resource bundles for german and english.
	 */
	private ResourceBundle rbDE = ResourceBundle.getBundle("demo-locale", new Locale("de", "CH"));
	private ResourceBundle rbEN = ResourceBundle.getBundle("demo-locale", new Locale("en", "UK"));
	
	/**
	 * Default locale is English. The {@code ResourceBundleService} is
	 * initialised with it.
	 */
	private ResourceBundleService rbs = new ResourceBundleService(rbEN);
	
	private Form formInstance;
	
	/**
	 * Creates or returns a form singleton instance.
	 * @return Returns the form instance.
	 */
	public Form getFormInstance() {
		
		//System.out.println("*** getFormInstance called ***");
		
		if (formInstance == null) {
			createForm();
		}
		return formInstance;
	}
	
	/**
	 * Provides the form fields with validation and messaging to the user.
	 */
	private void createForm() {
		
		LOGGER.info("createForm called");
		String[] pwCheck = new String[1];
		
		formInstance = Form.of(
			
			Group.of(
					Field.ofStringType(descriptor.siFirstNameProperty())
							.id("form-field")
							.required("required_error_message")
							.placeholder("name_placeholder")
							.validate(StringLengthValidator.between(3, 40,"name_error_message")),
					Field.ofStringType(descriptor.siOrigEmailProperty())
							.id("form-field")
							.required("required_error_message")
							.placeholder("email_placeholder")
							.validate(RegexValidator.forEmail("email_error_message")),
					Field.ofStringType(descriptor.siAgeProperty())
							.id("form-field")
							.required("required_error_message")
							.placeholder("age_placeholder")
							.validate( StringNumRangeValidator.between(1, 120, "omg_age_error")),
					Field.ofPasswordType(field1)
							.placeholder("password_first")
							.required("required_error_message")
							.validate(
									StringLengthValidator.between(8, 40, "pw_length_error_msg"),
									RegexValidator.forPattern("[0-9A-Za-z!#$%&?@]*", "illegal_char_error_msg"),
									RegexValidator.forPattern(".*[A-Z].*", "no_uppercase_error_msg"),
									RegexValidator.forPattern(".*[a-z].*", "no_lowercase_error_msg"),
									RegexValidator.forPattern(".*[0-9].*", "no_numeric_error_msg"),
									RegexValidator.forPattern(".*[!#$%&?@].*", "no_specchars_error_msg"),
									RegexValidator.forNotPattern(".*([0-9A-Za-z])\\1{2,}.*", "repeat_char_error_msg"),
									// only way to get the value from the field for confirmation with second pw field
									CustomValidator.forPredicate(e -> {
										pwCheck[0] = e;
										return true;
									}, "pw_match_error_msg")
							),
					Field.ofPasswordType(field2)
							.placeholder("password_second")
							.required("required_error_message")
							.validate(
									CustomValidator.forPredicate(e -> {
										//LOGGER.info(" pw1: {} matches pw2: {} is: {}", pwCheck[0], e, pwCheck[0].equals(e));
										return pwCheck[0].equals(e);
									}, "pw_match_error_msg"))
			)
		).title("form_label")
				.i18n(rbs);
	}
	
	/**
	 * Save user information or fail . If the user has not been created before,
	 * after success sends the user to fileSelectPane, otherwise fail.
	 */
	protected void formAction() {
		getFormInstance().persist();
		// set user information for file system access
		LOGGER.info("formAction() values data.getName(): {} data.getEmail: {}",
				descriptor.getSiFirstName(), descriptor.getSiOrigEmail());

		UserData.setFirstName(descriptor.getSiFirstName());
		UserData.setUserName(descriptor.getSiOrigEmail().toLowerCase());

		//S3Creds creds = new S3Creds(descriptor.getSiOrigEmail(), field2.getValue());
		//S3Create createS3 = new S3Create(descriptor.getSiOrigEmail(), field2.getValue());

		//LOGGER.debug("createS3 returns: {}", createS3.toString() );
		// attempt to create users authInfo
		if(saveAction())  {
			// set session data
			LOGGER.info("formAction() User created, sending user to fileSelectPane");
			// send user to fileSelectPane
			FlashMonkeyMain.getFileSelectPane();
			FlashMonkeyMain.setTopPane();
		} else {
			
			LOGGER.info("formAction() User creation failed ???");
			
			getFormInstance().reset();
			descriptor.siOrigEmailProperty().setValue("");
			field1.setValue("");
			field2.setValue("");
		}
	}

	/**
	 * Saves user data to file
	 * Prints a message if successful or not.
	 */
	protected boolean saveAction() {
		
		Verify v = new Verify();

		//LOGGER.info("In save action and pw is: " + field1.get() + " getUserName is: " + data.getEmail());

		// Create userFile data
		String msg = v.newUser(field1.get(), descriptor.getSiOrigEmail());
		LOGGER.info("saveAction() create newUser : {}",msg);
		if(msg.contains("Success")) {
			UserData.setUserName(descriptor.getSiOrigEmail());
			LOGGER.info("success: " + msg);
			return true;
		} else {
			// Prevent bug issue #0001
			UserData.clear();
			FxNotify.notificationPurple("", " Ooops! " + msg, Pos.CENTER, 20,
					"image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getWindow());
			return false;
		}
	}
	
	/**
	 * Sets the locale of the form.
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
