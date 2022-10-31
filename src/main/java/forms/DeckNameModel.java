package forms;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import fileops.LinkObj;
import flashmonkey.CreateFlash;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import forms.utility.DeckNameDescriptor;
import media.sound.SoundEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

public class DeckNameModel {
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckNameModel.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(DeckNameModel.class);

      private Form formInstance;
      private DeckNameDescriptor descriptor;

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
            descriptor = new DeckNameDescriptor();
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.info("DeckNameModel createForm called");
            DeckNameDescriptor descriptor = new DeckNameDescriptor();
            formInstance = Form.of(
                    Group.of(
                        Field.ofStringType(descriptor.nameProperty())
                            .id("form-field")
                            .required("required_error_message")
                            .placeholder("deck_name")
                            .validate(StringLengthValidator.between(5, 50, "deckname_error_message"))
                    ))
                .i18n(rbs);
      }

      /**
       * validate name and if valid save, then show Create Pane.
       */
      public void formAction(FormData data) {
            /* not used. */
      }

      public void formAction() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            System.out.println("DeckNameModel.formAction called");
            // First check if form has correct values. THis
            // prevents the form from submitting when the user
            // presses the enter key as opposed to using the submit
            // button. Note does not work unless persist() is called
            // Will not work without this call!!! GRRRRRRR!!!!!!
            if (getFormInstance().isPersistable()) {
                  getFormInstance().persist();
                  deckSaveAction();
                  FlashMonkeyMain.showCreatePane();
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

      private void deckSaveAction() {
            System.out.println("deckSaveAction Called in DeckNameModel");
            // get newfile name fm field
            String fileName = descriptor.getName();

            String fName = FlashCardOps.getInstance().setDeckFileName(fileName);
            LinkObj lo = new LinkObj(fName);
            FlashCardOps.getInstance().getAgrList().setLinkObj(lo);

            descriptor.nameProperty().setValue("");
            formInstance = null;
            descriptor = null;
      }
}
