package draw;

import javafx.geometry.Insets;
//import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.Serializable;

/**
 * Manages the values of the VBox that contains textAreas used by FlashMonkey.
 * It does not include editable textAreas. Refer to the child Class FMTextEditable if an
 * editable TextArea is needed.
 *
 * @author Lowell Stadelman
 */
public class FMText //implements Serializable
{
    //private static final long serialVersionUID = FlashMonkeyMain.VERSION;

    /** Variables **/
    protected String promptTxt;

    /** OBJECTS FROM JAVA LIBRARIES **/
    protected StackPane stackPane;
    protected TextArea textArea;

    


    /**
     * Default constructor
     */
    protected FMText() {
        /* empty */
    }

    /**
     * Full constructor
     * @param prompt
     */
    protected FMText(String prompt, double x, double y, double wd, double ht) {
        this.promptTxt = prompt;
        stackPane = new StackPane();
        stackPane.setLayoutX(x);
        stackPane.setLayoutY(y);
        stackPane.setPrefSize(wd, ht);
        stackPane.setMaxSize(wd, ht);
        stackPane.setMinSize(wd, ht);
        textArea = new TextArea();
        stackPane.setPadding(new Insets(6, 4, 6, 4));
        stackPane.setStyle("-fx-background-color: white");
        stackPane.setAlignment(Pos.BOTTOM_LEFT);
        textArea.setWrapText(true);
        textArea.setPromptText(promptTxt);

        stackPane.getChildren().add(textArea);
    }

    /** SETTERS **/
    protected void setPromptText(String prompt) {
        this.promptTxt = prompt;
    }

    /** GETTERS **/
    protected String getPromptText() {
        return promptTxt;
    }

    /**
     * Returns the VBox object
     * @return returns the VBox that contains the TextArea of this class
     */
    protected StackPane getStackPane() {
        return stackPane;
    }
}
