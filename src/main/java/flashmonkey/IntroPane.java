package flashmonkey;

import authcrypt.user.EncryptedStud;
import campaign.db.DBInsert;
import campaign.db.DBUpdate;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntroPane extends Pane {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntroPane.class);


    //private Hyperlink forgotLink;
    private Button signInBtn;
    private Button signUpBtn;
    private Button infoBtn;
    private Label msgLabel0;
    private Label msgLabel1;
    private Label msgLabel2;

    private GridPane mainGridPane;
    private Pane spacer;
    private Pane spacer1;
    private VBox labelVBox;
    private HBox buttonHBox;
    //private HBox forgotHBox;
    private VBox msgVBox;

    public IntroPane() {
        LOGGER.info("IntroPane called");
        init();
    }

    private void init() {
        initializeParts();
        layoutParts();
        getStylesheets().add(getClass().getResource("/css/fxformStyle.css").toExternalForm());
    }

    private void initializeParts() {
        //forgotLink = new Hyperlink("Forgot password?");
        signInBtn = new Button("I have an account");
        signUpBtn = new Button("I need an account");
        infoBtn = new Button("I need info");

        spacer = new Pane();
        spacer1 = new Pane();

        msgLabel0 = new Label("WELCOME");
        Text text1 = new Text("\nShow off who you are");
        String text2 =
            "\n\t- Get Better Grades " +
            "\n\t- Earn Pay " +
            "\n\t- And earn your Knowledge Credibility " +
            "\n\t     Score\u2122 for better jobs";
    //    text.setTextAlignment(TextAlignment.CENTER);

        msgLabel1 = new Label(text1.getText());
        msgLabel2 = new Label(text2);

        mainGridPane = new GridPane();
        // forgotHBox = new HBox();
        buttonHBox = new HBox(4);
        msgVBox = new VBox(4);
        // Send a note to the DB that the app has been
        // used for the first time.
        Timer.getClassInstance().setNote("p1 introPane, new user");
        // EncryptedStudent is not used.
        //DBInsert.SESSION_NOTE.doInsert(new EncryptedStud());
    }



    private void layoutParts() {
        mainGridPane.setAlignment(Pos.CENTER);
        mainGridPane.setHgap(10);
        mainGridPane.setVgap(12);
        mainGridPane.setId("opaqueMenuPaneDark");
        mainGridPane.setPrefSize(428, 290);
        spacer.setMinHeight(20);
        spacer1.setMinHeight(20);

        msgLabel0.setId("label24WhiteBld");
        msgLabel1.setId("label18WhiteLeft");
        msgLabel2.setId("label14white");
        signInBtn.setId("signInButton");
        signUpBtn.setId("signInButton");
        signUpBtn.setMaxWidth(Double.MAX_VALUE);
        signInBtn.setMaxWidth(Double.MAX_VALUE);

        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.getChildren().addAll(signInBtn, signUpBtn);
        buttonHBox.setPrefWidth(360);
        msgVBox.setAlignment(Pos.CENTER);
        msgVBox.getChildren().addAll(msgLabel0, msgLabel1, msgLabel2);

        mainGridPane.addRow(0, msgVBox);
        mainGridPane.addRow(1, spacer);
    //    mainGridPane.addRow(0, spacer1);
        mainGridPane.addRow(2, buttonHBox);

    }

    // *** GETTERS *** //

    protected GridPane getIntroPane() {
        return this.mainGridPane;
    }

    protected Button getSignInBtn() {
        return this.signInBtn;
    }

    protected Button getSignUpBtn() {
        return this.signUpBtn;
    }

}
