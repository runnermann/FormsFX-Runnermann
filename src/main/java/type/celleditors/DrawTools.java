package type.celleditors;

import draw.DrawObj;
import draw.shapes.*;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import flashmonkey.CreateFlash;
import fileops.FileOpsShapes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.awt.*;


/**
 * <p>Singleton Class. Not synchronized, not thread-safe.</p>
 * This class contains the drawing tools to draw over an image, or create an SVG drawing on a canvas.
 *      Desirements:
 *          - Click on a shapeButton once, and add several shapes with each new click on overlayPane.
 *          - Delete shapes
 *          - Rotate a shape
 *          - Resize a shape
 *          - Color a shape
 *          - Select several shapes and manipulate them
 *              - Each SVGshape is a series of points. As a group of shapes is grabbed at an outside point. And
 *              the point is dragged, All points grow or shrink by x = deltaX / 2, and y = deltaY / 2. Arcs grow
 *              by ???
 *          - Copy a shape
 *          - Add a copied shape to another shape
 *
 * Algorithm:
 *      1) When User releases the Mouse button while taking a snapshot, toolPane pops up near the mouse.
 *      2) The User clicks a shape, moves the mouse to the canvas, clicks and holds the mouse button down,
 *      drags the shape to the desired size, and releases.
 *      3) //@TODO The shape has an axis and can be rotated on the axis
 *      4) The shape has handles so the User.User can resize the shape.
 *      5) Saving: After the User.User presses a shape button, then clicks on the overlayPane
 *          - newShape is true
 *          - The shape is created according to the mouse drag.
 *          - When the EncryptedUser.EncryptedUser releases the mouse button
 *              - If the shape Width or Height are greater than 0 and newShape is true, it is added to the arrayOfBuilderShapes.
 *      6) When the User exits the ToolWindow, the arrayOfBuilderShapes are saved to a file for this card.
 *
 *      @author Lowell Stadelman
 */
public class DrawTools<T extends GenericShape<T>> //extends SectionEditor //implements CopyPasteInterface
{

    private static DrawTools CLASS_INSTANCE;


    // THE LOGGER
    // REMOVE BEFORE
    private static final Logger LOGGER = LoggerFactory.getLogger(DrawTools.class);


    // A reference to the right pane in the ass!!!
    private static SectionEditor classEditorRef;

    // For the overlay window. The window that shapes
    // are drawn in.
    private Stage overlayWindow;
    private Scene overlayScene;
    private Pane  overlayPane;
    // Nodes for grabbing a shape
    private Pane  nodesPane;
    // Contains the shapes and the image
    private Canvas overlayCanvas;

    // For the ToolWindow popUp
    private static Stage toolWindow;
    private static TilePane toolPane;
    // contains tool popUp and action buttons
    private static BorderPane popUpPane;
    private static Scene toolScene;
    // contains non-shape buttons
    private static HBox zBtnHBox;
    private static GridPane colorBtnGrid;
    public static GraphicsContext toolGC;
    // The shape buttons
    private static Button circleBtn, rectBtn, triangleBtn,
            polyBtn, lineBtn, arrowBtn, penBtn, txtBtn;

    // Other buttons
    private static Button deleteBtn, saveBtn, clearBtn, quitBtn;
    // The size of the overLayPane to be drawn on.
    private static double overlayHt;
    private static double overlayWd;
    // For the ShapeBuilders... IE CircleBuilder
    private static boolean shapeNotSelected;
    // Indicates if this is a new instance
    public static boolean clearMe;
    
    // Color related
    private static String strokeColor = UIColors.BELIZE_BLUE;
    private static String fillColor = "0x00000000";


