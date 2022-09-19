package type.celleditors;

import media.sound.SoundEffects;
import type.draw.DrawObj;
import flashmonkey.CreateFlash;
import uicontrols.SceneCntl;
import uicontrols.UIColors;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Singleton class is synchronized and thread safe... Maybe
 * DESCRIPTION: This class provides methods and variables for taking a snapshot of the
 * EncryptedUser's screen
 * ALGORITHM:
 * - Locate the first xy position of the mouse on the first mouse click
 * - Draw a square from the first xy to the current position of the mouse
 * - Get the second xy position of the mouse.
 * - flash the box or pic image, or make camera sound?
 * - show image in image box?
 * - save image to directory on EncryptedUser.EncryptedUser save of flash card.
 * - provide the name of the image file.
 * - provide image delete tool?
 * - provide image trim tool?
 * - close canvas, rectangle, and stackPane
 *
 * @author Lowell Stadelman
 */
public class SnapShot //extends SectionEditor //implements FMMouseInterface
{
      // Only one instance of this class may exist within a JVM.
      private static SnapShot CLASS_INSTANCE;
      private static final Logger LOGGER = LoggerFactory.getLogger(SnapShot.class);

      // The transparent stage used to capture an image from the screen.
      static Stage snapStage;
      // The scene that the image is captured from
      private Scene snapScene;
      // Min is the upper left xy, and max is lower
      // right xy of rectangle.
      private int minX, minY, screenY;
      // The image in the buffer
      private BufferedImage imageBuffer;
      private GraphicsContext gc;
      // gets the size of the screen to place a scene on top of it.
      // only way to capture the screenshot.
      private final int screenWt = SceneCntl.getScreenWd();
      private final int screenHt = SceneCntl.getScreenHt();
      private static int deltaX; // = (int) e.getSceneX() - this.minX;
      private static int deltaY; // = (int) e.getSceneY() - this.minY;
      private static Canvas canvas;
      private StackPane stackPane;
      private String imgName;
      // passes values from SectionEditor to SnapShot, then to DrawTools
      private DrawObj drawObj;


      /*******  METHODS ********/

      /**
       * no args constructor
       */
      private SnapShot() { /* empty */}

