package flashmonkey;

import authcrypt.user.EncryptedAcct;
import core.CoreUtility;
import core.ImageUtility;
import core.RobotUtility;
import core.ShapeUtility;
import fileops.utility.Utility;
import javafx.geometry.Bounds;
import javafx.scene.control.ButtonType;
import javafx.scene.media.MediaPlayer;
import org.testfx.api.FxRobot;
import type.draw.shapes.FMRectangle;
import type.draw.shapes.GenericShape;
import fileops.DirectoryMgr;
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
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import type.celleditors.DrawTools;
import type.celleditors.SnapShot;
import type.celltypes.MediaPopUp;
import type.testtypes.QandA;
import type.tools.imagery.Fit;
import uicontrols.FMAlerts;
import uicontrols.FxNotify;
import uicontrols.UIColors;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
/**
 * This is not a robot using class. Just testing if methods at lower level, and higher
 * level behave as expected.
 *
 * 1. Test creation from no cards to one card.
 *  - start at  high level, CreateFlash.CardSaver.saveCard
 */
public class CreateFlashCloseTester extends ApplicationTest {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CreateFlashCloseTester.class);

    private String userName = "idk@idk.com";
    private String deckName = "906b1830bb8c5bcbe8a90541d5a8c30d_906b1830bb8c5bcbe8a90541d5a8c30d$TestingDeck.dec";
    private String fileName = "1e3f03f29759f53ad1a895dff88ed535_1e3f03f29759f53ad1a895dff88ed535$4_cards_shapes_images.dec";

    private Canvas originalCanvas;
    // The FXRobot
    RobotUtility robot = new RobotUtility();
    private CreateCards create;
    BufferedImage buIm;
    Point2D paneXY;

    boolean bool;
    private int delta_X = 0;
    private int delta_Y = 10;


    // TESTING if an incomplete card triggers a message


    // TESTING if a complete card is saved
    //      with shapes only
    //      with media only
    //      with text only


    // TESTING equals
    // CHECK media equals
    // CHECK shapes equals
    // CHECK text equals

    /////////////////////////////////////////////////////////////////////////////

    //                                  support methods                                                   //

    /////////////////////////////////////////////////////////////////////////////

    @AfterAll
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
        robot.release(MouseButton.PRIMARY);
        robot.release(MouseButton.SECONDARY);
    }

    /**
     * Use this setup if testing the deck creation from the beginning
     * is important for this test. Clears the existing deck if it already
     * exists.
     * @throws Exception
     */
    //@BeforeAll
    public void setup(String userName, String pw) throws Exception {

        // Allow to connect to the internet?
        Utility.allowConnection(false);

        // Log in
        robot.robotSetup(userName, pw);
        //LOGGER.info("file deleted: " + fileBool);
        // Sleep Moved up here for windows
        sleep(100);
        // used to confirm if images are the same.
        bool = false;

        final Stage window = FlashMonkeyMain.getWindow();
        delta_X = (int) window.getX();
        delta_Y = (int) window.getY();
        create = new CreateCards(delta_X, delta_Y);
    }



    public void setUpFileExists(String fileName ) throws Exception {
        LOGGER.debug("setupFileExists(...) called");
        this.fileName = fileName;
        setUpFileExists();;
    }

    /**
     * Use this setup if the card deck needed in a test
     * already exists and is tested.
     * @throws Exception
     */
    //@BeforeAll
    public void setUpFileExists() throws Exception {

        File dir = new File(DirectoryMgr.getWorkingDirectory() + "/FlashMonkeyData/1e3f03f29759f53ad1a895dff88ed535/decks" );
        File file = new File(dir + "/" + fileName);


        //Path filePath = Paths.get(strPath);
        assertTrue("Deck located at: " + file.getPath() + " Does not exist. ", file.exists());


        final Stage window = FlashMonkeyMain.getWindow();
        delta_X = (int) window.getX();// getScegetY();
        delta_Y = (int) window.getY();
        create = new CreateCards(delta_X, delta_Y);
        // get the deck created in previous test
        //create.getExistingDeck();
        robot.clickOn(delta_X + 250, delta_Y + 420);
        //sleep(6000);


        //cfp = CreateFlashPane.getInstance();
    }


    private void bumpMouse(Point2D xy) {
        Point2D bumpXY = new Point2D(xy.getX() + 48, xy.getY() + 48);
        robot.moveTo(bumpXY);
        sleep(30);
        robot.moveTo(xy);
    }

