package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import flashmonkey.FlashMonkeyMain;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the container class with pane for the first of two pw reset forms.
 * This class contains the fields needed for the users original password.
 */
public class ResetOnePane extends SimpleFormParentPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResetOnePane.class);

    private ResetOneModel model;
    private Hyperlink signInLink;


    public ResetOnePane() {
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
        model = new ResetOneModel();
        displayForm = new FormRenderer(model.getFormInstance());
    }

    /**
     * This method is used to initializes all the properties of a class.
     */
    @Override
    public void initializeParts() {
        super.initializeParts();
        setFormTitle("Need a new password?");
        setMessageLabel("No Worries!", "We will send you a link.");
        signInLink = new Hyperlink("Go back");
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
        signInLink.setOnAction(e -> FlashMonkeyMain.showSignInPane());
        actionButton.setOnAction(e -> model.formAction());
        mainGridPane.setOnKeyPressed(f -> {
            if(f.getCode() == KeyCode.ENTER) {
                model.formAction();
            }
        });
    }

    @Override
    public void paneAction() {
        /* stub */
    }
}
