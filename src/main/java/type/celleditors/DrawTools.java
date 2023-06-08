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

package type.celleditors;

import ch.qos.logback.classic.Level;
import fileops.BaseInterface;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import media.sound.SoundEffects;
import org.slf4j.Logger;
import type.draw.DrawObj;
import type.draw.shapes.*;
import uicontrols.FMAlerts;
import uicontrols.UIColors;
import fmannotations.FMAnnotations;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import flashmonkey.CreateFlash;
import fileops.FileOpsShapes;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;


/**
 * <p>Singleton Class. Not synchronized, not thread-safe.</p>
 * This class contains the drawing tools to draw over an image, or create an SVG drawing on a canvas.
 * Desirements:
 * - Click on a shapeButton once, and add several shapes with each new click on overlayPane.
 * - Delete shapes
 * - Rotate a shape
 * - Resize a shape
 * - Color a shape
 * - Select several shapes and manipulate them
 * - Each SVGshape is a series of points. As a group of shapes is grabbed at an outside point. And
 * the point is dragged, All points grow or shrink by x = deltaX / 2, and y = deltaY / 2. Arcs grow
 * by ???
 * - Copy a shape
 * - Add a copied shape to another shape
 * <p>
 * Algorithm:
 * 1) When User releases the Mouse button while taking a snapshot, toolPane pops up near the mouse.
 * 2) The User clicks a shape, moves the mouse to the canvas, clicks and holds the mouse button down,
 * drags the shape to the desired size, and releases.
 * 3) //@TODO The shape has an axis and can be rotated on the axis
 * 4) The shape has handles so the User.User can resize the shape.
 * 5) Saving: After the User.User presses a shape button, then clicks on the overlayPane
 * - newShape is true
 * - The shape is created according to the mouse drag.
 * - When the EncryptedUser.EncryptedUser releases the mouse button
 * - If the shape Width or Height are greater than 0 and newShape is true, it is added to the arrayOfBuilderShapes.
 * 6) When the User exits the ToolWindow, the arrayOfBuilderShapes are saved to a file for this card.
 * <p><b>NOTE:</b> This class is a save on exit action.</p>
 * <p>This class makes use of underlying supporting classes such as GenericShape, and GenericBuilder for example.
 *   GenericBuilder contains the gbcopyArrayOfFMShapes. </p>
 * <p>ArrayOfFMShapes.get(0) and GBCopyOfArrayOfFMShapes contains the size of the window for DrawPad. </p>
 * @author Lowell Stadelman
 */
public class DrawTools implements BaseInterface
{
      private static DrawTools CLASS_INSTANCE;

      // THE LOGGER
      // REMOVE BEFORE
      private static final Logger LOGGER = LoggerFactory.getLogger(DrawTools.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DrawTools.class);

      // Includes a reference to the right pane. Note in this case
      // pane should be spelled pain. As in the right 'Pain in the arse!'.
      private static SectionEditor classEditorRef;

      // For the overlay window. The window that shapes
      // are drawn in.
      private Stage overlayWindow;
      private Scene overlayScene;
      private Pane overlayPane;
      // Nodes for grabbing a shape
      private Pane nodesPane;
      // Contains the shapes and the image
      private Canvas overlayCanvas;

      // For the ToolWindow popUp
      private static Stage toolWindow;
      private static TilePane toolPane;
      // contains tool popUp and action buttons
      private static BorderPane toolMainPane;
      private static Scene toolScene;
      // contains non-shape buttons
      private static HBox zBtnHBox;
      VBox containerVBox;
      private static GridPane colorBtnGrid;
      public static GraphicsContext toolGC;
      // The shape buttons
      private static Button circleBtn, rectBtn, triangleBtn,
          polyBtn, lineBtn, arrowBtn, penBtn, txtBtn;

      // Other buttons
      private static Button deleteBtn, clearBtn, quitBtn;
      // The size of the overLayPane to be drawn on.
      private static AtomicLong overlayHt;
      private static AtomicLong overlayWd;
      // For the ShapeBuilders... IE CircleBuilder
      private static boolean shapeNotSelected;

      // Color related
      private static StringProperty strokeProperty = new SimpleStringProperty(UIColors.BELIZE_BLUE);
      private static StringProperty fillProperty = new SimpleStringProperty("0x00000000");
      private static final StringProperty strokeBTNProperty = new SimpleStringProperty(UIColors.BELIZE_BLUE);
      private static final StringProperty fillBTNProperty = new SimpleStringProperty("0x00000000");

      /**
       * private No args constructor
       */
      private DrawTools() { /* empty */ }