//    private GenericShape[] getShapes(String fileName) {
//        LOGGER.debug("\n\n ~^~^~^~^~^~^~^~ in CreateFlashCloseTEster.connectBinaryIn, fileName: " + fileName + " ~^~^~^~^~^~^~^~\n");
//
//        ArrayList<GenericShape> shapes = new ArrayList<>(3);
//
//        try(ObjectInputStream input = new ObjectInputStream(CreateFlashCloseTester.class.getResourceAsStream(fileName))) {
//
//            while(true)
//            {
//                shapes.add( (GenericShape) input.readObject());
//            }
//        }
//        catch(EOFException e) {
//            // do nothing, expected
//        }
//        catch(FileNotFoundException e) {
//            LOGGER.warn("\tFileOpsShapes File Not Found exception: connectBinaryIn() Line 106 ");
//            e.printStackTrace();
//        }
//        catch(IOException e) {
//            LOGGER.warn("\t****** IO Exception in FlashCard.connectBinaryIn() *******");
//            e.printStackTrace();
//        }
//        catch(Exception e) {
//            // Set errorMsg in FlashMonkeyMain and let the ReadFlash class
//            // handle the error in createReadScene()
//
//            LOGGER.warn("\tUnknown Exception in connectBinaryIn: ");
//            LOGGER.warn("\tFileName: " + fileName);
//            e.printStackTrace();
//
//        }
//
//        return shapes;
//    }


    /////////////////////////////////////////////////////////////////////////////

    //                                              TESTING                                                   //

    /////////////////////////////////////////////////////////////////////////////


    /**
     * Expects FlashCardData for this user to be empty.
     * Checks if the combo box creates a popup error message if
     * the user does not set the test type first.
     * @throws Exception
     */
    @Test
    @Order(1)
    void test_ComboBox_Ignored_Causes_Popup() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();

        boolean deleted = CoreUtility.deleteTestFile(deckName , userName);
        assertTrue("Test deck file was not deleted", deleted);

        setup("idk@idk.com","bangBang#01");
        sleep(100);
        CoreUtility cu = new CoreUtility();

        if(cfp.getMasterPane() != null) {
            cfp.getMasterPane().setOnKeyPressed(cu::escapeKeyActions);
        }
        create.writeDeckNameHelper();

        sleep(100);
        // The EntryCombobox should start in the open position.
        assertTrue(CreateFlash.getInstance().entryComboBoxIsShowing());
        // Attempt to write in one of the editors. Should cause a popup
        // notification.
        Point2D xyUType = cfp.getEditor_U_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyUType.getX() + 100, xyUType.getY());

        sleep(5000);
        assertTrue(FxNotify.notificationIsShowing());
    }

    // **** DATA TESTING: CREATE ERROR MESSAGE POPUP ****
    @Test
    @Order(2)
    void test_TEXT_In_Editor_U_Only_Causes_Popup_on_newCardButton() throws Exception {

        CreateFlash cfp = CreateFlash.getInstance();
        boolean deleted = CoreUtility.deleteTestFile(deckName , userName);
        assertTrue("Test deck file was not deleted", deleted);
        //cfp.createFlashScene();
        setup("idk@idk.com","bangBang#01");
        sleep(60);
        create.writeDeckNameHelper();
        sleep(60);
        // Set CARD TYPE to Multi-Choice. Multi-choice is the top
        // choice in the CardType drpdown button. The DropDown
        // button should start opened.

        // The EntryCombobox should start in the open position.
        // assertTrue(CreateFlash.getInstance().entryComboBoxIsShowing());
        //System.out.println("EntryComboBox is showing? : " + CreateFlash.getInstance().entryComboBoxIsShowing());
        // Select multiChoice card type
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(200);


        // Type on the screen
        Point2D xyUType = cfp.getEditor_U_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyUType);
        robot.write("Test for text in upper editor causes prevButton to create error.");
        sleep(100);

        Point2D xy = CreateFlash.getInstance().getNewCardBtnXY();
        robot.clickOn(xy);
        assertTrue("moving from an non-perfect card should cause an error message.", FMAlerts.notificationIsShowing());
        sleep(6000);

        // click on save to remove issue with error when
        // app is closed with alert open.
        xy = FMAlerts.getCancelBtnXY();
        //robot.moveTo(xy.getX() + 80, xy.getY() +80);
        sleep(30);
        robot.clickOn(xy.getX() + 80, xy.getY() + 80);

    }

    /**
     * Test that adding an incomplete card at the end of the deck, on error
     * message, if user selects to save, the deck adds a new card and the
     * new card is displayed in the section editors.
     * @throws Exception
     */
    @Test
    @Order(3)
    void test_add_card_and_non_perfect_card_at_end_of_deck() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();
        boolean deleted = CoreUtility.deleteTestFile(deckName , userName);
        assertTrue("Test deck file was not deleted", deleted);
        //cfp.createFlashScene();
        setup("idk@idk.com","bangBang#01");
        sleep(200);
        create.writeDeckNameHelper();
        // The EntryCombobox should start in the open position.
        // assertTrue(CreateFlash.getInstance().entryComboBoxIsShowing());
        //System.out.println("EntryComboBox is showing? : " + CreateFlash.getInstance().entryComboBoxIsShowing());
        // Select multiChoice card type
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(200);

        int length = cfp.getCreatorList().size();

        // Write in the upper text area and attempt to add a new card.
        // Should create a popup message.
        Point2D xyLType = cfp.getEditor_L_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyLType);
        sleep(100);

        robot.write(" - Text only in lower area. Should  cause a msg.");
        sleep(50);

        // find the add card button and click on it.
        Point2D xy = CreateFlash.getInstance().getNewCardBtnXY();
        robot.clickOn(xy);
        sleep(50);

        // check that the error message is displayed then click on the
        // save button.
        assertTrue("moving from an non-perfect card should cause an error message.", FMAlerts.notificationIsShowing());

        // click on save
        xy = FMAlerts.getCancelBtnXY();
        robot.moveTo(xy.getX() + 80, xy.getY() +80);
        sleep(3000);
        robot.clickOn(xy.getX() + 80, xy.getY() + 80);

        // check deck length is + 1
        assertTrue("Adding a non-perfect card to the deck", length + 1 == cfp.getCreatorList().size());

        // add a perfect card
        Point2D xyUType = cfp.getEditor_U_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyUType);
        sleep(100);

        robot.write(" - Text in upper area.");
        sleep(50);

        // write in  lower area
        robot.clickOn(xyLType);
        sleep(100);
        robot.write(" - Text in lower area.");
        sleep(50);

        // find the add card button and click on it.
        xy = CreateFlash.getInstance().getNewCardBtnXY();
        robot.clickOn(xy);
        sleep(50);

        assertTrue("Adding a perfect card to the deck", length + 2 == cfp.getCreatorList().size());

    }


    ////////   CHECK TREE   ////////

    @Test
    @Order(4)
    void test_edit_save_deleteFirstCard_onTreeClick_error_dialoge() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();
      //  ReadFlash rf = ReadFlash.getInstance();
        setup("testeroffline@flashmonkey.xyz", "bangBang#01");
        sleep(200);
        // uses a pre created deck.
        setUpFileExists();
        sleep(60);

        Point2D xy = ReadFlash.getInstance().getCreateButtonXY();
        robot.clickOn(xy);

        // Click on 1st element:: From last to 1
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(1);
        robot.clickOn(xy);
        sleep(60);
        // should get no change and no message
        assertTrue("navigating from a card with no data should not cause an error message", FxNotify.builderIsNull());
        sleep(50);

        // make a change to the current card and
        // test that no message popup is showing.
        // Type on the screen
        // Click on 1st element
        Point2D xyUType = cfp.getEditor_L_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyUType);
        sleep(100);

        robot.write(" - Should not cause a msg.");
        sleep(50);

        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(0);
        robot.moveTo(xy);
        robot.clickOn(xy);
        sleep(60);

        assertTrue("editing and moving from a perfect card should NOT cause an error message.", FxNotify.builderIsNull());
        sleep(50);

        // COMPLETE

        // clear text and move to another card
        // should cause an error message.
        // save the card and should move to next
        // node/card
        LOGGER.debug("deleting text in lower editor, then clicking on another node in tree\n" +
            "should cause an error message popup.");
        xy = cfp.getLowerTextClearBtnXY(); //.getClearTextBtnXY();
        robot.clickOn(xy);
        sleep(60);
        // move to 3
        // should cause an error message
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(3);
        robot.clickOn(xy);
        sleep(100);

        assertTrue("moving from an non-perfect card should cause an error message.", FMAlerts.notificationIsShowing());

        // Action saves card and moves to the next one.
        xy = FMAlerts.getOKBtnXY();
        robot.moveTo(xy);
        robot.clickOn(xy);
        sleep(60);
        // should move to node three

        // on error message, cancel
        // edit and continue.
        // Click on zero, should be non-perfect.
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(0);
        robot.clickOn(xy);
        sleep(60);

        // should cause an error popup
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(4);
        robot.clickOn(xy);
        sleep(60);

        // click on cancel and edit check if we can edit.
        xy = FMAlerts.getXBtnXY();
        robot.clickOn(xy);
        sleep(60);

        assertTrue("Should be able to edit same card, if user cancels from Error dialogue.",  cfp.getEditor_U_ForTestingOnly().getText().equalsIgnoreCase("q1"));

        // Should be on the first element already.
        // On error message delete card
        int length = cfp.getCreatorList().size();
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(3);
        robot.clickOn(xy);
        sleep(100);

        // click on delete
        xy = FMAlerts.getOKBtnXY();
        robot.moveTo(xy.getX() + 80, xy.getY());
        sleep(30);
        robot.clickOn(xy.getX() + 80, xy.getY());
        sleep(30);

        // a confrim delete dialogue should be showing
        assertTrue("When delete is clicked, an \" delete dialogue\" should request confirmation to be deleted. ",  FMAlerts.notificationIsShowing());

        xy = FMAlerts.getOKBtnXY();
        robot.moveTo(xy.getX() + 200,  xy.getY() - 40);
        sleep(30);
        robot.clickOn(xy.getX() + 200,  xy.getY() - 20);

        // confirm node is deleted
        int changedLength = cfp.getCreatorList().size();
        sleep(30);
        assertTrue("When a card is deleted from the \" error dialogue\" it should be deleted",  changedLength < length);

        // get node 1 and check that it is not equal to q1
        FlashCardMM ccRet = (FlashCardMM) cfp.getCreatorList().get(0);
        assertTrue("Delete card failed. ", ! ccRet.getQText().equals("q1") );

        System.out.println("Returned first card upper text: " + ccRet.getQText());
        System.out.println("ENDING ...");

        sleep(60000);
    }

    @Test
    @Order(5)
    void test_delete_last_card_onTreeClick_from_dialoge() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();
        //  ReadFlash rf = ReadFlash.getInstance();
        setup("testeroffline@flashmonkey.xyz", "bangBang#01");
        sleep(200);
        // uses a pre created deck.
        setUpFileExists();
        sleep(60);
        Point2D xy = ReadFlash.getInstance().getCreateButtonXY();
        robot.clickOn(xy);
        sleep(30);

        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(200);

        // delete last element

        // first make card non-complete
        Point2D textArea_U_XY = cfp.getEditor_U_ForTestingOnly().getTextAreaXY();
        robot.clickOn(textArea_U_XY);
        sleep(60);
        robot.write("upper area only. then deleting on error dialogue");

        // click on another node.
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(3);
        robot.clickOn(xy);
        sleep(60);

        // click on delete
        xy = FMAlerts.getOKBtnXY();
       // robot.moveTo(xy.getX() + 80, xy.getY());
        sleep(30);
        robot.clickOn(xy.getX() + 80, xy.getY());
        sleep(300);

        // a confrim delete dialogue should be showing
        assertTrue("When delete is clicked, an \" delete dialogue\" should request confirmation to be deleted. ",  FMAlerts.notificationIsShowing());
        // click on new error message ok button
        xy = FMAlerts.getOKBtnXY();
        robot.clickOn(xy.getX() + 200,  xy.getY() );
        sleep(30);

        // confirm node is cleared
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(2);
        robot.clickOn(xy);
        sleep(60);
        String uText = cfp.getEditor_U_ForTestingOnly().getText();
        String LText = cfp.getEditor_L_ForTestingOnly().getText();
        sleep(60);
        // go back make sure data is cleared
        for( int i = 0; i < cfp.getCreatorList().size(); i++) {

            FlashCardMM ccRet = (FlashCardMM) cfp.getCreatorList().get(i);
            assertTrue("Delete card failed. ", ! ccRet.getQText().equals("q2"));
        }
        sleep(60);
        assertTrue("When clear data is clicked, the data should be cleared from the last card.", uText.isEmpty() && LText.isEmpty());
    }

    @Test
    @Order(6)
    void test__delete_from_center_on_dialoge() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();
        //  ReadFlash rf = ReadFlash.getInstance();
        setup("testeroffline@flashmonkey.xyz", "bangBang#01");
        sleep(200);
        // uses a pre created deck.
        setUpFileExists();
        sleep(60);
        Point2D xy = ReadFlash.getInstance().getCreateButtonXY();
        robot.clickOn(xy);
        sleep(30);

        // get 2nd circle
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(4);
        robot.clickOn(xy);
        sleep(30);

        // delete text in lower area
        xy = cfp.getEditor_L_ForTestingOnly().getClearTextBtnXY();
        robot.clickOn(xy);
        sleep(30);

        // click next button and should see a popup.
        // - click on next button
        xy = cfp.getNextBtnXY();
        robot.clickOn(xy);
        sleep(30);


        assertTrue("When nextButton is clicked, and leaves a non-perfect card \n a choice delete dialogue should be shown.  ",  FMAlerts.notificationIsShowing());
        // - delete the card, click on DELETE button
        xy = FMAlerts.getCancelBtnXY();
        robot.clickOn(xy.getX() + 140, xy.getY() + 80);
        sleep(60);
        // - delete dialogue should be showing
        assertTrue("When delete is clicked, a \" delete dialogue\" should request confirmation to be deleted. ",  FMAlerts.notificationIsShowing());
        xy = FMAlerts.getOKBtnXY();
        robot.clickOn(xy.getX() + 200,  xy.getY() - 20);

        // confirm 2nd card is deleted, and only the 2nd card is deleted.
        // get node 1 and check that it is not equal to q1
        for( int i = 0; i < cfp.getCreatorList().size(); i++) {

            FlashCardMM ccRet = (FlashCardMM) cfp.getCreatorList().get(i);
            assertTrue("Delete card failed. ", ! ccRet.getQText().equals("q2"));
        }

        Toolkit.getDefaultToolkit().beep();
        sleep(100);
        Toolkit.getDefaultToolkit().beep();
        sleep(100);
        Toolkit.getDefaultToolkit().beep();
        sleep(100);

    }



    @Test
    @Order(7)
    void test_nav_buttons_and_dialoge() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();
        //  ReadFlash rf = ReadFlash.getInstance();
        setup("testeroffline@flashmonkey.xyz", "bangBang#01");
        String q5Text = "upper text only, should cause error dialogue on prev button click";
        sleep(200);
        // uses a pre created deck.
        setUpFileExists();
        sleep(200);
        Point2D xy = ReadFlash.getInstance().getCreateButtonXY();
        robot.clickOn(xy);
        sleep(200);

        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(200);

        // test previous button click from non-complete card
        // starting at last card, add data to upper area.
        Point2D xyUType = cfp.getEditor_U_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyUType);
        sleep(100);
        robot.write(q5Text);

        Point2D prevBtnXY = cfp.getPrevBtnXY();
        robot.clickOn(prevBtnXY);
        sleep(60);
        // confirm dialogue is showing, then return to edit.
        assertTrue("When prev button is clicked, an \" choice error dialogue\" should show. ",  FMAlerts.notificationIsShowing());
        // cancel message and add text to lower area.
        xy = FMAlerts.getXBtnXY();
        robot.clickOn(xy);

        sleep(60);

        Point2D xyLType = cfp.getEditor_L_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyLType);
        sleep(60);
        robot.write("text in lower area");

        // click on  previous button again  and confirm that there is no message
        // Viewed card should be the previous card, and a new card should be added
        // to the end of the deck.
        robot.clickOn(prevBtnXY);
        sleep(60);
        assertTrue("When prev button is clicked on a complete card, \" no error messages should show. ",  ! FMAlerts.notificationIsShowing());
        sleep(60);



        // test delete from dialogue on prev button click
        // start at last card
