package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import flashmonkey.FlashMonkeyMain;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the container class with pane for the second of two pw reset forms.
 * This class contains the fields needed to authenticate the user and
 * provide the new password.
 */
public class ResetTwoPane extends SimpleFormParentPane {

      private static final Logger LOGGER = LoggerFactory.getLogger(ResetTwoPane.class);

      private ResetTwoModel model;
      private Hyperlink signInLink;


      public ResetTwoPane() {
            super();
            super.initializeSelf();
      }


      /**
       * Returns the FormPane from the child class.
       *
       * @return
       */
      @Override
      public GridPane getMainGridPain() {
            return mainGridPane;
      }


      /**
       * This method is called by Forms api ViewMixin
       */
      @Override
      public void initializeSelf() {
            model = new ResetTwoModel();
            displayForm = new FormRenderer(model.getFormInstance());
      }


      /**
       * This method is used to initializes all the properties of a class.
       */
      @Override
      public void initializeParts() {
            super.initializeParts();
            setFormTitle("PASSWORD RESET");
            setMessageLabelStyle("white14");
            setMessageLabel("Use the code sent to your email", "and enter a new password.");
            signInLink = new Hyperlink("back to sign-in");
            signInLink.setId("signInHyp");
            setLower2HBox(signInLink);
      }

      @Override
      public void layoutParts() {
            super.layoutParts();
            LOGGER.info(" layoutParts called");
      }

      @Override
      public void setupBindings() {
            LOGGER.info("setupBindings() called");
            actionButton.disableProperty().bind(model.getFormInstance().persistableProperty().not());
            //reset.disableProperty().bind(model.getFormInstance().changedProperty().not());
            displayForm.prefWidthProperty().bind(mainGridPane.prefWidthProperty());
      }

      /**
       * This method sets up the handling for all the button clicks.
       */
      @Override
      public void setupEventHandlers() {
            signInLink.setOnAction(e -> {
                  SimpleFormParentPane.resetDescriptor.clear();
                  FlashMonkeyMain.showSignInInnerPane();
            });
            actionButton.setOnAction(e -> model.formAction());
            mainGridPane.setOnKeyPressed(f -> {
                  if (f.getCode() == KeyCode.ENTER) {
                        model.formAction();
                  }
            });
      }

      /**
       * Implement specific actions that should occur
       * (in the pane, not the form) when the the submit button is
       * clicked. Any changes in the pane that need to be made
       * will be called by this method.
       */
      @Override
      public void paneAction() {
            /* stub */
      }


}
