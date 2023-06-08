/*
 * Copyright (c) 2019 - 2021. FlashMonkey Inc. (https://www.flashmonkey.co) All rights reserved.
 *
 * License: This is for internal use only by those who are current employees of FlashMonkey Inc, or have an official
 *  authorized relationship with FlashMonkey Inc..
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

import fileops.DirectoryMgr;
import flashmonkey.ReadFlash;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import javax.print.DocFlavor;

/**
 * Assists with building and selecting different cell types in
 * the UI.
 *
 * @author Lowell Stadelman
 */
public abstract class GenericCell//  extends GenericSection
{
      private static final String CANVAS = DirectoryMgr.getMediaPath('C');//"../flashMonkeyFile/media/";
      private static final String MEDIA = DirectoryMgr.getMediaPath('M');//  ../flashMonkeyFile/media/";
      /**
       * Returns the cell containing a media that is sized according to the scale given in the
       * parameters.
       *
       * @param cellType  Media (video or audio) = 'M'/'m' or Canvas / Image Drawing = 'C' 'c' 'D' 'd'. Note:
       *                  Upper case is a double section. Lower case is a single section.
       * @param pane
       * @param paneWd
       * @param paneHt
       * @param fileNames Files as an array. Expects the image or media to be at idx [0], shapes array idx [1]
       * @return Returns the cell containing a media
       */
      public static Pane cellFactory(CellLayout cellType, Pane pane, int paneWd, int paneHt, String... fileNames) {
            // Upper case is double section, lower case single section
            switch (cellType.get()) {
                  case 'M': {// Audio or Video doubleCell section
                        final AVCell mCell = new AVCell();
                        return mCell.buildCell(100, 100, MEDIA + fileNames[0]);
                  }
                  case 'm': // Audio or video for singleCell section
                  {
                        final HBox containerHBox = new HBox();
                        containerHBox.setPrefSize(paneWd, paneHt);
                        containerHBox.setId("whiteCurvedContainer");
                        containerHBox.setAlignment(Pos.CENTER);
                        final AVCell mCell = new AVCell();
                        containerHBox.getChildren().add(mCell.buildCell(200, 200, MEDIA + fileNames[0]));
                        return containerHBox;
                  }
                  case 'c': // image or both. for singleCell section
                  case 'd': // drawing no image. singleCell
                  {
                        final HBox containerHBox = new HBox();
                        containerHBox.setPrefSize(paneWd, paneHt);
                        containerHBox.setId("whiteCurvedContainer");
                        containerHBox.setAlignment(Pos.CENTER);
                        final String[] canvasPaths = new String[2];
                        int i = 0;
                        CanvasCell cCell = new CanvasCell();

                        for (String s : fileNames) {
                              canvasPaths[i++] = CANVAS + s;
                        }
                        containerHBox.getChildren().add(cCell.buildCell(pane, paneWd, paneHt, canvasPaths));
                        return containerHBox;
                  }
                  case 'C': // image, or both. for doubleCell section
                  case 'D': // drawing, no image. doubleCell
                  default: {
                        final String[] canvasPaths = new String[2];
                        int i = 0;
                        final CanvasCell cCell = new CanvasCell();

                        for (String s : fileNames) {
                              canvasPaths[i++] = CANVAS + s;
                        }
                        // the image cell on the right.
                        return cCell.buildCell(pane, paneWd, paneHt, canvasPaths);
                  }
            }
      }

      /**
       * Builds the left TextCell for ReadFlash
       *
       * @param text
       * @return
       */
      public static Pane cellFactory(String text, int paneWd, int paneHt, int numSections, boolean isEqual, double otherHt) {
            final TextCell tCell = new TextCell();
            return tCell.buildCell(text, "", paneWd, paneHt, numSections, isEqual, otherHt);
      }

      public static ScrollPane cellFactory(String response, int paneWd, int paneHt, int numSections) {
            final DjkResponseCell djk = new DjkResponseCell();
            return djk.buildCell(response, paneWd, paneHt, numSections);
      }


}