    /**
     * private No args constructor
     */
    private DrawTools() { /* empty */ }

    
    /**
     * Singleton class instantiation. There
     * should only be one instance of DrawTools
     * Synchronized
     * @return The class instance
     */
    public static synchronized DrawTools getInstance() {
        if(CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new DrawTools();
        }
        
        return CLASS_INSTANCE;
    }
    
    
    /**
     * Called by DrawPadButton in SectionEditor, Shapes and no image
     * @param x
     * @param y
     * @param fullPathName
     * @param paramEditor
     */
    public void buildDrawTools(double x, double y, String fullPathName, SectionEditor paramEditor) {
        // height and width of drawpad
        int wd = 300;
        int ht = 300;
        
        LOGGER.info("line 145) buildDrawTools for drawPad, fullPathName: " + fullPathName );

        StageStyle style = StageStyle.UTILITY;
        //shapeNotSelected = false;
        overlayWindow = buildDrawTools(x, y, wd, ht, fullPathName, paramEditor, style, null);
        // if the user clicks outside of the pane
        // keep overlay on top ALWAYS!!!
        overlayWindow.setAlwaysOnTop(true);
        overlayWindow.show();
    }
    
    
    /**
     * Called by SectionEditor immediately after image is captured.
     * @param drawObj The drawObject containing minX, minY, width, height, qOrA, cID, and deckName
     * @param paramEditor
     */
    public void buildDrawTools(DrawObj drawObj, SectionEditor paramEditor) {

       //System.out.println("Called buildDrawTools called by snapShot");
    
        //System.err.println("\t are images null in editor: " + (sEditor.getMediaFileName() == null));
        
        // Clear the arrayOfFMShapes if it has
        // shapes left in it from the last time
        // it was used.
        //if(sEditor.getArrayOfFMShapes() != null) {
        //    sEditor.getArrayOfFMShapes().clear();
        //}

        StageStyle style = StageStyle.TRANSPARENT;
        //shapeNotSelected = true;
        overlayWindow = buildDrawTools(drawObj.getMinX(), drawObj.getMinY(), drawObj.getDeltaX(), drawObj.getDeltaY(),
                drawObj.getFullPathName(), paramEditor, style, null);
        overlayWindow.show();
    }
    
    /**
     * Called by editor when card exists, by popup in SectionEditor
     * @param //drawObj
     * @param paramEditor
     * @param iView
     */
    //public void buildPopupDrawTools( DrawObj drawObj, SectionEditor paramEditor, ImageView iView) {
    public void buildDrawTools( String fullPathName, SectionEditor paramEditor, ImageView iView, double x, double y, double wd, double ht) {
    
       //System.out.println("Called buildDrawTools when a card exists and popUp is called. ");
        //System.err.println("Called buildDrawTools when a card exists and popUp is called. \n"
         //       + "\tdrawObj fullPathName: " + drawObj.getFullPathName());

       //System.out.printf("ht & wd, x, y: of imagePane: %6.2f, %6.2f, %6.2f, %6.2f %n", ht, wd, x, y );
        StageStyle style = StageStyle.DECORATED;
        
        //overlayWindow = buildDrawTools(drawObj.getMinX(), drawObj.getMinY(), drawObj.getDeltaX(), drawObj.getDeltaY(),
        //        drawObj.getFullPathName(), paramEditor, style, iView);
        overlayWindow = buildDrawTools(x, y, wd, ht, fullPathName, paramEditor, style, iView);
        overlayWindow.show();
    }



