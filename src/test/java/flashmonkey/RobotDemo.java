package flashmonkey;

import fileops.DirectoryMgr;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.robot.impl.SleepRobotImpl;
import type.celleditors.DrawTools;
import type.celltypes.CanvasCell;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.Assert.assertEquals;

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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("popup")
public class RobotDemo extends ApplicationTest {

    //private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotDemo.class);

    private int delta_X = 0;
    private int delta_Y = 10;

    FxRobot robot = new FxRobot();
    SleepRobotImpl sleep = new SleepRobotImpl();
    DirectoryMgr directory = new DirectoryMgr();
    String strPath = "../flashMonkeyFile/TestingDeck.dat";
    // image paths
    String imgPath = "src/resources/testing/TestImage.png";
    String otherPath = "src/resources/testing/beniofWifeAndFriend.png";
    Path filePath = Paths.get(strPath);
    // for the background stage
    private Toolkit tk = Toolkit.getDefaultToolkit();
    private Dimension d = tk.getScreenSize();
    private int screenWt = d.width;
    private int screenHt = d.height;

    boolean bool;

    Image originalImage;
    Image image2;
    Pane returnPane;
    Pane rightPane;
    CanvasCell cCell;
    Scene scene;
    Stage stage;
    CreateFlash cfp;
    CreateCards create;

    boolean isTrue;

    //private CreateCards create;


    @Override
    public void start(Stage stage) {

//        this.stage = stage;

//        rightPane = new Pane();



        // first image
        //originalImage   = new Image("File:" + imgPath);
        //image2          = new Image("File:" + otherPath);
        //ImageView iView1 = new ImageView(originalImage);
        //ImageView iView2 = new ImageView(image2);

        /** Testing popUp in CreateFlash **/
 //       cfp = CreateFlashPane.getInstance();

        // create the scene
     //   scene = cfp.createFlashScene();
      //  cfp.getEditorU().setText("this is a card");
     //   cfp.getEditorU().snapShotBtnAction();
    /*
        robot.moveTo(50,50);
        robot.clickOn(MouseButton.PRIMARY);
        robot.drag(MouseButton.PRIMARY);
        robot.drag(200, 200);
        robot.release(MouseButton.PRIMARY);

     */

        //cfp.getEditorU().snapShotBtnAction();

     //   rightPane.getChildren().add(iView1);
     //   rightPane.setStyle("-fx-background-color: #32CD32");
     //   cCell = new CanvasCell();

        // 2nd image returning from CanvasCell
     //   returnPane = cCell.buildCell(rightPane, otherPath);

//        isTrue = true;

//        Object obj = returnPane.getChildren().get(0);
//        ImageView returnIView = (ImageView) obj;
//        Image returnImage = returnIView.getImage();

 //       if(returnImage == null || image2 == null ) {
 //           isTrue = false;
//        } else {
/*
            for (int r = 0; r < 250; r++) {

                for (int c = 50; c < originalImage.getWidth() - 10; c++) {

                    System.out.print(" " + r + ", " + c + " | ");

                    if (originalImage.getPixelReader().getArgb(r, c) != image2.getPixelReader().getArgb(r, c)) {
                        isTrue = false;
                        break;
                    }
                }
            }


        System.out.println(); // clear the buffer
        System.out.println("isTrue " + isTrue);
*/


        // image two
        //Object obj = returnPane.getChildren().get(0);
        //ImageView iView2 = (ImageView) obj;
        //Pane pane2 = new Pane(iView2);
        //pane2.setStyle("-fx-background-color: white");
        //pane2.setMinWidth(100);

        //VBox vBox = new VBox(2);
        //vBox.setStyle("-fx-background-color: #0C73C5");
        //vBox.setPrefWidth(200);
        //vBox.setPrefHeight(200);
        //vBox.getChildren().addAll(iView2, iView1);




        /** first we create shapes */



        /** we check that shapes are saved in the correct shapesArray **/

        /** we check that when shapes change in BuildTool, they change in the right pane.
         *  At the same time **/

        /** Then we check editing.. this is harder! **/

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

        Scene backScene = new Scene(pane, screenWt, screenHt, Color.TRANSPARENT);
        stage = new Stage(StageStyle.TRANSPARENT);

        stage.setScene(backScene);
    //    stage.show();
		/* Do not forget to put the GUI in front of windows.
		 Otherwise, the robots may interact with another
        window, the one in front of all the windows... */
        stage.toFront();


        //scene = new Scene(vBox);
        //stage.setScene(scene);
        //stage.show();
        //stage.toFront();
    }