      /**
       * Singleton class instantiation. There
       * should only be one instance of DrawTools
       * Synchronized
       *
       * @return The class instance
       */
      public static synchronized DrawTools getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new DrawTools();
            }
            return CLASS_INSTANCE;
      }

      /**
       * Check if DrawTools has been instantiated before calling
       * a method for verifying state. No need to instantiate DrawTools
       * to verify if it exists.
       * Only public static method in this Singleton Class.
       * @return true if the CLASS_INSTANCE exists.
       */
      public static boolean instanceExists() {
            return CLASS_INSTANCE != null;
      }


      /**
       * Called by DrawPadButton in SectionEditor, Shapes and no image. Creates
       * a new 400 x 400 drawPad. Used when first creating a drawpad. For
       * editing a drawPad with exising shapes, see buildDrawTools that is
       * called by rightPane onClick.
       * <p><b>NOTE:</b> The DrawPad Window/Stage is sized at 400 x 400. The
       * 0th rect is set from the window. This is set based on the scenario
       * that the window only contains shapes and not tools or other items.
       * If other UI items are added, account for their size when the
       * rect is saved.</p>
       * <p>The 0th rect is sized when the DrawPad is clicked.</p>
       *
       * @param x             ..
       * @param y             ..
       * @param shapeFileName ..
       * @param paramEditor   ..
       */
      public void buildDrawTools(double x, double y, String shapeFileName, SectionEditor paramEditor) {
            // height and width of drawpad
            int wd = 400;
            int ht = 400;
            LOGGER.info("line 145) buildDrawTools for drawPad, fileName: " + shapeFileName);
            overlayCanvas = new Canvas();

            StageStyle style = StageStyle.UTILITY;

            overlayWindow = buildNewCanvasEditor(x, y, wd, ht, shapeFileName, paramEditor, style, null);
            // Add the 0th rectangle for the size of the drawpad.
            FMRectangle rect = setPaneSizeRectangle();
            rect.setWd(wd);
            rect.setHt(ht);
            paramEditor.getArrayOfFMShapes().add(rect);
            // Update the size of the rectangle used for the shapesPane / DrawPad size
            // in arrayOfFMShapes. Used when iView doesn't exist.
            overlayScene.setOnMouseClicked((MouseEvent e) -> {
                  rect.setWd(overlayPane.getBoundsInLocal().getWidth());
                  rect.setHt(overlayPane.getBoundsInLocal().getHeight());
                  // the 0th shape is always the size of the pane.
                  if (paramEditor.getArrayOfFMShapes().size() > 0) {
                        paramEditor.getArrayOfFMShapes().set(0, rect);
                  }
            });
            // if the user clicks outside of the pane
            // keep overlay on top ALWAYS!!!
            overlayWindow.setAlwaysOnTop(true);
            overlayWindow.show();
      }


      /**
       * Called by snapshot immediately after image is captured.
       * @param drawObj     The drawObject containing minX, minY, width, height, qOrA, cID, and deckName
       * @param paramEditor ..
       */
      public void buildDrawTools(DrawObj drawObj, SectionEditor paramEditor) {
            StageStyle style = StageStyle.TRANSPARENT;
            overlayCanvas = new Canvas();
            containerVBox = new VBox();
            containerVBox.setStyle("-fx-background-color: TRANSPARENT");
            overlayWindow = buildNewCanvasEditor(drawObj.getMinX(), drawObj.getMinY(), drawObj.getDeltaX(), drawObj.getDeltaY(),
                drawObj.getFileName(), paramEditor, style, null);
            overlayWindow.show();
      }

      /**
       * Called by SectionEditor rightPane when card exists, for editing a
       * card as opposed to creating a new card. For canvas ie shapes and image.
       * Creates a popup.
       *
       * @param shapeFileName    ..
       * @param paramEditor ..
       * @param iView       ..
       * @param x           ..
       * @param y           ..
       * @param wd          ..
       * @param ht          ..
       */
      public void buildDrawTools(String shapeFileName, SectionEditor paramEditor, ImageView iView, double x, double y, double wd, double ht) {
            StageStyle style = StageStyle.UTILITY;
            containerVBox = new VBox();
            containerVBox.setStyle("-fx-background-color: " + UIColors.FM_GREY);
            // Misc variables
            overlayCanvas = new Canvas();
            overlayHt = new AtomicLong((long) ht);
            overlayWd = new AtomicLong((long) wd);

            x = x < 20 ? 20 : x;
            y = y < 20 ? 20 : y;

            if(wd + ht != 0 && paramEditor.getArrayOfFMShapes().size() > 0) {
                  FMRectangle rect = setPaneSizeRectangle();
                  rect.setWd(wd);
                  rect.setHt(ht);
                  paramEditor.getArrayOfFMShapes().set(0, rect);
            }

            // **** INSERTING NEW CODE FROM buildDrawTools ***** //

            if (iView == null) {
                  overlayWindow = drawToolsStageSetUp(shapeFileName, style, paramEditor, x, y, wd, ht, ht + 39);
                  // drawpad/shapes, no image
                  setupForShapesOnlyEditing(wd, ht);
                  layoutBindings();
            } else {
                  // canvas: Image and/or shapes/drawpad
                  overlayWindow = drawToolsStageSetUp(shapeFileName, style, paramEditor, x, y, wd, ht, ht + 76);
                  setupForImageAndShapeEditing(iView, wd, ht);
//                  final Button[] toolBtns = toolButtons(paramEditor);
//                  setButtonsSettings(toolPane, toolBtns);
                  // Set the GraphicsContext
                  toolGC = overlayCanvas.getGraphicsContext2D();
            }

            final Scene overlayScene = overlaySceneSetup(paramEditor);

            // **** END INSERT NEW CODE ****
            overlayWindow.setScene(overlayScene);
            overlayWindow.show();
      }

      /**
       *
       * @param wd
       * @param ht
       */
      private void setupForShapesOnlyEditing(double wd, double ht) {
            overlayPane.getChildren().add(overlayCanvas);

            overlayScene = new Scene(overlayPane, wd, ht, new Color(0, 0, 0, .01));
            overlayPaneSettings(
                    ht,
                    wd,
                    overlayPane.getBoundsInParent().getMinX(),
                    overlayPane.getBoundsInParent().getMinY());
            overlayWd.set((long) overlayPane.getWidth());
            overlayHt.set((long) overlayPane.getHeight());
            // color must not be completely transparent for an overlay to work.
            //overlayScene = new Scene(overlayPane, ht, wd, new Color(0, 0, 0, .01));
      }

      /**
       *
       * @param iView
       * @param wd
       * @param ht
       */
      private void setupForImageAndShapeEditing(ImageView iView, double wd, double ht) {
            // set up for image display and editing
            ImageTools imgTools = ImageTools.getInstance();
            iView = imgTools.handler(iView);

            VBox southBox = new VBox();
            southBox.setAlignment(Pos.CENTER);
            VBox imgToolVBox = imgTools.getToolBoxContainer(classEditorRef);
            imgToolVBox.setAlignment(Pos.CENTER);
            //imgToolVBox.setPadding(new Insets(4));
            overlayPane.getChildren().addAll(iView, overlayCanvas);
            containerVBox.getChildren().addAll(overlayPane, imgToolVBox);
            // color must not be completely transparent for an overlay to work.
            overlayScene = new Scene(containerVBox, wd, ht, new Color(0, 0, 0, .01));
            overlayPaneSettings(
                    iView.getImage().getHeight(),
                    iView.getImage().getWidth(),
                    overlayPane.getBoundsInParent().getMinX(),
                    overlayPane.getBoundsInParent().getMinY());

            iView.setPreserveRatio(true);
            //            ImageView finalIView = iView;
            iView.fitHeightProperty().addListener(this::heightChangedListener);
            iView.fitWidthProperty().addListener(this::widthChangedLister);
      }

      /**
       *
       * @param shapeFileName
       * @param style
       * @param paramEditor
       */
      private Stage drawToolsStageSetUp(String shapeFileName, StageStyle style, SectionEditor paramEditor,
      double x, double y, double wd, double ht, double windowHt) {

            Stage drawPadWindow = new Stage(style);
            drawPadWindow.setTitle("DrawPad");
            // toolWindow = new Stage(StageStyle.UTILITY);

            drawPadWindow.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
                  @Override
                  public void changed(ObservableValue<? extends Boolean> prop, Boolean wasIconified, Boolean isIconified) {
                        drawPadWindow.setFullScreen(false);
                  }
            });
            // actions on close/on hidden

            drawPadWindow.setOnHidden(e -> {
                  if(null != shapeFileName) { saveOnExit(shapeFileName, paramEditor); }
                  onClose();
            });

            // Class SectionEditor referance
            classEditorRef = paramEditor;

            overlayPane = new Pane();
            overlayPane.setPrefSize(wd, ht);
            drawPadWindow.setHeight(windowHt);
            drawPadWindow.setX(x);
            drawPadWindow.setY(y);
            drawPadWindow.setAlwaysOnTop(true);
            popUpTools(paramEditor, drawPadWindow);

            return drawPadWindow;
      }

      private Scene overlaySceneSetup(SectionEditor paramEditor) {
            // Escape. Abandon, ask to save work.
            toolScene.setOnKeyPressed((KeyEvent f) -> {
                  if (f.getCode() == KeyCode.ESCAPE) {
                        abandAction(paramEditor);
                  }
            });
            // Escape, Abandon, ask to save work.
            overlayPane.setOnKeyPressed((KeyEvent f) -> {
                  if (f.getCode() == KeyCode.ESCAPE) {
                        abandAction(paramEditor);
                  }
            });
            // Update the size of the rectangle used for the shapespane size
            // in arrayOfFMShapes. Used for both with image and without.
            overlayScene.setOnMouseClicked((MouseEvent e) -> {
                  /* ******
                  NOTE: If having problems with shapes in right pane appearing and
                  disapearing. OverlayScene and OverlayPane vs OverlayCanvas may the the root cause.
                  ********* */
                  // Update the 0th shape. It is always the size of the pane.
                  FMRectangle rect = (FMRectangle) paramEditor.getArrayOfFMShapes().get(0);
                  if(overlayScene.getWidth() != 0) {
                        rect = setPaneSizeRectangle();
                        rect.setWd(overlayScene.getWidth());
                        rect.setHt(overlayScene.getHeight());
                        paramEditor.getArrayOfFMShapes().set(0, rect);
                  } else if (overlayCanvas.getWidth() != 0) {
                        rect = setPaneSizeRectangle();
                        rect.setWd(overlayCanvas.getWidth());
                        rect.setHt(overlayCanvas.getHeight());
                        paramEditor.getArrayOfFMShapes().set(0, rect);
                  }
            });

            overlayScene.getStylesheets().addAll("css/drawtools.css", "css/buttons.css");
            overlayScene.setOnMouseExited((MouseEvent e) -> releaseMouse());
            return overlayScene;
      }


      /**
       * The drawPadWindow has either an image with shapes, or just shapes. The drawPadWindow contains the overlayScene.
       * The overlayScene contains the overlayPane. The overlayPane contains an Iview with a transparent canvas "overlayCanvas"
       * that shapes are created and edited in. And the image tools are attatched below the lower border of the canves
       * within the overlayPane.
       * <p>Used when a there is no image nor drawing</p>
       * @param prevX
       * @param prevY
       * @param prevWd
       * @param prevHt
       * @param shapeFileName
       * @param paramEditor
       * @param style
       * @param iView
       * @return
       */
      private Stage buildNewCanvasEditor(double prevX, double prevY, double prevWd, double prevHt,
                                   String shapeFileName, SectionEditor paramEditor, StageStyle style, ImageView iView) {
            LOGGER.info("shapeFileName: {} ends with .shp: {}", shapeFileName, shapeFileName.endsWith(".shp"));

            Stage drawPadWindow = new Stage(style);

            prevX = prevX < 20 ? 20 : prevX;
            prevY = prevY < 20 ? 20 : prevY;

            // toolWindow = new Stage(StageStyle.UTILITY);

            drawPadWindow.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
                  @Override
                  public void changed(ObservableValue<? extends Boolean> prop, Boolean wasIconified, Boolean isIconified) {
                        drawPadWindow.setFullScreen(false);
                  }
            });

            // actions on close/on hidden
            drawPadWindow.setOnHidden(e -> {
                  if(null != shapeFileName) { saveOnExit(shapeFileName, paramEditor); }
                  onClose();
            });

            drawPadWindow.setTitle("DrawPad");

            // Misc variables
            overlayHt = new AtomicLong((long) prevHt);
            overlayWd = new AtomicLong((long) prevWd);

            // Class SectionEditor referance
            classEditorRef = paramEditor;

            // Object instantiation and pane assignment for the Drawing area
            overlayCanvas = new Canvas();
            overlayPane = new Pane();
