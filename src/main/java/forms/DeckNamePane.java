package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import flashmonkey.FlashMonkeyMain;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeckNamePane extends SimpleFormParentPane {
      private static final Logger LOGGER = LoggerFactory.getLogger(DeckNamePane.class);

      private DeckNameModel model;
      //private Hyperlink link;

      public DeckNamePane() {
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
            model = new DeckNameModel();
            displayForm = new FormRenderer(model.getFormInstance());
      }

      /**
       * This method is used to initialize all the properties of the form.
       */
      @Override
      public void initializeParts() {
            super.initializeParts();
            setFormTitle("DECK NAME");
            setMessageLabelStyle("white12");
            setMessageLabel("Name your deck. ",
                "Example: \"Calc 203 Estimating Limits Part 1\".");
      }

      @Override
      public void layoutParts() {
            super.layoutParts();
            LOGGER.info(" layoutParts called");
            mainGridPane.setPrefSize(360, 200);
            msgVBox.setAlignment(Pos.BASELINE_LEFT);
            actionButton.setMaxWidth(340);
            actionButton.setMinWidth(340);
            actionButton.setText("CREATE DECK");
      }

      @Override
      public void setupBindings() {
            LOGGER.info("setupBindings() called");
            actionButton.disableProperty().bind(model.getFormInstance().persistableProperty().not());
            displayForm.prefWidthProperty().bind(mainGridPane.prefWidthProperty());
      }

      /**
       * This method sets up the handling for all the button clicks.
       */
      @Override
      public void setupEventHandlers() {
            actionButton.setOnAction(e -> model.formAction());
            mainGridPane.setOnKeyPressed(f -> {
                  if (f.getCode() == KeyCode.ENTER) {
                        model.formAction();
                  }
            });
      }

      @Override
      public void paneAction() {
            /* stub */
      }
}
