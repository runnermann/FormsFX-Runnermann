package forms;

import authcrypt.Auth;
import authcrypt.UserData;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.StringLengthValidator;

import flashmonkey.FlashMonkeyMain;
import forms.utility.FirstDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import media.sound.SoundEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SignInModel.class);
      private final StringProperty field1 = new SimpleStringProperty("");
      private final FirstDescriptor descriptor = new FirstDescriptor();

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

      private Form formInstance;

      /**
       * Creates or simply returns to form singleton instance.
       *
       * @return Returns the form instance.
       */
      public Form getFormInstance() {
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
                            .required("email_placeholder")
                            .placeholder("email_placeholder")
                            .validate(StringLengthValidator.between(6, 40, "email_error_message")),
                        Field.ofPasswordType(field1)
                            .placeholder("password_first")
                            .required("password_first")
                            .validate(StringLengthValidator.between(3, 40, "non_error_message"))
                    )
                ).title("form_label")
                .i18n(rbs);
      }


      protected void formAction() {
            getFormInstance().persist();
            UserData.setUserName(descriptor.getSiOrigEmail().toLowerCase());

            LOGGER.debug("formAction() userName from form: {}", descriptor.getSiOrigEmail().toLowerCase());

            if (validate()) {
                  SoundEffects.ACCESS_GRANTED.play();

                  FlashMonkeyMain.setLoggedinToTrue();
                  UserData.setFirstName(descriptor.getSiFirstName());
                  FlashMonkeyMain.getFileSelectPane();
                  FlashMonkeyMain.setTopPane();

            } else {
                  // set message that user does not exist,
                  // and send to create new user form
                  getFormInstance().changedProperty().setValue(false);
                  getFormInstance().reset();
                  field1.setValue("");
            }
      }

      // validatorActionSwitch user information, if successful then return true
      // else we create a popup and return false.
      // returns true if not connected and pw username passes. If connected, returns
      // the response from Vert.x if the pw and username passes.
      // ifConnected & ifSuccessful, gets deckList from s3 and synchronizes with local list if exists.
      private boolean validate() {
            LOGGER.info("validatorActionSwitch() called");
            // We interface with s3resources underneath to prevent storing unencrypted passwords.
            Auth a = new Auth(field1.get(), descriptor.getSiOrigEmail().toLowerCase());
            // validatorActionSwitch, the forms input.
            return a.validatorActionSwitch(field1.get(), descriptor.getSiOrigEmail().toLowerCase(), "signin");
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