//            overlayPane.setAlignment(Pos.CENTER);
            if (iView == null) {
                  overlayPane.getChildren().add(overlayCanvas);
                  overlayPane.setStyle("-fx-border-color: rgba(48, 128, 185, .9); " +
                          "-fx-background-color: transparent; -fx-border-width: 3;");
                  // color must not be completely transparent for an overlay to work.
                  overlayScene = new Scene(overlayPane, prevWd, prevHt, new Color(0, 0, 0, .01));


            } else {
                  // set up for image display and editing
                  ImageTools imgTools = ImageTools.getInstance();
                  iView = imgTools.handler(iView);
                  //iView.setFitHeight(prevHt);

                  VBox southBox = new VBox();
                  southBox.setAlignment(Pos.CENTER);
                  VBox imgToolVBox = imgTools.getToolBoxContainer(classEditorRef);
                  imgToolVBox.setAlignment(Pos.CENTER);
                  //imgToolVBox.setPadding(new Insets(4));
                  overlayPane.getChildren().addAll(iView, overlayCanvas);
                  containerVBox.getChildren().addAll(overlayPane, imgToolVBox);
                  // color must not be completely transparent for an overlay to work.
                  overlayScene = new Scene(containerVBox, prevWd, prevHt, new Color(0, 0, 0, .01));
                  overlayPaneSettings(iView.getImage().getHeight(),
                                      iView.getImage().getWidth(),
                                      overlayPane.getBoundsInParent().getMinX(),
                                      overlayPane.getBoundsInParent().getMinY());
//                  drawPadWindow.setOnHidden(e -> {
//                        imgTools.saveOnExit(paramEditor);
//                        if(null != shapeFileName) { saveOnExit(shapeFileName, paramEditor); }
//                        onClose();
//                  });
            }

            layoutBindings();

            // Windows 10 fix ???

            overlayScene.getStylesheets().addAll("css/drawtools.css", "css/buttons.css");

            // overlay Window settings
            drawPadWindow.setHeight(prevHt);
            drawPadWindow.setWidth(prevWd);
            drawPadWindow.setX(prevX);
            drawPadWindow.setY(prevY);
            drawPadWindow.setScene(overlayScene);
            drawPadWindow.setAlwaysOnTop(true);

            // Update the size of the rectangle used for the shapespane size
            // in arrayOfFMShapes. Used for both with image and without.
            overlayScene.setOnMouseClicked((MouseEvent e) -> {
                  FMRectangle rect = setPaneSizeRectangle();
                  rect.setWd(overlayCanvas.getWidth());
                  rect.setHt(overlayCanvas.getHeight());
                  // the 0th shape is always the size of the pane.
                  if (paramEditor.getArrayOfFMShapes().size() > 0) {
                        paramEditor.getArrayOfFMShapes().set(0, rect);
                  }
            });

            overlayScene.setOnMouseExited((MouseEvent e) -> releaseMouse());

