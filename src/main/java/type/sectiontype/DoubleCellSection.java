/*
 * Copyright (c) 2019 - 2022. FlashMonkey Inc. (https://www.flashmonkey.xyz) All rights reserved.
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

package type.sectiontype;

import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.SceneCntl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import type.celltypes.GenericCell;

/**
 * @author Lowell Stadelman
 */
public class DoubleCellSection //extends GenericSection
{
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DoubleCellSection.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(DoubleCellSection.class);

      private Pane leftCell = new Pane();
      private Pane rightCell = new Pane();
      private final HBox sectionHBox = new HBox();


      //@Override
      public HBox sectionView(String txt, char type, int numHSections, boolean isEqual, double otherHt, String... fileName) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("in sectionView for DoubleCellSection. ");

            GenericCell gc = new GenericCell();
            int wd = SceneCntl.getCenterWd() - 108;
            int ht = (int) ReadFlash.getInstance().getMasterBPane().getHeight();
            // text cell
            leftCell = gc.cellFactory(txt, wd, ht, numHSections, isEqual, otherHt);
            rightCell.getChildren().clear();
            // Set the rightCell initial width and height from SceneCntl
            rightCell = gc.cellFactory(type, rightCell, SceneCntl.getRightCellWd(), SceneCntl.calcCellHt(), fileName);
            rightCell.setMinWidth(SceneCntl.getRightCellWd());
            rightCell.setMaxWidth(SceneCntl.getRightCellWd());
            sectionHBox.getChildren().addAll(leftCell, rightCell);
            // Set the initial section height
            double no = SceneCntl.calcCenterHt(30, 244, FlashMonkeyMain.getPrimaryWindow().getHeight());
            sectionHBox.setPrefHeight(no / numHSections);

            leftCell.setPrefHeight(no / numHSections);
            leftCell.setPrefWidth(FlashMonkeyMain.getPrimaryWindow().getWidth() - 108);

            // RESPONSIVE SIZING for width and height
            ReadFlash.getInstance().getMasterBPane().widthProperty().addListener((obs, oldval, newVal) -> {
                  leftCell.setMinWidth(newVal.doubleValue() - 108);
                  leftCell.setMaxWidth(newVal.doubleValue() - 108);
            });


            ReadFlash.getInstance().getMasterBPane().heightProperty().addListener((obs, oldval, newval) -> {
                  LOGGER.debug("resizing heigth line 75");
                  double val = newval.doubleValue() - 244.0;
                  val /= numHSections; // grrrrrr!
                  sectionHBox.setPrefHeight(val);
                  sectionHBox.setMaxHeight(val);
                  leftCell.setPrefHeight(val);
                  leftCell.setMaxHeight(val);
            });

            return sectionHBox;
      }


      /**
       * Returns the HBox containing this section.
       *
       * @return Returns the HBox containing this section
       */
      public HBox getSectionHBox() {
            return sectionHBox;
      }

}
