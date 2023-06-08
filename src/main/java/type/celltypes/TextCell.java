/*
 * Copyright (c) 2019 - 2023. FlashMonkey Inc. (https://www.flashmonkey.co) All rights reserved.
 *
 * DISCLAIMER OF WARRANTY.
 *
 * COVERED CODE IS PROVIDED UNDER THIS LICENSE ON AN "AS IS" BASIS, WITHOUT WARRANTY OF ANY
 *  KIND, EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES THAT THE COVERED
 *  CODE IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE
 *  ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED CODE IS WITH YOU. SHOULD ANY
 *  COVERED CODE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE INITIAL DEVELOPER OR ANY OTHER
 *  CONTRIBUTOR) ASSUME THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 *  DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.  NO USE OF ANY COVERED
 *  CODE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 *
 */

package type.celltypes;

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
public class TextCell {

      private VBox textCellVbox;
      private TextArea textArea;

      /**
       * Default constructor
       */
      public TextCell() { /* empty */ }

      /**
       * Only used by Create Edit.
       * @param txt
       * @param prompt
       * @param isEqual
       * @param otherHt
       * @return
       */
      public final VBox buildCell(String txt, String prompt, boolean isEqual, double otherHt) {
            return buildCell(txt, prompt, SceneCntl.getCenterWd(), SceneCntl.calcCellHt(), 2, isEqual, otherHt);
      }

      /**
       *
       * @param txt The text inside the TextArea. ie The answer or question
       * @param prompt What is shown if the text cell is empty
       * @param cellWd width
       * @param cellHt heigth
       * @param numSections number of sections
       * @param isEqual If the sections are not equal in height
       * @param otherHt If the sections are not equal, the height of the other section.
       * @return The VBox set with the correct ht wd containing this text cell.
       */
      public final VBox buildCell(String txt, String prompt, final int cellWd, final int cellHt, int numSections, boolean isEqual, double otherHt) {
            textCellVbox = new VBox();
            textArea = new TextArea();
            double ht;
            if(isEqual) {
                  ht = cellHt;
            } else {
                  double ht1 = ReadFlash.getInstance().getMasterBPane().getHeight();
                  ht = ht1 - otherHt;
            }
            // The starting size of this pane which will adapt to the settings
            // of the (parent) single or double section containers due to their responsive
            // settings.
            textArea.setPrefSize(cellWd, ht);
            textCellVbox.setPrefSize(cellWd, ht);
            textCellVbox.setStyle("-fx-background-color: WHITE; -fx-border-radius: 2; -fx-background-radius: 2");
            textArea.setWrapText(true);
            textArea.setEditable(false);
            textCellVbox.setPadding(new Insets(6, 6, 6, 6));
            textCellVbox.setAlignment(Pos.BOTTOM_LEFT);
            if ( txt.isEmpty() ) {
                  textArea.setPromptText(prompt);
            } else {
                  textArea.setText(txt);
            }
            textArea.setStyle("-fx-background-color: WHITE");
            textArea.setWrapText(true);
            textArea.setEditable(false);

            textCellVbox.getChildren().add(textArea);

            return textCellVbox;
      }

      /** SETTERS **/

      /**
       * GETTERS
       **/

      public final TextArea getTextArea() {
            return this.textArea;
      }

      public final VBox getTextCellVbox() {
            return this.textCellVbox;
      }

      /**
       * Sets the text style. Just as any CSS style, must
       * use common inline CSS format for javaFX.
       *
       * @param textStyle Expects style as a String.
       */
      public final void setTextAreaStyle(String textStyle) {
            this.textArea.setStyle(textStyle);
      }
}
