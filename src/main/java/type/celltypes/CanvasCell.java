package type.celltypes;

import draw.shapes.FMRectangle;
import draw.shapes.GenericShape;
import fileops.FileOpsShapes;
import fileops.MediaSync;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import fmannotations.FMAnnotations;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.tools.imagery.Fit;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * Contains images and shapes/drawings
 *
 * @author Lowell Stadelman
 */
public class CanvasCell<T extends GenericShape> extends GenericCell implements Serializable {
    private static final long serialVersionUID = FlashMonkeyMain.VERSION;

    // THE LOGGER
    // Logging reporting level is set in src/main/resources/logback.xml
    private static final Logger LOGGER = LoggerFactory.getLogger(CanvasCell.class);

    /**
     * Builds the canvas cell.
     * Note: If there is an image, the size of the pop-up comes from the
     * original image size, and the scale in Fit is set by the original
     * image size. If using a drawing created by DrawTools, the size of
     * the popup is set using the first element in the ArrayOfShapes
     * files. The first element is a rectangle that is the size of
     * the original pane.
     */
    String shapesPath = "";

    public Pane buildCell(Pane canvasPane, double imgWd, double imgHt, boolean rescalable, String... canvasPaths) {


        LOGGER.info("called, img and canvasPath: {}", canvasPaths);
        canvasPane.setId("rightPaneWhite");

        // clear the fields
        canvasPane.getChildren().clear();
        String imagePath = canvasPaths[0];
        ImageView scaledView;

        // Process shapes if present.
        // Get the shapes file from the second element
        // in canvasPaths.
        if (canvasPaths.length > 1) {
            shapesPath = canvasPaths[1];

            if (imagePath.endsWith("null")) {

               //System.out.println("imageBool is false, processing shape with -NO- image");

                canvasPane = InnerShapesClass.getShapesPane(shapesPath, true, true);
                canvasPane.setOnMouseClicked(e -> {
                    LOGGER.info("mouse clicked on Media popup with image. shapesPath: " + shapesPath);
                     MediaPopUp.getInstance().popUpScene(InnerShapesClass.getShapesPane(shapesPath, false, false));
                });

            } else {

                scaledView = processImage(imagePath, imgWd, imgHt, rescalable);
                canvasPane.getChildren().add(scaledView);
                canvasPane.getChildren().add(InnerShapesClass.getShapesPane(shapesPath, true, false));
                canvasPane.setOnMouseClicked(e -> {
                    LOGGER.info("mouse clicked on Media popup with image. imageFilePath: " + imagePath + ", shapesPath: " + shapesPath);
                    MediaPopUp.getInstance().popUpScene( imagePath,
                            InnerShapesClass.getShapesPane(shapesPath, false, false));
                });
            }

        } else {
            // process image only

            scaledView = processImage(imagePath, imgWd, imgHt, rescalable);
            canvasPane.getChildren().add(scaledView);

            canvasPane.setOnMouseClicked(e -> {
                LOGGER.info("mouse clicked on Media popup with image");
                MediaPopUp.getInstance().popUpScene(imagePath);

            });
        }

        //** SHAPES  and DrawPad**
        //   - 1st shape is an FMRectangle and
        // its ht and wd sets the drawpad size.
        // If no shapes are present do not show the pad.
        if (shapesPath.equals("")) { // && shapesPaths.length() < 1) {


        } //else {
           //System.out.println("shapesPath.equals(\"\")");
        //}

        return canvasPane;
    } // --- end buildCell ---



    private static ImageView iView;

