package forms;


import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import forms.utility.ProfessorDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p> On distributed systems where data may remain on public
 * * information systems such as computers in a university library.
 * * We do not leave the users personal information exposed. </p>
 * * <pre>
 *  *     -1 Data is created on a system, and is available to be created
 *  *     on any system with the application.
 *  *     -2 Data is encrypted and uploaded to the DB.
 *  *     -3 Personal Data is removed from the local system.
 *  *     -4 To edit personal data, Data is downloaded from the
 *  *     DB to the local system, decrytped, and placed in the from
 *  *     for the user to modify.
 *  *     -5 Information such as the SSN is not exposed in the form nor
 *  *     downloaded.
 *  * </pre>
 */
public class ProfessorModel implements FormModel {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ProfessorModel.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorModel.class);

      private final ProfessorDescriptor descriptor = new ProfessorDescriptor();

      /**
       * These are the resource bundles for german and english.
       */
      private final ResourceBundle rbDE = ResourceBundle.getBundle("demo-locale", new Locale("de", "CH"));
      private final ResourceBundle rbEN = ResourceBundle.getBundle("demo-locale", new Locale("en", "UK"));
      //private ResourceBundle rbSP = ResourceBundle.getBundle("demo-locale", new Locale("sp", "SP"));

      /**
       * Default locale is English. The {@code ResourceBundleService} is
       * initialised with it.
       */
      private final ResourceBundleService rbs = new ResourceBundleService(rbEN);

      private Form formInstance;

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

      /**
       * Provides the form fields with validation and messaging to the user.
       */
      public void createForm() {

      }

      @Override
      public void formAction(FormData data) {

      }

      @Override
      public void formAction() {

      }

      /**
       * Saves metaData created by this form to file.
       * sends metaData to the cloud.
       * Prints a message if successful or not.
       * returns true if successful.
       */
      @Override
      public boolean doAction(final FormData data) {
            return false;
      }

      /**
       * Sets the locale of the form.
       *
       * @param language The language identifier for the new locale. Either DE or EN.
       */
      @Override
      public void translate(String language) {
            switch (language) {
                  case "EN":
                        rbs.changeLocale(rbEN);
                        break;
                  case "DE":
                        //rbs.changeLocale(rbDE);
                        break;
                  default:
                        throw new IllegalArgumentException("Not a valid locale");
            }
      }

      @Override
      public ProfessorDescriptor getDescriptor() {
            return descriptor;
      }
}
