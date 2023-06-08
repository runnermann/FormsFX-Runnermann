package forms;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.CustomValidator;
import com.dlsc.formsfx.model.validators.RegexValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import fileops.CloudOps;
import flashmonkey.FlashMonkeyMain;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResetTwoModel {

    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ResetTwoModel.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetTwoModel.class);

    private Form formInstance;
    //private ResetDescriptor descriptor = new ResetDescriptor();
    private StringProperty field1 = new SimpleStringProperty("");
    private StringProperty field2 = new SimpleStringProperty("");

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


    public Form getFormInstance() {
        if (formInstance == null) {
            createForm();
        }
        //LOGGER.setLevel(Level.DEBUG);
        return formInstance;
    }

    /**
     * Provides the form fields with validation and messaging to the user.
     */
    public void createForm() {

        //LOGGER.setLevel(Level.DEBUG);
        LOGGER.info("ResetTwoModel createForm called");

        String[] pwCheck = new String[1];
        formInstance = Form.of(
                Group.of(
                        Field.ofStringType(SimpleFormParentPane.getResetDescriptor().resetCodeProperty())
                                .placeholder("reset_code")
                                .required("required_error_message")
                                .validate(
                                        StringLengthValidator.exactly(6, "use_code_error"),
                                        RegexValidator.forPattern(".*[0-9A-Z].*", "use_code_error")),
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
                )).title("form_label")
                .i18n(rbs);
    }

    /**
     * Save user information or fail . If the user has not been created before,
     * after success sends the user to fileSelectPane, otherwise fail.
     */

    public void formAction(FormData data) {

    }



    public void formAction() {
        // Will not work without this call!!! GRRRRRRR!!!!!!
        getFormInstance().persist();
        String email = SimpleFormParentPane.resetDescriptor.getEmailProperty().toLowerCase();
        int res = CloudOps.requestReset(email, field1.get(), SimpleFormParentPane.resetDescriptor.getResetCodeProperty());
        this.field1.setValue("");
        switch (res) {
            case -1: {
                String msg = "That didn't work.";
                FxNotify.notificationError("", " Hmmmm! " + msg, Pos.CENTER, 4,
                        "image/Flash_hmm_75.png", FlashMonkeyMain.getPrimaryWindow());
                SimpleFormParentPane.resetDescriptor.clear();
                this.clear();
                break;
            }
            case 0: {
                String msg = " Please check your connection to the internet";
                FxNotify.notificationError("", " Hmmmm! " + msg, Pos.CENTER, 4,
                        "image/flashFaces_smirking_75.png", FlashMonkeyMain.getPrimaryWindow());
//                FlashMonkeyMain.showResetTwoPane();
                SimpleFormParentPane.resetDescriptor.clear();
                this.clear();
                break;
            }
            case 1: {
                String msg = " That worked.";
                FxNotify.notificationNormal("", " Yay " + msg, Pos.CENTER, 4,
                        "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getPrimaryWindow());
                SimpleFormParentPane.resetDescriptor.clear();
                this.clear();
                FlashMonkeyMain.showSignInInnerPane();
                break;
            }
        }
    }



    /**
     * Saves metaData created by this form to file.
     * sends metaData to the cloud.
     * Prints a message if successful or not.
     * returns true if successful.
     */

    public boolean doAction(final FormData data) {
        return false;
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

    protected void clear() {
        this.field1.setValue("");
        this.field2.setValue("");
    }
}
