package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ViewMixin;

import flashmonkey.FlashMonkeyMain;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * css uses buttons.css and
 */
public class SignInPane extends Pane implements ViewMixin {

      //private static final Logger LOGGER = LoggerFactory.getLogger(SignInPane.class);

      private GridPane mainGridPane;
      private Pane spacer;
      private HBox newActHBox;
      private HBox forgotHBox;
      private HBox signInHBox;
      private static VBox msgVBox;
      private Button actionButton;
      private Hyperlink signUpLink;
      private Hyperlink resetLink;

      // FxForm related
      private FormRenderer displayForm;
      private final SignInModel model;

      public SignInPane(SignInModel signInModel) {
            this.model = signInModel;
            init();
      }

      /**
       * This method is used to set up the stylesheets.
       */
      @Override
      public void initializeSelf() {
            //getStylesheets().add(getClass().getResource("/css/fxformStyle.css").toExternalForm());
      }

      /**
       * This method initializes all nodes and regions.
       */
      @Override
      public void initializeParts() {
            mainGridPane = new GridPane();
            spacer = new Pane();
            newActHBox = new HBox();
            forgotHBox = new HBox();
            msgVBox = new VBox();
            signInHBox = new HBox();
            actionButton = new Button("SIGN IN");
            signUpLink = new Hyperlink("Join now");
            resetLink = new Hyperlink("Reset password?");

            displayForm = new FormRenderer(model.getFormInstance());
      }

      /**
       * This method sets up the necessary bindings for the logic of the
       * application.
       */
      @Override
      public void setupBindings() {
            actionButton.disableProperty().bind(model.getFormInstance().persistableProperty().not());
            displayForm.prefWidthProperty().bind(mainGridPane.prefWidthProperty());
      }

      /**
       * This method sets up listeners and sets the text of the state change
       * labels.
       */
      @Override
      public void setupValueChangedListeners() { /* empty */ }

      /**
       * This method sets up the handling for all the button clicks.
       */
      @Override
      public void setupEventHandlers() {

            actionButton.setOnAction(e -> {
                  model.formAction();
            });

            resetLink.setOnAction(e -> {
                  FlashMonkeyMain.showResetOnePane();
            });

            signUpLink.setOnAction(e -> {
                  FlashMonkeyMain.showSignUpInnerPane();
            });

      }

      /**
       * This method is used to layout the nodes and regions properly.
       */
      @Override
      public void layoutParts() {
            mainGridPane.setAlignment(Pos.CENTER);
            mainGridPane.setHgap(10);
            mainGridPane.setVgap(12);
            mainGridPane.setId("opaqueMenuPaneDark");
            mainGridPane.setPrefSize(340, 400);
            mainGridPane.setOnKeyPressed(f -> {
                  if (f.getCode() == KeyCode.ENTER) {
                        model.formAction();
                  }
            });

            spacer.setPrefHeight(20);

            Label formTitle = new Label("SIGN IN");
            formTitle.setId("label24WhiteSP");
            Label signUpLabel = new Label("New to FlashMonkey?");
            signUpLabel.setId("signUpLabel");
            //signUpLabel.setStyle("-fx-font-size: 12");

            actionButton.setMaxWidth(240);
            actionButton.setMinWidth(240);
            actionButton.setId("signInButton");

            signUpLink.setId("signInHyp");
            resetLink.setId("signInHyp");

            msgVBox.setAlignment(Pos.CENTER);
            forgotHBox.setAlignment(Pos.CENTER);
            forgotHBox.getChildren().add(resetLink);

            msgVBox.getChildren().add(formTitle);
            newActHBox.getChildren().addAll(signUpLabel, signUpLink);
            newActHBox.setAlignment(Pos.CENTER);

            signInHBox.setAlignment(Pos.CENTER);
            signInHBox.getChildren().add(actionButton);

            mainGridPane.addRow(0, msgVBox);
            mainGridPane.addRow(1, spacer);
            mainGridPane.addRow(2, displayForm);
            mainGridPane.addRow(3, signInHBox);
            mainGridPane.addRow(4, forgotHBox);
            mainGridPane.addRow(5, newActHBox);
      }


      public GridPane getMainGridPane() {
            return this.mainGridPane;
      }

      /**
       * Uses A working replacement for Label.wrapText() method.
       * Called by model sets errorMsg in msgVBox
       *
       * @param msg
       */
      public static void setErrorMsg(String msg) {
            msgVBox.getChildren().clear();
            int lineLength = 30;
            FormsUtility.setErrorMsg(msg, msgVBox, lineLength);
      }
}
