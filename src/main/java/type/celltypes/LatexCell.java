package type.celltypes;

import flashmonkey.ReadFlash;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import type.tools.calculator.LatexView;
import uicontrols.SceneCntl;

public class LatexCell {


    private VBox latexCellVbox;
    private TextArea latexTextArea;

    public LatexCell() { /* empty */ }

    /**
     * Only used by Create Edit.
     * @param txt
     * @param prompt
     * @param isEqual
     * @param otherHt
     * @return
     */
    public final VBox buildCell(String txt, String prompt, boolean isEqual, double otherHt) {
        // Edit box to create the Latex that will be displayed later.
        latexTextArea = new TextArea();
        latexTextArea.setWrapText(true);
        latexTextArea.setEditable(true);
        double cellWd = SceneCntl.getCenterWd();
        double ht = SceneCntl.calcCellHt();

        // The starting size of this pane which will adapt to the settings
        // of the (parent) single or double section containers due to their responsive
        // settings.
        latexTextArea.setPrefSize(cellWd, ht);
        latexCellVbox.setPrefSize(cellWd, ht);
        latexCellVbox.setStyle("-fx-background-color: WHITE; -fx-border-radius: 2; -fx-background-radius: 2");
        latexTextArea.setWrapText(true);
        latexTextArea.setEditable(false);
        if ( txt.isEmpty() ) {
            latexTextArea.setPromptText(prompt);
        } else {
            latexTextArea.setText(txt);
        }
        latexTextArea.setStyle("-fx-background-color: WHITE");

        latexCellVbox = new VBox(latexTextArea);
        latexCellVbox.setPadding(new Insets(6, 6, 6, 6));
        latexCellVbox.setAlignment(Pos.BOTTOM_LEFT);

        return latexCellVbox;
    }

    /**
     * Used by ReadFlash
     * @param latexString The string containing latex according to JLatexMath
     * @param prompt What is shown if the text cell is empty
     * @param cellWd width
     * @param cellHt heigth
     * @param numSections number of sections
     * @param isEqual If the sections are not equal in height
     * @param otherHt If the sections are not equal, the height of the other section. Otherwise null.
     * @return The VBox set with the correct ht wd containing this text cell.
     */
    public final VBox buildCell(String latexString, String prompt, final int cellWd, final int cellHt, int numSections, boolean isEqual, double otherHt) {

        // Read the latex

        String latex = "\\begin{array}{lr}\\mbox{\\textcolor{green}{English}}\\\\";
        latex += "\\mbox{This is just plain text \\LaTeX\\ }\\\\";
        latex += "\\int_0^\\infty{x^{2n} e^{-a x^2}\\,dx} = \\frac{2n-1}{2a} \\int_0^\\infty{x^{2(n-1)} e^{-a x^2}\\,dx} = \\frac{(2n-1)!!}{2^{n+1}} \\sqrt{\\frac{\\pi}{a^{2n+1}}}\\\\";
        latex += "\\end{array}";
        LatexView latexView = new LatexView(latex);

        latexView.setSize(20);

        double ht;
        if(isEqual) {
            ht = cellHt;
        } else {
            double ht1 = ReadFlash.getInstance().getMasterBPane().getHeight();
            ht = ht1 - otherHt;
        }

//        LatexView latexView = new LatexView(latexString);
        latexCellVbox = new VBox(latexView);
        latexCellVbox.setPadding(new Insets(6, 6, 6, 6));
        latexCellVbox.setAlignment(Pos.BOTTOM_LEFT);

        latexCellVbox.setPrefSize(cellWd, ht);
        latexCellVbox.setStyle("-fx-background-color: WHITE; -fx-border-radius: 2; -fx-background-radius: 2");

        // The starting size of this pane which will adapt to the settings
        // of the (parent) single or double section containers due to their responsive
        // settings.

        latexCellVbox.setPrefSize(cellWd, ht);
        latexCellVbox.setStyle("-fx-background-color: WHITE; -fx-border-radius: 2; -fx-background-radius: 2");


        return latexCellVbox;
    }



    /** SETTERS **/

    /**
     * GETTERS
     **/

    public final TextArea getTextArea()  {
        return this.latexTextArea;
    }

    public final VBox getTextCellVbox() {
        return this.latexCellVbox;
    }

    /**
     * Sets the text style. Just as any CSS style, must
     * use common inline CSS format for javaFX.
     *
     * @param textStyle Expects style as a String.
     */
    public final void setTextAreaStyle(String textStyle) {
        this.latexTextArea.setStyle(textStyle);
    }


}
