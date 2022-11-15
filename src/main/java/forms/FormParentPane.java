package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ViewMixin;
import com.sun.glass.ui.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import uicontrols.SceneCntl;

/**
 * The parent class for form panes
 */
public abstract class FormParentPane extends Pane implements ViewMixin {

      protected ScrollPane scrollPane = new ScrollPane();
      protected VBox mainVBox;
      protected Pane spacer;
      protected Pane spacer1;
      protected HBox buttonHBox;
      private static VBox msgVBox;
      protected GridPane innerGPane;
      protected FormModel model;
      protected FormData data;

      // Action related
      protected Button submitButton;
      // FxForm related
      protected FormRenderer formRenderer;


      /**
       * <pre>
       *     Order of initialization for ViewMixin.init()
       *         initializeSelf();
       *         initializeParts();
       *         layoutParts();
       *         setupEventHandlers();
       *         setupValueChangedListeners();
       *         setupBindings();
       * </pre>
       * <p>the use of init() should be called from the child class.?.</p>
       */
      protected FormParentPane() {
            init();
      }

      /**
       * Abstract call. Implement specific actions that should occur
       * (in the pane, not the form) when the submit button is
       * clicked. Any changes in the pane that need to be made
       * will be called by this method.
       */
      public abstract void paneAction();

      /**
       * Returns the FormPane from the child class.
       *
       * @return
       */
      public abstract ScrollPane getMainPane();

      //@Override
      public void initialize(FormModel model, FormData data) {
 //           scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            spacer = new Pane();
            spacer1 = new Pane();
            buttonHBox = new HBox();
            msgVBox = new VBox();
            // Action related
            submitButton = new Button("SUBMIT");
            mainVBox = new VBox(10);
            innerGPane = new GridPane();
            this.model = model;
            this.data = data;
      }

      /**
       * This method sets up the necessary bindings for the logic of the
       * application.
       */
      @Override
      public void setupBindings() {
            submitButton.disableProperty().bind(model.getFormInstance().persistableProperty().not());
            formRenderer.prefWidthProperty().bind(mainVBox.prefWidthProperty());
      }

      /**
       * Button Actions
       */
      @Override
      public void setupEventHandlers() {
            model.getFormInstance().persistableProperty().getValue();
            submitButton.setOnAction(e -> {
                  this.paneAction();
                  model.formAction(data);
            });
      }

      @Override
      public void layoutParts() {
            formRenderer.setMaxWidth(SceneCntl.getAppBox().getWd() - 10);
            // Card info
            innerGPane = new GridPane();
            innerGPane.setHgap(6);
            innerGPane.setVgap(10);
            innerGPane.setAlignment(Pos.CENTER);
            innerGPane.setPadding(new Insets(4, 2, 4, 2));

            mainVBox.setAlignment(Pos.CENTER);
            double scale = Screen.getMainScreen().getPlatformScaleY();
            if( scale != 1.0) {
                  double ht = Screen.getMainScreen().getVisibleHeight() * (int) (scale / 2);
                  // leave some room
                  ht -= 40;
                  if(ht > 800) {
                        ht = 800;
                  }
                  mainVBox.setPrefSize(500, ht);
            } else {
                  mainVBox.setPrefSize(500, 800);
            }

            innerGPane.setId("infoPane");
            formRenderer.setId("formPane");
            mainVBox.setId("infoPane");

            submitButton.setMaxWidth(300);
            submitButton.setMinWidth(300);
            submitButton.setId("signInButton");

            buttonHBox.setAlignment(Pos.CENTER);
            buttonHBox.getChildren().add(submitButton);
            spacer.setMinHeight(20);
            spacer1.setMinHeight(20);

            mainVBox.getChildren().addAll(spacer, innerGPane, formRenderer, buttonHBox, spacer1);
            scrollPane.setContent(mainVBox);
      }

      public void setFormPaneWidth(int w) {
            mainVBox.setMaxWidth(w);
            mainVBox.setMinWidth(w);
      }

      public void setFormPainHeight(int ht) {
            mainVBox.setMaxHeight(ht);
            mainVBox.setMinHeight(ht);
      }

      /**
       * Sets the infoPane message above the form. Label
       * is the primary label, Message is the message.
       * img is inserted into the image on the right.
       *
       * @param lbl
       * @param msg
       * @param imageFullPath the relitive path to the image and name.
       */
      protected void setInfoPane(String lbl, String msg, String imageFullPath) {
            Label l = new Label(lbl);
            l.setId("infoPane-label");
            Label m = new Label(msg);
            m.setId("infoPane-message");
            // column, row, col-span, row-span
            innerGPane.add(l, 0, 0, 1, 1);
            innerGPane.add(m, 0, 1, 1, 1);
            if(! imageFullPath.isEmpty()) {
                  ImageView img = new ImageView(imageFullPath);
                  img.setPreserveRatio(true);
                  img.setFitHeight(100);
                  innerGPane.add(img, 1, 0, 1, 2);
            }
      }

      public void setSubmitButtonTitle(String title) {
            submitButton.setText(title);
      }

      public void addExitBox(GridPane exitBox) {
            Region space = new Region();
            space.setPrefHeight(140);
            mainVBox.getChildren().addAll(space, exitBox);
            //mainVBox.addRow(7, exitBox);
      }
}
