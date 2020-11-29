package type.celltypes;

import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import uicontrols.SceneCntl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;


/**
 * This class manages the values of the VBox that contains textAreas used by FlashMonkey.
 * It does not include editable textAreas. Use getTextArea to access the text area.
 *
 * @author Lowell Stadelman
 */
public class TextCell
{
    // Variables
    private VBox textCellVbox;
    private TextArea textArea;


    /**
     * Default constructor
     */
    public TextCell() {
        textCellVbox = new VBox();
        textArea = new TextArea();
        textCellVbox.setPadding(new Insets(6, 6, 6, 6));
        textCellVbox.setStyle("-fx-background-color: white");
        textCellVbox.setAlignment(Pos.BOTTOM_LEFT);
        textArea.setWrapText(true);
        textArea.setEditable(false);

        textCellVbox.getChildren().add(textArea);
    }

    public final VBox buildCell(String txt, String prompt, boolean isEqual, double otherHt) {
        return buildCell(txt, prompt, SceneCntl.getCenterWd(), SceneCntl.getCellHt(), 2, isEqual, otherHt);
    }


    /**
     * Sets the text area
     * @param txt The text inside the TextArea. ie The answer or question
     */
    public final VBox buildCell(String txt, String prompt, int cellWd, int cellHt, int numSections, boolean isEqual, double otherHt)
    {
        textCellVbox = new VBox();
        textArea = new TextArea();


       //System.out.println("isInEditorMode: " + FlashMonkeyMain.isInEditorMode());


        // The starting size of this pane which will adapt to the settings
        // of the (parent) single or double section containers due to their responsive
        // settings.
        textArea.setPrefSize(cellWd, cellHt);
        textCellVbox.setPrefSize(cellWd, cellHt);
        textCellVbox.setStyle("-fx-background-color: white");
        textArea.setWrapText(true);
        textArea.setPromptText(prompt);
        textArea.setEditable(false);
        textArea.setText(txt);

        textCellVbox.getChildren().add(textArea);
        if(ReadFlash.getInstance().getMasterBPane() != null) {
            ReadFlash.getInstance().getMasterBPane().heightProperty().addListener((obs, oldval, newVal) -> {
                if(isEqual) {
                    textArea.setMinHeight((newVal.doubleValue() - 228) / numSections);
                    textArea.setMaxHeight((newVal.doubleValue() - 228) / numSections);
                } else {
                    textArea.setMinHeight((newVal.doubleValue() - 228) - otherHt);
                    textArea.setMaxHeight((newVal.doubleValue() - 228) - otherHt);
                }
            });
        }

        return textCellVbox;
    }

    /** SETTERS **/

    /** GETTERS **/

    public final TextArea getTextArea()
    {
        return this.textArea;
    }

    public final VBox getTextCellVbox()
    {
        return this.textCellVbox;
    }

    /**
     * Sets the text style. Just as any CSS style, must
     * use common inline CSS format for javaFX.
     * @param textStyle Expects style as a String.
     */
    public final void setTextAreaStyle(String textStyle) {

        this.textArea.setStyle(textStyle);
    }
}
