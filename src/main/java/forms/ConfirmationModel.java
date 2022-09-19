package forms;

import authcrypt.Auth;
import authcrypt.UserData;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.RegexValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import fileops.utility.Utility;
import flashmonkey.FlashMonkeyMain;
import forms.utility.CodeDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.util.Locale;
import java.util.ResourceBundle;

public class ConfirmationModel {

      private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationModel.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ConfirmationModel.class);


      private Form formInstance;
      private StringProperty code = new SimpleStringProperty("");
      private StringProperty field1 = new SimpleStringProperty("");
      private CodeDescriptor descriptor = new CodeDescriptor();

      /**
       * These are the resource bundles for german and english.
       */
      private final ResourceBundle rbDE = ResourceBundle.getBundle("demo-locale", new Locale("de", "CH"));
      private final ResourceBundle rbEN = ResourceBundle.getBundle("demo-locale", new Locale("en", "UK"));

      /**
       * Default locale is English. The {@code ResourceBundleService} is
       * initialised with it.
       */
      private final ResourceBundleService rbs = new ResourceBundleService(rbEN);

      /**
       * Creates or returns a form singleton instance.
       *
       * @return Returns the form instance.
       */
      public Form getFormInstance() {
            if (formInstance == null) {
                  createForm();
            }
            //LOGGER.setLevel(Level.DEBUG);
            return formInstance;
      }

      private void action() {
            if (Utility.isConnected()) {
                  String message = "You are not online. \nIf the password does not match your online account \nyou will not be able to synchronize with it." +
                      "\nYou may reset the password by clicking\n on \"reset password\" on the sign-in page.";
                  String emojiPath = "image/Flash_hmm_75.png";

                  FxNotify.notificationBlue("Ouch!", message, Pos.CENTER, 5,
                      emojiPath, FlashMonkeyMain.getPrimaryWindow());
            }
            createForm();
      }

      /**
       * Provides the form fields with validation and messaging to the user.
       */
      private void createForm() {
            LOGGER.info("createForm called");
            String[] pwCheck = new String[1];
            formInstance = Form.of(

                    Group.of(
                        Field.ofStringType(descriptor.getCodeProperty())
                            .id("form-field")
                            .required("use_code_error")
                            .validate(
                                StringLengthValidator.exactly(6, "use_code_error"),
                                RegexValidator.forPattern(".*[0-9A-Z].*", "use_code_error")),
                        Field.ofPasswordType(field1)
                            .placeholder("password_current")
                            .required("required_error_message")
                            .validate(StringLengthValidator.between(3, 40, "non_error_message"))
                    )).title("form_label")
                .i18n(rbs);
      }

      protected void formAction() {

            LOGGER.debug("formAction called");
            getFormInstance().persist();
            // if validation succeeds, send request to
            // vertx to create user.
            if (validateLocal()) {
                  Auth.finalizeUserRemote(descriptor.getCode(), field1.get());
            } else {
                  // TODO sign in failed message
                  String errorMessage = "There is a problem with your email-password combination. ";
                  notifyError(errorMessage);
                  UserData.clear();
            }
      }

      private boolean validateLocal() {
            LOGGER.debug("validateLocal called");
            // validate locally only
            Auth a = new Auth(field1.get(), UserData.getUserName().toLowerCase(), 'l');

            LOGGER.debug("returned state: {}, field1: {}, email aka userName: {}", a.authState(), field1.get(),
                UserData.getUserName().toLowerCase());
            return (a.authState() == 8675309);
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

      public CodeDescriptor getDataModel() {
            return this.descriptor;
      }

      public void onClose() {
            code = null;
            field1 = null;
            descriptor = null;
      }

      // **** HELPER METHODS **** //
      private void notifyError(String errorMessage) {
            FxNotify.notificationWarning("", " Ooops! " + errorMessage, Pos.CENTER, 8,
                "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getPrimaryWindow(), 15);
      }
}
