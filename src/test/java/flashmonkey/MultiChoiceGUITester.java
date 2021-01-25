package flashmonkey;



import core.CoreUtility;
import fileops.DirectoryMgr;
import fmtree.FMTWalker;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
import javafx.stage.WindowEvent;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.api.FxRobot;

import org.testfx.framework.junit5.ApplicationTest;

import org.testfx.robot.impl.SleepRobotImpl;
import type.testtypes.MultiChoice;
import uicontrols.ButtoniKon;

import java.awt.*;
import static org.junit.Assert.*;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.logging.Logger;




/**
 * This class tests at the top level. It tests the FlashMonkey GUI starting at the
 * FlashMonkeyMain start(Stage stage) method.
 *
 * IMPORTANT!!!
 * This test is expected to run on a 15 inch macbook pro screen. If it is
 * used on a different sized screen, the robot.moveTo(..) methods should
 * be modified for the new screen.
 *
 * WARNING!!!
 * This tester actively moves the mouse, clicks on the screen, and
 * writes to the screen. It may modify
 * anything on the screen which is under the mouse when robot
 * clicks on it. Which could become the "BAD ROBOT" if
 * the FlashMonkey GUI is not under the mouse when it clicks
 *
 * To avert this risk, ensure that a stage is always under the mouse
 *
 */

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MultiChoiceGUITester extends ApplicationTest
{
	// to attempt to account for screen differences
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private int delta_X = 0;
	private int delta_Y = 10;

	FxRobot robot = new FxRobot();
	SleepRobotImpl sleep = new SleepRobotImpl();
	DirectoryMgr directory = new DirectoryMgr();
	String strPath = "../flashMonkeyFile/TestingDeck.dat";
	Path filePath = Paths.get(strPath);
	// for the background stage
	private Toolkit tk = Toolkit.getDefaultToolkit();
	private Dimension d = tk.getScreenSize();
	private int screenWt = d.width;
	private int screenHt = d.height;

	private CreateCards create;



	/* This operation comes from ApplicationTest and loads the GUI to test. */
	//@Override
	@Override
    public void start(Stage stage) throws Exception
    {
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

       
        //System.out.println("\n TestingDeck.dat removed from file was: " + bool);
    }

    private void setup() throws Exception {

		String strPath = "../flashMonkeyFile/TestingDeck.dat";
		File file = new File(strPath);
		Path filePath = Paths.get(strPath);
		//file = new File(filePath);
		//if(file.exists()) {
		//	files.delete();
		//}
	//	boolean bool = Files.deleteIfExists(filePath);
	//	LOGGER.info("file deleted: " + bool);


		FxToolkit.registerPrimaryStage();
		FxToolkit.setupApplication(FlashMonkeyMain.class);

		final Stage window = FlashMonkeyMain.getWindow();

		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});

		delta_X = (int) window.getX();// getScegetY();
		delta_Y = (int) window.getY();
		create = new CreateCards(delta_X, delta_Y);

	}

	@BeforeEach
	public void build() throws Exception{
		Boolean bool = false;
		bool = Files.deleteIfExists(filePath);
		LOGGER.info("is flashMonkeyFolder deleted: " + bool);
	}
    
    /*
    @After
    public void teardown() throws TimeoutException
    {
        FxToolkit.hideStage();
        release(new KeyCode[] {});
        release(new MouseButton[] {});
    }
    */

	/**
	 * The escapeKey while FxRobot is running
	 * @param e
	 */
	/*
	private void escapeKeyActions(KeyEvent e) {

		LOGGER.info("escapeKeyAction called");

		if(e.getCode() == KeyCode.ESCAPE) {
			Platform.exit();
			System.exit(0);
		}
	}

	 */


    /** HELPER METHOD **/

    public <T extends Node> T find(final String query)
    {
        return (T) lookup(query).queryAll().iterator().next();
    }

    /**
     * Check that creates a new flashdeck, Enters 
     * a name in the name field, and the new file 
     * is created. Switches to new createCards Scene when 
     * complete. 
     * @throws Exception
     */
    @Ignore
	//@Order(0)
    public void createNewDeck() throws Exception
    {
    	setup();

		create.writeDeckNameHelper();
    	assertTrue(new File(strPath).exists());
    }




	public void selectCreate() {
		robot.clickOn(delta_X + 150, delta_Y + 300);
	}
    
    @Test
	//@Order(1)
    public void createNewCards() throws Exception
    {
		setup();

		create.writeDeckNameHelper();
		create.createNewCardsHelper();

		assertTrue("Deck is not larger than 4", CreateFlash.getInstance().getLength() > 3 );
    }





	//@Order(2)
	@Ignore
	public void getTestDeck() throws Exception
	{
		//setup();

		create.getTestDeckHelper();
		assertTrue(ButtoniKon.getStudyButton().isVisible());
	}




	/**
	 * Sequences through button clicks until the first
	 * test page is showing. Tests that a test page
	 * is showing.
	 * NOTE: Check that no decks exists in the
	 * FlashMonkeyFile folder except TestingDeck.dat
	 * @throws Exception
	 */
	@Ignore
    //@Test
	//@Order(3)
    public void changeToTestPage() throws Exception
    {
		setup();

		create.getTestDeckHelper();
		changeToTestPageHelper();
    	//sleep(300);
    	assertTrue("ReadFlash mode is not in test ", ReadFlash.getInstance().getMode() == 't');
    }

    private void changeToTestPageHelper() {
		//robot.moveTo(650, 230 - delta_Y);

		LOGGER.info("changeToTestPageHelper called");

		// click on startStudying
		ReadFlash rf = ReadFlash.getInstance();

		// click on test
		robot.clickOn(rf.getStudyButtonXY());
		sleep(200);
		robot.clickOn(rf.getTestButtonXY());

		sleep(300);
	}

	/**
	 * NOTE: Check that no decks exists in the
	 * FlashMonkeyFile folder except TestingDeck.dat
	 * @throws Exception
	 */
	@Ignore
	//@Test
	//@Order(4)
    public void testIsMultipleChoice() throws Exception
    {
		setup();
		//checkFileExists();
		create.getTestDeckHelper();
    	// !! SCENE CHANGE !! to study options

    	assertTrue("Not in multiple choice mode", ( (FlashCardMM) FMTWalker.getCurrentNode().getData()).getTestType() ==
				 0b1000000000000010 );
    }


	/**
	 * NOTE: Check that no decks exists in the
	 * FlashMonkeyFile folder except TestingDeck.dat
	 * @throws Exception
	 */
	@Ignore
	//@Test
	//@Order(5)
    public void firstQButton_is_inactive_at_start() throws Exception
    {
    	setup();
		//checkFileExists();
		create.getTestDeckHelper();
		changeToTestPageHelper();


    	// !! SCENE CHANGE !! to multiple choice test
    	
    	/** Check buttons status and actions for first scene change to TestScene **/
    	/**
    	 * buttons disabled
    	 * - prevQButton
    	 * - firstQButton
    	 * - prevAnswerButton
    	 */
    	
    	// click on firstQButton
    	// - should be inactive
    	//robot.moveTo(600, 520 - delta_Y);
    	robot.clickOn(delta_X + 20, delta_Y + 500);
    	//sleep(300);
    	assertTrue("firstQButton is not inactive,", ReadFlash.firstQButton.isDisable());
    }


	/**
	 * Tests the MultiChoice (TestType) behavior when
	 * the authcrypt.user is in test mode.
	 *
	 * NOTE: Check that no decks exists in the
	 * FlashMonkeyFile folder except TestingDeck.dat
	 * @throws Exception
	 */
	//@Ignore
	@Test
	//@Order(6)
    public void theRestOfTheTest() throws Exception
    {

    	LOGGER.info("Starting");

		setup();
		//checkFileExists();
		sleep(100);

		CreateFlash cfp = CreateFlash.getInstance();
		ReadFlash rfp = ReadFlash.getInstance();
		MultiChoice mc = MultiChoice.getInstance();

		// test deck is already built
		// use it.
		create.getTestDeckHelper();
		changeToTestPageHelper();

		int navBtnY = delta_Y + 360;
		int navBtnX = delta_X + 20;
		int ansBtnY = delta_Y + 360;
		int ansBtnX = delta_X + 200;

		// Make this pane the top pane by clicking on it.
		robot.clickOn(navBtnX, navBtnY + 100);

		CoreUtility cu = new CoreUtility();

		if(cfp.getMasterPane() != null) {
			cfp.getMasterPane().setOnKeyPressed(cu::escapeKeyActions);
		} else {
			rfp.getMasterBPane().setOnKeyPressed(cu::escapeKeyActions);
		}


    	    	
    	/**
    	 * buttons enabled and status
    	 * Run through tree, check there are 4 elements in tree
    	 * Check next answer button, run through answer array
    	 * Check next answer button is disabled at end of tree
    	 * Check next answer button is enabled on nextQButton
    	 * run through answers till next answer button is disabled again
    	 * check prevQbutton re-enables next answer button.
    	 * - then check 
    	 */

		// click on prevQButton
		// - should be inactive
		robot.clickOn(rfp.getPrevQButtonXY());
		sleep(300);

		// click on prev answer button
		// - should not be active

		assertTrue(mc.getPrevAnsButton().isDisabled());
		sleep(300);


    	
    	// Run through answer array and check nextAnsButton disabled
    	// click nextAnsButton
    	// should be enabled
    	robot.moveTo(mc.getNextAnsButtonXY());


    	for(int i = 0; i < 4; i++) {
			robot.clickOn(mc.getNextAnsButtonXY());
			sleep(500);
		}
    	// NextAnsButton should now be disabled

		assertTrue(mc.getNextAnsButton().isDisabled());
    	//robot.clickOn(mc.getNextAnsButtonXY());
    	
//    	if(ReadFlash.nextAnsButton.isDisable())
//    	{
//    		System.out.println("nextAnsButton is disabled lastElementTest()");
//    	} else {
//    		System.err.println("nextAnsButton did not disable on last element");
//    	}
//    	sleep(2000);
    	
    	// Run back to start of answer array and check prevAnsButton disables
    	// Check prevAnsButton is enabled 
    	// - should not be active
    	robot.moveTo(ansBtnX, ansBtnY);
		for(int i = 0; i < 4; i++) {
			robot.clickOn(mc.getPrevAnsButtonXY());
			sleep(200);
		}
    	
    	// Prev answer button should now be disabled
		//robot.clickOn(ansBtnX, ansBtnY);
		assertTrue(mc.getPrevAnsButton().isDisabled());


    	// click on selectAnswerButton
    	//robot.moveTo(730, ansBtnY);
		robot.clickOn(mc.getSelectAnsButtonXY());
		sleep(2000);
    	
    	// click on nextQButton size of tree
    	//robot.moveTo(680, navBtnY);
		for(int i = 0; i < FMTWalker.getCount(); i++) {
			robot.clickOn(rfp.getNextQButtonXY());
			sleep(500);
		}
    	
    	// -nextQButton should be disabled
    	robot.clickOn(navBtnX + 90, navBtnY);
		assertTrue(rfp.getNextQButton().isDisabled());
    	sleep(200);
    	
    	// lastQButton should be disabled
    	//robot.moveTo(710, navBtnY);
    	robot.clickOn(navBtnX + 130, navBtnY);
		assertTrue(rfp.getEndQButton().isDisabled());

    	// Click on prevQButton till the beginning of the tree
    	// click on prevQButton
		for(int i = 0; i < FMTWalker.getCount(); i++) {
			robot.clickOn(rfp.getPrevQButtonXY());
			sleep(200);
		}



    	// -prevQButton should be disabled
		//robot.clickOn(navBtnX + 50, navBtnY);
		assertTrue(rfp.getPrevQButton().isDisabled());
		sleep(1000);

		// click on firstQButton should be disabled
		//robot.clickOn(navBtnX , navBtnY);
		assertTrue(rfp.getFirstQButton().isDisabled());
		sleep(1000);
    	
    	// click on selectAnswerButton
		robot.clickOn(mc.getSelectAnsButtonXY());
    	sleep(500);
    	
    	// click on lastQButton
    	//robot.moveTo(710, navBtnY);
		robot.clickOn(rfp.getEndQButtonXY());;
    	sleep(200);
    	
    	// click on selectAnswerButton
    	//robot.moveTo(730, ansBtnY);
		robot.clickOn(mc.getSelectAnsButtonXY());
    	sleep(200);
    	
    	// click on prevQButton
    	// - moves to next to last question
    	//robot.moveTo(640, navBtnY);
		robot.clickOn(rfp.getPrevQButtonXY());
    	sleep(1000);
    	
    	// click on selectAnswerButton
    	// - answer button is enabled and 
    	// the question is answered. and the next question
		// comes into view, test if ansButton in enabled.
		assertFalse(mc.getSelectAnsButton().isDisabled());
		// Move to prevQ and check if selectAnsButton is diabled.
		robot.clickOn(rfp.getNextQButtonXY());
		sleep(1000);
		assertTrue(mc.getSelectAnsButton().isDisabled());
    	
    	/** 
    	 * Move to the start of the tree, check buttons
    	 */
    	
    	//** Check firstQButton is enabled and action moves to first question in tree**
    	
    	// click on firstQButton
    	// - Moves to the first question.
    	//robot.moveTo(600, navBtnY);
		robot.clickOn(rfp.getFirstQButtonXY());

    	sleep(1000);
    	
    	///** Check prevQButton is now disabled **
    	
    	/** Check firstQButton is now disabled **/
    	
    	/** Check selectAnswerButton is now disabled **/
    	// click on selectAnswerButton
    	// - answer button is disabled
    	// and nothing happens
    	//robot.moveTo(730, ansBtnY);
		assertTrue(mc.getSelectAnsButton().isDisabled());
    	sleep(100);

    }





	/* IMO, it is quite recommended to clear the ongoing events, in case of. */

	//@AfterAll
	public void tearDown() //throws TimeoutException {
	{
		/* Close the window. It will be re-opened at the next test. */

		//FxToolkit. hideStage();
		release(new KeyCode[] {});
		release(new MouseButton[] {});
	}

}
