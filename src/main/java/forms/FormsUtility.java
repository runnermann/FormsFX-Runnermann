package forms;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FormsUtility {

    public FormsUtility() { /* no args constructor */ }

    /**
     * A working replacement for Label.wrapText() method.
     * Returns a VBox containing an array of labels that are
     * set to the number of chars in lineLength. If A word
     * causes the label to exceed the lineLength, it is moved
     * to the next row.
     * @param msg THe message to be displayed
     * @param msgVBox The VBox that contains the message
     * @param lineLength THe length of a label by characters.
     * @return Returns a VBox with an array of labels where the
     * label width is limited by the lineLength.
     */
    public static VBox setErrorMsg(String msg, VBox msgVBox, int lineLength) {

        int numRows = msg.length() / lineLength;
        Label[] msgLblAry = new Label[numRows + 2];
        //StringBuffer temp = new StringBuffer();
        StringBuilder temp = new StringBuilder();
        String[] parts = msg.split(" ");

        for (int i = 0, j = 0; j < msgLblAry.length; ) {
            if (i < parts.length && (temp.length() + parts[i].length() < lineLength)) {
                temp.append(parts[i] + " ");
                i++;
            } else {
                msgLblAry[j] = new Label(temp.toString());
                msgLblAry[j].setId("errorLabel");
                temp = new StringBuilder();
                msgVBox.getChildren().add(msgLblAry[j]);
                j++;
            }
        }
        return msgVBox;
    }
}