    private Stage buildDrawTools(double prevX, double prevY, double prevWd, double prevHt,
                                String fullPathName, SectionEditor paramEditor, StageStyle style, ImageView iView)
    {
        
        LOGGER.info("fullPathName: {} ends with .date: {}", fullPathName, fullPathName.endsWith(".dat"));

        Stage window = new Stage(style);
        // Misc variables
        overlayHt = prevHt;
        overlayWd = prevWd;

        popUpPane = new BorderPane();
        toolScene = new Scene(popUpPane);

        // Class SectionEditor referance
        classEditorRef = paramEditor;

        // Object instantiation and pane assignment for the Drawing area
        overlayCanvas = new Canvas();
        overlayPane = new Pane();
        if(iView == null) {
            overlayPane.getChildren().add(overlayCanvas);
        } else {
            overlayPane.getChildren().addAll(iView, overlayCanvas);
        }
 
        // Windows 10 fix
        overlayScene = new Scene(overlayPane, prevWd, prevHt, new Color(0, 0, 0, 1d / 255d));
        
        // overlay Window settings
        window.setHeight(prevHt);
        window.setWidth(prevWd);
        window.setX(prevX);
        window.setY(prevY);
        window.setScene(overlayScene);
        overlayScene.getStylesheets().addAll("css/drawtools.css", "css/buttons.css");

        // Overlay Pane settings
        overlayPane.setMaxHeight(overlayPane.getBoundsInParent().getHeight());
        overlayPane.setMaxWidth(overlayPane.getBoundsInParent().getWidth());
        overlayPane.setLayoutX(overlayPane.getBoundsInParent().getMinX());
        overlayPane.setLayoutY(overlayPane.getBoundsInParent().getMinY());
        overlayPane.setStyle("-fx-border-color: rgba(48, 128, 185, .9); " +
                "-fx-background-color: transparent; -fx-border-width: 3;");
        
        // Overlay canvas settings
        overlayCanvas.setWidth( overlayPane.getBoundsInParent().getWidth() );
        overlayCanvas.setHeight( overlayPane.getBoundsInParent().getHeight() );
        overlayCanvas.setLayoutX( overlayPane.getBoundsInParent().getMinX() );
        overlayCanvas.setLayoutY( overlayPane.getBoundsInParent().getMinY() );
        toolGC = overlayCanvas.getGraphicsContext2D();

        toolScene.setOnKeyPressed((KeyEvent f) -> {
            if(f.getCode() == KeyCode.ESCAPE)
            {
               //System.out.println(" escape pressed while canvas is active");

                window.close();
                toolWindow.close();
                onClose(fullPathName, paramEditor);
            }
        });

        overlayPane.setOnKeyPressed( (KeyEvent f) -> {
            if(f.getCode() == KeyCode.ESCAPE)
            {
                window.close();
                toolWindow.close();
                onClose(fullPathName, paramEditor);
            }
        });

        // Update the size of the rectangle used for the shapespane size
        // in arrayOfFMShapes. Used when iView doesn't exist.
        overlayScene.setOnMouseClicked( (MouseEvent e) -> {
            FMRectangle rect = paneSizeRectangle();
            rect.setWd(overlayCanvas.getWidth());
            rect.setHt(overlayCanvas.getHeight());
            if(paramEditor.getArrayOfFMShapes().size() > 0) {
                paramEditor.getArrayOfFMShapes().set(0, rect);
            }
        });
        
        FMCircle fmCirc = new FMCircle(-10, 0, 10, 10, 3,
                UIColors.BUTTON_COMPLIMENT, UIColors.TRANSPARENT, 0);
        Ellipse btnCircle = fmCirc.getShape();
        FMRectangle fmRect = new FMRectangle(0, 0, 20, 20, 3,
                UIColors.BUTTON_COMPLIMENT, UIColors.TRANSPARENT, 0);
        Rectangle btnSqr = fmRect.getShape();
        FMTriangle fmTri = new FMTriangle(10, 0, 20, 20, 3,
                UIColors.BUTTON_COMPLIMENT, UIColors.TRANSPARENT, 0);
        Polygon btnTri = fmTri.getShape();
        FMLetter letter = new FMLetter(0, 0, 10, 10, 3,
                UIColors.BUTTON_COMPLIMENT, UIColors.TRANSPARENT, 0, "T");
        Text text = letter.getShape();
        
        // Tool buttons array
        Button[] toolBtns = {
                circleBtn = new Button("Circle", btnCircle),
                rectBtn = new Button("Rectangle", btnSqr),
                triangleBtn = new Button("Triangle", btnTri),
                polyBtn = new Button("Freeform"),
                lineBtn = new Button("Line"),
                arrowBtn = new Button("Arrow"),
                penBtn = new Button("Scribble"),
                txtBtn = new Button("Text", text)
        };
        circleBtn.setAlignment(Pos.BASELINE_LEFT);
        rectBtn.setAlignment(Pos.BASELINE_LEFT);
        triangleBtn.setAlignment(Pos.BASELINE_LEFT);
        txtBtn.setAlignment(Pos.BASELINE_LEFT);
        
        polyBtn.setDisable(true);
        lineBtn.setDisable(true);
        arrowBtn.setDisable(true);
        penBtn.setDisable(true);
        
        
        // Color buttons for Fill Color and Stroke Color
        final int btnSize = 20;
        String[] colors = {UIColors.FM_WHITE, UIColors.FLASH_RED, UIColors.HIGHLIGHT_PINK, UIColors.HIGHLIGHT_ORANGE,
                UIColors.HIGHLIGHT_YELLOW, UIColors.HIGHLIGHT_GREEN, UIColors.BELIZE_BLUE_OPAQUE,
                UIColors.GRAPH_BGND, UIColors.FLASH_BLACK};
    
        Rectangle[] strokeColorBtns = createColorBtns(btnSize, false, colors);
        Rectangle[] fillColorBtns = createColorBtns(btnSize, true, colors);
        
        colorBtnGrid = new GridPane();
        colorBtnGrid.setVgap(2);
        colorBtnGrid.setHgap(2);
        colorBtnGrid.setPadding(new Insets(10,2,2,2));
        colorBtnGrid.setAlignment(Pos.CENTER);
        colorBtnGrid.addRow(0, strokeColorBtns);
        colorBtnGrid.addRow(1, fillColorBtns);


        //  Set shapes to move in right pane as shapes are moved
        //  in overlayPane
        
        // Shape button calls
        rectBtn.setOnAction(a -> rectBtnAction(paramEditor));
        circleBtn.setOnAction(a -> circleBtnAction(paramEditor));
        triangleBtn.setOnAction(a -> triangleBtnAction(paramEditor));
        txtBtn.setOnAction(a -> txtBtnAction(paramEditor));

        // Service buttons
        deleteBtn = new Button("delete");
        deleteBtn.setTooltip(new Tooltip("Deletes the active shape"));

        saveBtn = new Button("save");
        saveBtn.setTooltip(new Tooltip("Save and exit"));
        saveBtn.setOnAction(a -> saveExitAction(fullPathName, paramEditor));
        
        clearBtn = new Button("clear");
        clearBtn.setTooltip(new Tooltip("Clear all shapes"));
        clearBtn.setOnAction(a -> clearShapesAction(paramEditor));
        
        quitBtn = new Button("quit");
        quitBtn.setTooltip(new Tooltip("clears the shapes and exits"));
        quitBtn.setOnAction(e -> quitBtnAction(paramEditor));

        zBtnHBox = new HBox(saveBtn, quitBtn, clearBtn);
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
        overlayScene.setOnMouseExited((MouseEvent e) -> releaseMouse());

        setButtonsSettings(toolPane, toolBtns);
        
        //for(int i = 0; i < overlayPane.getChildren().size(); i++) {

           // System.out.println(overlayPane.getChildren().get(i).getClass().getName());
        //}

        return window;

    } // *** END buildDrawTools() ***


