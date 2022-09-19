package forms;


import authcrypt.user.EncryptedAcct;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import org.slf4j.LoggerFactory;


public class AccountPane extends SimpleFormParentPane {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AccountPane.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(DeckMetaModel.class);

    private AccountModel model;
    //protected GridPane mainGridPain;
    // cancel subscription
    //    private Hyperlink cancelSubtLink;

    public AccountPane() {
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
        return this.mainGridPane;
    }

    /**
     * This method is called by Forms api ViewMixin
     */
    @Override
    public void initializeSelf() {
        model = new AccountModel();
        displayForm = new FormRenderer(model.getFormInstance());
    }


    /**
     * This method is used to initializes all the properties of a class.
     */
    @Override
    public void initializeParts() {
        super.initializeParts();
        setFormTitle("CANCEL SUBSCRIPTION");
        setMessageLabel("Awe... we are sorry to see you go.",
                        "FYI.",
                "When you cancel you lose your" +
              "\nfuture earnings, if any, to FlashMonkey Inc" +
              "\nor the next highest distributor.",
                "If you are an early adopter, you " +
              "\nlose your subscription discount.");

        super.actionButton.setText("AGREE and SUBMIT");

    }

    @Override
    public void layoutParts() {
        super.layoutParts();
    }

    /**
     * This method sets up the necessary bindings for the logic of the
     * application.
     */
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
        EncryptedAcct a = new EncryptedAcct();
        actionButton.setOnAction(e -> model.formAction());
    }

    /**
     * Implement specific actions that should occur
     * 	(in the pane, not the form) when the the submit button is
     * 	clicked. Any changes in the pane that need to be made
     * 	will be called by this method.
     */
    @Override
    public void paneAction() {
        /* stub */
    }

}
