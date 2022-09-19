package forms;

import authcrypt.Auth;
import authcrypt.UserData;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import fileops.CloudOps;
import flashmonkey.FlashMonkeyMain;
import fmexception.EmptyTokenException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.util.Locale;
import java.util.ResourceBundle;

public class AccountModel {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AccountModel.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(AccountModel.class);

      private Form formInstance;
      //private ResetDescriptor descriptor = new ResetDescriptor();
      private final StringProperty field1 = new SimpleStringProperty("");

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
            LOGGER.info("AccountModel createForm called");

            formInstance = Form.of(
                    Group.of(
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
            try {
                  if (validateLocal()) {
                        CloudOps.requestSubCancel(field1.get());
                  } else {
                        // set message that user does not exist,
                        String msg = "That didn't work.... \n If you continue to have problems, Try \nresetting your password.";
                        FxNotify.notificationDark("", " Hmmmm! " + msg, Pos.CENTER, 8,
                            "image/Flash_hmm_75.png", FlashMonkeyMain.getPrimaryWindow());

                        field1.setValue("");
                  }
            } catch (EmptyTokenException e) {
                  LOGGER.warn(e.getMessage());
            }

      }

      // validatorActionSwitch user information, if successful then return true
      // else we create a popup and return false.
      // returns true if not connected and pw username passes. If connected, returns
      // the response from Vert.x if the pw and username passes.
      // ifConnected & ifSuccessful, gets deckList from s3 and synchronizes with local list if exists.
      private boolean validateLocal() {
            LOGGER.info("validatorActionSwitch() called");

            Auth a = new Auth(field1.get(), UserData.getUserName().toLowerCase(), 0);
            return a.validateLocalOnly(field1.get(), UserData.getUserName().toLowerCase());
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


}
