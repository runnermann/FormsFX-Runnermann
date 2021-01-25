package flashmonkey;

import core.RobotUtility;
import fileops.DirectoryMgr;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SelectDeckTester extends ApplicationTest {
	
	/* ROBOT RELATED */
	RobotUtility bobTheBot = new RobotUtility();
	
	private static String userName = "idk@idk.com";
	private static final String SYSTEM_DIR = core.CoreUtility.getWorkingDirectory() + "/FlashMonkeyData/";
	private static final File FOLDER = new File(SYSTEM_DIR + userName.hashCode() + "/decks");
	/**
	 * The objective of this test is to test that the correct decks are listed,
	 * and if when there are two existing decks in both the cloud and local, then
	 * the correct deck is listed. This test is to test that a deck that exists
	 * locally and is only a file name, or is less than 6 bytes, does not override
	 * a deck that exists in the cloud when it is larger than 6 bytes.
	 * @throws Exception
	 */
/*	@Test
	@Order(8)
	public void AgrFileListHasCorrectLinks() throws Exception {
		
		AgrFileList agrList = new AgrFileList(FOLDER, );
		
		ArrayList<LinkObj> linkObjList = agrList.getRecentFiles();
		
		
		String descript;
		for(int i = 0; i < linkObjList.size(); i++) {
			LinkObj element = agrList.getRecentFiles().get(i);
			descript = element.getDescrpt();
			System.out.println( descript + " is cloudLink: " + (element.getCloudLink() != null));
		}
		
	} */

	
	/*  ROBOT APP TESTING  */
	@Test
	@Order(9)
	public void readingCorrectDeck() throws Exception {
		
		bobTheBot.robotSetup();
		
		// click on create button
		Point2D xy = FlashMonkeyMain.getCreateButtonXY();
		bobTheBot.getBob().clickOn(xy);
		sleep(4000);
		
		System.out.println("deckName: " + ReadFlash.getInstance().getDeckName());
		
		String deckName = ReadFlash.getInstance().getDeckName();
		assertTrue(deckName.equals("a new deck onS3.dat"), "Not showing correct deck.");
		
	}
	
	
}