    /**
     * The popup tool window
     */
    public void popUpTools()
    {
        toolWindow = new Stage();
        popUpPane.setTop(colorBtnGrid);
        popUpPane.setCenter(toolPane);
        popUpPane.setBottom(zBtnHBox);
        popUpPane.setStyle("-fx-background-color: " + UIColors.GRAPH_BGND);
//        toolScene.getStylesheets().add("css/newCascadeStyleSheet.css");

        toolWindow.setScene(toolScene);
        double x = overlayWindow.getX() + overlayWindow.getWidth() + 10;
        double y = overlayWindow.getY() + 10;
        toolWindow.setX(x);
        toolWindow.setY(y);
        toolWindow.setTitle("Draw tools");
        // toolWindow/stage should always be
        // on top until it is closed.
        toolWindow.setAlwaysOnTop(true);
        toolWindow.setOnHidden(e -> justClose());
        toolWindow.show();
    }


    /** ************************************************************************************************************ ***
     *                                                                                                                 *
                                                        GETTERS
     *                                                                                                                 *
     ** ************************************************************************************************************ **/


    public boolean getShapeNotSelected()
    {
        return shapeNotSelected;
    }

    public Canvas getCanvas()
    {
        return overlayCanvas;
    }

    public Pane getOverlayPane()
    {
        return overlayPane;
    }

    public GraphicsContext getGrapContext()
    {
        return toolGC;
    }

    public Scene getOverlayScene() { return overlayScene; }

    public Pane getNodesPane() { return nodesPane; }

    public double getOverlayHt()
    {
        return this.overlayHt;
    }

    public double getOverlayWd()
    {
        return this.overlayWd;
    }
    
  //  public DrawObj getDrawObj() {
  //      return this.drawObj;
  //  }




    /** ************************************************************************************************************ ***
     *                                                                                                                 *
                                                        SETTERS
     *                                                                                                                 *
     ** ************************************************************************************************************ **/


    public static void setShapeNotSelected(boolean bool)
    {
        shapeNotSelected = bool;
    }

    public void setOverlayWindow(Stage window)
    {
        this.overlayWindow = window;
    }

    public void setOverlayScene(Scene scene)
    {
        this.overlayScene = scene;
    }

    public void setOverlayPane(Pane pane)
    {
        this.overlayPane = pane;
    }


