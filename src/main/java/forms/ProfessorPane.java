package forms;

import authcrypt.user.EncryptedProf;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import fmannotations.FMAnnotations;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays users data. 1) Ensure this form is not displayed unless the user
 * has passed login. 2) Ensure users data is not over-writen unless the user
 * has passed login. 3) Ensure users data is not available unless the user
 * has passed login.
 */
public class ProfessorPane extends FormParentPane {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ProfessorPane.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorPane.class);

      ProfessorModel model;
      EncryptedProf prof;

      public ProfessorPane() {
            LOGGER.info("ProfessorPane called");
            init();
      }

      @Override
      public void initializeSelf() {
            model = new ProfessorModel();
            prof = new EncryptedProf();
            this.initialize(model, prof);

            // get data from DB if exists

      }

      /**
       * This method initializes all nodes and regions.
       */
      @Override
      public void initializeParts() {

            super.formRenderer = new FormRenderer(model.getFormInstance());
      }

      @Override
      public void setupValueChangedListeners() {
		/* stub */
      }

      @Override
      public void layoutParts() {
            super.layoutParts();
            LOGGER.info(" layoutParts called");

            // innerGPane.addRow(0, idkPane) // not used

      }

      @Override
	public ScrollPane getMainPane() {
		return this.scrollPane;
      }

      @Override
      public void paneAction() { /* do nothing */}

      /* *** FOR TESTING *** */
      @FMAnnotations.DoNotDeployMethod
      public ProfessorModel getModel() {
            return this.model;
      }
}
