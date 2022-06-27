/*
 * Copyright (c) 2019 - 2021. FlashMonkey Inc. (https://www.flashmonkey.xyz) All rights reserved.
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

package type.celleditors;

import fileops.DirectoryMgr;
import org.slf4j.Logger;
import type.draw.shapes.FMRectangle;
import type.draw.shapes.GenericShape;
import fileops.FileNaming;
import flashmonkey.CreateFlash;
import flashmonkey.FlashMonkeyMain;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.LoggerFactory;
import uicontrols.SceneCntl;

import java.util.ArrayList;

public class ShapesEditorPopup {

      // THE LOGGER
      private static final Logger LOGGER = LoggerFactory.getLogger(ShapesEditorPopup.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ShapesEditorPopup.class);

      private static ShapesEditorPopup CLASS_INSTANCE;

      private DrawTools draw;
      private ArrayList<GenericShape> editorShapeAry;


      private ShapesEditorPopup() {
            //drawObj = new DrawObj();
      }

      /**
       * Singleton class instantiation. There
       * should only be one instance of DrawTools
       * Synchronized
       *
       * @return The class instance
       */
      public static synchronized ShapesEditorPopup getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new ShapesEditorPopup();
            }
            //LOGGER.setLevel(Level.DEBUG);
            return CLASS_INSTANCE;
      }

      public void init() {
            this.draw = DrawTools.getInstance();
            this.editorShapeAry = new ArrayList<>(4);
      }


      /**
       * @return arrayList of shapes
       */
      public ArrayList<GenericShape> getEditorShapeAry() {
            return editorShapeAry;
      }

      /**
       * @param editorShapeAry ..
       */
      public void setEditorShapeAry(ArrayList<GenericShape> editorShapeAry) {
            ArrayList<GenericShape> newList = new ArrayList<>(editorShapeAry.size());
            for (GenericShape s : editorShapeAry) {
                  newList.add(s.clone());
            }
            this.editorShapeAry = newList;
      }


      /**
       * Creates shapes in the drawpad popup and gets DrawTools for editing the shapes in the drawpad popup.
       *
       * @param shapeArray    ..
       * @param thisEditor    ..
       * @param shapeFileName ..
       */
      public void shapePopupHandler(ArrayList<GenericShape> shapeArray, SectionEditor thisEditor, String shapeFileName,
                                    String deckName, String cID, char qOrA) {

            CreateFlash.getInstance().setFlashListChanged(true);
            CreateFlash cfp = CreateFlash.getInstance();
            cfp.disableButtons();

            double mediaWd = ((FMRectangle) shapeArray.get(0)).getWd();
            double mediaHt = ((FMRectangle) shapeArray.get(0)).getHt();
            double popUpX = FlashMonkeyMain.getWindow().getX() - (mediaWd - 20);
            double popUpY = FlashMonkeyMain.getWindow().getY() + 25;

            //   drawObj.setDems(popUpX, popUpY + 15, mediaWd, mediaHt);

            //   fileNaming = new FileNaming(deckName, data.getUserName(), cID, qOra, ".shp");
            //   String shapeFileName = fileNaming.getImgFileName();
            //  drawObj.setFullPathName(shapeFileName);

            draw = DrawTools.getInstance();
            //draw.buildPopupDrawTools(drawObj, thisEditor, null);
            draw.buildDrawTools(shapeFileName, thisEditor, null, popUpX, popUpY, mediaWd, mediaHt);
            draw.popUpTools();

            // If there are shapes to display
            if (shapeArray.size() > 1) {
                  for (int i = 1; i < shapeArray.size(); i++) {
                        // adds itself to the canvas pane?
                        shapeArray.get(i).getBuilder(thisEditor, false);
                  }
            }
      }


      /**
       * Creates a popUp of the right pane with Image and shapes (if they exist)
       * * on top of the image1.
       *
       * @param shapeArray     ..
       * @param thisEditor     ..
       * @param mediaFileNames ..
       * @param deckName       ..
       * @param cID            ..
       */
      public void imagePopupHandler(ArrayList<GenericShape> shapeArray, SectionEditor thisEditor, final String[] mediaFileNames,
                                    String deckName, String cID) {
            LOGGER.info("imagePopupHandler called");

            CreateFlash cfp = CreateFlash.getInstance();
            cfp.disableButtons();
            String path = DirectoryMgr.getMediaPath('c');

            Image image = new Image("File:" + path + mediaFileNames[0]);
            ImageView localIView = new ImageView(image);
            double popUpX = FlashMonkeyMain.getWindow().getX() - localIView.getFitWidth() - 450;
            double popUpY = FlashMonkeyMain.getWindow().getY() + 25;
            double mediaWd;
            double mediaHt;

            if (image != null) {
                  if (localIView.getFitWidth() < SceneCntl.getScreenWd()) {
                        LOGGER.info("image1 is smaller than screen width");
                        mediaWd = image.getWidth();
                        mediaHt = image.getHeight();
                  } else {
                        LOGGER.info("image1 is larger than screen in one dimension");
                        mediaWd = SceneCntl.getScreenWd() - 400;
                        mediaHt = SceneCntl.getScreenHt() - 400;
                  }

                  localIView = new ImageView(image);
                  LOGGER.info("In popup, mediaWd: {}, mediaHt: {}", mediaWd, mediaHt);

            } else {
                  mediaWd = ((FMRectangle) shapeArray.get(0)).getWd();
                  mediaHt = ((FMRectangle) shapeArray.get(0)).getHt();
            }

            LOGGER.debug("ImageFileName: {}", mediaFileNames[0]);

            draw = DrawTools.getInstance();
            String shapeFile = "";

            if (mediaFileNames.length == 1) {
                  shapeFile = FileNaming.getShapesName(mediaFileNames[0]);
            } else {
                  shapeFile = mediaFileNames[1];
            }

            draw.buildDrawTools(shapeFile, thisEditor, localIView, popUpX, popUpY, mediaWd, mediaHt);
            draw.popUpTools();

            // If there are shapes to display
            if (shapeArray != null && shapeArray.size() > 1) {
                  for (int i = 1; i < shapeArray.size(); i++) {
                        // adds itself to the canvas pane?
                        shapeArray.get(i).getBuilder(thisEditor, false);

                        LOGGER.debug("shape strokeColor: " + shapeArray.get(i).getStrokeColor());
                  }
            }
      }
}