    /**
     * Sets the graphics setting for these buttons
     * @param grid
     * @param buttons
     */
    private static void setButtonsSettings(Pane grid, Button[] buttons)
    {
        for(Button b : buttons)
        {
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
     * Creates an array of square rectangles
     * @param isFill
     * @param colors An array of UIColors
     * @return Returns an array of rectangle color buttons based on the UIColors provided in
     * the parameter.
     */
    private Rectangle[] createColorBtns(final int btnSize, boolean isFill, String ... colors) {
        
        UIColors uiColor = new UIColors();
        Rectangle[] rects = new Rectangle[colors.length];
        int idx = 0;
        for(String s : colors) {
            // Fill color
            if (isFill) {
                rects[idx] = new Rectangle(btnSize, btnSize, uiColor.convertColor(s));
                rects[idx].setOnMouseClicked(e -> {
                    fillColor = s;
                    strokeColor = s;
                });
                rects[idx].setOnMouseReleased(e -> strokeColor = UIColors.TRANSPARENT);
            // Stroke Color
            } else {
                rects[idx] = new Rectangle(btnSize - 2, btnSize);
                rects[idx].setStroke(uiColor.convertColor(s));
                rects[idx].setStrokeWidth(2);
                rects[idx].setFill(uiColor.convertColor(UIColors.TRANSPARENT));
                rects[idx].setOnMouseClicked(e -> {
                    strokeColor = s;
                    fillColor = UIColors.TRANSPARENT;
                });
            }
            idx++;
        }
        
        return rects;
    }

    public void clearNodes() {
        if(overlayPane.getChildren().contains(nodesPane))
        {
            nodesPane.getChildren().clear();
            overlayPane.getChildren().remove(nodesPane);
        }
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

    
    
    /**  ****** BUTTON ACTIONS *******  **/
    
    private void clearShapesAction(SectionEditor paramEditor) {
        
        // @todo bug with overlayPane shapes. After clear, you do not see the shapes when the pane is redrawn on.
        
        paramEditor.clearShapes(paramEditor.getArrayOfFMShapes());
        overlayPane.getChildren().remove(1, overlayPane.getChildren().size());
    }


    private void saveExitAction(String fullPathName, SectionEditor paramEditor) {
        LOGGER.info("exitButtonAction called");
        // Exit overLayWindow, User does not want to create a drawing,
        popUpPane.getChildren().clear();
        overlayWindow.close();
        toolWindow.close();

        onClose(fullPathName, paramEditor);
    }
    
    private void quitBtnAction(SectionEditor paramEditor) {
        clearShapesAction(paramEditor);
        justClose();
    }
    
    /**
     * Saves the arrayOfFMShapes to a file for this card
     * @param fullPath The full path including the fileName;
     */
    private static void saveShapesAction(String fullPath, SectionEditor paramEditor)
    {
        FileOpsShapes fo = new FileOpsShapes();
        LOGGER.info("\n*** SaveShapesAction called  ***\n"
                + "\tshapeFilePathName: " + fullPath);

        // Pass the shapeFileName to cardEditor
        fo.setShapesInFile(paramEditor.getArrayOfFMShapes(), fullPath);
    }

    /**
     * Adds a circle to the drawing
     * @param ed
     */
    private void circleBtnAction(SectionEditor ed)
    {
        // Clear the overlayPane of any resize nodes if they exist
        clearNodes();
        CircleBuilder cBuilder = new CircleBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeColor, fillColor);

        overlayScene.setOnMousePressed(cBuilder::mousePressed);
        overlayScene.setOnMouseDragged(cBuilder::mouseDragged);
        overlayScene.setOnMouseReleased(cBuilder::mouseReleased);

       //System.out.println("\n *** circeBtnAction called ***");
        toolGC = overlayCanvas.getGraphicsContext2D();
        
        overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR) );
        overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
        shapeNotSelected = true;
    }
    
    /*   ********** SHAPE BUTTON ACTIONS **********    */

    /**
     * Adds a rectangle to the drawing
     * @param ed
     */
    private void rectBtnAction(SectionEditor ed)
    {
        // Clear the overlayPane of any resize nodes if they exist
        clearNodes();

        RectangleBuilder rectBuilder = new RectangleBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeColor, fillColor);

        overlayScene.setOnMousePressed(rectBuilder::mousePressed);
        overlayScene.setOnMouseDragged(rectBuilder::mouseDragged);
        overlayScene.setOnMouseReleased(rectBuilder::mouseReleased);

