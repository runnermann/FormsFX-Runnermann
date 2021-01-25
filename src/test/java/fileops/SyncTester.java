package fileops;

import authcrypt.UserData;
import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonProcessingException;
import core.RobotUtility;
import flashmonkey.FlashCardMM;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import forms.utility.Alphabet;
import io.reactivex.Observable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
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
import security_and_login.LoginTesterUtility;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * <pre>
 *     Ensure Vertx is running. Access to S3 testing

 *      File Synchronization occurs in several phases depending on the files to be synchronized.
 *      1) the start of the program: After login, a request is sent to VertX for a list of decks for the
 *      user that has corrrectly signed in. This list of decks is synchronized between the cloud and local.
 *      In this case, synchronization is based on if the deck exists, and if in both places, which one has been accessed most recently.
 *      only means that AgrFile list is detecting if the deck is local, or remote.
 *      2) After a deck has been selected, the media is synchronized between local, and remote.
 *      3) After a deck has been edited, the media is again synchronized to update files in the cloud
 *      4) After a user has finished a study session, the deck is sent to the cloud.
 *
 *      Prior to running the test.
 *          1) Ensure that TestDeck.dat exists locally. Robot will/should sync the deck
 *      between local and remote.
 *          2) Ensure that TestDeck media exists locally. Media will/should be syncronized
 *          between local and remote.
 * </pre>
 *
 * @throws Exception
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SyncTester extends ApplicationTest {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SyncTester.class);

    private static final String TEST_DECK = "History 2054";
    CloudOps clops = new CloudOps();
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

        String folder = DirectoryMgr.getMediaPath('t');
        String fileName = "abcdTestDeck.dat";
        String strPath = folder + fileName;


        //String strPath = "../flashMonkeyFile/TestingDeck.dat";
        File file = new File(strPath);
        //Path filePath = Paths.get(strPath);
        assertTrue("Deck located at: " + strPath + " Does not exist. ", file.exists());

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

    @AfterAll
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
    }


    /**
     * Test that upload to users S3 has the correct number of media files
     *
     * @throws Exception
     */

    @Test
    @Order(0)
    public void mediaSyncUploads() throws Exception {
        setup();
        MediaSync ms = new MediaSync();

        System.out.println("\n\n ********** ROBOT SETUP COMPLETE ********** \n\n");
        // set idk@idk.com S3 to contain some media but not all
        // a. get remote list of files
        System.out.println("SyncTest Test0: mediaSyncUploads+");

        ArrayList<CloudLink> cloudLinks = FlashCardOps.getInstance().CO.getS3MediaList(TEST_DECK);
        //List<String> removedList = Collections.synchronizedList(new ArrayList<>());

        System.out.println("cloudLinks size: <{}>" + cloudLinks.size());
        // b. Create list of delete files
        StringBuilder remove = new StringBuilder();
        try {
            int i = 0;
            for (CloudLink link : cloudLinks) {
                System.out.print(i + ", ");
                if (i % 3 == 0) {
                    remove.append(link.getKey() + ",");
                    //removedList.add(link.getName());
                }
                i++;
            }
            // delete media from s3
            deleteMedia(remove.toString(), ms);

        } catch (Exception e) {
            System.out.println("mediaSyncUploads test failed: {}" + e.getMessage());
            e.printStackTrace();
        }
        // sync media
        FlashCardOps.getInstance().setMediaIsSynced(false);
        System.out.println("\n\n ****** MediaSync is being called from SyncTester test 0 ***** \n\n");

        ms =  ms.syncMediaTester(FlashCardOps.getInstance().getFlashList(), ms);

        List<String> uploadList = new ArrayList();
        for(MediaSyncObj o : ms.getUpload()) {
            int num = o.getFileName().lastIndexOf("_");
            uploadList.add(o.getFileName().substring(num + 1).trim());
        }

        // test that all of the correct media names are on upload list, and none extra
        String[] removed = remove.toString().split(",");

        System.out.println("removed length: {}" + removed.length);
        System.out.println("ms.uploads size: {}" + ms.getUpload().size());

   //     assertTrue("ms is null", ms.getUpload() != null);
   //     assertTrue("removed is null", removed != null);
   //     assertTrue(ms.getUpload().size() == removed.length);
        String[] tmp = remove.toString().split(",");
        List<String> removeList = new ArrayList<>();
        for(String s : tmp) {
            int num = s.lastIndexOf("_");
            removeList.add(s.substring(num + 1).trim());
        }

        boolean bool = false;

        ArrayList<String> uploadExtras = new ArrayList<>();


        for(String s : uploadList) {
            if (! removeList.contains(s)) {
                uploadExtras.add(s);
            }
        }
        removeList.removeAll(uploadList);


        Collections.sort(uploadExtras);
        Collections.sort(removeList);
        System.out.println("Remaining removedList");
        for(String s : removeList) {
            System.out.println(s);
        }
        System.out.println("Remaining uploadList");
        for(String s : uploadExtras) {
            System.out.println(s);
        }

        assertTrue("upload does not contain the removed elements", bool);





        // upload

        // confirm the correct media is present in S3


    }

    /**
     * Test that download to client has the correct number of media files
     */
    @Test
    @Order(1)
    public void mediaSyncDownloads() throws Exception {
        // setup();
        // remove some media from local files

        // test that all of the correct media names are in local directory

    }


    /**
     * Test that delete from S3 has the correct number of media files
     */
    public void mediaSyncMarkForDelete() throws Exception {

    }


    /**
     * Test that cache to users local system has the correct number of media files.
     */
    public void mediaSyncCache() throws Exception {

    }



    /**
     * Deletes the media provided in the arguements
     * @param keys
     */
    private void deleteMedia(String keys, MediaSync ms) {

        ms.clearMediaStateValues();
        LOGGER.debug("deleteMedia called, request to delete <{}>", keys);

        String json = "[{\"keys\":\"" + keys + "\"}]";

        String destination = "media-s3-delete";

        final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // vertx expects router.post("/resource-s3-list")
        // we post when using passwords:
        final HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/" + destination))
                //@TODO set S3Creds to HTTPS
                //.uri(URI.create("https://localhost:8080/resource-s3-list"))
                .header("Content-Type", "application/json")
                .build();
        try {
            final HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("response code {}", response.statusCode());

            if (response.statusCode() == 200 & response.body().length() > 5) {
                LOGGER.debug("response is 200 delete succeeded");
            }

            //@TODO wait and retry if failed
        } catch (
                ConnectException e) {
            // failed, send user a notification
            LOGGER.warn("ConnectException: <{}> is unable to connect: Server not found", Alphabet.encrypt(UserData.getUserName()));
            //return -1;
        } catch (
                JsonProcessingException e) {
            LOGGER.warn("ERROR: while parsing JSON to java message: {}", e.getMessage());
            e.printStackTrace();
        } catch (
                IOException e) {
            LOGGER.warn("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            LOGGER.debug(e.getMessage());
            e.printStackTrace();
        }
        LOGGER.debug("deleteMedia completed");
        //return 0;
    }





}
