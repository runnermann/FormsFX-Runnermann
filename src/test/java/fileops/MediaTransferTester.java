package fileops;

import ch.qos.logback.classic.Level;
import core.RobotUtility;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Prior to using this test. Ensure that there are the correct media files in both S3 and on the local drive.
 * <pre>
 *     1) Check synchronize media for correct outcomes
 *
 *     2) Test Link Ambiguation
 *
 *          a) Test request media links / signedURL
 *
 *          b) Test request DeckList
 *
 *          c) Test request MediaList
 *
 *          d) Test getDeck Link / signedURL
 *
 *     2) Test MediaDownloads
 *
 *     3) Test MediaUploads
 *
 *     4) Test DeckUploads
 *
 *     5) Test DeckDownloads
 * </pre>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Tag("popup")
public class MediaTransferTester extends ApplicationTest {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MediaTransferTester.class);

    CloudOps clops = new CloudOps();

    private static final String TEST_DECK = "History 2054";

    // The image from the snapshot in original form.
    BufferedImage buIm;
    Point2D paneXY;
    boolean bool;// = false;

    private static int delta_X;
    private static int delta_Y;

    RobotUtility bobTheBot = new RobotUtility();
    FxRobot bob;// = new FxRobot();

    // for the background stage
    private java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
    private java.awt.Dimension d = tk.getScreenSize();
    private int screenWt = d.width;
    private int screenHt = d.height;

    /* This operation comes from ApplicationTest and loads the GUI to test. */
    //@Override
    @Override
    public void start(Stage stage) throws Exception {

        LOGGER.setLevel(Level.DEBUG);
        // At the beggining of the test session, remove the file
        // if it exists

        // create a background stage the size of the window
        // in case the robot is not on top of the
        // intended stage.
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: TRANSPARENT");

        Scene backScene = new Scene(pane, screenWt, screenHt, Color.TRANSPARENT);
        stage = new Stage(StageStyle.TRANSPARENT);

        stage.setScene(backScene);
        stage.show();
		/* Do not forget to put the GUI in front of windows.
		 Otherwise, the robots may interact with another
        window, the one in front of all the windows... */
        stage.toFront();

        // logs the test in
        bobTheBot.robotSetup();


        //System.out.println("\n TestingDeck.dat removed from file was: " + bool);
    }


    @BeforeAll
    public void setup() throws Exception {

        // logs the test in
        bobTheBot.robotSetup();

        //String folder = DirectoryMgr.getMediaPath('t');
        //String fileName = "testFile.JPG";
        //String fileName = ReadFlash.getInstance().getDeckName() + ".dat";
        //String strPath = folder + fileName;


        //String strPath = "../flashMonkeyFile/TestingDeck.dat";
        //File file = new File(strPath);
        //Path filePath = Paths.get(strPath);
        //assertTrue("Deck located at: " + strPath + " Does not exist. ", file.exists());

        bool = false;

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(FlashMonkeyMain.class);


        final Stage window = FlashMonkeyMain.getWindow();
        delta_X = (int) window.getX();// getScegetY();
        delta_Y = (int) window.getY() - 30;

        bob = new FxRobot();

        // Log-in
        //LoginTesterUtility login = new LoginTesterUtility(delta_X, delta_Y);
        //login.logIn("idk@idk.com", "bangBang#01", bob);
    }



    @Test
    public void testRequestMediLinks() {

    }

    @Test
    public void testRequestDeckList() {

    }

    @Test
    public void testRequestMediList() {

    }

    @Test
    public void testRequestDeckLink() {

    }

    @Test
    public void testMediaDownloads() {

    }

    @Test
    public void testDeckUploads() throws Exception {
        setup();

        //S3PutObjs putObj = new S3PutObjs();
        CloudOps CO = FlashCardOps.getInstance().CO;
        CO.putDeck(ReadFlash.getInstance().getDeckName() + ".dat");
        //CO.putDeck("testFile.JPG");




    }

    @Test
    public void testDeckDownloads() {

    }

    @Test
    public void testMediaUploads() {

    }

/*
    @Test
    @Order(2)
    public void checkImageDownloadtoS3_thumbs() {
        //clops.setBucketName("iooily.flashmonkey.deck-thumbs");

        ArrayList<String> names = new ArrayList<>(4);
        names.add("a823f34bac.png");
        names.add("img1.png");
        names.add("img2.png");
        names.add("img3.png");
        ArrayList<Image> imgs = clops.getMedia(names);

        assertTrue(imgs.get(0) != null);
        System.out.println("imgs size = " + imgs.size());
        assertTrue(imgs.size() == 4);
    }

    @Test
    @Order(1)
    public void checkImageUploadtos3_thumbs() {
        CloudOps clops = new CloudOps();
        //clops.setBucketName("iooily.flashmonkey.deck-thumbs");
        ArrayList<String> imgNames = new ArrayList<>(5);
        // imgNames.add("history2054.png");
        String name = "a823f34bac.png";

        clops.connectCloudOut('d',"", name);

        imgNames.add(name);
        ArrayList<Image> imgs = clops.getMediaFmS3(imgNames);
        assertTrue("if failed, comment out seperate thread in connectCloudOut()",(imgs.get(0)) != null);
    }

 */



}
