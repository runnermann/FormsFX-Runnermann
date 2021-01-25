package core;

import fileops.DirectoryMgr;
import flashmonkey.CreateCards;
import flashmonkey.FlashMonkeyMain;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.robot.impl.SleepRobotImpl;
import security_and_login.LoginTesterUtility;

import org.testfx.framework.junit5.ApplicationTest;


import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotUtility extends ApplicationTest {

	/* ROBOT RELATED */
	FxRobot bob;// = new FxRobot();
	SleepRobotImpl sleep = new SleepRobotImpl();
	private CreateCards create;
	//private CreateFlash create;
	private LoginTesterUtility loginUtility;
	
	private int delta_X = 0;
	private int delta_Y = 10;
	
	
	
	/* ROBOT SETUP */
	public void robotSetup() throws Exception {

		bob = new FxRobot();
		
		FxToolkit.registerPrimaryStage();
		FxToolkit.setupApplication(FlashMonkeyMain.class);
		
		//sleep(100);
		final Stage window = FlashMonkeyMain.getWindow();
		delta_X = (int) window.getX();// getScegetY();
		delta_Y = (int) window.getY() -10;
		create = new CreateCards(delta_X, delta_Y);
		loginUtility = new LoginTesterUtility(delta_X, delta_Y);
		loginUtility.logIn("idk@idk.com", "bangBang#01", bob);
		sleep(200);
		// Hey Bob, this deck has a lot of stuff, don't delete it please.
		// create.removePrevFile(strPath);
		System.out.println("MetaDataTEster: robotSetup() before create.getExistingDeck()");
		create.getExistingDeck();
		sleep(5000);
		
		System.out.println("MetaDataTester: robotSetup() complete");
	}
	
	public FxRobot getBob() {
		return bob;
	}
}
