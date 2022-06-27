package forms;

import authcrypt.user.EncryptedStud;
import campaign.db.DBInsert;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ViewMixin;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.Timer;
import fmannotations.FMAnnotations;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.UIColors;


public class ConfirmationPane extends Pane implements ViewMixin {

      private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationPane.class);

      private GridPane confirmPane;
      private Pane spacer;
      private Pane spacer1;
      private HBox buttonHBox;
      private HBox linkHBox;
      private VBox msgVBox;

      // labels and text
      private Label label;
      private Label label1;
      private Text info;
      private final String str = "You should receive an email from FlashMonkey " +
          "that provides a code. Enter the code below and press continue." +
          "If you don't see the email, be sure to check your SPAM folder.";

      // Action related
      private Button submitButton;
      private Hyperlink signInLink;

      // FX related
      private final ConfirmationModel model;
      private FormRenderer displayForm;


      public ConfirmationPane(ConfirmationModel model) {
            LOGGER.info("ConfirmationPane called. ");
            this.model = model;
            init();
      }

      /**
       * This method is used to set up the stylesheets.
       */
      @Override
      public void initializeSelf() {
            getStylesheets().add(getClass().getResource("/css/fxformStyle.css").toExternalForm());
      }


      /**
       * This method is used to initializes all the properties of a class.
       */
      @Override
      public void initializeParts() {
            buttonHBox = new HBox();
            linkHBox = new HBox();
            msgVBox = new VBox();
            info = new Text(str);

            submitButton = new Button("CONTINUE");
            signInLink = new Hyperlink("go back");

            spacer = new Pane();
            spacer1 = new Pane();
            confirmPane = new GridPane();
            displayForm = new FormRenderer(model.getFormInstance());

            // Send a note to the DB that the app has been
            // used for the first time.
            Timer.getClassInstance().setNote("p3, User is at the confirmation pane.");
            // EncryptedStudent is not used.
            DBInsert.SESSION_NOTE.doInsert(new EncryptedStud());
      }

      /**
       * This method sets up the necessary bindings for the logic of the
       * application.
       */
      @Override
      public void setupBindings() {
            submitButton.disableProperty().bind(model.getFormInstance().persistableProperty().not());
            displayForm.prefWidthProperty().bind(confirmPane.prefWidthProperty());
      }


      /**
       * Button Actions
       */
      @Override
      public void setupEventHandlers() {

            submitButton.setOnAction(e -> {
                  model.formAction();
            });

            signInLink.setOnAction(e -> {
                  FlashMonkeyMain.showSignInPane();
            });
      }

      /**
       * This method is used to align the parts of a class.
       */
      @Override
      public void layoutParts() {

            LOGGER.debug("*** create CoonfirmationPane called ***");

            confirmPane.setAlignment(Pos.CENTER);
            confirmPane.setHgap(10);
            confirmPane.setVgap(12);
            confirmPane.setId("fileSelectPane");
            confirmPane.setPrefSize(325, 420);
            confirmPane.setOnKeyPressed(f -> {
                  if (f.getCode() == KeyCode.ENTER) {
                        model.formAction();
                  }
            });

            info.setFill(Color.web(UIColors.FM_WHITE));
            info.setTextAlignment(TextAlignment.CENTER);
            info.setWrappingWidth(260);

            submitButton.setMaxWidth(240);
            submitButton.setMinWidth(240);
            submitButton.setId("signInButton");

            signInLink.setId("signInHyp");

            msgVBox.setAlignment(Pos.CENTER);
            msgVBox.getChildren().addAll(info);
            buttonHBox.setAlignment(Pos.CENTER);
            buttonHBox.getChildren().add(submitButton);
            linkHBox.setAlignment(Pos.CENTER);
            linkHBox.getChildren().addAll(signInLink);

            spacer.setMinHeight(20);
            spacer1.setMinHeight(20);

            displayForm.setMaxWidth(280);

            confirmPane.addRow(0, spacer);
            confirmPane.addRow(1, msgVBox);
            confirmPane.addRow(2, spacer1);
            confirmPane.addRow(3, displayForm);
            confirmPane.addRow(4, buttonHBox);
            confirmPane.addRow(5, linkHBox);

      }

      public GridPane getConfirmPane() {
            return this.confirmPane;
      }

      public void onClose() {
            confirmPane = null;
            displayForm = null;
            model.onClose();
            return;
      }

      /**
       * TESTING
       **/

      @FMAnnotations.DoNotDeployMethod
      public Point2D getSignUpPaneMinXY() {
            Bounds bounds = displayForm.getLayoutBounds();
            return displayForm.localToScreen(bounds.getMinX(), bounds.getMinY());
      }
}