       //System.out.println("\n *** rectBtnAction called ***");
        toolGC = overlayCanvas.getGraphicsContext2D();

        overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR) );
        overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
        shapeNotSelected = true;
    }

    /**
     * Adds an isosocles triangle to the drawing
     * @param ed
     */
    private void triangleBtnAction(SectionEditor ed)
    {
        // Clear the overlayPane of any resize nodes if they exist
        clearNodes();

        TriangleBuilder triBuilder = new TriangleBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeColor, fillColor);

        overlayScene.setOnMousePressed(triBuilder::mousePressed);
        overlayScene.setOnMouseDragged(triBuilder::mouseDragged);
        overlayScene.setOnMouseReleased(triBuilder::mouseReleased);

       //System.out.println("\n *** triangleBtnAction called ***");
        toolGC = overlayCanvas.getGraphicsContext2D();

        overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR) );
        overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
        shapeNotSelected = true;

    }

    /**
     * Creates a closed shape with multiple-points
     */
    private void polyBtnAction()
    {


    }

    /**
     * Creates a one or more straight lines with multiple-points
     */
    private void lineBtnAction()
    {


    }

    /**
     * Creates an arrow
     */
    private void arrowBtnAction()
    {


    }

    /**
     * Creates a freeform line that curves
     */
    private void freeformBtnAction()
    {

    }

    /**
     * Creates a textbox and places it on the drawing
     */
    private void txtBtnAction(SectionEditor ed)
    {
       //System.out.println("\n *** txtBtnAction called ***");
        // Clear the overlayPane of any resize nodes if they exist
        clearNodes();
    
        LetterBuilder letterBuilder = new LetterBuilder(overlayCanvas, toolGC, overlayPane, ed, strokeColor, fillColor);
    
        overlayScene.setOnMousePressed(letterBuilder::mousePressed);
        overlayScene.setOnMouseDragged(letterBuilder::mouseDragged);
        overlayScene.setOnMouseReleased(letterBuilder::mouseReleased);
    
        LOGGER.info("txtBtnAction line 719");
        
        toolGC = overlayCanvas.getGraphicsContext2D();
    
        overlayPane.setOnMouseEntered((MouseEvent e) -> overlayPane.setCursor(Cursor.CROSSHAIR) );
        overlayPane.setOnMouseExited((MouseEvent e) -> overlayPane.setCursor(Cursor.DEFAULT));
        shapeNotSelected = true;
        
        LOGGER.info("txtBtnAction completed");

    }
    
    
    /**
     * Action taken when the user closes this class
     * with the intention of saving files. Or in case
     * of a failure close, saves users work.
     * Saves the shapes to file. Sets shapesFileName
     * in SectionEditor to the fileName here.
     * @param fullPath
     * @param paramEditor
     */
    private void onClose(String fullPath, SectionEditor paramEditor)
    {
        
        LOGGER.info("onCLose called");
        
        CreateFlash cfp = CreateFlash.getInstance();
        cfp.enableButtons();
        // save arrayOfBuilderShapes to file.
        saveShapesAction(fullPath, paramEditor);
        
        int num = fullPath.lastIndexOf('/');
        String fileName = fullPath.substring(num + 1);
    
       //System.out.println("DrawTools onClose fileName: " + fileName);
        
        
        paramEditor.setShapeFile(fileName);
        toolWindow.close();
        overlayWindow.close();
        // cannot clear the array of shapes here.
        // It is needed in card editor
    }

    /**
     * When the User.User does something unexpected.
     * close everything
     */
    public void justClose() {
        CreateFlash cfp = CreateFlash.getInstance();
        cfp.enableButtons();

        if(toolWindow != null) {
            toolWindow.close();
            overlayWindow.close();
        }
    }



    /**
     * Provides the size of the pane from the first shape in shapesArray
     * @return
     */
    private static FMRectangle paneSizeRectangle() {
        FMRectangle rect = new FMRectangle();
        rect.setWd(0);
        rect.setHt(0);
        rect.setStrokeWidth(1.0);
        rect.setStrokeColor(UIColors.TRANSPARENT);
        rect.setFillColor(UIColors.TRANSPARENT);
        rect.setX(0);
        rect.setY(0);
        return rect;
    }

    //---------------------- Testing Puposed Methods ----------------------

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
    public Point2D getExitBtnXY() {
        Bounds bounds = saveBtn.getLayoutBounds();
        return saveBtn.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
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



}
