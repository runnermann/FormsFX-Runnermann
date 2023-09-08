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

import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import type.draw.shapes.FMRectangle;
import type.draw.shapes.GenericShape;
import fileops.FileOpsShapes;
import flashmonkey.FlashMonkeyMain;
import fmannotations.FMAnnotations;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.tools.imagery.Fit;
import uicontrols.MediaWait;
import uicontrols.SceneCntl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Contains images and shapes/drawings
 *
 * @author Lowell Stadelman
 */
public class CanvasCell implements Serializable {
      private static final long serialVersionUID = FlashMonkeyMain.VERSION;
      // THE LOGGER
      // Logging reporting level is set in src/main/resources/logback.xml
      private static final Logger LOGGER = LoggerFactory.getLogger(CanvasCell.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CanvasCell.class);

      String shapesPath = "";

      /**
       * Builds the canvas cell.
       * Note: If there is an image, the size of the pop-up comes from the
       * original image size, and the scale in Fit is set by the original
       * image size. If using a drawing created by DrawTools, the size of
       * the popup is set using the first element in the ArrayOfShapes
       * files. The first element is a rectangle that is the size of
       * the original pane.
       */
      private double finalHt = 0;
      private double finalWd = 0;
      public Pane buildCell(Pane canvasPane, double toWd, final double notUsed, String... canvasPaths) {
            //LOGGER.setLevel(Level.DEBUG);
            int toHt = SceneCntl.calcCellHt();
            LOGGER.info("called, img and canvasPath: {}", canvasPaths);
            canvasPane.setId("rightPaneWhite");
            // clear the fields

            final String imagePath = canvasPaths[0];
            final ImageView[] scaledView = new ImageView[1];
            // Process shapes if present.
            // Get the shapes file from the second element
            // in canvasPaths.
            if (canvasPaths.length > 0) {
                  shapesPath = canvasPaths[1];
                  // Shapes only
                  if (imagePath.endsWith("null")) {
                        LOGGER.debug("imageBool is false, processing shape with -NO- image");

                        canvasPane = InnerShapesClass.getShapesPane(shapesPath, true, true, toWd, toHt);
                        canvasPane.setOnMouseClicked(e -> {
                              LOGGER.info("mouse clicked on Media popup with image. shapesPath: " + shapesPath);
                              MediaPopUp.getInstance().popUpScene(InnerShapesClass.getShapesPane(shapesPath, false, false, toWd, toHt));
                        });
                  // Image or image and shapes
                  } else {
                        final File file = new File("File:" + imagePath);
                        if( file.canRead()) { // Set to the reverse 2022-11-4. In windows, the opposite is true
                              System.out.println("Image file WILL NOT read: " + imagePath);
                        } else {

                              // ********** NEW ********** //
                              final Pane finalCanvasPane = canvasPane;
                              ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
                              AtomicInteger ct = new AtomicInteger();
                              Pane finalCanvasPane1 = canvasPane;
                              Runnable task = () -> {
                                    // crate image
                                    Image image = new Image("File:" + imagePath, true);
                                    scaledView[0] = Fit.viewResize(image, toWd, toHt);
                                    scaledView[0].setSmooth(true);

                                    image.progressProperty().addListener((obs, ov,  nv) -> {

                                          if(nv.doubleValue() == 1 && !image.isError()) {
                                                finalCanvasPane1.getChildren().clear();
                                                scheduledExecutor.shutdownNow();

                                                scaledView[0] = Fit.viewResize(image, toWd, toHt);
                                                scaledView[0].setSmooth(true);

                                                finalCanvasPane.getChildren().clear();
                                                finalCanvasPane.getChildren().add(scaledView[0]);
                                                finalCanvasPane.getChildren().add(InnerShapesClass.getShapesPane(shapesPath, true, false, toWd, toHt));

                                                finalCanvasPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                                      @Override
                                                      public void handle(MouseEvent event) {
                                                            MediaPopUp.getInstance().popUpScene(imagePath,
                                                                    InnerShapesClass.getShapesPane(shapesPath, false, false, toWd, toHt));
                                                      }
                                                });


                                          } else if (ct.get() < 1){
                                                ct.set(1);
                                                finalCanvasPane.getChildren().clear();
                                                // show working screen.
                                                //scaledView[0] = MediaWait.getPreBlur(finalWd, finalHt);
                                                finalCanvasPane.getChildren().add(MediaWait.getPreBlur(toWd));
                                          }
                                    });

                                    return;
                              };

                              scheduledExecutor.scheduleWithFixedDelay(task, 434, 300, TimeUnit.MILLISECONDS);
                        }
                  }
            }
            return canvasPane;
      } // --- end buildCell ---

