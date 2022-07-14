package forms;

import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.util.ResourceBundleService;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class ModelParent implements FormModel {

      /**
       * These are the resource bundles for german and english.
       */
      //protected ResourceBundle rbDE = ResourceBundle.getBundle("demo-locale", new Locale("de", "CH"));
      protected ResourceBundle rbEN = ResourceBundle.getBundle("demo-locale", new Locale("en", "US"));
      //private ResourceBundle rbSP = ResourceBundle.getBundle("demo-locale", new Locale("sp", "SP"));}

      /**
       * Default locale is English. The {@code ResourceBundleService} is
       * initialised with it.
       */
      protected ResourceBundleService rbs = new ResourceBundleService(rbEN);

      protected Form formInstance;

      /**
       * Creates or returns a form singleton instance.
       *
       * @return Returns the form instance.
       */
      @Override
      public Form getFormInstance() {
            if (formInstance == null) {
                  createForm();
            }
            return formInstance;
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
}
