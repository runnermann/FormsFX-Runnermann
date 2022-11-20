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

import java.awt.*;
import java.util.ArrayList;

/**
 * <p>Provides the Canvas Editor Popup. Handles the display of an image
 *  and shapes overlay that provides manipulation of shapes and the image.</p>
 *  <p>Top level class that supplies the methods
 *  to underlying DrawTools and DrawPad.</p>
 */
public class CanvasEditorPopup {

      // THE LOGGER
      private static final Logger LOGGER = LoggerFactory.getLogger(CanvasEditorPopup.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ShapesEditorPopup.class);
      private static CanvasEditorPopup CLASS_INSTANCE;
      private DrawTools draw;
      private ArrayList<GenericShape> editorShapeAry;


      private CanvasEditorPopup() {
            //drawObj = new DrawObj();
      }

      /**
       * Singleton class instantiation. There
       * should only be one instance of DrawTools
       * Synchronized
       *
       * @return The class instance
       */
      public static synchronized CanvasEditorPopup getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new CanvasEditorPopup();
            }
            //LOGGER.setLevel(Level.DEBUG);
            return CLASS_INSTANCE;
      }

      public void init() {
            this.draw = DrawTools.getInstance();
            this.editorShapeAry = new ArrayList<>(4);
      }


      /**
       * Call to create popup for either shapes only aka drawpad, or image/shapes and image only.
       * @param shapeArray
       * @param thisEditor
       * @param mediaFileNames
       * @param deckName
       * @param cID
       */
      public void popup(ArrayList<GenericShape> shapeArray, SectionEditor thisEditor, final String[] mediaFileNames,
                        String deckName, String cID) {
            if(mediaFileNames[0] == null) {
                  shapePopupHandler(shapeArray, thisEditor, mediaFileNames[1], deckName, cID);
            } else {
                  imagePopupHandler(shapeArray, thisEditor, mediaFileNames, deckName, cID);
            }

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
      private void shapePopupHandler(ArrayList<GenericShape> shapeArray, SectionEditor thisEditor, String shapeFileName,
                                    String deckName, String cID) {

            CreateFlash.getInstance().setFlashListChanged(true);
            CreateFlash.getInstance().disableButtons();

            double mediaWd = ((FMRectangle) shapeArray.get(0)).getWd();
            double mediaHt = ((FMRectangle) shapeArray.get(0)).getHt();
            double popUpX = FlashMonkeyMain.getPrimaryWindow().getX() - (mediaWd - 20);
            double popUpY = FlashMonkeyMain.getPrimaryWindow().getY() + 24;

            if(popUpX < 20.0 | popUpY < 20.0) {
                  Toolkit.getDefaultToolkit().beep();
                  //Thread.dumpStack();
            }

            draw = DrawTools.getInstance();
            draw.buildDrawTools(shapeFileName, thisEditor, null, popUpX, popUpY, mediaWd, mediaHt);

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
       * on top of the image1.
       *
       * @param shapeArray     ..
       * @param thisEditor     ..
       * @param mediaFileNames ..
       * @param deckName       ..
       * @param cID            ..
       */
      private void imagePopupHandler(ArrayList<GenericShape> shapeArray, SectionEditor thisEditor, final String[] mediaFileNames,
                                    String deckName, String cID) {
            LOGGER.info("imagePopupHandler called");

            CreateFlash cfp = CreateFlash.getInstance();
            cfp.disableButtons();
            String path = DirectoryMgr.getMediaPath('c');

            Image image = null;
            ImageView localIView = null;
            double popUpY = FlashMonkeyMain.getPrimaryWindow().getY() + 25;
            double popUpX = 0;
            image = new Image("File:" + path + mediaFileNames[0]);
            localIView = new ImageView(image);
            double mediaWd;
            double mediaHt;

            popUpX = FlashMonkeyMain.getPrimaryWindow().getX() - localIView.getFitWidth() - 450;
            if (localIView.getFitWidth() < SceneCntl.getScreenWd()) {
                  LOGGER.info("image1 is smaller than screen width");
                  mediaWd = image.getWidth();
                  mediaHt = image.getHeight();
            } else {
                  LOGGER.info("image1 is larger than screen in one dimension");
                  mediaWd = SceneCntl.getScreenWd() - 400;
                  mediaHt = SceneCntl.getScreenHt() - 400;
            }

            LOGGER.info("In popup, mediaWd: {}, mediaHt: {}", mediaWd, mediaHt);
            LOGGER.debug("ImageFileName: {}", mediaFileNames[0]);
            draw = DrawTools.getInstance();
            String shapeFile = "";

            /*If shapesFile does not yet exist, Create the name*/
            if (mediaFileNames.length == 1) {
                  shapeFile = FileNaming.getShapesName(mediaFileNames[0]);
            } else {
                  shapeFile = mediaFileNames[1];
            }

            thisEditor.setShapeFileName(shapeFile);

            draw.buildDrawTools(shapeFile, thisEditor, localIView, popUpX, popUpY, mediaWd, mediaHt);
            //draw.popUpTools();

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