    //@BeforeAll
    public void setup() throws Exception {

        String strPath = "../flashMonkeyFile/TestingDeck.dat";
        //File file = new File(strPath);
        Path filePath = Paths.get(strPath);

        boolean fileBool = Files.deleteIfExists(filePath);
        if(fileBool){
            System.out.println("I failed and didn't remove the file. It was: " + filePath);
            System.exit(1);

        }


        LOGGER.info("file deleted: " + fileBool);
        // used to confirm if images are the same.
        bool = false;


        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(FlashMonkeyMain.class);

        sleep(100);
        final Stage window = FlashMonkeyMain.getWindow();
        delta_X = (int) window.getX();// getScegetY();
        delta_Y = (int) window.getY();
        create = new CreateCards(delta_X, delta_Y);
        //create.removePrevFile();

        cfp = CreateFlash.getInstance();

    }

    @AfterAll
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }


    /**
     * Test if rightPane contains
     * the correct image.
     */
    @Test
    @Order(2)
    void correctMediaDisplayedInRightPane() throws Exception{
        //how do we test if we have the correct image?+
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
        // click on finder in dock
        robot.moveTo(370, 800);
        sleep(500);
        //robot.clickOn(370, 800);
        robot.drag(MouseButton.PRIMARY);
        //    robot.drag(200,300);
        robot.moveTo(xyUType);
        sleep(500);
        robot.release(MouseButton.PRIMARY);
        sleep(1000);

        robot.clickOn(xyUType);
        robot.write("\n" +
                "I’m a college student trying to keep up with my homework, my labs, the crazy professors, and earn an income." +
                "\n\nSo … this is how I roll with FlashMonkey.\n\n" +
                "... Watch me take notes. ");

        // Clear the question text
        Point2D xy = cfp.getEditorU().getClearTextBtnXY();
        robot.clickOn(xy);
        sleep(2000);


        // Type a question into the question area.
        robot.clickOn(xyUType);
        write("What are General-Purpose Registers?");

        // Cut and paste some words into the
        // question.
        robot.moveTo(400, 200);
        sleep(1000);

        double diff_x = 200;
        double diff_y = 200;



        // get snapshotbuton minxy
        xy = cfp.getEditorL().getSnapShotBtnXY();

        robot.clickOn(xy);
        sleep(500);

        // Create the snapshot, draw a rectangle
        robot.moveTo(50 + diff_x,50 + diff_y);
        robot.drag(MouseButton.PRIMARY);
        //    robot.drag(200,300);
        robot.moveTo(400 + diff_x, 300 + diff_y);
        //    sleep(1000);
        robot.release(MouseButton.PRIMARY);
        sleep(2000);



        // create a triangle
        DrawTools dt = DrawTools.getInstance();
        xy = dt.getTriBtnXY();
        robot.clickOn(xy);
        sleep(200);

        // Create triangle on overlay pane
        robot.moveTo(235 + diff_x,173 + diff_y);
        sleep(200);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(255 + diff_x, 173 + diff_y);
        robot.release(MouseButton.PRIMARY);
        sleep(2000);


        // create a rectangle
        // click on rectangleButton

        xy = dt.getRectBtnXY();
        robot.clickOn(xy);

        // Create rectangle on overlay pane
        robot.moveTo(100 + diff_x,100 + diff_y);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(150 + diff_x, 150 + diff_y);
        robot.release(MouseButton.PRIMARY);
        sleep(500);

        // move rectangle
        robot.moveTo(120 + diff_x, 120 + diff_y);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(211 + diff_x, 215 + diff_y);
        robot.release(MouseButton.PRIMARY);
        sleep(500);

        // resize rectangle
        robot.clickOn(MouseButton.SECONDARY);
        sleep(500);
        robot.moveTo(214 + diff_x, 193 + diff_y);
        sleep(100);
        robot.drag(MouseButton.PRIMARY );
        robot.moveTo(214 + diff_x, 215 + diff_y);
        robot.release(MouseButton.PRIMARY);
        sleep(15000);


        // SHOW THE SHAPES IN THE RIGHT PANE
        xy = cfp.getEditorL().getRightPaneXY();
        robot.moveTo(xy);
        sleep(2000);
        robot.clickOn(xy);
        sleep(3000);

        // Save the shapes
    //    xy = dt.getSaveBtnXY();
    //    robot.clickOn(xy);
    //    sleep(250);

        // now exit shapes and draw
        xy = dt.getExitBtnXY();
        robot.clickOn(xy);
        sleep(300);






        // No robot methods for this in java 8....
        // We are doing it by hand for the purpos of a demo.


        // and exit
/*
        robot.moveTo(250, 170);
        sleep(2000);
        robot.drag(MouseButton.PRIMARY );
        robot.moveTo(250, 120);
        robot.release(MouseButton.PRIMARY);
        sleep(5000);
*/



        assertEquals("Failed. and I'm going to fail. Original image reference is not the image displayed. ",
                isTrue, true);

    }




}