//            final Button[] toolBtns = toolButtons(paramEditor);
//            setButtonsSettings(toolPane, toolBtns);
            // Set the GraphicsContext
            popUpTools(paramEditor, drawPadWindow);
            toolGC = overlayCanvas.getGraphicsContext2D();

            // Escape. Abandon, ask to save work.
            toolScene.setOnKeyPressed((KeyEvent f) -> {
                  if (f.getCode() == KeyCode.ESCAPE) {
                        abandAction(paramEditor);
                  }
            });
            // Escape, Abandon, ask to save work.
            overlayPane.setOnKeyPressed((KeyEvent f) -> {
                  if (f.getCode() == KeyCode.ESCAPE) {
                        abandAction(paramEditor);
                  }
            });

            return drawPadWindow;
      } // *** END buildDrawTools() ***




      /**
       * Builds the ToolButtons for DrawPad Tools
       * @param paramEditor
       * @return
       */
      private Button[] toolButtons(SectionEditor paramEditor) {
            double[] linePts = {0, 0, 20, 0};
            double[] arrowPts = {23.4970013410768, 25.295000853688194, 44.0, 13.0, 23.502999877840676, 0.6950015850386819,
                    23.501200316811513, 8.075001365633536, 3.0011997073527747, 8.080000146270098, 2.9988002926472253,
                    17.9199998537299, 23.498800902105963, 17.91500107309334, 23.4970013410768, 25.295000853688194};
            double[] polyPts = {20.0, 19.0, 32.0, 8.0, 9.0, 8.0, 9.0, 29.0, 31.0, 30.0};
            FMCircle fmCirc = new FMCircle(-10, 0, 10, 10, 3, strokeBTNProperty.getValue(), fillBTNProperty.getValue(), 0);
            FMRectangle fmRect = new FMRectangle(0, 0, 20, 20, 3, strokeBTNProperty.getValue(), fillBTNProperty.getValue(), 0);
            FMTriangle fmTri = new FMTriangle(10, 0, 20, 20, 3, strokeBTNProperty.getValue(), fillBTNProperty.getValue(), 0);
            FMLetter fmLetter = new FMLetter(0, 0, 10, 10, 3, strokeBTNProperty.getValue(), fillBTNProperty.getValue(), 0, "T");
            FMPolyLine fmLine = new FMPolyLine(linePts, 3, strokeBTNProperty.getValue(), fillBTNProperty.getValue(), 0);
            FMPolyLine fmArrow = new FMPolyLine(arrowPts, 3, strokeBTNProperty.getValue(), fillBTNProperty.getValue(), 0);
            FMPolygon fmPolygon = new FMPolygon(polyPts, 3, strokeBTNProperty.getValue(), fillBTNProperty.getValue(), 0);
            // Shapes for button symbols
            Ellipse btnCircle = fmCirc.getShape();
            Rectangle btnSqr = fmRect.getShape();
            Polygon btnTri = fmTri.getShape();
            Polyline btnLine = fmLine.getShape();
            Text btnText = fmLetter.getShape();
            btnText.setStyle("-fx-font-size: 24;");
            btnText.setFill(Color.web(strokeBTNProperty.getValue()));
            Polyline btnArrow = fmArrow.getShape();
            Polygon btnPoly = fmPolygon.getShape();

            btnCircle.fillProperty().bind(Bindings.createObjectBinding(() -> Color.web(fillBTNProperty.get()), fillBTNProperty));
            btnCircle.strokeProperty().bind(Bindings.createObjectBinding(() -> Color.web(strokeBTNProperty.get()), strokeBTNProperty));
            btnSqr.fillProperty().bind(Bindings.createObjectBinding(() -> Color.web(fillBTNProperty.get()), fillBTNProperty));
            btnSqr.strokeProperty().bind(Bindings.createObjectBinding(() -> Color.web(strokeBTNProperty.get()), strokeBTNProperty));
            btnTri.fillProperty().bind(Bindings.createObjectBinding(() -> Color.web(fillBTNProperty.get()), fillBTNProperty));
            btnTri.strokeProperty().bind(Bindings.createObjectBinding(() -> Color.web(strokeBTNProperty.get()), strokeBTNProperty));

            btnText.fillProperty().bind(Bindings.createObjectBinding(() -> Color.web(strokeBTNProperty.get()), strokeBTNProperty));
            btnText.strokeProperty().bind(Bindings.createObjectBinding(() -> Color.web(strokeBTNProperty.get()), strokeBTNProperty));

            btnLine.fillProperty().bind(Bindings.createObjectBinding(() -> Color.web(fillBTNProperty.get()), fillBTNProperty));
            btnLine.strokeProperty().bind(Bindings.createObjectBinding(() -> Color.web(strokeBTNProperty.get()), strokeBTNProperty));

            btnArrow.fillProperty().bind(Bindings.createObjectBinding(() -> Color.web(fillBTNProperty.get()), fillBTNProperty));
            btnArrow.strokeProperty().bind(Bindings.createObjectBinding(() -> Color.web(strokeBTNProperty.get()), strokeBTNProperty));

            btnPoly.fillProperty().bind(Bindings.createObjectBinding(() -> Color.web(fillBTNProperty.get()), fillBTNProperty));
            btnPoly.strokeProperty().bind(Bindings.createObjectBinding(() -> Color.web(strokeBTNProperty.get()), strokeBTNProperty));

            // Tool buttons array
            Button[] toolBtns = {
                    circleBtn = new Button("", btnCircle),
                    rectBtn = new Button("", btnSqr),
                    triangleBtn = new Button("", btnTri),
                    polyBtn = new Button("", btnPoly),
                    lineBtn = new Button("", btnLine),
                    arrowBtn = new Button("", btnArrow),
                    penBtn = new Button(""),
                    txtBtn = new Button("", btnText)
            };
            circleBtn.setAlignment(Pos.BASELINE_LEFT);
            rectBtn.setAlignment(Pos.BASELINE_LEFT);
            triangleBtn.setAlignment(Pos.BASELINE_LEFT);
            txtBtn.setAlignment(Pos.BASELINE_LEFT);
            arrowBtn.setAlignment(Pos.BASELINE_LEFT);
            lineBtn.setAlignment(Pos.BASELINE_LEFT);
            polyBtn.setAlignment((Pos.BASELINE_LEFT));
            penBtn.setAlignment(Pos.BASELINE_LEFT);
            penBtn.setDisable(true);

            // Color buttons for Fill Color and Stroke Color
            final int btnSize = 20;
            String[] colors = {
                    UIColors.FM_WHITE, UIColors.FLASH_RED, UIColors.HIGHLIGHT_PINK,
                    UIColors.HIGHLIGHT_ORANGE, UIColors.HIGHLIGHT_YELLOW, UIColors.HIGHLIGHT_GREEN,
                    UIColors.BELIZE_BLUE_OPAQUE, UIColors.GRAPH_BGND, UIColors.FLASH_BLACK
            };

            Shape[] strokeColors = createColors(btnSize, false, colors);
            Shape[] fillColors = createColors(btnSize, true, colors);

            colorBtnGrid = new GridPane();
            colorBtnGrid.setVgap(2);
            colorBtnGrid.setHgap(2);
            colorBtnGrid.setPadding(new Insets(10, 2, 2, 2));
            colorBtnGrid.setAlignment(Pos.CENTER);
            colorBtnGrid.addRow(0, strokeColors);
            colorBtnGrid.addRow(1, fillColors);

            //  Set shapes to move in right pane as shapes are moved
            //  in overlayPane

            // Shape button calls
            arrowBtn.setOnAction(a -> arrowBtnAction(paramEditor));
            rectBtn.setOnAction(a -> rectBtnAction(paramEditor));
            circleBtn.setOnAction(a -> circleBtnAction(paramEditor));
            triangleBtn.setOnAction(a -> triangleBtnAction(paramEditor));
            lineBtn.setOnAction(a -> lineBtnAction(paramEditor));
            polyBtn.setOnAction(a -> polyBtnAction(paramEditor));
            txtBtn.setOnAction(a -> txtBtnAction(paramEditor));

            // Service buttons
            deleteBtn = new Button("delete");
            deleteBtn.setTooltip(new Tooltip("Deletes the active shape"));

            clearBtn = new Button("clear");
            clearBtn.setTooltip(new Tooltip("Clear all shapes"));
            clearBtn.setOnAction(a -> clearShapesAction(paramEditor));

            quitBtn = new Button("quit");
            quitBtn.setTooltip(new Tooltip("Exits without saving changes."));
            quitBtn.setOnAction(e -> quitBtnAction());

            zBtnHBox = new HBox(quitBtn, clearBtn);
            zBtnHBox.setAlignment(Pos.CENTER);
            zBtnHBox.setPadding(new Insets(2, 0, 4, 0));
            zBtnHBox.setId("buttonBox");
            // Pane containing the shape buttons
            toolPane = new TilePane();
            toolPane.setVgap(3);
            toolPane.setHgap(3);
            toolPane.setPrefColumns(2);
            toolPane.setAlignment(Pos.CENTER);
            toolPane.setPadding(new Insets(10, 4, 20, 4));
            // Pane containing nodes when a shape is
            // resized.
            nodesPane = new Pane();

            return toolBtns;
      }

      private void overlayPaneSettings(double ht, double wd, double minX, double minY) {
            overlayHt.set((long) ht);
            overlayWd.set((long) wd);
            overlayPane.setMaxSize(wd, ht);
            overlayPane.setMinSize(wd, ht);
            overlayPane.setLayoutX(minX);
            overlayPane.setLayoutY(minY);
            overlayPane.setStyle("-fx-border-color: rgba(48, 128, 185, .9); " +
                    "-fx-background-color: transparent; -fx-border-width: 3;");
      }


      /**
       * The popup tool window
       */
      public void popUpTools(SectionEditor paramEditor, Stage overlayWindow) {
            toolWindow = new Stage(StageStyle.UTILITY);
            toolMainPane = new BorderPane();
            toolScene = new Scene(toolMainPane);

            toolWindow.setResizable(false);

            final Button[] toolBtns = toolButtons(paramEditor);
            setButtonsSettings(toolPane, toolBtns);

            toolMainPane.setTop(colorBtnGrid);
            toolMainPane.setCenter(toolPane);
            toolMainPane.setBottom(zBtnHBox);
            toolMainPane.setStyle("-fx-background-color: " + UIColors.GRAPH_BGND);
            toolWindow.setScene(toolScene);
            double x = overlayWindow.getX() + overlayWindow.getWidth() + 10;
            double y = overlayWindow.getY() + 10;
            toolWindow.setX(x);
            toolWindow.setY(y);
            toolWindow.setTitle("Draw tools");
            // toolWindow/stage should always be
            // on top until it is closed.
            toolWindow.setAlwaysOnTop(true);
            toolWindow.setOnHidden(e -> onClose());
            toolWindow.show();
      }


      /**
       * *********************************************************************************************************** ***
       * *
       * GETTERS
       * *
       * * ************************************************************************************************************
       **/

      public StringProperty getStrokeProperty() {
            return strokeProperty;
      }

      public StringProperty getFillProperty() {
            return fillProperty;
      }

      public boolean getShapeNotSelected() {
            return shapeNotSelected;
      }

      public Canvas getCanvas() {
            return overlayCanvas;
      }

      public Pane getOverlayPane() {
            return overlayPane;
      }

      public GraphicsContext getGrapContext() {
            return toolGC;
      }

      public Scene getOverlayScene() {
            return overlayScene;
      }

      public Pane getNodesPane() {
            return nodesPane;
      }

      public double getOverlayHt() {
            return overlayHt.get();
      }

      public double getOverlayWd() {
            return overlayWd.get();
      }


      /**
       * *********************************************************************************************************** ***
       * *
       * SETTERS
       * *
       * * ************************************************************************************************************
       **/


      public void setShapeNotSelected(boolean bool) {
            shapeNotSelected = bool;
      }

      public void setOverlayWindow(Stage window) {
            this.overlayWindow = window;
      }

      public void setOverlayScene(Scene scene) {
            this.overlayScene = scene;
      }

      public void setOverlayPane(Pane pane) {
            this.overlayPane = pane;
      }


      /**
       * Sets the graphics setting for these buttons
       *
       * @param grid    ..
       * @param buttons ..
       */
      private static void setButtonsSettings(Pane grid, Button[] buttons) {
            for (Button b : buttons) {
                  b.setId("drawToolButton");
                  b.setMaxWidth(Double.MAX_VALUE);
                  b.setMaxHeight(Double.MAX_VALUE);
                  grid.getChildren().add(b);
            }
      }


      /** ************************************************************************************************************ ***
       *                                                                                                                 *
       OTHER METHODS
       *                                                                                                                 *
       ** ************************************************************************************************************ **/

      /**
       * Binds the heigth and width properties to the window/stage.
       */
      private void layoutBindings() {
            overlayCanvas.heightProperty().bind(overlayPane.heightProperty());
            overlayCanvas.widthProperty().bind(overlayPane.widthProperty());
      }

      private void heightChangedListener(ObservableValue<? extends Number> property, Number oldVal, Number newVal) {
            LOGGER.debug("called, HEIGHT changed. newVal: <{}>", newVal);

            double ht = (double) newVal;
            overlayHt.set((long) ht);
            overlayPane.setMaxHeight(ht);
            overlayPane.setMinHeight(ht);
            overlayCanvas.setHeight(ht);
            // add for image toolbar
            overlayWindow.setMaxHeight(ht + 76);
            overlayWindow.setMinHeight(ht + 76);
      }

      private void widthChangedLister(ObservableValue<? extends Number> property, Number oldVal, Number newVal) {
            LOGGER.debug("called, WIDTH changed. newVal: <{}>", newVal);

            double wd = (double) newVal;
            overlayWd.set((long) wd);
            overlayPane.setMaxWidth(wd);
            overlayPane.setMinWidth(wd);
            overlayCanvas.setWidth(wd);
            overlayWindow.setMaxWidth(wd);
            overlayWindow.setMinWidth(wd);
      }


      /**
       * Creates an array of square rectangles. Must isolate the transparent button because its color
       * cannot be used to create the button. lol
       *
       * @param btnSize ..
       * @param isFill  ..
       * @param colors  An array of UIColors
       * @return Returns an array of rectangle color buttons based on the UIColors provided in
       * the parameter.
       */
      private Shape[] createColors(final int btnSize, boolean isFill, String... colors) {
            Shape[] shape = new Shape[colors.length + 1];
            double[] squareSlashPts = {0, 0, 18, 0, 18, 18, 0, 18, 0, 0, 18, 18};

            //  ***** Fill color ***** //
            // 1st button is transparent
            if (isFill) {
                  shape[0] = new Polygon(squareSlashPts);
                  shape[0].setStrokeWidth(2);
                  shape[0].setStrokeLineJoin(StrokeLineJoin.BEVEL);
                  shape[0].setStroke(Color.web(UIColors.FM_WHITE));
                  shape[0].setFill(Color.TRANSPARENT);
                  shape[0].setOnMouseClicked(e -> {
                        SoundEffects.PRESS_BUTTON_COMMON.play();
                        fillBTNProperty.setValue(UIColors.TRANSPARENT);
                        fillProperty.setValue(UIColors.TRANSPARENT);
                        setShapesColors();
                  });
                  // 0 is the transparent button, we start the idx
                  // at 1 while "s" starts at the first color.
                  int idx = 1;
                  for (String s : colors) {
                        shape[idx] = new Rectangle(btnSize, btnSize, Color.web(s));
                        shape[idx].setOnMouseClicked(e -> {
                              SoundEffects.PRESS_BUTTON_COMMON.play();
                              fillBTNProperty.setValue(s);
                              fillProperty.setValue(s);
                              setShapesColors();
                        });
                        // increment the idx
                        idx++;
                  }

                  // ***** Stroke Color ***** //
            } else {
                  shape[0] = new Polygon(squareSlashPts);
                  shape[0].setStroke(Color.web(UIColors.GRAPH_BGND));
                  shape[0].setStrokeWidth(2);
                  shape[0].setStrokeLineJoin(StrokeLineJoin.BEVEL);
                  shape[0].setFill(Color.web(UIColors.FM_WHITE));
                  shape[0].setOnMouseClicked(e -> {
                        SoundEffects.PRESS_BUTTON_COMMON.play();
                        strokeBTNProperty.setValue(UIColors.TRANSPARENT);
                        strokeProperty.setValue(UIColors.TRANSPARENT);
                        setShapesColors();
                  });
                  // 0 is the transparent button, we start the idx
                  // at 1 while "s" starts at the first color.
                  int idx = 1;
                  for (String s : colors) {
                        shape[idx] = new Rectangle(btnSize - 2, btnSize - 2);
                        shape[idx].setStroke(Color.web(s));
                        shape[idx].setStrokeWidth(2);
                        shape[idx].setFill(Color.web(UIColors.TRANSPARENT));
                        shape[idx].setOnMouseClicked(e -> {
                              SoundEffects.PRESS_BUTTON_COMMON.play();
                              strokeBTNProperty.setValue(s);
                              strokeProperty.setValue(s);
                              setShapesColors();
                        });
                        // increment the idx
                        idx++;
                  }
            }
            return shape;
      }

      private void setShapesColors() {
            if (!classEditorRef.getArrayOfFMShapes().isEmpty()) {
                  FMRectangle r = (FMRectangle) classEditorRef.getArrayOfFMShapes().get(0);
            }
      }

      public void clearNodes() {
            if (overlayPane.getChildren().contains(nodesPane)) {
                  nodesPane.getChildren().clear();
                  overlayPane.getChildren().remove(nodesPane);
            }
      }

      /**
       * Creates new properties and clears all listeners.
       */
      public void clearListeners() {
            LOGGER.debug("clearListeners() called");
            fillProperty = new SimpleStringProperty(fillProperty.getValue());
            strokeProperty = new SimpleStringProperty(strokeProperty.getValue());
      }


      public void releaseMouse() {
            overlayScene.setCursor(Cursor.DEFAULT);
            overlayScene.setOnMousePressed(null);
            overlayScene.setOnMouseReleased(null);
            overlayScene.setOnMouseDragged(null);
            overlayPane.setOnMousePressed(null);
            overlayPane.setOnMouseDragged(null);
            overlayPane.setOnMouseEntered(null);
            overlayPane.setOnMouseReleased(null);
            overlayPane.setOnMouseExited(null);
      }


      /**
       * ***** BUTTON ACTIONS *******
       **/

      private void clearShapesAction(SectionEditor paramEditor) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            // @todo bug with overlayPane shapes. After clear, you do not see the shapes when the pane is redrawn on.

            paramEditor.clearShapes(paramEditor.getArrayOfFMShapes());
            overlayPane.getChildren().remove(1, overlayPane.getChildren().size());
      }

      /**
       * Quit and do not save changes.
       */
      private void quitBtnAction() {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            onClose();
      }

      /**
       * Saves the arrayOfFMShapes to a file for this card
       *
       * @param fileName The fileName only;
       */
      private static void saveShapesToFile(String fileName, SectionEditor paramEditor) {
            // Provide the correct type. Added during changes for 1.3.0, commented out on 30 July 2022,
            // causing images to be hidden when snapshot is used;
            // paramEditor.setSectionType('D')
            FileOpsShapes fo = new FileOpsShapes();
            // Pass the shapeFileName to cardEditor
            if (paramEditor.getArrayOfFMShapes().size() > 1) {
                  fo.setShapesInFile(paramEditor.getArrayOfFMShapes(), fileName);
            }
      }

      /**
       * Adds a circle to the drawing
       *
       * @param ed ..
       */
      private void circleBtnAction(SectionEditor ed) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            clearListeners();
            // Clear the overlayPane of any resize nodes if they exist
            clearNodes();

            toolGC = overlayCanvas.getGraphicsContext2D();
            CircleBuilder cBuilder = new CircleBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeProperty.getValue(), fillProperty.getValue());

            overlayScene.setOnMousePressed(cBuilder::mousePressed);
            overlayScene.setOnMouseDragged(cBuilder::mouseDragged);
            overlayScene.setOnMouseReleased(cBuilder::mouseReleased);

            overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR));
            overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
            shapeNotSelected = true;
      }

      /*   ********** SHAPE BUTTON ACTIONS **********    */

      /**
       * Adds a rectangle to the drawing
       *
       * @param ed ..
       */
      private void rectBtnAction(SectionEditor ed) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            clearListeners();
            // Clear the overlayPane of any resize nodes if they exist
            clearNodes();
            toolGC = overlayCanvas.getGraphicsContext2D();
            RectangleBuilder rectBuilder = new RectangleBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeProperty.getValue(), fillProperty.getValue());

            // builds the first shape, not edit
            overlayScene.setOnMousePressed(rectBuilder::mousePressed);
            overlayScene.setOnMouseDragged(rectBuilder::mouseDragged);
            overlayScene.setOnMouseReleased(rectBuilder::mouseReleased);
            overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR));
            overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
            shapeNotSelected = true;
      }

      /**
       * Adds an isosocles triangle to the drawing
       *
       * @param ed
       */
      private void triangleBtnAction(SectionEditor ed) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            clearListeners();
            // Clear the overlayPane of any resize nodes if they exist
            clearNodes();

            TriangleBuilder triBuilder = new TriangleBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeProperty.getValue(), fillProperty.getValue());

            overlayScene.setOnMousePressed(triBuilder::mousePressed);
            overlayScene.setOnMouseDragged(triBuilder::mouseDragged);
            overlayScene.setOnMouseReleased(triBuilder::mouseReleased);

            //System.out.println("\n *** triangleBtnAction called ***");
            toolGC = overlayCanvas.getGraphicsContext2D();

            overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR));
            overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
            shapeNotSelected = true;

      }

      /**
       * Creates a closed shape with multiple-points
       *
       * @param ed ..
       */
      private void polyBtnAction(SectionEditor ed) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            clearListeners();
            // Clear the overlayPane of any resize nodes if they exist
            clearNodes();
            PolygonBuilder polygonBuilder = new PolygonBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeProperty.getValue(), fillProperty.getValue());

            overlayScene.setOnMousePressed(polygonBuilder::mousePressed);
            overlayScene.setOnMouseDragged(polygonBuilder::mouseDragged);
            overlayScene.setOnMouseReleased(polygonBuilder::mouseReleased);
            toolGC = overlayCanvas.getGraphicsContext2D();
            overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR));
            overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
            shapeNotSelected = true;
      }

      /**
       * Creates a one or more straight lines with multiple-points
       *
       * @param ed ..
       */
      private void lineBtnAction(SectionEditor ed) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            clearListeners();
            // Clear the overlayPane of any resize nodes if they exist
            clearNodes();
            PolylineBuilder polylineBuilder = new PolylineBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeProperty.getValue(), fillProperty.getValue());
            overlayScene.setOnMousePressed(polylineBuilder::mousePressed);
            overlayScene.setOnMouseDragged(polylineBuilder::mouseDragged);
            overlayScene.setOnMouseReleased(polylineBuilder::mouseReleased);
            toolGC = overlayCanvas.getGraphicsContext2D();

            overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR));
            overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
            shapeNotSelected = true;
      }

      /**
       * Creates an arrow
       *
       * @param ed ..
       */
      private void arrowBtnAction(SectionEditor ed) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            clearListeners();
            clearNodes();
            ArrowBuilder arrow = new ArrowBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeProperty.getValue(), fillProperty.getValue());
            overlayScene.setOnMousePressed(arrow::mousePressed);
            overlayScene.setOnMouseDragged(arrow::mouseDragged);
            overlayScene.setOnMouseReleased(arrow::mouseReleased);
            toolGC = overlayCanvas.getGraphicsContext2D();

            overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR));
            overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
            shapeNotSelected = true;
      }

      /**
       * Creates a freeform line that curves
       */
      private void freeformBtnAction() {

      }

      /**
       * Creates a textbox and places it on the drawing
       *
       * @param ed ..
       */
      private void txtBtnAction(SectionEditor ed) {
            SoundEffects.PRESS_BUTTON_COMMON.play();
            //System.out.println("\n *** txtBtnAction called ***");
            clearListeners();
            // Clear the overlayPane of any resize nodes if they exist
            clearNodes();
            LetterBuilder letterBuilder = new LetterBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeProperty.getValue(), fillProperty.getValue(), 16);
            overlayScene.setOnMousePressed(letterBuilder::mousePressed);
            overlayScene.setOnMouseDragged(letterBuilder::mouseDragged);
            overlayScene.setOnMouseReleased(letterBuilder::mouseReleased);
            LOGGER.info("txtBtnAction line 719");
            toolGC = overlayCanvas.getGraphicsContext2D();
            overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR));
            overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
            shapeNotSelected = true;

            LOGGER.info("txtBtnAction completed");
      }


      /**
       * <P> Allows a saveOnExit from outside of this class</p>
       * <p> Use with caution. Calls a private method for saving on exit
       * that requires the DeckFileName and SectionEditor. References may
       * not be correct.</P>
       * <p>Called by CreateFlash saveOnExit</p>
       * <p>Ensures that this class complies with BaseInterface.</p>
       * @return Always returns true;
       */
      @Override
      public boolean saveOnExit() {
            /*LOGGER.debug("saveOnExit : classEditorRef. number of shapes: {}\n" +
                "deckFileName: {}", classEditorRef.getArrayOfFMShapes().size(), FlashCardOps.getInstance().getDeckFileName());*/
            if(null != classEditorRef) {
                  String shapeFileName = classEditorRef.getShapesFileName();
                  if (null != shapeFileName) {
                        saveOnExit(shapeFileName, classEditorRef);
                  }
            }
            return true;
      }

      /**
       * As standard in most applications, data is saved on exit as a default. THUS
       * we are storing on the exit event. Saves the shapes to file if there are more
       * than one shape in the array. The first element in the array is the drawpad
       * ht and wd. Sets the section type, and sets the shapeFileName. Note, calling
       * this method will result in it being called twice if it is called for an exit
       * Action.
       *
       * <p><b>NOTE:</b> Does not exit the Stage/Window</p>
       * <p><b>NOTE:</b> Does not check for null. Null pointer should be handled before
       * this method.</p>
       *
       * @param shapeFileName
       * @param paramEditor
       */
      private void saveOnExit(String shapeFileName, SectionEditor paramEditor) {
            if(ImageTools.instanceExists()) {
                  ImageTools.getInstance().saveOnExit(paramEditor);
            }
            // The '0' element is the size of the drawpad when it is full scale. Shapes start at
            // '1'
            saveShapesToFile(shapeFileName, paramEditor);
      }


      /**
       * <p>Not intended to be called outside of this class.</p>
       * <p>Action taken when the user closes this class</p>
       * <p> Implemented Method from BaseInterface</p>
       * <p><b>NOTE:</b> Calls overlayWindow and toolWindow close.
       * and saveOnExit is called when the windows are closed. Calling saveOnExit
       *  will result in it being called twice. </p>
       */
      @Override
      public void onClose() {
            // save arrayOfBuilderShapes to file.
            //if ( DrawTools.instanceExists() ) {
            ImageTools imgTools = ImageTools.getInstance();
            imgTools.onClose();
            // @TODO !!!! check if the lower call is circular !!!!
            // is this a circular call???
            if(null != classEditorRef) {
                  classEditorRef.setDrawPadClosed();
                  toolMainPane.getChildren().clear();
                  if(overlayWindow != null) {
                        overlayWindow.close();
                  }
                  toolWindow.close();
            }

            clearListeners();
            LOGGER.info("onCLose called");
            FlashMonkeyMain.wasMaximizedReset();

            CreateFlash cfp = CreateFlash.getInstance();
            cfp.enableButtons();
            CLASS_INSTANCE = null;
      }

      /**
       * Provides the user a choice box when they click on abandon changes. In the case
       * they did it on accident. this is a save.
       *
       * @param paramEditor
       */
      public void abandAction(SectionEditor paramEditor) {
            FMAlerts alert = new FMAlerts();
            String msg = "Delete all changes!\n\nAre you Sure?\n\n";
            int b = alert.choiceOptionActionPopup(
                "Hmmmmm!", msg,
                "emojis/flashFaces_sunglasses_60.png",
                UIColors.FM_RED_WRONG_OPAQUE,
                "Delete",
                "SAVE");
            if (b == 1) {
                  // do not save
                  onClose();
            } else {
                  // save the shapes
                  String shapeFileName = FlashCardOps.getInstance().getDeckFileName();
                  if(null != shapeFileName) { saveOnExit(shapeFileName, paramEditor); }
                  onClose();
            }
            return;
      }


      /**
       * Provides the size of the pane from the first shape in shapesArray
       * Should be sized by the overlayCanvas
       * @return an FMRectangle
       */
      private static FMRectangle setPaneSizeRectangle() {
            LOGGER.debug("calling setPaneSizeRectangle(), setting to new FMRectangle\n\t-StackTrace");
            return new FMRectangle(0, 0, 0, 0, 1.0, UIColors.TRANSPARENT, UIColors.TRANSPARENT, 0);
      }

      //---------------------- Testing Methods ----------------------

      @FMAnnotations.DoNotDeployMethod
      public Point2D getRectBtnXY() {
            Bounds bounds = rectBtn.getLayoutBounds();
            return rectBtn.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getTriBtnXY() {
            Bounds bounds = triangleBtn.getLayoutBounds();
            return triangleBtn.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getCircleBtnXY() {
            Bounds bounds = circleBtn.getLayoutBounds();
            return circleBtn.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getClearListBtnXY() {
            Bounds bounds = clearBtn.getLayoutBounds();
            return clearBtn.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getOverlayPaneXY() {
            Bounds bounds = overlayPane.getLayoutBounds();
            return overlayPane.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public boolean checkOverLayPaneRef(Pane pane) {
            return pane == this.overlayPane;
      }

      @FMAnnotations.DoNotDeployMethod
      public Canvas getOverlayTestCanvas() {
            //System.out.println("Canvas reference in getOverlayCanvas() " + overlayCanvas);
            return overlayCanvas;
      }

      @FMAnnotations.DoNotDeployMethod
      public String getFileString() {

            //System.out.println("in testMethod getFileString, fileStringName: " + classEditorRef.getShapesFileNameTestMethod());

            return classEditorRef.getShapesFileNameTestMethod();
      }

      @FMAnnotations.DoNotDeployMethod
      public Stage getDrawToolWindow() {
            return toolWindow;
      }


      @FMAnnotations.DoNotDeployMethod
      public Point2D getHighlightBtnXY() {
            // work-around since we cannot get the grid nor color rectangle xy
            Point2D xy = getRectBtnXY();
            // get pink button
            return new Point2D(xy.getX() - 10, xy.getY() - 40);
      }


}
