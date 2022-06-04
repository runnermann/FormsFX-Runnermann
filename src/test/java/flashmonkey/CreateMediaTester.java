package flashmonkey;

import authcrypt.UserData;
import core.ImageUtility;
import core.ShapeUtility;
import type.draw.shapes.FMRectangle;
import type.draw.shapes.GenericShape;
import fileops.DirectoryMgr;
import fileops.FileNaming;
import fileops.FileOpsUtil;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.robot.impl.SleepRobotImpl;
import type.celleditors.DrawTools;
import type.celleditors.SnapShot;
import type.celltypes.CanvasCell;
import type.celltypes.MediaPopUp;
import type.testtypes.QandA;
import type.tools.imagery.Fit;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Image OCR libraries

//import org.bytedeco.javacpp.BytePointer;
//import org.bytedeco.javacpp.*;
//import static org.bytedeco.javacpp.lept.*;
//import static org.bytedeco.javacpp.tesseract.*;
//import static org.bytedeco.javacpp.lept.PIX;
//import static org.bytedeco.javacpp.lept.pixRead;
//import static org.bytedeco.javacpp.tesseract.*;
//import net.sourceforge.tess4j.Tesseract;

//import org.bytedeco.javacpp.*;

/**
 * Tests from the GUI level.
 *
 * 1. Adding images and media: Ensure that for the possible ways to add
 *  images and media, that the following work correctly for the operations
 *  in a - e
 *      - Take a snap-shot of an image
 *      - Drag-and-Drop image from memory
 *      - Drag-and-Drop video from memory
 *      - Drag-and-drop audio from memory
 *      a. ensure proper naming and proper storage of file
 *      b. image appears in right pane
 *      c. image is scaled to right pane
 *      d. delete button appears in the top
 *         right corner of the right pane. in the
 *         right stackPane -stackR
 *      e. delete button appears in the top
 *         right corner of the textArea. In the
 *         left stackPane -stackL
 *
 *
 * 2. Remove Media:
 *   a. Images, Audio, and Video media is removed from the view, and
 *      from the file system.

 * 3. Tests pop-up for correct references
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("popup")
public class CreateMediaTester extends ApplicationTest {

    //private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CreateMediaTester.class);
    
    private int delta_X = 0;
    private int delta_Y = 10;
    private Canvas originalCanvas;

    FxRobot robot;// = new FxRobot();
    SleepRobotImpl sleep = new SleepRobotImpl();

    String strPath = "../flashMonkeyFile/TestingDeck.dec";
    // image paths
    String imgPath = "src/resources/testing/TestImage.png";
    Path filePath = Paths.get(strPath);
    // for the background stage
    private Toolkit tk = Toolkit.getDefaultToolkit();
    private Dimension d = tk.getScreenSize();
    private int screenWt = 0;//d.width;
    private int screenHt = 0;//d.height;

    //Image originalImage;
    //Image image2;
    Pane returnPane;
    Pane rightPane;
    CanvasCell cCell;
    Scene scene;
    Stage stage;
 //   CreateFlashPane cfp;
    CreateCards create;

    // The image from the snapshot in original form.
    BufferedImage buIm;
    Point2D paneXY;
    boolean bool;// = false;
    //boolean isTrue;
    protected static SnapshotParameters snapParams = new SnapshotParameters();
    // used to check shapes on the screen.
    //java.awt.Color awtColor;



    /* *** Create cards with shapes *** */
    // create an array of shapes that will exist in each
    // position.

    // question shapes
    Rectangle rect1 = new Rectangle(20 , 50,40 + 5, 60 - 5);
    Rectangle rect2 = new Rectangle(300 , 50 ,40, 60);
    Rectangle rect3 = new Rectangle(300 , 200 ,40 - 5, 60 + 5);
    Rectangle rect4 = new Rectangle(20 , 200 ,40 - 10, 60 + 10);


    Ellipse circle1 = new Ellipse(100, 130 ,40 + 5, 60 - 5);
    Ellipse circle2 = new Ellipse(120, 150 ,40, 60);
    Ellipse circle3 = new Ellipse(140, 170 ,40 - 5, 60 + 5);
    Ellipse circle4 = new Ellipse(160, 190 ,40 - 10, 60 + 10);

    // answer shapes
    Rectangle rect13 = new Rectangle(30 , 30 ,60 + 5, 40 - 20);   // xxx
    Rectangle rect14 = new Rectangle(260 , 30 ,60 + 10, 40 - 10 );
    Rectangle rect15 = new Rectangle(260 , 210 ,60 + 15, 40 - 15);
    Rectangle rect16 = new Rectangle(30 , 210 ,60 + 20, 40 - 20);

    Ellipse circle13 = new Ellipse(140, 170 ,60 + 5, 40 - 5);
    Ellipse circle14 = new Ellipse(160, 190 ,60 + 10,40 - 10);
    // adjusted for answer to keep shape in bounds.
    Ellipse circle15 = new Ellipse(180, 210 ,60 - 45, 40 - 15);
    Ellipse circle16 = new Ellipse(200, 230 ,60 - 30, 40 - 20);

    // The Question or Upper shapes array
    Shape[][] shapesDblAry = {{rect1,circle1}, {rect2, circle2}, {rect3, circle3}, {rect4, circle4}};
        //this.shapesDblAry = shapesDblAry;

    // The Answer or lower shapes array
    public Shape[][] revShapesDblAry = {{rect13, circle13}, {rect14, circle14}, {rect15, circle15}, {rect16, circle16}};
        //this.revShapesDblAry = revShapesDblAry;


    @Override
    public void start(Stage backgroundStage) {


        /** first we create shapes */



        /** we check that shapes are saved in the correct shapesArray **/

        /** we check that when shapes change in BuildTool, they change in the right pane.
         *  At the same time **/

        /** Then we check editing.. **/

            // add an image for the rightPaneœ

            // click on the right pane

            // check the popUp for the correct image

            // @test every time... That shapeArray is correct

        /** Testing shapes in RightPane **/

        // add shapes to shapes array

        // check right pane


        /** Testing shapes in popUp **/

        // click rightPane

        // check for shapes in popUp

        // check there are still shapes in rPane and they are related. ie
        // when they are moved in the PopUp pane, they are moved in the
        // rightPane.

        /** Testing shapes in popUp are Editable **/

            /** change image size **/
            // click on shape

            // change size of shape

            // save shape

            // check shape is correct size
            /** change image location **/


            /** Delete image **/


            /** add a new image **/


            /** When **/


        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: TRANSPARENT");
        // Set color to 0,0,0,0.1 instead of TRANSPARENT to fix windows 10 issue.
        Scene backScene = new Scene(pane, screenWt, screenHt, new Color(0, 0, 0, 1d / 255d));
        backgroundStage = new Stage(StageStyle.TRANSPARENT);

        backgroundStage.setScene(backScene);
        backgroundStage.show();
		/* Do not forget to put the GUI in front of windows.
		 Otherwise, the robots may interact with another
        window, the one in front of all the windows... */
        backgroundStage.toFront();


    }


    /**
     * Use this setup if testing the deck creation from the beginning
     * is important for this test. Clears the existing deck if it already
     * exists.
     * @throws Exception
     */
    //@BeforeAll
    public void setup() throws Exception {

        robot = new FxRobot();

        String strPath = "../flashMonkeyFile/TestingDeck.dat";
        //File file = new File(strPath);
        Path filePath = Paths.get(strPath);

        boolean fileBool = Files.deleteIfExists(filePath);
        if(!fileBool){
            System.out.println("It does not exist or, IDK where that file is: " + filePath);
        //    System.exit(1);
        }
        LOGGER.info("file deleted: " + fileBool);
        // Sleep Moved up here for windows
        sleep(100);
        // used to confirm if images are the same.
        bool = false;

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(FlashMonkeyMain.class);

        //sleep(100);
        final Stage window = FlashMonkeyMain.getWindow();
        delta_X = (int) window.getX();// getScegetY();
        delta_Y = (int) window.getY();
        create = new CreateCards(delta_X, delta_Y);
        create.removePrevFile(strPath);

        //CreateFlashPane cfp = CreateFlashPane.getInstance();
    }

    /**
     * Use this setup if the card deck needed in a test
     * already exists and is tested.
     * @throws Exception
     */
    public void setUpFileExists() throws Exception {
        robot = new FxRobot();
        String strPath = "../flashMonkeyFile/TestingDeck.dat";
        File file = new File(strPath);
        //Path filePath = Paths.get(strPath);
        assertTrue("Deck located at: " + strPath + " Does not exist. " +
                "Ensure test 3 runs previous to using this method", file.exists());

        bool = false;

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(FlashMonkeyMain.class);

        final Stage window = FlashMonkeyMain.getWindow();
        delta_X = (int) window.getX();// getScegetY();
        delta_Y = (int) window.getY();
        create = new CreateCards(delta_X, delta_Y);
        // get the deck created in previous test
        create.getExistingDeck();

        //cfp = CreateFlashPane.getInstance();

    }

    @AfterAll
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
        robot.release(MouseButton.PRIMARY);
        robot.release(MouseButton.SECONDARY);
    }

    // Test if rectangle has verticies and is resizable.
    //   - check for update in rightPane
    // Check if rectangle is moveable in popup pane
    // Check if rectangle appears in the right pane
    // Check if image appears in the right pane
    // check if image appears in the pop-up
    // check if rectangle appears in pop-up
    // check if a rectangle can be added to the pop-up
    // check if the newly added rectangle appears in the right pane

    // *** TESTING NOT COMPLETED IN THIS TEST ***
    // check if the saved additions are saved in the arrayOfShapes.
    // Check if a shape can be deleted from the deck
    /**
     * See notes in Source Code
     *
     * and tests that the rightPane contains the correct image.
     */
    @Test
    @Order(1)
    void checkImagesAndShapesinOneCard() throws Exception{
        //how do we test if we have the correct image?+

        CreateFlash cfp = CreateFlash.getInstance();
        setup();
        sleep(1000);
        create.writeDeckNameHelper();
        sleep(500);



        // Type on the screen
        Point2D xyUType = cfp.getEditorU().getTextAreaXY();
        robot.clickOn(xyUType);
        robot.write("Hello, I’m Flash.");

        // Drag and drop image of FlashMonkey
        // bring finder on top of bg pane
        // click on image on desktop
    /*
        robot.moveTo(370, 800);
        sleep(500);
        robot.drag(MouseButton.PRIMARY);
        //    robot.drag(200,300);
        robot.moveTo(xyUType);
        sleep(500);
        robot.release(MouseButton.PRIMARY);
        sleep(1000);
    */
    /*    robot.clickOn(xyUType);
        robot.write("\n" +
                "... Watch me take notes. ");
    */
        // Clear the question text
        Point2D xy = cfp.getEditorU().getClearTextBtnXY();
        robot.clickOn(xy);
        sleep(500);

        //  *** Create snapshot ***


        double diff_x = 200;
        double diff_y = 200;

        // get snapshotbutton minxy
        xy = cfp.getEditorL().getSnapShotBtnXY();
        robot.clickOn(xy);
        sleep(500);

        // Create the snapshot by drawing the
        // snapshot rectangle on the screen.
        robot.moveTo(50 + diff_x,50 + diff_y);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(400 + diff_x, 300 + diff_y);
        robot.release(MouseButton.PRIMARY);
        sleep(500);

        /** At this point, should test the image in the right pane **/



        // create 1st shape, a triangle
        DrawTools dt = DrawTools.getInstance();
        // set the originalCanvas
        this.originalCanvas = dt.getCanvas();
        // get triangle button xy location
        xy = dt.getTriBtnXY();
        robot.clickOn(xy);
        sleep(200);

        // Create triangle on overlay pane
        robot.moveTo(235 + diff_x,173 + diff_y);
        sleep(200);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(255 + diff_x, 173 + diff_y);
        robot.release(MouseButton.PRIMARY);
        sleep(200);

        /** At this point, should test the image in the right pane **/
        buIm = SnapShot.getInstance().getImgBuffer();
        Image imageL = SwingFXUtils.toFXImage(buIm, null);
        ImageView rPaneiView = (ImageView) cfp.getEditorL().getRightPane().getChildren().get(0);

        assertEquals("Image in the right pane does not look correct. ", true, ImageUtility.imagesLookTheSame(" lower pane ", imageL, rPaneiView.getImage()) );

        // create a rectangle & check it is there
        // click on rectangleButton

        xy = dt.getRectBtnXY();
        robot.clickOn(xy);

        // Create rectangle on overlay pane
        robot.moveTo(100 + diff_x,100 + diff_y);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(150 + diff_x, 150 + diff_y);
        // Make sure rectangle is showing while it is built

            // Print the refernce for the canvas
        dt.getOverlayTestCanvas();

        System.out.println("Printing classes to visually verify canvas reference");
        for(int i = 0; i < dt.getOverlayPane().getChildren().size(); i++) {
            System.out.println(dt.getOverlayPane().getChildren().get(i).getClass().getName() + " " + dt.getOverlayPane().getChildren().get(i) );
        }



        for(Node p : dt.getOverlayPane().getChildren()) {
            System.out.println("panes in overlayPane " + p.getClass().getName());
            if( p.getClass().getName().contains("Canvas") ) {
                bool = true;
            }
        }

        assertTrue("line 355, overlayCanvas is not the original reference", (originalCanvas == dt.getCanvas()));
        assertTrue("overlayCanvas is missing from overlayPane", bool);


        // Check if the rectangle is showing while being drawn.
        // May fail if background of first pixel is not white
        String msg1 = "Rectangle does not appear to be on the screen";
//        checkPixColor(100 + (int) diff_x, 100 + (int) diff_y, msg1, 255, 55, 255);


        robot.release(MouseButton.PRIMARY);
        sleep(200);

        // move rectangle
        robot.moveTo(120 + diff_x, 120 + diff_y);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(211 + diff_x, 215 + diff_y);
        robot.release(MouseButton.PRIMARY);
        sleep(200);

        // resize rectangle
        robot.clickOn(MouseButton.SECONDARY);
        sleep(200);
        robot.moveTo(214 + diff_x, 193 + diff_y);
        sleep(100);
        robot.drag(MouseButton.PRIMARY );
        robot.moveTo(214 + diff_x, 215 + diff_y);
        robot.release(MouseButton.PRIMARY);
        sleep(300);

        // Save the shapes
    //    xy = dt.getSaveBtnXY();
    //    robot.clickOn(xy);
    //    sleep(250);

        // now exit shapes
        xy = dt.getExitBtnXY();
        robot.clickOn(xy);
        sleep(300);

        /**
         * Check Right Pane for image, editing shapes,
         * adding new shapes, and removing shapes.
         */

        // CLICK ON RIGHT PANE
        // popUp should be created with editable
        // shapes and popUp DrawTools
        xy = cfp.getEditorL().getRightPaneXY();
        robot.moveTo(xy);
        //sleep(2000);
        robot.clickOn(xy);
        sleep(750);


        /**  Check if popUp pane contains the correct image **/
        buIm = SnapShot.getInstance().getImgBuffer();
        imageL = SwingFXUtils.toFXImage(buIm, null);
        ImageView popUpView = (ImageView) dt.getOverlayPane().getChildren().get(0);

        assertEquals("Image in the right pane does not look correct. ", true, ImageUtility.imagesLookTheSame("popUp ", imageL, popUpView.getImage()) );


        // Check editing an existing shape.

        double ht1;
        double rtpaneShapeHt1;// = (double) cfp.getEditorL().getShapesFmRPane().get(1);
        double wd1;


        //Click on shape in popUpPane and move the top of the rect upwards then check
        ArrayList<GenericShape> shapeAry = cfp.getEditorL().getArrayOfFMShapes();

        FMRectangle fmRect = new FMRectangle();
        int num = 0;
        for(GenericShape gs : shapeAry) {

            if(num > 0 && gs.getClass().getName() == "FMRectangle") {
                fmRect = (FMRectangle) gs;
                System.out.println("found it" + fmRect.toString());
                break;
            }
            System.out.println("ShapeAry element class: " + gs.getClass().getName());
            num++;
        }

        if(fmRect.getX() >= 1) {

            //need to add the panes x & y location to the xy position
            paneXY = dt.getOverlayPaneXY();

            xy = new Point2D(fmRect.getX() + paneXY.getX(), fmRect.getY() + paneXY.getY());
            robot.moveTo(xy);
            // should see verticies of rectangle in popUpPane
            robot.clickOn(MouseButton.SECONDARY);

            ht1 = fmRect.getHt();
            double x1 = xy.getX() + 15;
            double y1 = xy.getY() - fmRect.getHt() / 2 + 5;

            // Move top verticy and check if ht has changed

            robot.moveTo(x1 , y1 );
            sleep(500);
            robot.drag(MouseButton.PRIMARY);
            robot.moveTo(x1, y1 - 10 );
            sleep(500);
            robot.release(MouseButton.PRIMARY);

            assertTrue("Rectangle, Could not change ht with verticies", fmRect.getHt() >= ht1 + 8.0);
            sleep(1000);

            // check if shape is in the right pane and is the right size
            ArrayList arrayList = cfp.getEditorL().getNodesFmRPane();
            Rectangle fxRect = new Rectangle();

            for(num = 0; num < arrayList.size(); num++) {

                System.out.println("arrayList class name: " + arrayList.get(num).getClass().getName());

                if(num > 1 && arrayList.get(num).getClass().getName() == "javafx.scene.shape.Rectangle") {
                    fxRect = (Rectangle) arrayList.get(num);
                    System.out.println("found it again");

                    break;
                }
                //System.out.println("ShapeAry element class: " + num.getClass().getName());
                //num++;
            }

            rtpaneShapeHt1 = fxRect.getHeight();

            double scale;// = fmRect.getWd() / ((FMRectangle) shapeAry.get(0)).getWd();
            scale = Fit.calcScale(((FMRectangle) shapeAry.get(0)).getWd(), ((FMRectangle) shapeAry.get(0)).getHt(), 100, 100);

            int ceil1 = (int) fmRect.getScaledShape(scale).getHeight();
            int ciel2 = (int) rtpaneShapeHt1;
            assertEquals("Shape in RtPane is iether not present or the wrong size", ceil1, ciel2);

            System.out.println("fmRect location X: " + xy.getX() + " Y: " + xy.getY());


        } else {
            LOGGER.warn("No rectangle found. Size of shapeArray: "
                    + shapeAry.size());
        }


        sleep(1000);


        // Add a new rectangle and check right pane

        // move to the rectangle button and click on it.
        xy = dt.getRectBtnXY();
        robot.clickOn(xy);

        dt.getOverlayTestCanvas();

        // create new rectangle
        robot.moveTo(paneXY.getX() + 50, paneXY.getY() + 50);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(paneXY.getX() + 150, paneXY.getY() + 150);

        // Check if new rectangle is showing while being drawn
  //      assertTrue("line 520, overlayCanvas is not the original reference", (originalCanvas == dt.getCanvas()));
        // May fail if background of first pixel is not white
        String msg = "New rectangle does not appear to be being drawn on the canvas";
//        checkPixColor((int) paneXY.getX() + 50, (int) paneXY.getY() + 50, msg, 221, 0 ,223);
        ImageUtility.checkPixColor((int) paneXY.getX() + 50, (int) paneXY.getY() + 50, msg, 206, 26 ,207);

        robot.release(MouseButton.PRIMARY);



        // check right pane for new rectangle
        Pane rPane = cfp.getEditorL().getRightPane();
        int rPaneSize = rPane.getChildren().size();

        //Rectangle fxRect = new Rectangle();

        for(num = 0; num < rPaneSize; num++) {

            System.out.println("arrayList class name: " + rPane.getChildren().get(num).getClass().getName());

            if(num > 3 && rPane.getChildren().get(num).getClass().getName() == "javafx.scene.shape.Rectangle") {
                //fxRect = (Rectangle) rPane.getChildren().get(num);
                System.out.println("found it again again");

                break;
            }
            //System.out.println("ShapeAry element class: " + num.getClass().getName());
            //num++;
        }

        Shape s = (Shape) rPane.getChildren().get(num);


        System.out.println("New rectangle height: " + ((Rectangle) s).getHeight() );


        assertTrue("Width in new Rectangle is not correct", 29 >= (int) ((Rectangle) s).getWidth() - 2 );
        assertTrue("Height in new Rectangle is not correct",29 >= (int) ((Rectangle) s).getHeight() - 2 );
        assertTrue("RightPane does not contain the new Rectangle", rPane.getChildren().get(rPaneSize - 1).getClass().getName().contains("Rectangle"));
        sleep(1000);


        // CLOSE AND RE-OPEN AND CHECK FOR CORRECT SHAPES

        // save and close popup and tools.
    //    xy = dt.getSaveBtnXY();
    //    robot.clickOn(xy);
        xy = dt.getExitBtnXY();
        robot.moveTo(xy);
        robot.clickOn(xy);
        sleep(200);

        // re-open
        xy = cfp.getEditorL().getRightPaneXY();
        robot.moveTo(xy);
        //sleep(2000);
        robot.clickOn(xy);
        sleep(1000);

        // check that only one popUp will open
        robot.moveTo(500, 500);
        robot.clickOn(xy);
        sleep(1000);


        // @TODO check if the saved additions are saved in the arrayOfShapes, Check if a shape is deleted

    }  // END TEST 1



    Image qTxtImg;
    Image aTxtImg;
    Image[][] images = new Image[4][4];

    int i = 0;
    /**
     * <pre>
     * Builds the 4 card test that test 4, 5 and 6 use.
     * Check text, shapes, images, modifications, deletions, and additions are persistant in editor
     * and in read pane.
     * 1) Verify that shapes that are modified, added and deleted while in edit
     * mode are saved.
     * 2) Ensure that Shape Pane does not contain extra shapes.
     * 3) Ensure that shape pane displays the proper number of shapes while in edit mode
     * 4. Ensure that the proper number of shapes and at the correct size are displayed
     *      in readFlash session.
     *      a) rightPane
     *      b) in popUp.
     * </pre>
     * @throws Exception
     */
    @Test
    void checkTextImagesAndShapesMultipleCards() throws Exception {
        setup();
        sleep(1000);
        create.writeDeckNameHelper();
        sleep(500);
        int numCards = 4;


        // Create the cards, and get the images from creating cards
        images = create.createNewCardsAndShapesHelper( numCards, shapesDblAry, revShapesDblAry);

        CreateFlash cfp = CreateFlash.getInstance();
        Point2D xy = cfp.getSaveDeckButtonXY();
        robot.clickOn(xy);

        /* *** CHECK DECK FOR SHAPES, IMAGES, and TEXT *** */

        // GO TO Q&A and COMPARE IMAGES and SHAPES in Question and Answer
        // press study button
        ReadFlash rf = ReadFlash.getInstance();
        xy = rf.getStudyButtonXY();
        robot.moveTo(xy);
        robot.clickOn(xy);
        sleep(500);

        // press Q&A button
        xy = rf.getQnAButtonXY();
        robot.moveTo(xy);
        robot.clickOn(xy);
        sleep(200);

        // should see card 1 in Q&A
        DirectoryMgr dirMgr = new DirectoryMgr();
        String mediaPath = dirMgr.getMediaPath('C');


        FileOpsUtil.folderExists(new File(mediaPath));

        // For now save Text Images to file and use them in a minute.
        String qFileStr = mediaPath + "tempQ_txt.png";
        String ansFileStr = mediaPath + "tempA_txt.png";
        File qFile = new File(qFileStr);
        File aFile = new File(ansFileStr);

        snapParams.setViewport(new Rectangle2D(+10,0, 100, 35));

        QandA.QandASession qa = QandA.QandASession.getInstance();

        // Read the cards in the read session, and ensure the data is correct.
        // Use images taken of the nodes to ensure that the data is correct at
        // the highest level.
        // Image Array Order is 0-qText, 1-qImage, 2-aText, 3-aImg
        /*
        Loop through the cards and check images, shapes, and text. :)
         */
        for(i = 0; i < FlashCardOps.getInstance().getFlashList().size(); i++) {

            // get answer
            xy = qa.getAnswerButtonXY();
            robot.moveTo(xy);
            robot.clickOn(xy);
            sleep(500);

            // Take a snapshot of the text area for Question and Answer and
            // compare text in Upper and Lower section.

            Platform.runLater(() -> {
                //Take a snapshot of the questionText  and answerText
                // areas, then create a file
                Node qNode = qa.getUpperHBox();
                Node aNode = qa.getLowerHBox();
                qTxtImg = qNode.snapshot(snapParams, null);
                aTxtImg = aNode.snapshot(snapParams, null);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(qTxtImg, null), "png", qFile);
                    ImageIO.write(SwingFXUtils.fromFXImage(aTxtImg, null), "png", aFile);
                } catch (Exception e) {
                    System.out.println("File not created");
                    System.out.println(e.getStackTrace().toString());
                }
            });

            // Compare with img[i][0]
            // Display questions in a popUp

            Platform.runLater(() -> {
                ImageView imgV1 = new ImageView(images[i][0]);
                ImageView imgV2 = new ImageView(new Image("File:" + qFileStr));

                HBox hbox = new HBox(2);
                hbox.getChildren().addAll(imgV1, imgV2);
                Scene scene = new Scene(hbox);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setX(30);
                stage.show();
                stage.toFront();

                // Question text... Its different than answer but they work very well
                // with 0 differences in number of pixels between images. Do not change
                // for mac.
                // do not move!!!
                bool = ImageUtility.txtImgsLookTheSame(images[i][0], new Image("File:" + qFileStr));
            });

            sleep(1000);
            assertTrue("Question text is not the same", bool);

            // Now check the answerTxt is correct
            bool = ImageUtility.txtImgsLookTheSame(images[i][2], new Image("File:" + ansFileStr));

            // Display the answers in a popup

            Platform.runLater(() -> {
                ImageView imgV1 = new ImageView(images[i][2]);
                ImageView imgV2 = new ImageView(new Image("File:" + ansFileStr));

                HBox hbox = new HBox(2);
                hbox.getChildren().addAll(imgV1, imgV2);
                Scene scene = new Scene(hbox);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
                stage.toFront();

            });

            sleep(1000);
            assertTrue("Answer text is not the same", bool);


        // compare images in Upper and Lower sections with originals
        // upper

            HBox upperBox = (HBox) qa.getUpperHBox();
            FindNode find = new FindNode();
            // check the image
            Node foundNode = find.findNodeInPaneGraphStarter( new ImageView(), upperBox);
            if(foundNode == null) {
                LOGGER.warn("Image in " + find.getClass().getName() + " was not in upperBox.");
                assertTrue("foundNode is null", (foundNode != null));
                System.exit(0);
            }
            ImageView tempIView = (ImageView) foundNode;
            // The image from the right pane.
            Image imgFmRPane = tempIView.getImage();

            Platform.runLater(() -> {

                ImageView imgV1 = new ImageView(images[i][1]);
                ImageView imgV2 = new ImageView(imgFmRPane);

                HBox hbox = new HBox(2);
                hbox.getChildren().addAll(imgV1, imgV2);
                Scene scene = new Scene(hbox);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setX(30);
                stage.show();
                stage.toFront();

                System.out.println("upperHBox ");

                // Works alright here
                bool = ImageUtility.imagesLookTheSame("upperHBox", images[i][1], imgFmRPane);
            });
            sleep(2000);

            assertTrue("Images are not the same in upper right pane", bool);

        // check upper popUp

            xy = rf.getRPCenter2dPoint();
            xy = new Point2D(xy.getX() + 460, xy.getY() + 20);
            robot.moveTo(xy);
            robot.clickOn(xy);
            sleep(2000);

            // Get the shapes from the popUp canvas
            // and make sure they are there
            Stage popWin = MediaPopUp.getInstance().testingGetWindow();
            Scene popScene = popWin.getScene();
            Pane rootAns = (Pane) popScene.getRoot();
            foundNode = find.findNodeInPaneGraphStarter( new Pane(), rootAns);
            System.out.println("foundNode is a Pane: " + foundNode.getClass().getName().endsWith("Pane"));
            // Check the popUp for correct question shapes
            ShapeUtility su = new ShapeUtility();
            ShapeUtility.CheckShapes check = su.new CheckShapes();
            bool = check.checkShapes(shapesDblAry[i], foundNode);
            assertTrue("Shapes in the question popUp pane did not match the origninal shapes at card [" + i + "]", bool);



        // lower

            HBox lowerBox = (HBox) qa.getLowerHBox();
            FindNode find2 = new FindNode();
            Node foundNode2 = find2.findNodeInPaneGraphStarter(new ImageView(), lowerBox);
            if(foundNode2 == null) {
                System.out.println("Image in " + find2.getClass().getName() + " was not in lowerBox.");
                assertTrue("foundNode2 is null", (foundNode2 != null));
                System.exit(0);
            }
            ImageView tempIView2 = (ImageView) foundNode2;
            Image tempImg2 = tempIView2.getImage();

            bool = ImageUtility.imagesLookTheSame("lowerHBox", images[i][3], tempImg2);

            Platform.runLater(() -> {

                ImageView imgV1 = new ImageView(images[i][3]);
                ImageView imgV2 = new ImageView(tempImg2);
                HBox hbox = new HBox(2);
                hbox.getChildren().addAll(imgV1, imgV2);
                Scene scene = new Scene(hbox);
                Stage stage = new Stage();
                stage.setX(30);
                stage.setScene(scene);
                stage.show();
                stage.toFront();

                System.out.println("lowerHBox ");

            });
            sleep(2000);

            assertTrue("Images are not the same in lower right pane", bool);

            // check lower popUp, click on the rightPane
            xy = rf.getRPCenter2dPoint();
            xy = new Point2D(xy.getX() + 460, xy.getY() + 460);
            robot.moveTo(xy);
            robot.clickOn(xy);
            sleep(500);
            robot.clickOn(xy);
            sleep(2000);


            // Get the shapes from the popUp canvas
            // and make sure they are there
            popWin = MediaPopUp.getInstance().testingGetWindow();
            popScene = popWin.getScene();
            rootAns = (Pane) popScene.getRoot();
            foundNode = find.findNodeInPaneGraphStarter( new Pane(), rootAns);
            System.out.println("foundNode is now a: " + foundNode.getClass().getName().endsWith("Pane"));
            // Checks the popUp for correct answer shapes
            bool = check.checkShapes(revShapesDblAry[i], foundNode);
            assertTrue("Shapes in the answer popUp pane did not match the origninal shapes at [" + i + "]", bool);

            // move to next card
            xy = rf.getNextQButtonXY();
            robot.moveTo(xy);
            robot.clickOn(xy);
            sleep(500);
        }

    } // END TEST 3


    /*
    // Starting from opening an existing file
    // go to create/edit cards
    //
    // verify there are the correct shapes in the right pane
    // there are the correct images in the right pane
    // verify correct text

    // verify that correct shapes and images are in the pop-up // although we've done this in test 2.
    // modify an existing shape
    // add a shape
    // exit popUp
    // modify text
    // go to new card
    // verify correct images and shapes and text in rightPane
    // go back to prev card
    // verify modifications in right pane,
    // verify modifications in textArea
    // verify modifications in popup
    // Modify and verify 2 more cards
    // Exit to read and verify changes.

     */

    Image upperTxtImg;
    Image lowerTxtImg;
    ImageView rPaneiView;
    /**
     * Checks that correct text, shapes, and images appear
     * in the editor from a saved file.
     * @throws Exception
     */
    @Ignore
    @Order(4)
    void check_Text_Images_n_shapes_In_Editor() throws Exception {
/*
        //robot = new FxRobot();
        setUpFileExists();
        sleep(500);
        //create.createDeckHelper();
        //sleep(500);

        final DirectoryMgr dirMgr = new DirectoryMgr();
        final String mediaPath = dirMgr.getMediaPath('C');
        final ReadFlash rf = ReadFlash.getInstance();
        final FileNaming fileNaming = new FileNaming();
        final authcrypt.UserData data = new authcrypt.UserData();

        CreateFlash cfp = CreateFlash.getInstance();
        // click on createFlash button
        Point2D xy;
        xy = rf.getCreateButtonXY();
        // Should now see create a new card
        robot.clickOn(xy);
        sleep(200);
        // Set xy to prevBtn
        xy = cfp.getPrevBtnXY();

        for( i = 3; i > 0; i--) {
            // Press back button to get a previous card
            robot.clickOn(xy);
            sleep(500);


            // *** IMAGES *** //
    
            
            // Name for qImg to retrieve from file
            // Need to update this test so that it is naming the file based on how it looks.
            FileNaming qName = new FileNaming(UserData.getUserName(), fileNaming.getCardHash(ReadFlash.getInstance().getDeckName(), "SHA"), 'i', ".png");
            String qImgStr = mediaPath + qName.getImgFileName();

            // get the question image from the file
            Image qImage = new Image("File:" + qImgStr);

            // get qImage from rPane
            FindNode find = new FindNode();
            rPaneiView = new ImageView();
            Node node = find.findNodeInPaneGraphStarter(new ImageView(), cfp.getEditorU().getRightPane());
            if(node.getClass().toString().contains("ImageView")) {
                rPaneiView = (ImageView) node;
                if(rPaneiView == null) {
                    System.err.println("rPaneIView is null");
                }
            } else {
                System.out.println("did not find ImageView in right pane at line 1073 " );
                assertTrue("ImageView could not be found", (rPaneiView == null));
                System.exit(1);
            }

            assertEquals("Image in the upper right pane does not look correct. ",
                    true, ImageUtility.imagesLookTheSame("upper right pane", qImage, rPaneiView.getImage()) );


            // *** Answer Image *** //
            // Need to update this test so that it is naming the file based on how it looks.
            FileNaming ansName = new FileNaming(UserData.getUserName(), fileNaming.getCardHash(ReadFlash.getInstance().getDeckName(), "SHA"), 'i', ".png");
            String ansImgStr = mediaPath + ansName.getImgFileName();
            Image ansImage = new Image("File:" + ansImgStr);

            // get answer Image from rPane
            find = new FindNode();
            rPaneiView = new ImageView();
            node = find.findNodeInPaneGraphStarter(new ImageView(), cfp.getEditorL().getRightPane());
            if(node.getClass().toString().contains("ImageView")) {
                rPaneiView = (ImageView) node;
                if(rPaneiView == null) {
                    System.err.println("rPaneIView is null ansImage");
                }
            } else {
                System.out.println("did not find ImageView in ans rightPane at line 1100 " );
                assertTrue("ImageView could not be found", (rPaneiView == null));
                System.exit(1);
            }

            assertEquals("Image in the lower right pane does not look correct. ",
                    true, ImageUtility.imagesLookTheSame("upper right pane", ansImage, rPaneiView.getImage()) );

            //  *** compare text *** //

            // check text
            // Take snapshot of upper and lower text areas
            Platform.runLater(() -> {
                System.out.println("In runlater and i is: " + i);
                    snapParams.setViewport(new Rectangle2D(+10,0, 100, 35));
                    upperTxtImg = CreateFlash.getInstance().getEditorU().tCell.getTextArea().snapshot(snapParams, null);
                    lowerTxtImg = CreateFlash.getInstance().getEditorL().tCell.getTextArea().snapshot(snapParams, null);

                // ** QUESTION TEXT images ** //
                String qTextFileStr = mediaPath + "createTEST_Q_img_" + i + ".png";
                Image savedTextImage = new Image("File:" + qTextFileStr);
         //       System.out.println("image fileName: " + savedImage.impl_getUrl());
                if (upperTxtImg != null && savedTextImage != null) {
                    bool = ImageUtility.txtImgsLookTheSame(upperTxtImg, savedTextImage);
                    assertTrue("Upper text are not the same", bool);
                }

                // ** ANSWER TEXT IMAGES ** //
                String ansTextFileStr = mediaPath + "createTEST_A_img_" + i + ".png";
                savedTextImage = new Image("File:" + ansTextFileStr);
                if (lowerTxtImg != null && savedTextImage != null) {
                    bool = ImageUtility.txtImgsLookTheSame(lowerTxtImg, savedTextImage);
                    assertTrue("Lower text are not the same", bool);
                }
            });


            //  *** compare shapes for upper editor *** //
            ArrayList<Node> nodes =  cfp.getEditorU().getNodesFmRPane();
            Shape[] clsShapes = shapesDblAry[i];
            ShapeUtility su = new ShapeUtility();
            ShapeUtility.CheckShapes check = su.new CheckShapes();
            bool = check.compareShapesWRightPane(nodes, clsShapes, qImage, i);
            // check shapes in upper rightPane
            assertTrue("\nShapes do not match in upper editor", bool);

            // *** compare shapes for lower editor *** //
            nodes = cfp.getEditorL().getNodesFmRPane();
            clsShapes = revShapesDblAry[i];
            bool = check.compareShapesWRightPane(nodes, clsShapes, qImage, i);
            // check shapes in upper rightPane
            assertTrue("\nShapes do not match in lower editor", bool);

            sleep(100);

 */
    }
}





