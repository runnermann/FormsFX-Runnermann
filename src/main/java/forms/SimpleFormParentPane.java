package forms;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ViewMixin;

import forms.utility.ResetDescriptor;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

/**
 * <p>This class provides a common layout for simple forms. Title is optional,
 *  as well as the message. This should be extended if the form is just
 *  a few entries, and the data requires little manipulation. IE for log ins
 *  or for saving file names. Data that requires manipulation such as the
 *  Student Description form, should not use this class. Instead, look at the
 *  FormParentPane.</p>
 *  <p>Set the title to the form using setTitle.
 *  Set the messageLabel to the form and its style. setMessage() and setMessageStyle.
 *  messageLabelStyle choices are white14 or if left blank it is set at
 *  white letter, font size 20 and bold. </p>
 */
public abstract class SimpleFormParentPane extends Pane implements ViewMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFormParentPane.class);
    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SimpleFormParentPane.class);

    private static Label formTitle;
    private Label msgLabel;

    protected static GridPane mainGridPane;
    private ArrayList<Node> nodeAry;
    private Pane spacer;
    private static VBox titleVBox;
    protected VBox msgVBox;
    private String msgLabelStyle;

    private VBox buttonVBox;
    private HBox lower1HBox;
    private HBox lower2HBox;

    protected Button actionButton;
    protected FormData data;


    // FxForm related
    protected FormRenderer displayForm;
    // protected FormModel model;
    // Persisting the ResetDescriptor across classes that
    // inherit this class
    protected static ResetDescriptor resetDescriptor;



    public SimpleFormParentPane() {
        //LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("SimpleFormParentPane called");
        resetDescriptor = new ResetDescriptor();
        init();
    }

    public static ResetDescriptor getResetDescriptor() {
        return resetDescriptor;
    }

    // Abstract call. Implement specific actions that should occur
    // (in the pane, not the form) when the the submit button is
    // clicked. Any changes in the pane that need to be made
    // will be called by this method
    public abstract void paneAction();

    /**
     * This method sets up the handling for all the button clicks.
     */
    @Override
    public abstract void setupEventHandlers();

    /**
     * Returns the FormPane from the child class.
     * @return
     */
    public abstract GridPane getMainGridPain();

    /**
     * This method is used to set up the stylesheets.
     */
    @Override
    public void initializeSelf() {
        initializeSelf();
        initializeParts();
        layoutParts();
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
    }


    /**
     * This method is used to set up the stylesheets.
     */
    @Override
    public void initializeParts() {
        LOGGER.info("initializeParts called");
        nodeAry = new ArrayList<>();
        mainGridPane = new GridPane();
        formTitle = new Label("");
        //msgLabel1 = new Label("");
        //msgLabel2 = new Label("");
        titleVBox = new VBox();
        msgVBox = new VBox();
        spacer = new Pane();

        buttonVBox = new VBox();
        lower1HBox = new HBox();
        lower2HBox = new HBox();

        actionButton = new Button("SUBMIT");
    }


    /**
     * This method sets up the necessary bindings for the logic of the
     * application.
     */
    @Override
    public abstract void setupBindings();

    /**
     * This method sets up listeners and sets the text of the state change
     * labels.
     */
    @Override
    public void setupValueChangedListeners() {
        LOGGER.info("setupValueChangedListeners called");
    }

    /**
     * This method is used to layout the nodes and regions properly.
     */
    @Override
    public void layoutParts() {

        LOGGER.info("*** layoutParts called ***");

        //displayForm.setMaxWidth(260);
        mainGridPane.setAlignment(Pos.CENTER);
        mainGridPane.setHgap(10);
        mainGridPane.setVgap(8);
        mainGridPane.setId("opaqueMenuPaneDark");
        mainGridPane.setPrefSize(325, 400);


        // If there is a title, add it to
        // the nodeArray. and add the spacer.
        if(formTitle.getText().length() > 0) {
            formTitle.setId("label24WhiteSP");
            formTitle.setAlignment(Pos.CENTER);
            titleVBox.getChildren().add(formTitle);
            titleVBox.setAlignment(Pos.CENTER);
            spacer.setPrefHeight(20);
            nodeAry.add(titleVBox);
        }


        // msgVBox elements are added in
        // a loop. Just set it here in the
        // nodeArray.
        msgVBox.setAlignment(Pos.CENTER);
        nodeAry.add(msgVBox);
        // Add spacer
        nodeAry.add(spacer);
        // Add the form
        nodeAry.add(displayForm);
        // Add the action button
        actionButton.setMaxWidth(240);
        actionButton.setMinWidth(240);
        actionButton.setId("signInButton");
        buttonVBox.getChildren().add(actionButton);
        buttonVBox.setAlignment(Pos.CENTER);
        nodeAry.add(buttonVBox);
        // Finally add the hBoxes containing
        // buttons or hyperlinks if any.
        lower2HBox.setAlignment(Pos.CENTER);
        lower1HBox.setAlignment(Pos.CENTER);
        nodeAry.add(lower1HBox);
        nodeAry.add(lower2HBox);

        // Add the nodes to the GridPane
        for(int i = 0; i < nodeAry.size(); i++) {
            mainGridPane.addRow(i, nodeAry.get(i));
        }
    }


    public GridPane getMainGridPane() {
        return this.mainGridPane;
    }

    /**
     * Uses A working replacement for Label.wrapText() method.
     * Called by model sets errorMsg in msgVBox
     * @param msg
     */
    public static void setErrorMsg(String msg) {
        titleVBox.getChildren().clear();
        int lineLength = 30;
        FormsUtility.setErrorMsg(msg, titleVBox, lineLength);
    }

    public void setFormTitle(String title) {
        this.formTitle.setText(title);
    }

    public void setLower2HBox(Node node) {
        this.lower2HBox.getChildren().add(node);
    }

    public void setMessageLabel(String ... msg) {
        String style = msgLabelStyle;// != null ? msgLabelStyle : "label14white";
        for(String s : msg) {
                msgLabel = new Label(s);
                //msgLabel.setId(style);
                msgVBox.getChildren().add(msgLabel);
                if(style.equals("white14")) {
                    msgLabel.setStyle("-fx-text-fill: WHITE; -fx-font-size: 16px; -fx-text-alignment: CENTER;");
                }else {
                    msgLabel.setStyle("-fx-font-size: 20px; -fx-text-alignment: CENTER;");
                }
        }
    }

    /**
     * Provide the desired style for the message
     * The default it white 14.
     * @param style
     */
    public void setMessageLabelStyle(String style) {
        msgLabelStyle = style;
    }

}