//      public static void setPlayLeft(TranslateTransition trx) {
//            MediaWait.playLeft = trx;
//      }

      public static TranslateTransition transitionFmLeft(Node node) {
            // Transition the textframe
            final TranslateTransition trans = new TranslateTransition(Duration.millis(1400), node);
            trans.setFromX(-500f);
            trans.setToX(0);
            trans.setCycleCount(TranslateTransition.INDEFINITE);
            trans.setAutoReverse(false);

            return trans;
      }



      // *** GETTERS ***

//      /**
//       * Resizes an SVG shape. Not finished
//       *
//       * @param svgShape
//       * @param h
//       * @param w
//       */
//      public void resizeSVG(Shape svgShape, int h, int w) {
//            double originalWidth = h;
//            double originalHeight = w;
//            double scaleX = w / originalWidth;
//            double scaleY = h / originalHeight;
//
//            svgShape.setTranslateY((h - 100) / 2);
//            svgShape.setTranslateX((w - 100) / 2);
//            svgShape.setScaleX(scaleX);
//            svgShape.setScaleY(scaleY);
//      }

      /*************************************************************
       INNER CLASS
       For shapes
       *************************************************************/

      public static class InnerShapesClass {
            private static ArrayList<GenericShape> fmShapesAry;

            /**
             * Helper method to buildCell(). Parses shapes from the
             * drawing filePath provided, and outputs them in a
             * scaled size or in thier original size depending on the
             * boolean parameter scaled. If true they are scaled.
             *
             * @param scaled      Is the result a smaller size than original?
             * @param drawingPath Path to the shapes, includes shapes file name.
             * @return
             */
            public static Pane getShapesPane(String drawingPath, boolean scaled, boolean drawingOnly, double toWd, double toHt) {

                  LOGGER.debug("getShapesPane called");
                  final Pane shapesPane = new Pane();
                  fmShapesAry = new ArrayList<>();
                  if (drawingOnly) {
                        shapesPane.setId("rightPaneWhite");
                  } else {
                        shapesPane.setId("rightPaneTransp");
                  }

                  // If a shapeFile is not uploaded, do not crash the system with null.
                  // If the shapesPath is not null
                  final File check = drawingPath != null ? new File(drawingPath) : new File("pathDoesNotExist");
                  LOGGER.info("file check = " + check.exists() + " drawingPath: " + drawingPath);

                  if (check.exists()) {
                        final FileOpsShapes fo = new FileOpsShapes();
                        fmShapesAry = fo.getListFromPath(drawingPath);
                        LOGGER.info("getShapesPane() getListFromFile request using drawingPath: {}", drawingPath);

                        if (fmShapesAry == null || fmShapesAry.size() == 0) {
                              LOGGER.warn("shapes array is null or empty");
                        } else {
                              // The first shape, or shapesPaths[0], is the FMRectangle containing the
                              // resizable drawpad/pane size. Use FMRectagnel's getWd and getHt from
                              // the first shape.
                              final double origWd = ((FMRectangle) fmShapesAry.get(0)).getWd();
                              final double origHt = ((FMRectangle) fmShapesAry.get(0)).getHt();
                              if (scaled) {
                                    final double scale = Fit.calcScale(origWd, origHt, toWd, toHt);
                                    for (int i = 1; i < fmShapesAry.size(); i++) {
                                          shapesPane.getChildren().add(fmShapesAry.get(i).getScaledShape(scale));
                                    }
                              } else {
                                    for (int i = 1; i < fmShapesAry.size(); i++) {
                                          shapesPane.getChildren().add(fmShapesAry.get(i).getShape());
                                          shapesPane.setPrefWidth(origWd);
                                          shapesPane.setPrefHeight(origHt);
                                    }
                              }
                        }
                  } else {
                        // Check if files are the most current
                        // MediaSync.syncMedia();
                  }

                  return shapesPane;
            }

            public static ArrayList<GenericShape> getFMShapesAry() {
                  return fmShapesAry;
            }

            /**
             * Returns the arrayOfFmShapes in original size,
             * <p>The first element in the array is a rectangle set at the
             * original size of the image that the shapes are overlayed on.
             * If the Shapes are over a DrawPad, the first eleemtn is the size
             * of the DrawPad. </p>
             *
             * @param shapesAry
             */
            public static void setFmShapesAry(ArrayList<GenericShape> shapesAry) {
                  fmShapesAry = shapesAry;
            }
      }

      /* Test Only Methods */

      @FMAnnotations.DoNotDeployMethod
      public CanvasCell getCanvasCell() {
            return this;
      }

}
