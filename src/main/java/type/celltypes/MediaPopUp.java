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

package type.celltypes;

import flashmonkey.FlashMonkeyMain;
import javafx.scene.layout.Region;
import uicontrols.SceneCntl;
import fmannotations.FMAnnotations;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The media popup class. A Singleton Class. Synchronized
 */
public class MediaPopUp {

      private static MediaPopUp CLASS_INSTANCE;
      private final int screenWt = SceneCntl.getScreenWd();
      private final int screenHt = SceneCntl.getScreenHt();

      private MediaPopUp() { /* no args constructor */}

      /**
       * Returns an instance of this singleton class.
       * Synchronized, not thread safe. Expect only
       * one to exist within a JVM.
       *
       * @return
       */
      public static synchronized MediaPopUp getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new MediaPopUp();
            }
            return CLASS_INSTANCE;
      }

      /**
       * Popup with Image and Shapes:
       * <p>Creates a popUp Scene with the image and shapes
       * provided in the parameter</p>
       *
       * @param imgPath   The image path.
       * @param shapePane The unscaled image pane.
       */
      public void popUpScene(String imgPath, Pane shapePane) {

            Image img = new Image("File:" + imgPath);
            ImageView view = new ImageView(img);
            //System.out.println("called popUp using (ImageView view, Pane shapePane)");
            double wd = img.getWidth() + 4;
            wd = wd < screenWt ? wd : screenWt;
            double ht = img.getHeight() + 4;
            ht = ht < screenHt ? ht : screenHt;

            StackPane pane = new StackPane();
            pane.setMaxWidth(screenWt - 50);
            pane.setMaxHeight(screenHt - 100);
            pane.setPrefSize(wd, ht);
            pane.getChildren().add(view);

            shapePane.setStyle("-fx-background-color: TRANSPARENT");
            testingShapesPane = new Pane(shapePane);

            pane.getChildren().add(shapePane);

            Scene scene = new Scene(pane, wd, ht);

            getInstance();
            FlashMonkeyMain.setActionWindow(scene);
      }

      /**
       * Popup with shapes only:
       * <p>Creates a popUp Scene with the shapes provided in the parameter</p>
       *
       * @param shapePane
       */
      public void popUpScene(Pane shapePane) {
            shapePane.setMaxWidth(screenWt);
            shapePane.setMaxHeight(screenHt);
            Scene scene = new Scene(shapePane);

            FlashMonkeyMain.setActionWindow(scene);
      }

      /**
       * Popup with image only:
       * <p>Creates a popUp Scene with an image from the
       * path provided in the parameter</p>
       *
       * @param imgPath The image to be displayed
       */
      public void popUpScene(String imgPath) {
            Image img = new Image("File:" + imgPath);
            ImageView view = new ImageView(img);
            view.setPreserveRatio(true);

            double wd = sizeWidth(img.getWidth() + 4);
            double ht = sizeHeight(img.getHeight() + 4);

            if (wd > ht) {
                  view.setFitHeight(ht);
            } else {
                  view.setFitWidth(wd);
            }

            StackPane stack = new StackPane(view);
            Scene scene = new Scene(stack);
            getInstance();
            FlashMonkeyMain.setActionWindow(scene);
      }

      private double sizeWidth(double imgWidth) {
            double wd = imgWidth < screenWt ? imgWidth : screenWt;
            return wd < 1024 ? wd : 1024;
      }

      private double sizeHeight(double imgHeight) {
            double ht = imgHeight < screenHt ? imgHeight : screenHt;
            return ht < 1024 ? ht : 1024;
      }

      // ******  Media MediaPopUp ******


      // *** FOR TESTING ***
      @FMAnnotations.DoNotDeployField
      Pane testingShapesPane;

      @FMAnnotations.DoNotDeployMethod
      public Pane testingGetShapePane() {
            return testingShapesPane;
      }

}
