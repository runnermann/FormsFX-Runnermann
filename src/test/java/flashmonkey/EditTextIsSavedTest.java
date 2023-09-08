package flashmonkey;

import core.ImageUtility;
import fileops.DirectoryMgr;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.robot.impl.SleepRobotImpl;
import type.celltypes.CanvasCell;
import type.testtypes.QandA;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("editedShapes")
public class EditTextIsSavedTest extends ApplicationTest {

    String editedQMsg   = "Ques cxd. idx: ";
    String editedAnsMsg = "Ans edxd. idx: ";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EditTextIsSavedTest.class);
    //private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
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



    @Override
    public void start(Stage backgroundStage) {

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

        final Stage window = FlashMonkeyMain.getPrimaryWindow();
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



    int i;
    CreateMediaTester cmt = new CreateMediaTester();
    /**
     * Edits text in existing cards, saves the edits,
     * then checks that edits are kept in:
     * {@code
     * <ul>
     *     <li>editedCard</li>
     *     <li>readPane</li>
     *     <li>are not shared with cards that should not be changed</li>
     * </ul>
     * }
     * @throws Exception
     */
    @Test
    void editedTextIsSaved() throws Exception {

        setUpFileExists();
        sleep(500);
        //create.createDeckHelper();
        sleep(500);

        Image[][] textImages = new Image[4][2];

        //final DirectoryMgr dirMgr = new DirectoryMgr();
        final String mediaPath = DirectoryMgr.getMediaPath('C');
        final ReadFlash rf = ReadFlash.getInstance();
        final CreateFlash cfp = CreateFlash.getInstance();
        snapParams.setViewport(new Rectangle2D(+10,0, 100, 35));
        Point2D xy = new Point2D(999, 999);

        // Go to CreateFlashPane
        // click on createFlash button
        xy = rf.getCreateButtonXY();
        robot.clickOn(xy);
        sleep(200);
        // When editing, we start at the new card
        // located at the end of the deck.
        for(i = 3; i >= 0; i--) {
            // Set xy to prevBtn
            xy = cfp.getPrevBtnXY();
            robot.clickOn(xy);
            sleep(200);
            // set xy to delete question text to clear the field
            xy = cfp.getEditor_L_ForTestingOnly().getClearTextBtnXY();
            robot.clickOn(xy);
            // check text area is clear
            assertTrue("Question area did not clear on textDel button click", cfp.getEditor_U_ForTestingOnly().getText().equals(""));
            // edit text in upper text area
            xy = cfp.getEditor_U_ForTestingOnly().getTextAreaXY();
            robot.clickOn(xy);
            write(editedQMsg + i + ".........");
            // take snapshot of text

            // For now save them to file and use them in a minute.
            String qFileStr = mediaPath + "editedTEST_Q_img_" + i + ".png";

            System.out.println("qFileStr: " + qFileStr);

            String ansFileStr = mediaPath + "editedTEST_A_img_" + i + ".png";
            File qFile = new File(qFileStr);
            File aFile = new File(ansFileStr);

            Platform.runLater(() -> {
                textImages[i][0] = CreateFlash.getInstance().getEditor_U_ForTestingOnly().tCell.getTextArea().snapshot(snapParams, null);

                try {

                    ImageIO.write(SwingFXUtils.fromFXImage(textImages[i][0], null), "png", qFile);
                    System.out.println("\n\n\n ********* saving the " + qFile.getName() + " image for " + i + " ********** \n\n\n");
                } catch (Exception e) {
                    System.out.println("!!! ERROR: File not created !!!");
                    System.out.println(e.getStackTrace().toString());
                }
            });


            // set xy to delete answer text to clear the field
            xy = cfp.getEditor_L_ForTestingOnly().getClearTextBtnXY();
            robot.clickOn(xy);
            // check text area is clear
            assertTrue("Question area did not clear on textDel button click", cfp.getEditor_L_ForTestingOnly().getText().equals(""));

            // edit text in lower text area
            xy = cfp.getEditor_L_ForTestingOnly().getTextAreaXY();
            robot.clickOn(xy);
            write(editedAnsMsg + i + ".........");

            Platform.runLater(() -> {
                textImages[i][1] = CreateFlash.getInstance().getEditor_L_ForTestingOnly().tCell.getTextArea().snapshot(snapParams, null);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(textImages[i][1], null), "png", aFile);
                } catch (Exception e) {
                    System.out.println("File not created");
                    System.out.println(e.getStackTrace().toString());
                }
            });
            sleep(100);
        }
        // *** end for loop ***
        // Run back to the end and
        // check that text is correct in all cards within the editor
        xy = cfp.getNextBtnXY();
        for(int idx = 0; idx < 4; idx++) {

            // check text in upper text area
            assertEquals("\nText did not match in upper text area on card [" + idx + "]", cfp.getEditor_U_ForTestingOnly().getText(), editedQMsg + idx + ".........");
            // check text in lower text area
            assertEquals("Text did not match in lower text area on card [" + idx + "]", cfp.getEditor_L_ForTestingOnly().getText(), editedAnsMsg + idx + ".........");
            //set xy to next button
            if(idx < 3) {
                robot.clickOn(xy);
            }
            sleep(100);
        }

        // check text during read/test
        // We use images instead of checking from memory.
        // Thus we can be sure that we are testing the full
        // system for any errors.

        // Save deck changes
        // xy = cfp.getSaveDeckButtonXY();
        robot.clickOn(xy);
        sleep(500);

        // go to study
        xy = rf.getStudyButtonXY();
        robot.clickOn(xy);

        // go to text / qAnda
        xy = rf.getQnAButtonXY();

        robot.clickOn(xy);
        sleep(500);

        // should now see the q and a card
        // check text in text fields
        QandA.QandASession qa = QandA.QandASession.getInstance();
        final Point2D ansBtnXY = qa.getAnswerButtonXY();
        ReadFlash read = ReadFlash.getInstance();
        if (read == null) {
            System.out.println("read is null. Exiting ...");
            System.exit(0);
        }
        final Point2D nextQxy = read.getNextQButtonXY();
        System.out.println("nextQButton xy: " + nextQxy);

        for(i = 0; i < 4; i++) {
            // show the answer
            robot.moveTo(ansBtnXY);
            robot.clickOn(ansBtnXY);

            sleep(500);
            Platform.runLater(() -> {

                // compare using images?
                // take snapshot of Question Text
                Node qNode = qa.getUpperHBox();
                Node aNode = qa.getLowerHBox();
                Image qImage = qNode.snapshot(snapParams, null);
                Image ansImage = aNode.snapshot(snapParams, null);

                Stage stage = new Stage();
                HBox hBox1 = new HBox(2);
                hBox1.getChildren().addAll(new ImageView(textImages[i][0]), new ImageView(qImage));

                HBox hBox2 = new HBox(2);
                hBox2.getChildren().addAll(new ImageView(textImages[i][1]), new ImageView(ansImage));

                VBox vBox = new VBox(2);
                vBox.getChildren().addAll(hBox1, hBox2);
                Scene scene = new Scene(vBox);
                stage.setScene(scene);
                stage.show();


                // compare text images
                ImageUtility iu = new ImageUtility();
                bool = iu.txtImgsLookTheSame(textImages[i][0], qImage);

                assertTrue("Question text does not look the same in readPane on [" + i + "]", bool);
                LOGGER.info("bool for q: " + bool);

                bool = iu.txtImgsLookTheSame(textImages[i][1], ansImage);

                LOGGER.info("bool for a: " + bool);
                assertTrue("Answer text does not look the same in readPane on [" + i + "]", bool);

                //sleep(10000); Sleep in the loop causes issues with Pane.Snapshot.
            });
            sleep(10000);
            // go to next card
            if(robot == null) {
                System.out.println("robot is null at 1337");
            }
            robot.moveTo(nextQxy);
            robot.clickOn(nextQxy);
            sleep(500);
        }
    }
}