    /**
     *  Helper method to buildCell(). Processes and scales an image to the
     *  size of it's container. Checks if media files are synchronized.
     *
     * @param imgPath The path to the image
     * @param imgWd
     * @param imgHt
     * @param rescaleable If this ImageView should resize/scale if the
     *                 container it is in grows or shrinks.
     * @return Returns an ImageView containing a scaled
     * image that was provided in the parameter. Resizes if scalable is true
     * else it  does not rescale if it's container is resized after its initial
     * size.
     */
        public static ImageView processImage(final String imgPath, double imgWd, double imgHt, boolean rescaleable) {

            //ImageView iView;
            Image image;

            File check = new File(imgPath);
            if (check.exists()) {
               //System.out.println("\t Buggs was right");
                LOGGER.info("\t imagePath: " + imgPath);

                image = new Image("File:" + imgPath);
            } else {
                image = new Image("emojis/concentrating-poop-emoji.png");
               //System.out.println("\t Martians don't exist");
                LOGGER.warn("\t imagePath: {}", imgPath);
                // If the image is missing,
                // check if files are synchronized,
                // and attempt to download missing files.
                LOGGER.warn("Calling syncMedia from CanvasCell.processImage. Image is missing");
                MediaSync.syncMedia();
            }

            //if (scaled) {
               //System.out.println("creating iView using scaled");
                iView = Fit.viewResize(image, imgWd, imgHt);

            iView.setPreserveRatio(true);
            iView.setSmooth(true);


            if(rescaleable) {
                ReadFlash.getInstance().getMasterBPane().widthProperty().addListener((obs, oldval, newVal) -> iView.setFitWidth(newVal.doubleValue() - 8));
                ReadFlash.getInstance().getMasterBPane().heightProperty().addListener((obs, oldval, newVal) -> iView.setFitHeight((newVal.doubleValue() - 214) / 2));
            }

            return iView;
        }



    // *** GETTERS ***

    /**
     * Resizes an SVG shape. Not finished
     *
     * @param svgShape
     * @param h
     * @param w
     */
    public void resizeSVG(Shape svgShape, int h, int w) {
        double originalWidth = h;
        double originalHeight = w;

        double scaleX = w / originalWidth;
        double scaleY = h / originalHeight;

        svgShape.setTranslateY((h - 100) / 2);
        svgShape.setTranslateX((w - 100) / 2);
        svgShape.setScaleX(scaleX);
        svgShape.setScaleY(scaleY);
    }

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
         * @param scaled Is the result a smaller size than original?
         * @param drawingPath Path to the shapes, includes shapes file name.
         * @return
         */
        public static Pane getShapesPane(String drawingPath, boolean scaled, boolean drawingOnly) {

            Pane shapesPane = new Pane();
            fmShapesAry = new ArrayList<>();
            if(drawingOnly) {
                shapesPane.setId("rightPaneWhite");
            } else {
                shapesPane.setId("rightPaneTransp");
            }

            // If the shapesPath is not null
            File check = new File(drawingPath);

            LOGGER.info("file check = " + check.exists() + " drawingPath: " + drawingPath);

            if (check.exists()) {

               //System.out.println("\t There are drawings on mars too!!!");
                FileOpsShapes fo = new FileOpsShapes();
                fmShapesAry = fo.getListFromFile(drawingPath);
                LOGGER.info("getShapesPane() getListFromFile request using drawingPath: {}", drawingPath);
                //System.out.println("\n\tfmShapesAry size: " + fmShapesAry.size());

                if (fmShapesAry == null || fmShapesAry.size() == 0) {
                    LOGGER.warn("shapes array is null or empty");
                } else {

                   //System.out.println("\n\tfmShapesAry size: " + fmShapesAry.size());

                    // The first shape, or shapesPaths[0], is the FMRectangle containing the
                    // resizable drawpad/pane size. Use FMRectagnel's getWd and getHt from
                    // the first shape.
                    double origWd = ((FMRectangle) fmShapesAry.get(0)).getWd();
                    double origHt = ((FMRectangle) fmShapesAry.get(0)).getHt();

                   //System.out.println("origWd: " + origWd);
                   //System.out.println("origHt: " + origHt);


                    if (scaled) {
                        double scale;
                        scale = Fit.calcScale(origWd, origHt, 100, 100);

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
         * @param shapesAry
         */
        public static void setFmShapesAry(ArrayList<GenericShape> shapesAry ) {
            fmShapesAry = shapesAry;
        }
    }

    /* Test Only Methods */

    @FMAnnotations.DoNotDeployMethod
    public CanvasCell getCanvasCell() {
        return this;
    }

}
