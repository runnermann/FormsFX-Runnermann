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

package type.sectiontype;

import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.celltypes.CellLayout;
import uicontrols.SceneCntl;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import type.celltypes.GenericCell;

/**
 * Provides a single cell section that can either be text only, MM or
 * any other type of card that requires a file type.
 *
 * @author Lowell Stadelman
 */
public class SingleCellSection //extends GenericSection
{

      private static final Logger LOGGER = LoggerFactory.getLogger(SingleCellSection.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SingleCellSection.class);

      private final HBox sectionHBox = new HBox();
      private Pane leftCell;// = new Pane();
      // Effects responsive height.
      // if a section ht is the same for all sections
//      private boolean notEqual = false;
      // if an upper section is the mate for a
      // restricted lower/other section. Provide the
      // ht of the lower/other section.
//      private double otherHt = 0; //100;

      /* *** GETTERS ans SETTERS *** */

      /**
       * Provide a 't' in the mmType and this will be a text box,
       * else it is a view type that can use a file.
       * NOTE: The default for this method. All sections are
       * an equal height. If you need to provide for a section where
       * a card has two sections, and one section has a non-adjustable
       * height, as in the case for TrueOrFalse, set notEqual to true,
       * and provide the ht of the other section. The other section
       * should use the sectionView(Pane) method.
       *
       * @param txt
       * @param type         The type of section
       * @param numHSections Number of Horizontal sections if they are
       *                     equal ht or a single section. Is not used
       *                     if this section is notEqual.
       * @param paths        Media paths
       * @return Returns an HBox containing this section if it is
       */
      //@Override
      public HBox sectionView(String txt, CellLayout type, int numHSections, boolean isEqual, double otherHt, String... paths) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.info("in SingleCellSection.sectionView(string,char,string... )");

            //Single section still has a left cell.
            leftCell = buildPaneSwitch(txt, type, numHSections, isEqual, (int) otherHt, paths);
            leftCell.setPrefWidth(ReadFlash.getInstance().getMasterBPane().widthProperty().get());

            sectionHBox.setStyle("-fx-background-color: WHITE; -fx-background-radius: 3");
            leftCell.setStyle("-fx-background-color: TRANSPARENT;");

            // Bind section width with a listener for responsive width
            ReadFlash.getInstance().getMasterBPane().widthProperty().addListener((obs, oldval, newVal) -> {
                  double val = (double) newVal - 8;
                  sectionHBox.setMinWidth(val);
                  leftCell.setMinWidth(val);
            });
            // Make section height responsive
            ReadFlash.getInstance().getMasterBPane().heightProperty().addListener((obs, oldval, newVal) -> {
                  double val = SceneCntl.calcCenterHt(30, 244, (double) newVal);
                  val -= 8;

                  if (isEqual) { // is equal
                        val /= numHSections; // grrrrrr!
                        //val -= otherHt;
                  } else {
                        LOGGER.debug("SingleSection notEqual is true");
                        val = newVal.doubleValue() - otherHt;
                  }
                  sectionHBox.setMinHeight(val);
                  sectionHBox.setMaxHeight(val);
                  leftCell.setMinHeight(val);
                  leftCell.setMaxHeight(val);
            });
            sectionHBox.getChildren().add(leftCell);
            sectionHBox.setAlignment(Pos.CENTER);
            return sectionHBox;
      }

      /**
       * section view provides the sizing (H, W) for each section.
       * Provide a pane with the contents if they are non-standard.
       * For instance, it is not text nor media such as for the
       * Used by TrueOrFalse lower pane.
       *
       * @param lowerPane
       * @return
       */
      public HBox sectionView(Pane lowerPane) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("in singleCellSection.sectionView(Pane localPane)");
            HBox sectionHBox = new HBox();

            // Make width responsive
            ReadFlash.getInstance().getMasterBPane().widthProperty().addListener((obs, oldval, newVal) -> {
                  sectionHBox.setMinWidth(newVal.doubleValue() - 8);
                  lowerPane.setMinWidth(newVal.doubleValue() - 8);
            });

            LOGGER.debug("in singleCellSection.sectionView()");
            sectionHBox.getChildren().add(lowerPane);

            return sectionHBox;
      }

      private Pane buildPaneSwitch(String txt, CellLayout type, int numHSections, boolean isEqual, int otherHt, String... paths) {

            //GenericCell gc = new GenericCell();
            int wd = SceneCntl.getCenterWd();
            int ht = (int) ReadFlash.getInstance().getMasterBPane().getHeight();
            switch (type.get()) {
                  // Audio Video = m / media
                  //case 'M': // should not be called here
                  // Canvas = images and shapes/drawings
                  //case 'C': // should not be called here
                  case 'c':
                  case 'm': {
                        GridPane singlePane = new GridPane();
                        return GenericCell.cellFactory(type, singlePane, wd, ht, paths);
                  }
                  case 't': // Text only in this view
                  default: {
                        LOGGER.debug("SingleSection switch: at default == text singlePane. ");
                        LOGGER.debug("in SingleSection, setting ht to: " + ht);
                        return GenericCell.cellFactory(txt, wd, ht, numHSections, isEqual, otherHt);
                  }
            }
      }
}