//        for(int i = 0; i < cfp.getCreatorList().size(); i++){
//            xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(i);
//            robot.clickOn(xy);
//            sleep(3000);
//        }
        // this is the last element. It should not show a blank card
        // since save on prevButton, and treeNode clicks were not
        // adding new cards.
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(2);
        robot.clickOn(xy);
        // now add a new card
        xy = cfp.getNewCardBtnXY();
        robot.clickOn(xy);
        sleep(30);
        // add text to upper area.
        robot.clickOn(xyUType);
        robot.write(" some text in q6");

/*        Toolkit.getDefaultToolkit().beep();
        sleep(100);
        Toolkit.getDefaultToolkit().beep();
        sleep(100);
        Toolkit.getDefaultToolkit().beep();
        sleep(100);*/


        // click on prev button
        // should get error, then delete.
        robot.clickOn(prevBtnXY);
        sleep(60);
        // click on delete
        xy = FMAlerts.getOKBtnXY();
        robot.clickOn(xy.getX() + 80, xy.getY());
        sleep(30);
        // a confrim clear data dialogue should be showing
        assertTrue("When clear card is clicked, a \"clear dialogue\" should request confirmation. ",  FMAlerts.notificationIsShowing());
        // click clear data, click twice!!!
        xy = FMAlerts.getOKBtnXY();
        sleep(60);
        robot.clickOn(xy.getX() + 200,  xy.getY() );
        sleep(60);
        robot.clickOn(xy.getX() + 200,  xy.getY() );
        sleep(60);
        // go back make sure data is cleared
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(3);
        robot.clickOn(xy);
        sleep(60);
        String uText = cfp.getEditor_U_ForTestingOnly().getText();
        String LText = cfp.getEditor_L_ForTestingOnly().getText();
        assertTrue("When clear data is clicked, the data should be cleared from the last card.", uText.isEmpty() && LText.isEmpty());

        // Test next button on non-complete card,
        // then just save the card.
        // - delete the text in card 4. Note tree is not in order with array.
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(5);
        robot.clickOn(xy);
        xy = cfp.getEditor_L_ForTestingOnly().getClearTextBtnXY();
        robot.clickOn(xy);
        sleep(60);

        // - click on next button
        xy = cfp.getNextBtnXY();
        robot.clickOn(xy);
        sleep(60);
        // - verify there is an error dialogue
        assertTrue("When nextButton s clicked, and leaves a non-perfect card \n a choice delete dialogue should be shown.  ",  FMAlerts.notificationIsShowing());
        // - click save
        xy = FMAlerts.getOKBtnXY();
        robot.clickOn(xy);
        // - should move to next card, card 5
        sleep(60);
        assertTrue("Card showing should be the 5th node.",  cfp.getEditor_U_ForTestingOnly().getText().equalsIgnoreCase( q5Text ));


        // Test next button on non-complete card
        // and delete the card.
        int length = cfp.getCreatorList().size();

        // - move to card 2 and delete lower text to make non-complete
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(2);
        robot.clickOn(xy);
        sleep(60);
        xy = cfp.getEditor_L_ForTestingOnly().getClearTextBtnXY();
        robot.clickOn(xy);
        sleep(60);
        // click on next button ahdnd should see dialogue
        xy = cfp.getNextBtnXY();
        robot.clickOn(xy);
        sleep(60);
        // - verify there is an error dialogue
        assertTrue("When nextButton is clicked, and leaves a non-perfect card \n a choice delete dialogue should be shown.  ",  FMAlerts.notificationIsShowing());
        // - delete the card, click on DELETE button
        xy = FMAlerts.getCancelBtnXY();
        robot.clickOn(xy.getX() + 140, xy.getY() + 80);
        sleep(60);
        // - delete dialogue should be showing
        assertTrue("When delete is clicked, a \" delete dialogue\" should request confirmation to be deleted. ",  FMAlerts.notificationIsShowing());
        xy = FMAlerts.getOKBtnXY();
        robot.clickOn(xy.getX() + 200,  xy.getY() - 20);
        // - card 2 should be deleted and card 4 should be showing.
        // play sound
        Toolkit.getDefaultToolkit().beep();
