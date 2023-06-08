package forms;

import authcrypt.user.EncryptedStud;
import campaign.db.DBInsert;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ViewMixin;

import fileops.VertxLink;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.Timer;
import forms.utility.FirstDescriptor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import media.sound.SoundEffects;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class SignUpPane extends Pane implements ViewMixin {
      //private static final Logger LOGGER = LoggerFactory.getLogger(SignUpPane.class);
      // NOTE: EntryFields are created in FormModel
      private GridPane signUpPane;
      private Pane spacer;
      private Pane spacer1;
      private HBox signInHBox;
      private HBox signUpHBox;
      private static VBox msgVBox;
      private Label msgLabel1;
      private Label msgLabel2;
      private Label msgLabel3;
      // Action related
      private Button signUpBtn;
      private Hyperlink signInLink;
      private Hyperlink eulaLink;
      //FxForm related
      private FormRenderer displayForm;
      private final SignUpModel model;


      public SignUpPane(SignUpModel signUpModel) {
            this.model = signUpModel;
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
            spacer = new Pane();
            spacer1 = new Pane();
            signInHBox = new HBox();
            signUpHBox = new HBox();
            msgVBox = new VBox();
            // Action related
            signUpBtn = new Button("SIGN UP");
            signInLink = new Hyperlink("SIGN IN");
            eulaLink = new Hyperlink("View EULA Agreement");
            signUpPane = new GridPane();

            msgLabel1 = new Label("I just need ");
            msgLabel2 = new Label("a bit of information");
            msgLabel3 = new Label("to get started.");

            displayForm = new FormRenderer(model.getFormInstance());

            // used for the first time.
            Timer.getClassInstance().setNote("p2 SignUpPane, user signing up");
            // EncryptedStudent is not used.
            ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                  DBInsert.SESSION_NOTE.doInsert(new EncryptedStud());
                  scheduledExecutor.shutdown();
            };
            scheduledExecutor.execute(task);
      }

      /**
       * This method sets up the necessary bindings for the logic of the
       * application.
       */
      @Override
      public void setupBindings() {
            signUpBtn.disableProperty().bind(model.getFormInstance().persistableProperty().not());
            displayForm.prefWidthProperty().bind(signUpPane.prefWidthProperty());
      }

      /**
       * This method sets up listeners and sets the text of the state change
       * labels.
       */
      @Override
      public void setupValueChangedListeners() {
            /* stub */
      }

      /**
       * Button Actions
       */
      @Override
      public void setupEventHandlers() {
            signUpBtn.setOnAction(e -> {
                  model.formAction();
            });

            signInLink.setOnAction(e -> {
                  FlashMonkeyMain.showSignInInnerPane();
            });

            eulaLink.setOnAction(e -> {
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  FlashMonkeyMain.getWebView(VertxLink.EULA_POLICY.getLink());
            });
      }


      /**
       * This method is used to layout the nodes and regions properly.
       */
      @Override
      public void layoutParts() {
            signUpPane.setAlignment(Pos.CENTER);
            signUpPane.setHgap(10);
            signUpPane.setVgap(12);
            signUpPane.setId("opaqueMenuPaneDark");
            signUpPane.setPrefSize(340, 420);
            signUpPane.setOnKeyPressed(f -> {
                  if (f.getCode() == KeyCode.ENTER) {
                        model.formAction();
                  }
            });

            msgLabel1.setId("label24White");
            msgLabel2.setId("label24White");
            msgLabel3.setId("label24White");
            Label signInLabel = new Label("Already have an account?");
            signInLabel.setId("signUpLabel");

            signUpBtn.setMaxWidth(240);
            signUpBtn.setMinWidth(240);
            signUpBtn.setId("signInButton");

            signInLink.setId("signInHyp");

            eulaLink.setId("signInHyp");
            eulaLink.setAlignment(Pos.CENTER);
            HBox eulaBox = new HBox(eulaLink);
            eulaBox.setAlignment(Pos.CENTER);
            eulaBox.setPadding(new Insets( -20, 0, 0, 0));

            msgVBox.setAlignment(Pos.CENTER);
            signUpHBox.setAlignment(Pos.CENTER);
            signUpHBox.getChildren().add(signUpBtn);

            msgVBox.getChildren().addAll(msgLabel1, msgLabel2, msgLabel3);
            signInHBox.getChildren().addAll(signInLabel, signInLink);
            signInHBox.setAlignment(Pos.CENTER);

            spacer.setMinHeight(20);
            spacer1.setMinHeight(20);

            displayForm.setMaxWidth(280);

            signUpPane.addRow(0, spacer);
            signUpPane.addRow(1, msgVBox);
            signUpPane.addRow(2, displayForm);
            signUpPane.addRow(3, eulaBox);
            signUpPane.addRow(4, signUpHBox);
            signUpPane.addRow(5, signInHBox);
            //signUpPane.addRow(5, spacer1);
      }

      public GridPane getSignUpPane() {
            return this.signUpPane;
      }


      /**
       * TESTING
       **/

//      @FMAnnotations.DoNotDeployMethod
//      public Point2D getSignUpPaneMinXY() {
//            Bounds bounds = displayForm.getLayoutBounds();
//            return displayForm.localToScreen(bounds.getMinX(), bounds.getMinY());
//      }

}
