package fileops;

import fileops.utility.Utility;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.image.BufferedImage;
import java.io.File;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertTrue;
import authcrypt.UserData;
import core.RobotUtility;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;


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
@Tag("mediaTransferTester")
public class MediaTransferTester extends ApplicationTest {

    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MediaTransferTester.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaTransferTester.class);

    private static final String TEST_EMAIL = "flash@flashmonkey.xyz";
    private static final String TEST_DECK = "History 2054.dec";
    //private static final CloudOps CO = new CloudOps();
    private static Image localImage;
    private String token;

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


        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(FlashMonkeyMain.class);
        //LOGGER.setLevel(Level.DEBUG);
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
    }


/*    @BeforeAll
    public void setup() throws Exception {

        // logs the test in
        bobTheBot.robotSetup();

        //String folder = DirectoryMgr.getMediaPath('t');
        //String fileName = "testFile.JPG";
        //String fileName = ReadFlash.getInstance().getDeckName() + ".dec";
        //String strPath = folder + fileName;


        //String strPath = "../flashMonkeyFile/TestingDeck.dec";
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

 */
    private void setup() {
        S3ListObjs s3ListObjs = new S3ListObjs();
        //ReadFlash.getInstance().setDeckName(TEST_DECK);
        LOGGER.debug("TEST_DECK folder: {}", TEST_DECK);
        FlashCardOps.getInstance().setDeckFileName(TEST_DECK);
        int res = 0;
            s3ListObjs = new S3ListObjs();
            s3ListObjs.listDecks(TEST_EMAIL, "bangBang#01");

            token = s3ListObjs.getToken();
    }

    private boolean isImageEqual(Image firstImage, Image secondImage){
        // Prevent `NullPointerException`
        if(firstImage != null && secondImage == null) return false;
        if(firstImage == null) return secondImage == null;

        // Compare images size
        if(firstImage.getWidth() != secondImage.getWidth()) return false;
        if(firstImage.getHeight() != secondImage.getHeight()) return false;

        // Compare images color
        for(int x = 0; x < firstImage.getWidth(); x++){
            for(int y = 0; y < firstImage.getHeight(); y++){
                int firstArgb = firstImage.getPixelReader().getArgb(x, y);
                int secondArgb = secondImage.getPixelReader().getArgb(x, y);

                if(firstArgb != secondArgb) return false;
            }
        }

        return true;
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
        S3GetObjs go = new S3GetObjs();
        CloudOps.putDeck(FlashCardOps.getInstance().getDeckFileName());
    }

    @Test
    public void testDeckDownloads() {

    }

    @Test
    public void testMediaUploads() {

    }





    @Test
    @Order(2)
    public void checkImageDownloadFmS3() throws Exception{
        // The Martian
        final String imageName = "i09090F0A0Ai_flash@flashmonkeyxyz.png";
        FxRobot bobTheBot = new FxRobot();
        JFXPanel jfxPanel = new JFXPanel();
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(FlashMonkeyMain.class);
        setup();

        UserData.setUserName(TEST_EMAIL);
        FlashCardOps.getInstance().setDeckFileName(TEST_DECK);

        String toDiskDir = DirectoryMgr.getMediaPath( 'M');
        File diskFile = new File(toDiskDir + "/" + imageName);
        LOGGER.debug("toDiskFile: {}", diskFile);
        if(diskFile.exists()) {
            LOGGER.debug("Image exists, deleting.");
            diskFile.delete();
    }
        // retrieve the image from s3
        S3GetObjs getObjs = new S3GetObjs();
        ArrayList<MediaSyncObj> cloudLinks = new ArrayList<>(1);
        cloudLinks.add(new MediaSyncObj(imageName, 'm', 4));

        getObjs.serialGetMedia(cloudLinks, token);
        localImage = new Image("File: images/Martian.png");

        // test that the image received is correct
        Image s3Img = new Image("File: " + diskFile.getPath());
        boolean bool = isImageEqual(s3Img, localImage) && diskFile.exists();

        assertTrue("Images do not match", bool);
    }

    @Test
    @Order(1)
    public void checkImageUploadtos3() throws Exception {
        // USING REFLECTION TO TEST PRIVATE METHOD
        //Method method = SectionEditor.class.getDeclaredMethod("saveImage", Image.class, String.class, String.class);
        //method.setAccessible(true);

        FxRobot bobTheBot = new FxRobot();
        JFXPanel jfxPanel = new JFXPanel();
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(FlashMonkeyMain.class);
        setup();
        UserData.setUserName(TEST_EMAIL);
        FlashCardOps.getInstance().setFileName(TEST_DECK);
        //CloudOps clops = new CloudOps();
        String filePathName = "/image/Martian.png";
        File f = new File(filePathName);

        Image img = new Image(getClass().getResourceAsStream(filePathName));
        BufferedImage inputImage = SwingFXUtils.fromFXImage(img, null);
        FileNaming fileNaming = new FileNaming( FileNaming.getImageHash(inputImage), 'i', ".png");
        boolean bool = FlashCardOps.getInstance().saveImage(fileNaming.getMediaFileName(), img, "png", 'c');

        //method.invoke(img, "png", fileName.getFileName());

        if(Utility.isConnected()) {
            String[] strs = new String[1];
            strs[0] = fileNaming.getMediaFileName();
            S3PutObjs putObjs = new S3PutObjs();
            ArrayList<String> l = new ArrayList<>(Arrays.asList(strs));
            putObjs.serialPutMedia(l, token);
        }
    }

    @Test
    @Order(3)
    public void checkShapesUploadToS3() throws TimeoutException {
        // USING REFLECTION TO TEST PRIVATE METHOD
        //Method method = SectionEditor.class.getDeclaredMethod("saveImage", Image.class, String.class, String.class);
        //method.setAccessible(true);

        FxRobot bobTheBot = new FxRobot();
        JFXPanel jfxPanel = new JFXPanel();
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(FlashMonkeyMain.class);
        setup();
        UserData.setUserName(TEST_EMAIL);
        FlashCardOps.getInstance().setFileName(TEST_DECK);


    }
}