//        sleep(4000);
//        for(int i = 0; i < 6; i++){
//            xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(i);
//            robot.moveTo(xy);
            sleep(30);
//        }

        int changedLength = cfp.getCreatorList().size();
        assertTrue("When a card is deleted from the \" error dialogue\" the length of the creatorList should be less,",  changedLength < length);
        assertTrue("Card showing should be the 5th node.",  cfp.getEditor_U_ForTestingOnly().getText().equalsIgnoreCase( "q4" ));

        Toolkit.getDefaultToolkit().beep();
        bumpMouse(xy);
        Toolkit.getDefaultToolkit().beep();
        bumpMouse(xy);
        Toolkit.getDefaultToolkit().beep();
        sleep(400);
    }


    @Test
    @Order(8)
    void test_undo_button() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();
        //  ReadFlash rf = ReadFlash.getInstance();
        setup("testeroffline@flashmonkey.xyz", "bangBang#01");
        String q5Text = "upper text only, should cause error dialogue on prev button click";
        sleep(200);
        // uses a pre created deck.
        setUpFileExists();
        sleep(200);
        Point2D xy = ReadFlash.getInstance().getCreateButtonXY();
        robot.clickOn(xy);
        sleep(200);

        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(200);

        // this is the 2nd element.
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(1);
        robot.clickOn(xy);
        sleep(30);
        // Get text and store it.
        String originalUpper = cfp.getEditor_U_ForTestingOnly().getText();
        String originalLower = cfp.getEditor_L_ForTestingOnly().getText();
        sleep(30);

        // add text to upper area.
        Point2D xyUType = cfp.getEditor_U_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyUType);
        robot.write(" some extra text in q2 upper text area");
        sleep(60);

        // add text to lower area.
        Point2D xyLType = cfp.getEditor_L_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyLType);
        robot.write(" some extra text in q2 LOWER text area");
        sleep(60);

        // save the changes by moving to a new card
        // this is the 2nd element.
        xy = cfp.getNextBtnXY();
        robot.clickOn(xy);
        sleep(30);

        // move back and undo changes
        xy = cfp.getPrevBtnXY();
        robot.clickOn(xy);
        sleep(30);

        // click undo button and check text
        xy = cfp.getUndoCardBtnXY();
        robot.clickOn(xy);
        sleep(30);

        // go to card 3
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(3);
        robot.clickOn(xy);
        sleep(30);

        // go to card 2
        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(1);
        robot.clickOn(xy);
        sleep(30);

        // check that the card saved the changes back to its original state
        String upper = cfp.getEditor_U_ForTestingOnly().getText();
        String lower = cfp.getEditor_L_ForTestingOnly().getText();

        sleep(30);
        assertTrue("Undo button was not successful", upper.equals(originalUpper) && lower.equals(originalLower) );
    }

    @Test
    @Order(9)
    void test_saveDeck() throws Exception {
        // Start off with an empty deck, build it up, and save it using exit edit
        // Then repeat the process for exit using "x".

        CreateFlash cfp = CreateFlash.getInstance();
        ReadFlash rf = ReadFlash.getInstance();

        boolean deleted = CoreUtility.deleteTestFile(deckName , userName);
        assertTrue("Test deck file was not deleted", deleted);

        setup("idk@idk.com","bangBang#01");
        sleep(100);
        CoreUtility cu = new CoreUtility();

        if(cfp.getMasterPane() != null) {
            cfp.getMasterPane().setOnKeyPressed(cu::escapeKeyActions);
        }
        create.writeDeckNameHelper();
        // The entry combo-box shoud start off open.
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(200);
        // move create cards in the editors.

        String upperText = "q";
        String lowerText = "a";

        Point2D xyUpper = cfp.getEditor_U_ForTestingOnly().getTextAreaXY();
        Point2D xyLower = cfp.getEditor_L_ForTestingOnly().getTextAreaXY();
        Point2D addBtnXY = cfp.getNewCardBtnXY();

        // for the sake of numbering i starts at 1.
        for(int i = 1; i < 5; i++) {
            // write in upper text area
            robot.clickOn(xyUpper);
            sleep(30);
            robot.write(upperText + i);
            sleep(30);

            // write in lower text area
            robot.clickOn(xyLower);
            sleep(30);
            robot.write(lowerText + i);
            sleep(30);

            // Click on add button to save card
            // and create a new one.
            robot.clickOn(addBtnXY);
            sleep(30);
        }

        robot.clickOn(xyUpper);
        robot.write("q5");
        sleep(30);

        robot.clickOn(xyLower);
        robot.write("a5");
        sleep(30);

        Point2D xy = cfp.getPrevBtnXY();
        robot.clickOn(xy);

        // save the deck, move to review deck
        // and check that there are no errors.
        xy = cfp.getSaveDeckBtnXY();
        robot.clickOn(xy);
        sleep(30);

        // go to read flash test mode and check that the deck is 5 cards
        // long.
        xy = FlashMonkeyMain.getCreateButtonXY();
        robot.clickOn(xy.getX(), xy.getY() - 40);
        sleep(30);

        rf.getStudyButtonXY();
        //robot.moveTo(xy.getX() + 300, xy.getY() + 110);
        robot.clickOn(xy.getX() + 310, xy.getY() + 106);
        sleep(30);

        int size = FlashCardOps.getInstance().getFlashList().size();
        assertTrue("FlashList is not size 5", size == 5);

        xy = FlashMonkeyMain.AVLT_PANE.getCircleXY(2);
        robot.clickOn(xy);
        // check that node 5 is correct manually.
        sleep(1000);
        String s = ((FlashCardMM) FlashCardOps.getInstance().getFlashList().get(4)).getQText();

        assertTrue("Did not save the last card", s.equals("q5"));


//        xy = rf.getBackToMenuBtnXY();
//        robot.clickOn(xy);
//        sleep(30);
//
//        xy = rf.getCreateButtonXY();
//        robot.clickOn(xy);
//
//        push(KeyCode.DOWN);
//        push(KeyCode.ENTER);
//        sleep(200);

//        robot.clickOn(xyUpper);
//        robot.write("q5");
//        sleep(30);
//
//        robot.clickOn(xyLower);
//        robot.write("a5");
//        sleep(30);
//
//        xy = cfp.getPrevBtnXY();
//        robot.clickOn(xy);

//        xy = cfp.getSaveDeckBtnXY();
//        robot.clickOn(xy);
//        sleep(30);
//
//        // go to read flash test mode and check that the deck is 5 cards
//        // long.
//        xy = FlashMonkeyMain.getCreateButtonXY();
//        robot.clickOn(xy.getX(), xy.getY() - 40);
//        sleep(30);
//
//        rf.getStudyButtonXY();
//        //robot.moveTo(xy.getX() + 300, xy.getY() + 110);
//        robot.clickOn(xy.getX() + 310, xy.getY() + 106);
//        sleep(30);
//
//        Toolkit.getDefaultToolkit().beep();
//        sleep(100);
//        Toolkit.getDefaultToolkit().beep();
//        sleep(100);
//        Toolkit.getDefaultToolkit().beep();
//        sleep(30);
//
//        size = FlashCardOps.getInstance().getFlashList().size();
//        assertTrue("FlashList is not size 4", size == 5);


        //FlashMonkeyMain.getWindow().close();
        //Platform.exit();
//        if(FxToolkit.isFXApplicationThreadRunning() ) {
//            FxToolkit.hideStage();
//            sleep(60);
//            FxToolkit.hideStage();
//        }
//        Toolkit.getDefaultToolkit().beep();
//        sleep(60);
//        Toolkit.getDefaultToolkit().beep();
//        sleep(60);
//        Toolkit.getDefaultToolkit().beep();
//        sleep(5000);



//        // restart and check if file is corrupted
//        setup("idk@idk.com","bangBang#01");
//        sleep(100000);
//
//        deckName = "1e3f03f29759f53ad1a895dff88ed535_1e3f03f29759f53ad1a895dff88ed535$4_cards_shapes_images.dec ";
//        setUpFileExists(deckName);
//        sleep(200);
//        rf.getStudyButtonXY();
//        //robot.moveTo(xy.getX() + 300, xy.getY() + 110);
//        robot.clickOn(xy.getX() + 310, xy.getY() + 106);
//        sleep(30);
//
//        size = FlashCardOps.getInstance().getFlashList().size();
//        assertTrue("FlashList is not size 4", size == 4);

    }

    @Test
    @Order(8)
    void test_nav_buttons_disabled() throws Exception {
        // Start off with an empty deck, build it up, and save it using exit edit
        // Then repeat the process for exit using "x".

//        CreateFlash cfp = CreateFlash.getInstance();
//
//        boolean deleted = CoreUtility.deleteTestFile(deckName , userName);
//        assertTrue("Test deck file was not deleted", deleted);
//
//        setup("idk@idk.com","bangBang#01");
//        sleep(100);
//        CoreUtility cu = new CoreUtility();
//
//        if(cfp.getMasterPane() != null) {
//            cfp.getMasterPane().setOnKeyPressed(cu::escapeKeyActions);
//        }
//        create.writeDeckNameHelper();
//        // The entry combo-box shoud start off open.
//        push(KeyCode.DOWN);
//        push(KeyCode.ENTER);
//        sleep(200);
//        // move create cards in the editors.
//
//        String upperText = "Text in Q";
//        String lowerText = "Text in A";
//
//        Point2D xyUpper = cfp.getEditor_U_ForTestingOnly().getTextAreaXY();
//        Point2D xyLower = cfp.getEditor_L_ForTestingOnly().getTextAreaXY();
//        Point2D addBtnXY = cfp.getNewCardBtnXY();
//
//        for(int i = 0; i < 4; i++) {
//            // write in upper text area
//            robot.clickOn(xyUpper);
//            sleep(30);
//            robot.write(upperText + i);
//            sleep(30);
//
//            // write in lower text area
//            robot.clickOn(xyLower);
//            sleep(30);
//            robot.write(lowerText + i);
//            sleep(30);
//
//            // Click on next button to save card
//            // and create a new one.
//            robot.clickOn(addBtnXY);
//            sleep(30);
//        }
//
//        // save the deck, move to review deck
//        // and check that there are no errors.
//        Point2D xy = cfp.getSaveDeckBtnXY();
//        robot.clickOn(xy);
//
//        sleep(6000);




    }


    @Test
    @Order(10)
    void test_resetOrder() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();
        //  ReadFlash rf = ReadFlash.getInstance();
        setup("testeroffline@flashmonkey.xyz", "bangBang#01");
        String q5Text = "upper text only, should cause error dialogue on prev button click";
        sleep(200);
        // uses a pre created deck.
        setUpFileExists();
        sleep(200);
        Point2D xy = ReadFlash.getInstance().getCreateButtonXY();
        robot.clickOn(xy);
        sleep(200);

        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(200);


    }


    @Test
    @Order(11)
    void test_bailOut() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();
        //  ReadFlash rf = ReadFlash.getInstance();
        setup("testeroffline@flashmonkey.xyz", "bangBang#01");
        String q5Text = "upper text only, should cause error dialogue on prev button click";
        sleep(200);
        // uses a pre created deck.
        setUpFileExists();
        sleep(200);
        Point2D xy = ReadFlash.getInstance().getCreateButtonXY();
        robot.clickOn(xy);
        sleep(200);

        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(200);


    }


    @Test
    @Order(12)
    void test_SHAPES_InEditorUOnlyCausesPopup() throws Exception {
        CreateFlash cfp = CreateFlash.getInstance();
        if(cfp.getMasterPane() != null) {
            CoreUtility cu = new CoreUtility();
            cfp.getMasterPane().setOnKeyPressed(cu::escapeKeyActions);
        }

        boolean deleted = CoreUtility.deleteTestFile(deckName , userName);
        assertTrue("Test deck file was not deleted", deleted);
        //cfp.createFlashScene();
        setup("idk@idk.com","bangBang#01");
        sleep(200);
        create.writeDeckNameHelper();
        sleep(30);
        // The EntryCombobox should start in the open position.
        // assertTrue(CreateFlash.getInstance().entryComboBoxIsShowing());
        //System.out.println("EntryComboBox is showing? : " + CreateFlash.getInstance().entryComboBoxIsShowing());
        // Select multiChoice card type
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(30);

        // Register the stage so we can
        // test when the DrawTools window
        // is closed.
        DrawTools dt = DrawTools.getInstance();
        FxToolkit.toolkitContext().setRegisteredStage(dt.getDrawToolWindow());

        //  *** Create snapshot ***

        double diff_x = 200;
        double diff_y = 200;
        Point2D xy = cfp.getEditor_L_ForTestingOnly().getDrawpadBtnXY();
        robot.clickOn(xy);
        sleep(30000);

        //  CHECK FOR CORRECT SHAPES
        // Resize the shapes box make sure this is saved in future test
        double minX = dt.getDrawToolWindow().getX();
        double minY = dt.getDrawToolWindow().getY();
 //       dt.

        // save and close popup and tools.
//        dt.getDrawToolWindow();
//        xy = dt.getSaveBtnXY();
//        robot.clickOn(xy);
//        xy = dt.getExitBtnXY();
//        robot.moveTo(xy);
//        robot.clickOn(xy);
//        sleep(200);

        // re-open
//        xy = cfp.getEditor_L_ForTestingOnly().getRightPaneXY();
//        robot.moveTo(xy);
//        //sleep(2000);
//        robot.clickOn(xy);
//        sleep(1000);
//
//        // check that only one popUp will open
//        robot.moveTo(500, 500);
//        robot.clickOn(xy);
//        sleep(1000);



    }




    /**
     * Example tester from before ,...
     * @throws Exception
     */
    @Test
    @Order(13)
    void test_general_shapes() throws Exception {

        CreateFlash cfp = CreateFlash.getInstance();
        if(cfp.getMasterPane() != null) {
            CoreUtility cu = new CoreUtility();
            cfp.getMasterPane().setOnKeyPressed(cu::escapeKeyActions);
        }

        boolean deleted = CoreUtility.deleteTestFile(deckName , userName);
         assertTrue("Test deck file was not deleted", deleted);
        //cfp.createFlashScene();
        setup("idk@idk.com","bangBang#01");
        sleep(200);
        create.writeDeckNameHelper();
        sleep(30);
        // The EntryCombobox should start in the open position.
        // assertTrue(CreateFlash.getInstance().entryComboBoxIsShowing());
        //System.out.println("EntryComboBox is showing? : " + CreateFlash.getInstance().entryComboBoxIsShowing());
        // Select multiChoice card type
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        sleep(30);

        Point2D xyUType = cfp.getEditor_L_ForTestingOnly().getTextAreaXY();
        robot.clickOn(xyUType);
        robot.write("Taking a snapshot and creating two shapes");
        sleep(50);

        //  *** Create snapshot ***

        double diff_x = 200;
        double diff_y = 200;
        Point2D xy = cfp.getEditor_L_ForTestingOnly().getSnapShotBtnXY();
        robot.clickOn(xy);
        sleep(30);

        // Create the snapshot by drawing the
        // snapshot rectangle on the screen.
        robot.moveTo(50 + diff_x,50 + diff_y);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(400 + diff_x, 300 + diff_y);
        robot.release(MouseButton.PRIMARY);
        sleep(500);

        // Register the stage so we can
        // test when the DrawTools window
        // is closed.
        DrawTools dt = DrawTools.getInstance();
        FxToolkit.toolkitContext().setRegisteredStage(dt.getDrawToolWindow());

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

        // At this point, should test the image in the right pane
        buIm = SnapShot.getInstance().getImgBuffer();
        Image imageL = SwingFXUtils.toFXImage(buIm, null);
        ImageView rPaneiView = (ImageView) cfp.getEditor_L_ForTestingOnly().getRightPane().getChildren().get(0);

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
//        xy = dt.getSaveBtnXY();
//        robot.clickOn(xy);
//        sleep(250);

        // now exit shapes. Shapes should
        // save on exit
        //dt.getDrawToolWindow();
        FxToolkit.hideStage();


        /*
         * Check Right Pane for image, editing shapes,
         * adding new shapes, and removing shapes.
         */

        // CLICK ON MAIN STAGE/WINDOW RIGHT PANE
        // popUp should be created with editable
        // shapes and popUp DrawTools
        xy = cfp.getEditor_L_ForTestingOnly().getRightPaneXY();
        robot.moveTo(xy);
        //sleep(2000);
        robot.clickOn(xy);
        sleep(500);


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
        ArrayList<GenericShape> shapeAry = cfp.getEditor_L_ForTestingOnly().getArrayOfFMShapes();

        FMRectangle fmRect = new FMRectangle();
        int num = 0;
        for(GenericShape gs : shapeAry) {

            if(num > 0 && gs.getClass().getName().endsWith("FMRectangle")) {
                fmRect = (FMRectangle) gs;
                System.out.println("found it" + fmRect.toString());
                break;
            }
            System.out.println("idx: " + num + ") ShapeAry element class: " + gs.getClass().getName());
            num++;
        }

        if(fmRect.getWd() >= 10) {

            //need to add the panes x & y location to the xy position
            paneXY = dt.getOverlayPaneXY();

            xy = new Point2D(fmRect.getX() + paneXY.getX(), fmRect.getY() + paneXY.getY());
            robot.moveTo(xy);
            sleep(200);
            // should see verticies of rectangle in popUpPane
            robot.clickOn(MouseButton.SECONDARY);
            sleep(200);

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
            ArrayList arrayList = cfp.getEditor_L_ForTestingOnly().getNodesFmRPane();
            Rectangle fxRect = new Rectangle();

            for(num = 0; num < arrayList.size(); num++) {

                System.out.println("arrayList class name: " + arrayList.get(num).getClass().getName());

                if(num > 0 && arrayList.get(num).getClass().getName().equals("javafx.scene.shape.Rectangle")) {
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


        //sleep(1000);


        // Add a new rectangle and check right pane
        // change to a highlight color
        xy = dt.getHighlightBtnXY();
        //robot.moveTo(xy);
        robot.clickOn(xy);
        sleep(100);

        // move to the rectangle button and click on it.
        xy = dt.getRectBtnXY();
        robot.clickOn(xy);

        dt.getOverlayTestCanvas();

        // create new rectangle
        robot.moveTo(paneXY.getX() + 50, paneXY.getY() + 50);
        robot.drag(MouseButton.PRIMARY);
        robot.moveTo(paneXY.getX() + 150, paneXY.getY() + 150);
        robot.release(MouseButton.PRIMARY);

        sleep(30);

        // Check if new rectangle is showing while being drawn
//        assertTrue("line 1415, overlayCanvas is not the original reference", (originalCanvas == dt.getCanvas()));
        // May fail if background of first pixel is not white
        String msg = "New rectangle does not appear to be being drawn on the canvas";

        Toolkit.getDefaultToolkit().beep();
        sleep(100);
        Toolkit.getDefaultToolkit().beep();
        sleep(100);
        Toolkit.getDefaultToolkit().beep();
        sleep(100);

        sleep(3000);

        // Compare with UICOlors.HIGHLIGHT_PINK
        ImageUtility.checkPixColor((int) paneXY.getX() + 85, (int) paneXY.getY() + 85, msg, 255, 13 ,255);
        sleep(3000);


        // check right pane for new rectangle
        Pane rPane = cfp.getEditor_L_ForTestingOnly().getRightPane();
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

        Shape s = (Shape) rPane.getChildren().get(num - 1);

        System.out.println("New rectangle height: " + ((Rectangle) s).getHeight() );

        assertTrue("Width in new Rectangle is not correct", 29 >= (int) ((Rectangle) s).getWidth() - 2 );
        assertTrue("Height in new Rectangle is not correct",29 >= (int) ((Rectangle) s).getHeight() - 2 );
        assertTrue("RightPane does not contain the new Rectangle", rPane.getChildren().get(rPaneSize - 1).getClass().getName().contains("Rectangle"));
        //sleep(1000);

        // CLOSE AND RE-OPEN AND CHECK FOR CORRECT SHAPES

        // save and close popup and tools.
//        dt.getDrawToolWindow();
//        xy = dt.getSaveBtnXY();
//        robot.clickOn(xy);
//        xy = dt.getExitBtnXY();
//        robot.moveTo(xy);
//        robot.clickOn(xy);
//        sleep(200);

        // re-open
//        xy = cfp.getEditor_L_ForTestingOnly().getRightPaneXY();
//        robot.moveTo(xy);
//        //sleep(2000);
//        robot.clickOn(xy);
//        sleep(1000);
//
//        // check that only one popUp will open
//        robot.moveTo(500, 500);
//        robot.clickOn(xy);
//        sleep(1000);


        // @TODO check if the saved additions are saved in the arrayOfShapes, Check if a shape is deleted

    }  // END TEST






}
