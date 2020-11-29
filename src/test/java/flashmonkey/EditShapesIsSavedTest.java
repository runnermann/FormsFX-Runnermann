package flashmonkey;

import core.CoreUtility;
import core.ShapeUtility;
import fileops.DirectoryMgr;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.robot.impl.SleepRobotImpl;
import type.celleditors.DrawTools;
import type.celltypes.CanvasCell;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("editedShapes")
public class EditShapesIsSavedTest extends ApplicationTest {

    //private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Logger LOGGER = LoggerFactory.getLogger(EditShapesIsSavedTest.class);
    
    private int delta_X = 0;
    private int delta_Y = 10;





    FxRobot robot;// = new FxRobot();
    SleepRobotImpl sleep = new SleepRobotImpl();

    String strPath = "../flashMonkeyFile/TestingDeck.dat";
    // image paths
    String imgPath = "src/resources/testing/TestImage.png";
    String otherPath = "src/resources/testing/beniofWifeAndFriend.png";
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

    BufferedImage buIm;
    Point2D paneXY;
    boolean bool;// = false;
    //boolean isTrue;
    protected static SnapshotParameters snapParams = new SnapshotParameters();

    ShapeUtility su = new ShapeUtility();


    @Override
    public void start(Stage backgroundStage) {

        CoreUtility utility = new CoreUtility();

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
     * Use this setup if the card deck needed in a test
     * already exists and is tested.
     * @throws Exception
     */
    //@BeforeAll
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

    // verify that edited shapes are saved and properly displayed in
    // editor and in ReadFlash / TestTypes
    public static final int UPPER = 0;
    public static final int LOWER = 1;
    @Test
    @Order(6)
    void editedShapesIsSaved() throws Exception {

        setUpFileExists();
        sleep(500);
        //create.writeDeckNameHelper();
        sleep(500);
        RemoveThisTester removeTester = new RemoveThisTester();

        // check if shapeFileNames are the same
        final CreateFlash cfp = CreateFlash.getInstance();
        //   bool = checkFileNameStrings(cfp.getCreatorList());
        //   assertTrue("ShapeFileNames match in more than one card", bool );

        javafx.scene.shape.Shape[][][] shapesEditedTplArry = new javafx.scene.shape.Shape[4][2][];

        final DirectoryMgr dirMgr = new DirectoryMgr();
        //final String mediaPath = dirMgr.getMediaPath('C');
        final ReadFlash rf = ReadFlash.getInstance();

        //snapParams.setViewport(new Rectangle2D(+10,0, 100, 35));
        Point2D xy;// = new Point2D(999, 999);


        // goto edit create cards
        xy = rf.getCreateButtonXY();
        robot.clickOn(xy);
        sleep(200);

        for(int idx = 3, i = 3; idx > -1; idx--, i--) {

            // press backQ button
            // Set xy to prevBtn
            if(idx > -1) {
                xy = cfp.getPrevBtnXY();
                robot.clickOn(xy);
                sleep(200);
            }


            // *** EDIT THE UPPER SECTION SHAPES *** //


            // CLICK ON RIGHT PANE
            // popUp should be created with editable
            // shapes and popUp DrawTools
            xy = cfp.getEditorU().getRightPaneXY();
            //       robot.moveTo(xy);
            robot.clickOn(xy);
            sleep(750);


            DrawTools dt = DrawTools.getInstance();



            // make rectangle wider by 20
            // Find the rectangle, find edit points, move top point.
            // Check that currently dispalyed shapes are correct
            System.out.println("Checking if shapes are correct: line 1420");
            FindNode find = new FindNode();
            Node foundNode = find.findNodeInPaneGraphStarter(new javafx.scene.shape.Rectangle(), dt.getOverlayPane());


            // for visual verification, the shapes that are stored in the
            // file.
            int shapeIdx = 0;
            for(Node n : dt.getOverlayPane().getChildren()) {

                System.out.println(shapeIdx + ") " + n.getClass().getName());
                shapeIdx++;
            }

            ShapeUtility.CheckShapes check = su.new CheckShapes();
            //            bool = check.checkShapes(shapesDblAry[i], foundNode.getParent());
            //            assertTrue("Shapes in the question popUp pane did not match the origninal shapes at [" + i + "]", bool);
            find = new FindNode();
            foundNode = find.findNodeInPaneGraphStarter( new javafx.scene.shape.Rectangle(), dt.getOverlayPane());

            System.out.println("findNode found a Rectangle: " + foundNode.getClass().getName().contains("Rectangle"));

            if(foundNode == null) {
                LOGGER.warn("Image in " + find.getClass().getName() + " was not in upperBox.");
                assertTrue("foundNull is null", (foundNode == null));
                System.exit(0);
            }

            // Right click on the rectangle to get
            // resize handles
            javafx.scene.shape.Rectangle tempRect = (javafx.scene.shape.Rectangle) foundNode;
            Bounds bounds = tempRect.getLayoutBounds();
            Bounds sb = tempRect.localToScreen(bounds);
            // Click on rect
            xy = new Point2D(sb.getMinX() + 5, sb.getMinY() + 5);
            robot.clickOn(xy, MouseButton.SECONDARY);
            // calc top node
            double x = sb.getMinX() + (sb.getWidth() / 2);
            double y = sb.getMinY();
            Point2D topNode = new Point2D(x , y);

            // drag topNode up 20 pix
            robot.moveTo(topNode);
            sleep(2000);
            robot.drag( MouseButton.PRIMARY);
            robot.moveTo(x, y - 20);
            robot.release(MouseButton.PRIMARY);
            sleep(1000);

            // delete ellipse
            // Find the ellipse
            foundNode = find.findNodeInPaneGraphStarter( new Ellipse(), dt.getOverlayPane());

            System.out.println("findNode found a Ellipse: " + foundNode.getClass().getName().contains("Ellipse"));

            if(foundNode == null) {
                LOGGER.warn("Shape in " + find.getClass().getName() + " was not in upperBox.");
                assertTrue("foundNode is null", (foundNode == null));
                System.exit(0);
            }

            // RightClick on the circle to get
            // delete button
            Ellipse tempEl = (Ellipse) foundNode;
            bounds = tempEl.getLayoutBounds();
            sb = tempEl.localToScreen(bounds);
            x = sb.getMinX() + (sb.getWidth() / 2);
            y = sb.getMinY() + (sb.getHeight() / 2);
            xy = new Point2D(x, y);
            robot.moveTo(xy);
            robot.clickOn(xy, MouseButton.SECONDARY);
            // Click on the delete button. Guessing where it is with lazy magic hands.
            x += 15;
            y += 15;
            robot.moveTo(x,y);
            robot.clickOn(x,y);

            // save the edited shapes for the upper section.

            shapesEditedTplArry[i][UPPER] = su.copyShapes(tempRect);

            // close save shapes and close draw tools

        //    xy = dt.getSaveBtnXY();
        //    robot.clickOn(xy);
            sleep(50);

            xy = dt.getExitBtnXY();
            robot.clickOn(xy);
            sleep(100);


            // *** EDIT THE LOWER SECTION SHAPES *** //


            // CLICK ON RIGHT PANE
            // popUp should be created with editable
            // shapes and popUp DrawTools
            xy = cfp.getEditorL().getRightPaneXY();
            robot.moveTo(xy);
            //sleep(2000);
            robot.clickOn(xy);
            sleep(3000);

            // errors if the fileStrings are the same as the original above in the
            // 1st loop.
            removeTester.testFileStrings(dt.getFileString());
            // Check that shapes are correct
            CreateMediaTester cmt = new CreateMediaTester();
            bool = check.checkShapes(cmt.revShapesDblAry[i], dt.getOverlayPane());
            assertTrue("Shapes in the question popUp pane did not match the origninal shapes at [" + i + "]", bool);

            // make rectangle wider by 20
            // Find the rectangle, find edit points, move top point.
            dt = DrawTools.getInstance();
            find = new FindNode();
            foundNode = find.findNodeInPaneGraphStarter( new javafx.scene.shape.Rectangle(), dt.getOverlayPane());

            if(foundNode == null) {
                LOGGER.warn("Shape in " + find.getClass().getName() + " was not in upperBox.");
                assertTrue("Shape could not be found", (foundNode == null));
                System.exit(0);
            }

            // Right click on the rectangle to get
            // resize handles
            tempRect = (javafx.scene.shape.Rectangle) foundNode;
            bounds = tempRect.getLayoutBounds();
            sb = tempRect.localToScreen(bounds);
            // get nodes click on rect with right Mouse btn
            xy = new Point2D(sb.getMaxX() - 5, sb.getMinY() + 5);
            //xy = new Point2D(sb.getMinX() + 10, sb.getMinY() + 10);
            robot.clickOn(xy, MouseButton.SECONDARY);

            // calc right node
            x = sb.getMaxX();
            y = sb.getMinY() + (sb.getHeight() / 2);
            Point2D rightNode = new Point2D(x , y);

            // drag rightNode right 20 pix
            robot.moveTo(rightNode);
            sleep(2000);
            robot.drag( MouseButton.PRIMARY);
            robot.moveTo(x + 20, y);
            robot.release(MouseButton.PRIMARY);
            sleep(1000);

            // create 2nd rectangle
            // create rectangle
            double delta_X = dt.getOverlayPaneXY().getX();
            double delta_Y = dt.getOverlayPaneXY().getY();

            double minX = tempRect.getX() + delta_X;
            double minY = tempRect.getY() + delta_Y + 25;
            double w = tempRect.getWidth();
            double h = tempRect.getHeight();
            double wX = w + minX;
            double hY = h + minY;

            // The new rectangle to store in the
            // shapesEditedTplArry
            javafx.scene.shape.Rectangle tempRect1 = new Rectangle(minX, minY, w, h);

            System.out.println("Moving to dt.rectangleButton");

            // click on the rect button
            // dt = DrawTools.getInstance();
            xy = dt.getRectBtnXY();
            robot.clickOn(xy);
            sleep(100);

            System.out.println("Creating new rectangle");

            // create the new rectangle
            robot.moveTo(minX ,minY );
            robot.drag(MouseButton.PRIMARY);
            robot.moveTo(wX , hY );
            robot.release(MouseButton.PRIMARY);
            sleep(200);

            foundNode = find.findNodeInPaneGraphStarter( new Ellipse(), dt.getOverlayPane());

            System.out.println("findNode found a Ellipse: " + foundNode.getClass().getName().contains("Ellipse"));

            if(foundNode == null) {
                LOGGER.warn("Shape in " + find.getClass().getName() + " was not in upperBox.");
                assertTrue("ellipse could not be found", (foundNode == null));
                System.exit(0);
            }


            // the unchanged ellipse to be stored
            // in the shapesEditedTplArry.
            Ellipse tempEl1 = (Ellipse) foundNode;

            // save the edited shapes for the lower section
            shapesEditedTplArry[i][LOWER] = su.copyShapes(tempRect, tempRect1, tempEl1);

            // close save shapes and close draw tools
       //     xy = dt.getSaveBtnXY();
       //     robot.clickOn(xy);
       //     sleep(50);
            xy = dt.getExitBtnXY();
            robot.clickOn(xy);

            sleep(1000);
        }

        // save changes
        xy = cfp.getSaveDeckButtonXY();
        robot.clickOn(xy);

        // check if shapeFileNames are the same
        //    bool = checkFileNameStrings(cfp.getCreatorList());
        //    assertTrue("ShapeFileNames match in more than one card", bool );

        // check if saved shapes are not correct
        bool = su.compareShapesInCreatorList(shapesEditedTplArry, cfp.getCreatorList());
        assertTrue("Shape comparison failed. Shapes did not save correctly", bool);


        // close popUp

        // *** Verify edits in editor ***



        // *** Verify edits in rightPane ***



        // *** Verify edits in readFlash ***



        // *** Verify edits in readFlash_PopUp
    }
}