      /**
       * Returns an instance of the class. Must set
       * the currentCardID, the type media = char letter, and
       * the parentEditor using the convienience
       * snapShotBuilder(...) method.
       *
       * @return CLASS_INSTANCE
       */
      public static synchronized SnapShot getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new SnapShot();
            }
            return CLASS_INSTANCE;
      }

      /**
       * Builder used to set the variables
       * for this class.
       * //@param deckName
       * //@param letter The media type
       */
      public void snapShotBuilder(DrawObj drawObj) {
            this.drawObj = drawObj;
            //this.snapStage = snapStage;
            start(snapStage);
            //    snapStage.show();
      }


      /**
       * stage start
       *
       * @param stage
       */
      public void start(Stage stage) {
            stage.setHeight(screenHt);
            stage.setWidth(screenWt);

            canvas = new Canvas();
            canvas.setHeight(screenHt);
            canvas.setWidth(screenWt);
            // Set exit on escape key press
            canvas.setOnKeyPressed((KeyEvent e) -> {
                  if (e.getCode() == KeyCode.ESCAPE) {
                        //System.out.println(" escape pressed while canvas is active");
                        onClose();
                  }
            });


            // Pane for taking the ScreenShot
            stackPane = new StackPane();
            stackPane.setStyle("-fx-background-color: TRANSPARENT");
            stackPane.getChildren().add(canvas);

            // Set the scene size to the screen size
            // Work around for windows 10
            snapScene = new Scene(stackPane, screenWt - 8, screenHt - 8, new Color(0, 0, 0, 1d / 255d));
            // and cursor
            snapScene.setCursor(Cursor.CROSSHAIR);
            // instantiate GraphicContext & set, mouse actions handle
            // creating the rectangle on the users screen.
            gc = canvas.getGraphicsContext2D();
            gc.setLineWidth(3);
            //@TODO speed up dotted lines on snapshot
            gc.setLineDashes(10d, 7d, 3d, 7d); //really slow
            gc.setStroke(Color.web(UIColors.BELIZE_BLUE, 0.9));
            // mouse actions
            snapScene.setOnMousePressed(e -> mousePressed(e));
            snapScene.setOnMouseDragged(e -> mouseDragged(e));
            snapScene.setOnMouseReleased(e -> mouseReleased(e));

            // exit on escape key press
            snapScene.setOnKeyPressed(e -> {
                  KeyCode key = e.getCode();
                  if (key == KeyCode.ESCAPE) {
                        onClose();
                  }
            });
            //Set stage to scene, "return stage"
            stage.setScene(snapScene);
      }

      // ******* Mouse Actions ****** //

      /**
       * Action for mouse initial press. C
       *
       * @param e MouseEvents
       */
      // On mouse down, locate the first xy
      // position of the mouse.
      public void mousePressed(MouseEvent e) {
            SoundEffects.ROBOT_SERVO.play();
            this.minX = (int) e.getSceneX();
            this.minY = (int) e.getSceneY();
            this.screenY = (int) e.getScreenY();

            //LOGGER.debug("minX = " + minX);
            //LOGGER.debug("minY = " + minY);

            e.consume();
      }

      /**
       * Draws a square from the first xy to the current location of the mouse
       *
       * @param e
       */
      public void mouseDragged(MouseEvent e) {
            gc.clearRect(0, 0, this.screenWt, this.screenHt);
            deltaX = (int) e.getSceneX() - this.minX;
            deltaY = (int) e.getSceneY() - this.minY;

            if ((deltaX & deltaY) < 0) // bit hack, if both are negitive
            {
                  deltaX = deltaX * -1;
                  deltaY = deltaY * -1;
                  this.minX = (int) e.getSceneX();
                  this.minY = (int) e.getSceneY();
                  gc.strokeRect(this.minX, this.minY, deltaX, deltaY);
            } else if (deltaX < 0) {
                  deltaX = deltaX * -1;
                  this.minX = (int) e.getSceneX();
                  gc.strokeRect(this.minX, this.minY, deltaX, deltaY);
            } else if (deltaY < 0) {
                  deltaY = deltaY * -1;
                  this.minY = (int) e.getSceneY();
                  gc.strokeRect(this.minX, this.minY, deltaX, deltaY);
            } else {
                  gc.strokeRect(this.minX, this.minY, deltaX, deltaY);
            }

            e.consume();
      }


      /**
       * saves the current Mouse postion in relation to the screen
       * at the rectangle min X and min Y.
       * <p>
       * algorithm:
       * 1) Save the image.
       * 2) convert mouse crosshairs back to pointer
       * 3) Pop-up drawingPane next to image.
       * a) and limit the bounds of the drawing area.
       *
       * @param e
       */
      public void mouseReleased(MouseEvent e) {
            SoundEffects.CAMERA.play();

            // Save the image
            captureImage();
            //this.saveImage();

            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                  // Mac
                  drawObj.setDems(this.minX, this.minY + 15, deltaX, deltaY);
            } else {
                  // Windows
                  drawObj.setDems(this.minX, this.minY, deltaX, deltaY);
            }
            // convert mouse crosshairs back to pointer
            snapScene.setCursor(Cursor.DEFAULT);
            // Close the stage
            snapStage.close();
            e.consume();

            //m.dispose();
      }


      /**
       * Gets the image created in the rectangle, holds it in a buffer until it
       * is saved to a file.
       */
      private void captureImage() {
            Rectangle rect = new Rectangle(20, 20, 200, 200);
            if ((Math.abs(deltaX) - 6) > 0 && (Math.abs(deltaY) - 6) > 0) {
                  rect = new Rectangle(this.minX + 3, this.screenY + 3, Math.abs(deltaX) - 6, Math.abs(deltaY) - 6);
                  //        drawObj.setDems(this.minX + 3, this.screenY + 3, Math.abs(deltaX) - 6, Math.abs(deltaY) - 6);
            }
            try {
                  // AWT version
                  imageBuffer = new Robot().createScreenCapture(rect);
            } catch (AWTException ex) // for Robot.createScreenCapture errors
            {
                  System.err.println("ERRROR: AWTException in SnapShot while saving image to file" +
                      "\n line 254ish");
                  ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
                  System.err.println("ERROR: IllegalArgumentException in SnapShot while saving image to file.");
                  ex.printStackTrace();
            }
      }


      /**
       * Creates the mediaName, and saves the file to the hard drive. Sets
       * the mediaName in SectionEditor to the current fileName for this image.
       * @param fileName ImageName
       */
    /*protected void saveImage(String fileName) {
        try {
            String path = DirectoryMgr.getMediaPath('c');
            FileOpsUtil.folderExists(new File(path + fileName));
            ImageIO.write(imageBuffer, "png", new File(path + fileName));
        }
        catch (IOException e) // for ImageIO.write errors
        {
           System.out.println("ERROR: IO Exception in snapShot while saving image to file" +
                    "\n line 253");
           e.printStackTrace();
        }
    }*/

      // ********** GETTERS ******** //

      /**
       * Returns the ImageBuffer.
       *
       * @return Returns the ImageBuffer.
       */
      public BufferedImage getImgBuffer() {
            return this.imageBuffer;
      }


      // remove rectangle
      protected void removeRect(MouseEvent e) {
            minY = 0;
            minX = 0;
            screenY = 0;
      }

      /**
       * Called when the user presses the escape key to
       * abort a snapshot.
       */
      public void onClose() {
            if (snapStage != null) {
                  snapStage.close();
                  imageBuffer = null;
                  CreateFlash cfp = CreateFlash.getInstance();
                  cfp.enableButtons();
            }
            //super.onClose();
      }
}
