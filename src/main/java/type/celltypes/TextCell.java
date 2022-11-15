/*
 * Copyright (c) 2019 - 2022. FlashMonkey Inc. (https://www.flashmonkey.xyz) All rights reserved.
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
//            textCellVbox.setStyle("-fx-background-color: white; -fx-border-radius: 3; -fx-background-radius: 3");
            textCellVbox.setAlignment(Pos.BOTTOM_LEFT);
            textArea.setWrapText(true);
            textArea.setEditable(false);

            textCellVbox.getChildren().add(textArea);
      }

      public final VBox buildCell(String txt, String prompt, boolean isEqual, double otherHt) {
            return buildCell(txt, prompt, SceneCntl.getCenterWd(), SceneCntl.calcCellHt(), 2, isEqual, otherHt);
      }


      /**
       * Sets the text area
       *
       * @param txt The text inside the TextArea. ie The answer or question
       */
      public final VBox buildCell(String txt, String prompt, int cellWd, int cellHt, int numSections, boolean isEqual, double otherHt) {
            textCellVbox = new VBox();
            textArea = new TextArea();
            // The starting size of this pane which will adapt to the settings
            // of the (parent) single or double section containers due to their responsive
            // settings.
            textArea.setPrefSize(cellWd, cellHt);
            textCellVbox.setPrefSize(cellWd, cellHt);
            textCellVbox.setStyle("-fx-background-color: white; -fx-border-radius: 2; -fx-background-radius: 2");
//            textArea.setStyle("-fx-background-color: white; -fx-border-radius: 3; -fx-background-radius: 3");
            textArea.setWrapText(true);
            textArea.setEditable(false);
            if ( txt.isEmpty() ) {
                  textArea.setPromptText(prompt);
            } else {
                  textArea.setText(txt);
            }

            textCellVbox.getChildren().add(textArea);
            if (ReadFlash.getInstance().getMasterBPane() != null) {
                  ReadFlash.getInstance().getMasterBPane().heightProperty().addListener((obs, oldval, newVal) -> {
                        double bottomPaneHt = SceneCntl.getBottomHt();
                        if (isEqual) {
                              textArea.setMinHeight((newVal.doubleValue() - bottomPaneHt) / numSections);
                              textArea.setMaxHeight((newVal.doubleValue() - bottomPaneHt) / numSections);
                        } else {
                              textArea.setMinHeight((newVal.doubleValue() - bottomPaneHt) - otherHt);
                              textArea.setMaxHeight((newVal.doubleValue() - bottomPaneHt) - otherHt);
                        }
                  });
            }

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
