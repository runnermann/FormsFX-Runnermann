package flashmonkey;




import ch.qos.logback.classic.Level;
import fileops.DirectoryMgr;
import fileops.FileOpsUtil;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;


import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.robot.impl.SleepRobotImpl;

import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.Assert.assertEquals;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateCards extends ApplicationTest {
    
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CreateCards.class);
    
    
    private int delta_X = 0;
    private int delta_Y = 10;

    FxRobot robot = new FxRobot();
    SleepRobotImpl sleep = new SleepRobotImpl();
    DirectoryMgr directory = new DirectoryMgr();

    String strPath = "../flashMonkeyFile/TestingDeck.dec";
    Path filePath = Paths.get(strPath);
    // for the background stage
    private Toolkit tk = Toolkit.getDefaultToolkit();
    private Dimension d = tk.getScreenSize();
    private int screenWt = d.width;
    private int screenHt = d.height;


    /**
     * Constructor
     * @param delta_X
     * @param delta_Y
     */
    public CreateCards(int delta_X, int delta_Y) {
        this.delta_X = delta_X;
        this.delta_Y = delta_Y;
    }



    void build() throws Exception {
        Boolean bool = false;
        bool = Files.deleteIfExists(filePath);
        LOGGER.info("is flashMonkeyFolder deleted: " + bool);
    }

    /** creates a new deck
     * NOTE: Expects that no decks exists in the
     * FlashMonkeyFile folder
     */
    void writeDeckNameHelper() //throws Exception
    {
        // Delete the file directory if it exists already
        LOGGER.setLevel(Level.DEBUG);
        // Directory for idk@idk.com
        String deckName = "TestingDeck";

        //String fileName = "TestingDeck";

        // click on create new textƒield
        //FlashCardOps fo = new FlashCardOps();
        //Point2D xy = fo.getNewNameFieldXYforTest();
        //robot.moveTo(xy);
        //robot.clickOn(xy);
        sleep(150);
        write(deckName).push(KeyCode.ENTER);
    }


    public void getTestDeckHelper() {
        // click on top file rdo button
        if( ! Files.exists(filePath)) {
            writeDeckNameHelper();
            createNewCardsHelper();
        } else {
            getExistingDeck();
            sleep(200);
        }
    }
    
    public void getStudyButton() {
        ReadFlash rf = ReadFlash.getInstance();
        xy = rf.getStudyButtonXY();
        robot.moveTo(xy);
        robot.clickOn(xy);
        sleep(500);
    }

    public void getExistingDeck()
    {
        Stage startWindow = FlashMonkeyMain.getPrimaryWindow();
        // Find the node that is the fileButton in filePane
        System.out.println("finding node");
        FindNode find = new FindNode();
        System.out.println("after finding node");
        // FInd the gridpane that contains the scrollpane with the radio button in it.
        // then move to the button from there.
        Node foundNode = find.findNodeInPaneGraphStarter( new GridPane(), startWindow);
        if(foundNode == null) {
            LOGGER.warn("Image in " + find.getClass().getName() + " was not in upperBox.");
            System.exit(0);
        }

        System.out.println("foundNode name: " + foundNode.getClass().getName());
   
        GridPane gp = (GridPane) foundNode;
        
        LOGGER.debug("getting bounds");
        Bounds bounds = gp.getBoundsInLocal();
        Bounds screenBounds = gp.localToScreen(bounds);
    
        LOGGER.debug("clicking on a node");

        // click on the button location based on gridpane
        robot.clickOn(screenBounds.getMinX() + 150, screenBounds.getMinY() + 120);
        //robot.clickOn(xy);
        sleep(200);
    }

    void createNewCardsHelper() {

        CreateFlash createFlash = CreateFlash.getInstance();

        int x = (int) createFlash.getComboBoxXY().getX();
        int y = (int) createFlash.getComboBoxXY().getY();

        // Select the test type
        robot.clickOn(x,y);
        // click on multiple-choice
        robot.clickOn(x, createFlash.getComboBoxXY().getY() + 55);

        sleep(50);

        for(int i = 1; i < 5; i++)
        {
            System.out.println("in loop");
            // Click question area
            robot.clickOn(createFlash.getEditor_U_ForTestingOnly().getTextAreaXY());
            write("Question" + i);
            // click answer area
            robot.clickOn(createFlash.getEditor_L_ForTestingOnly().getTextAreaXY());
            write("answer" + i);
            // click next card button
            robot.clickOn(createFlash.getNewCardBtnXY());
            sleep(100);
            //push(enterCard);
        }

        // save deck
        sleep(100);
        robot.clickOn(x + 75, y + 185);
        sleep(300);

        saveFile();
    }



    int i = 0;
    Point2D xy;
    String qFileStr;
    String ansFileStr;
    File qFile;
    File aFile;
    /**
     * Creates the number of cards specified in the parameter/argument. Takes seperate snapshots
     * of Text Area and Image area, and stores them to the array.
     * Order is 0-qText, 1-qImage, 2-aText, 3-aImg
     * @param numCards The number of cards, recommend keeping it small but must be at least 4
     * @param shapesDblAry ..
     * @return Returns the array containing the snapshots of text and images.
     */
    Image[][] createNewCardsAndShapesHelper(int numCards, Shape[][] shapesDblAry, Shape[][] revShapesDblAry) {

        //int x = delta_X + 45;
        //int y = delta_Y + 315;

        CreateFlash createFlash = CreateFlash.getInstance();
        DrawTools dt = DrawTools.getInstance();

        int x = (int) createFlash.getComboBoxXY().getX();
        int y = (int) createFlash.getComboBoxXY().getY();

        // Save upper image in 0 and 1 and lower image in 2 and 3
        Image[][] images = new Image[numCards][4];

        // Select the test type
        robot.clickOn(x,y);
        // click on multiple-choice
        robot.clickOn(x, createFlash.getComboBoxXY().getY() + 55);
        sleep(50);
        // minimize the size of the image, must be the same as
        // createMediaTester
        SnapshotParameters snapParams = CreateMediaTester.snapParams;
        snapParams.setViewport(new Rectangle2D(+10,0, 100, 35));

        DirectoryMgr dirMgr = new DirectoryMgr();
        String mediaPath = dirMgr.getMediaPath('C');


        FileOpsUtil.folderExists(new File(mediaPath));


        for(i = 0 ; i < numCards; i++)
        {

            System.out.println("in loop");
            // Click question area

            StringBuilder strBuilder = new StringBuilder();
            for(int j = 0; j < i + 1; j ++) {
                strBuilder.append(" \u2b1b");
            }
            String qStr = "-q " + i + strBuilder + " --------------";
            robot.clickOn(createFlash.getEditor_U_ForTestingOnly().getTextAreaXY());
            write(qStr);


            // For now save them to file and use them in a minute.
            qFileStr = mediaPath + "createTEST_Q_img_" + i + ".png";
            ansFileStr = mediaPath + "createTEST_A_img_" + i + ".png";
            qFile = new File(qFileStr);
            aFile = new File(ansFileStr);


            // take a node snapshot of the questionTextArea
            Platform.runLater(() -> {

                    images[i][0] = CreateFlash.getInstance().getEditor_U_ForTestingOnly().tCell.getTextArea().snapshot(snapParams, null);
                    try {
                        System.out.println("\n\n\n ********* saved the " + qFile.getName() + " image for " + i + " ********** \n\n\n");
                        ImageIO.write(SwingFXUtils.fromFXImage(images[i][0], null), "png", qFile);
                    } catch (Exception e) {
                        System.out.println("File not created");
                        System.out.println(e.getStackTrace().toString());
                    }

            });
            // create FMsnapshot for upper editor and save the image to the array
            // save the image to the array
            images[i][1] = takeSnapShotWShapes((i + 1) * 40, (i + 1) * 40, CreateFlash.getInstance().getEditor_U_ForTestingOnly(), DrawTools.getInstance(), shapesDblAry[i]);


       //     xy = dt.getSaveBtnXY();
       //     robot.moveTo(xy);
       //     robot.clickOn(xy);
       //     xy = dt.getExitBtnXY();
        //    robot.moveTo(xy);
       //     robot.clickOn(xy);
       //     sleep(500);

            // click answer area
            robot.clickOn(createFlash.getEditor_L_ForTestingOnly().getTextAreaXY());
            String aStr = "-a " + i + strBuilder + " --------------";
            write(aStr);

            Platform.runLater(() -> {
                    // take a snapshot of the questionTextArea
                    //CreateFlashPane.getInstance().getEditorL().tCell.getTextArea().setCursor(Cursor.NONE);
                    images[i][2] = CreateFlash.getInstance().getEditor_L_ForTestingOnly().tCell.getTextArea().snapshot(snapParams, null);

                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(images[i][2], null), "png", aFile);
                    } catch (Exception e) {
                        System.out.println("File not created");
                        System.out.println(e.getStackTrace().toString());
                    }
            });

            // create FMsnapshot for lower editor and
            // save the image to the array
            System.out.println("rev idx: " + (shapesDblAry.length - i - 1));


            images[i][3] = takeSnapShotWShapes((i + 1) * 60, (i + 1) * 60, CreateFlash.getInstance().getEditor_L_ForTestingOnly(), DrawTools.getInstance(), revShapesDblAry[i]);


        //    xy = dt.getSaveBtnXY();
        //    robot.clickOn(xy);
        //    xy = dt.getExitBtnXY();
        //    robot.moveTo(xy);
        //    robot.clickOn(xy);
        //    sleep(200);

            // click next card button
            CreateFlash cfp = CreateFlash.getInstance();
            xy = cfp.getNewCardBtnXY();
            robot.clickOn(xy);
            sleep(200);
            //push(enterCard);
        }


        return images;
    }


    void saveFile() {

        //FlashCardOps fo = new FlashCardOps();
        robot.clickOn(CreateFlash.getInstance().getSaveDeckBtnXY());
        //fo.saveFlashList();
    }


    Image takeSnapShotWShapes(double difX, double difY, SectionEditor editorUorL, DrawTools dt, Shape[] shapeAry) {

        //  *** Create snapshot ***

        int diff_x = (int) difX + 50;
        int diff_y = (int) difY + 50;

        // get snapshotbutton minxy
        Point2D xy = editorUorL.getSnapShotBtnXY();
        robot.clickOn(xy);
        sleep(100);

        // Create the snapshot rectangle
        robot.moveTo(diff_x,diff_y);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(400 + diff_x, 300 + diff_y);
        sleep(200);
        robot.release(MouseButton.PRIMARY);
        sleep(50);

        // create rectangle
        int minX = (int) ((Rectangle) shapeAry[0]).getX() + diff_x;
        int minY = (int) ((Rectangle) shapeAry[0]).getY() + diff_y;
        int wX = (int) ((Rectangle) shapeAry[0]).getWidth() + minX;
        int hY = (int) ((Rectangle) shapeAry[0]).getHeight() + minY;

        // click on the rect button
        xy = dt.getRectBtnXY();
        robot.clickOn(xy);
        sleep(100);

        // create the rectangle
        robot.moveTo(minX ,minY );
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(wX , hY );
        robot.release(MouseButton.PRIMARY);
        sleep(200);

        // create 2nd shape, an ellipse/circle
        int centerX = (int) ((Ellipse) shapeAry[1]).getCenterX() + diff_x;
        int centerY = (int) ((Ellipse) shapeAry[1]).getCenterY() + diff_y;
        int radX = (int) ((Ellipse) shapeAry[1]).getRadiusX() + centerX;
        int radY = (int) ((Ellipse) shapeAry[1]).getRadiusY() + centerY;

        xy = dt.getCircleBtnXY();
        robot.clickOn(xy);
        sleep(100);

        // Create ellipse on overlay pane,
        robot.moveTo(centerX ,centerY );
        sleep(100);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(radX , radY );
        sleep(100);
        robot.release(MouseButton.PRIMARY);
        sleep(100);

        return editorUorL.getImage();
    }


    /**
     * May fail if color of first pixel is not white
     * @param x
     * @param y
     * @param msg
     * @throws Exception
     */
    void checkPixColor(int x, int y, String msg) throws Exception{
        Robot awtRobot = new Robot();
        Color awtPixColor = awtRobot.getPixelColor( x,  y);
        Color checkAwtColor = new Color(251, 27, 255);

        assertEquals(msg, checkAwtColor, awtPixColor);
    }



    public boolean removePrevFile() throws IOException
    {
        File dir = new File(DirectoryMgr.getWorkingDirectory() + "/FlashMonkeyData/906b1830bb8c5bcbe8a90541d5a8c30d/decks" );
        File file = new File(dir + "/906b1830bb8c5bcbe8a90541d5a8c30d_906b1830bb8c5bcbe8a90541d5a8c30d$TestingDeck.dec");
        LOGGER.debug("deleting directory: {}", dir);
        if(file.exists())  {
            LOGGER.debug("directory exists? {} ... deleting", true );
            file.delete();
            dir.delete();
            return true;
        }
        return false;
    }
}
