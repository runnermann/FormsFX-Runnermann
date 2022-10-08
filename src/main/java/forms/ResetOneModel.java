package forms;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.RegexValidator;
import fileops.CloudOps;
import flashmonkey.FlashMonkeyMain;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResetOneModel {

    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ResetOneModel.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(DeckMetaModel.class);

    private Form formInstance;
    //private StringProperty email = new SimpleStringProperty("");
    //private ResetDescriptor descriptor = new ResetDescriptor();

    /**
     * These are the resource bundles for german and english.
     */
      private final ResourceBundle rbDE = ResourceBundle.getBundle("demo-locale", new Locale("de", "CH"));
      private final ResourceBundle rbEN = ResourceBundle.getBundle("demo-locale", new Locale("en", "UK"));

    /**
     * The default locale is English, thus the {@code ResourceBundleService} is
     * initialised with it.
     */
      private final ResourceBundleService rbs = new ResourceBundleService(rbEN);


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
        LOGGER.info("DeckMetaModel createForm called");

        formInstance = Form.of(
                Group.of(
                        Field.ofStringType(SimpleFormParentPane.getResetDescriptor().emailEntryProperty())
                                .id("form-field")
                                .required("required_error_message")
                                .placeholder("email_placeholder")
                                .validate(RegexValidator.forEmail("email_error_message"))
                ))
                .title("form_label")
                .i18n(rbs);
    }


    /**
     * Save user information or fail . If the user has not been created before,
     * after success sends the user to fileSelectPane, otherwise fail.
     */
    public void formAction(FormData data) {
        /* not used. */
    }


    public void formAction() {
        // Will not work without this call!!! GRRRRRRR!!!!!!
        getFormInstance().persist();
        int  res = CloudOps.requestResetCode(SimpleFormParentPane.getResetDescriptor().getEmailProperty().toLowerCase());

        switch (res) {
            case -1: {
                String msg = "That didn't work. Is your email correct?";
                FxNotify.notification("", " Hmmmm! " + msg, Pos.CENTER, 8,
                        "image/Flash_hmm_75.png", FlashMonkeyMain.getPrimaryWindow());
                break;
            }
            case 0: {
                String msg = " Bad connection. Check to make sure your online and try again.";
                FxNotify.notification("", " Ouch! " + msg, Pos.CENTER, 8,
                        "image/flashFaces_smirking_75.png", FlashMonkeyMain.getPrimaryWindow());
                break;
            }
            case 1: {
                FlashMonkeyMain.showResetTwoPane();
                break;
            }
        }
    }


    /**
     * Not used
     * @param data
     * @return
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
}
