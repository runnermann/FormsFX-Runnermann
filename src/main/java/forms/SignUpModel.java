package forms;

import authcrypt.Auth;
import authcrypt.UserData;
import authcrypt.user.EncryptedStud;
import campaign.db.DBInsert;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.CustomValidator;
import com.dlsc.formsfx.model.validators.RegexValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.model.validators.StringNumRangeValidator;
import com.dlsc.formsfx.view.controls.SimpleBooleanControl;
import com.dlsc.formsfx.view.controls.SimpleCheckBoxControl;
import com.dlsc.formsfx.view.util.ColSpan;
import fileops.utility.Utility;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.Timer;
import forms.utility.FirstDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class is used to create the form and holds all the necessary data. This
 * class acts as a singleton where the current instance is available using
 * {@code getInstance}.
 *
 * @author Lowell Stadelman
 */
public class SignUpModel {

      private static final Logger LOGGER = LoggerFactory.getLogger(SignUpModel.class);

      private Form formInstance;
      private StringProperty field1 = new SimpleStringProperty("");
      private StringProperty field2 = new SimpleStringProperty("");
      private FirstDescriptor descriptor = new FirstDescriptor();

      /**
       * These are the resource bundles for german and english.
       */
      private final ResourceBundle rbDE = ResourceBundle.getBundle("demo-locale", new Locale("de", "CH"));
      private final ResourceBundle rbEN = ResourceBundle.getBundle("demo-locale", new Locale("en", "US"));

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
            return formInstance;
      }

      private void action() {
//            if (Utility.isConnected()) {
//                  String message = "You are not online. \nIf the password does not match your online account \nyou will not be able to synchronize with it." +
//                      "\nYou may reset the password by clicking\n on \"reset password\" on the sign-in page.";
//                  String emojiPath = "image/Flash_hmm_75.png";
//
//                  FxNotify.notificationError("ouch!", message, Pos.CENTER, 5,
//                      emojiPath, FlashMonkeyMain.getPrimaryWindow());
//            }
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
                        Field.ofStringType(descriptor.siOrigEmailProperty())
                            .id("form-field")
                            .required("required_error_message")
                            .placeholder("email_placeholder")
                            .validate(RegexValidator.forEmail("email_error_message")),
                        Field.ofPasswordType(field1)
                            .placeholder("password_first")
                            .required("required_error_message")
                            .validate(
                                StringLengthValidator.between(8, 40, "pw_length_error_msg"),
                                RegexValidator.forPattern("[0-9A-Za-z!#$%&?@ ().~_\\[\\]\\{\\}`':;^+=].*", "illegal_char_error_msg"),
                                RegexValidator.forPattern(".*[A-Z].*", "no_uppercase_error_msg"),
                                RegexValidator.forPattern(".*[a-z].*", "no_lowercase_error_msg"),
                                RegexValidator.forPattern(".*[0-9].*", "no_numeric_error_msg"),
                                RegexValidator.forPattern(".*[!#$%&?@ ().~_\\[\\]\\{\\}`':;^+=].*", "no_specchars_error_msg"),
                                RegexValidator.forNotPattern(".*([0-9A-Za-z])\\1{2,}.*", "repeat_char_error_msg")
                            ),
                        Field.ofStringType("Please accept our EULA")
                                .multiline(true)
                                .editable(false),
                        Field.ofStringType("        ")
                                .editable(false)
                                .span(ColSpan.HALF),
                        Field.ofBooleanType(descriptor.getAcceptEULAProperty())
                             .required("required_eula_message")
                                .span(ColSpan.HALF)

                    )


                ).title("form_label")
                .i18n(rbs);
      }

      /**
       * Save user information or fail. If the user does not exist in the DB,
       * they are created and sent to step 2 in user creation, the set code pane.
       * If they exist in the DB, sends the user to fileSelectPane, otherwise
       * the lower methods fail and the user is returned to the create user pane.
       */
      protected void formAction() {
            // set user information for file system access
            LOGGER.info("formAction() values data.getName(): {} data.getEmail: {}",
                descriptor.getSiFirstName(), descriptor.getSiOrigEmail().toLowerCase());

            //LOGGER.debug("createS3 returns: {}", createS3.toString() );
            // attempt to create users authInfo
            if (formInstance.isPersistable()) {
                  getFormInstance().persist();
                  if (saveAction()) {
                        String message = "Your user is created. ";
                        String emojiPath = "image/flashFaces_sunglasses_60.png";

                        FxNotify.notificationNormal("Congrats!", message, Pos.CENTER, 5,
                                emojiPath, FlashMonkeyMain.getPrimaryWindow());

                        // set session data
                        LOGGER.info("formAction() User created, sending user to fileSelectPane");
                        // send user to fileSelectPane
                        FlashMonkeyMain.getFileSelectPane();
                        FlashMonkeyMain.setTopPane();
                  } else {
                        LOGGER.info("formAction() User creation failed ???");
                        getFormInstance().reset();
                        descriptor.siOrigEmailProperty().setValue("");
                        // clear PW
                        if(field1 != null) {
                              field1.setValue("");
                              // clear Email
                              field2.setValue("");
                        }
                  }
            }

            if (Utility.isConnected()) {
                  // used for the first time.
                  Timer.getClassInstance().setNote("p4, user clicked submit btn on SignUpPane, Attempted to create their profile.");
                  // EncryptedStudent is not used.
                  ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
                  Runnable task = () -> {
                        DBInsert.SESSION_NOTE.doInsert(new EncryptedStud());
                        scheduledExecutor.shutdown();
                  };
                  scheduledExecutor.execute(task);
            }
      }

      /**
       * Saves user data to file
       * Prints a message if successful or not.
       */
      protected boolean saveAction() {
            String email = descriptor.getSiOrigEmail().toLowerCase();
            UserData.setUserName(email);

            Auth a = new Auth(field1.get(), email);
            // Create userFile data
            return a.validatorActionSwitch(field1.get(), email, "signup");
      }

      public void onCLose() {
            field1 = null;
            field2 = null;
            descriptor = null;
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
