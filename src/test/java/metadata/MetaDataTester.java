package metadata;

import core.RobotUtility;
import fileops.DirectoryMgr;
import flashmonkey.*;

import forms.DeckMetaModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;


import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("metaDataTester")
public class MetaDataTester extends ApplicationTest {
	
	/* ROBOT RELATED */
	RobotUtility bobTheBot = new RobotUtility();
	
	DeckMetaData metaInstance;// = DeckMetaData.getInstance();
	//private String deckNameOnly = "testDeckMetaData";
	private HashMap<String, String> metaMapOne;
	private HashMap<String, String> metaMapTwo;
	

	private long lastDate = System.currentTimeMillis();
	private String createDate = "10101";
	private String origAuthor = "The original author";
	//private String deckFileName = "testDeckMetaData";
	// This decks metaData
	//private String[] deckMetaDataAry;
	private String descript = "This description wrote by robot";
	private int numImg = 5;
	private int numVideo = 7;
	private int numAudio = 9;
	private int numCard = 11;
	private int numSessions = 13;
	private String deckBook = "A test book";
	private String deckClass = "MetaDataTesting class room and building";
	private String deckProf = "Professor Meta";
	private String deckSchool = "MetaData University";
	private String subj = "MetaData subject";
	private String subjSubCat = "MetaData subCat data";
	private String lang = "A friendly language";
	private String courseCode = "UTHist12345";
	private String photoUrl = "url=AmHist2054";
	private int price = 16;
	private boolean isShareDistro = true;
	private boolean isSellDeck = true;


	private String[] keyAry = {
			"deck_id",
			"deck_photo",
			"deck_descript",
			"creator_email",
			"last_date",
			"create_date",
			"subj",
			"section",
			"deck_book",
			"deck_class",
			"deck_prof",
			"deck_name",
			"test_types",
			"num_cards",
			"num_imgs",
			"num_video",
			"num_audio",
			"deck_numstars",
			"num_users",
			"price",
			"course_id",
			"share_distro",
			"sell_deck"
	};

	
	private String testTypes;
	// date in Millis, score
	private ArrayList<DeckMetaData.ScoreNode> scores = new ArrayList<>(1);
	
	@BeforeAll
	public void init() {
		//ops = FlashCardOps.getInstance();
		metaInstance = DeckMetaData.getInstance();
		
	}
	
	@BeforeEach
	public void setup() {
//		metaInstance.newInstance();
		metaInstance.close();
		metaInstance = DeckMetaData.getInstance();
		metaMapOne = new HashMap<>();
		metaMapOne.put("deck_descript", "default");
		metaMapTwo = new HashMap<>();
		metaMapTwo.put("deck_descript", "default");
		// Set the test DeckMetaData object to the
		// variables and data in this test
		setTestMetaData(metaInstance);
		// Udate the array to this
		metaInstance.updateDataMap();
		//meta.setDeckMetaDataAry(metaAryOne);
	}
	
	
	
	
	
	//@AfterAll
	public void tearDown() throws Exception {
		FxToolkit.hideStage();
		release(new KeyCode[]{});
		release(new MouseButton[]{});
		bobTheBot.getBob().release(MouseButton.PRIMARY);
		bobTheBot.getBob().release(MouseButton.SECONDARY);
	}
	
	/**
	 * Enables testing of JavaFX, creates overlay pane.
	 * @param backgroundStage
	 */
	@Override
	public void start(Stage backgroundStage) {
		StackPane pane = new StackPane();
		pane.setStyle("-fx-background-color: TRANSPARENT");
		// Set color to 0,0,0,0.1 instead of TRANSPARENT to fix windows 10 issue.
		Scene backScene = new Scene(pane, 400, 400, new Color(0, 0, 0, 1d / 255d));
		backgroundStage = new Stage(StageStyle.TRANSPARENT);
		
		backgroundStage.setScene(backScene);
		backgroundStage.show();
		/* Do not forget to put the GUI in front of windows.
		 Otherwise, the robots may interact with another
        window, the one in front of all the windows... */
		backgroundStage.toFront();
	}
	
	/**
	 * Checks if the file exists. File must be saved
	 * prior to this test.
	 */
	@Test
	@Order(1)
	public void saveDeckMetaDataTest() {
		//read.setDeckName(deckName);
		setMetaInFile(this.metaMapOne, "testDeckMetaData");
		
		String folder = DirectoryMgr.getMediaPath('t');
		String fileName = "testDeckMetaData.met";
		String path = folder + fileName;
		File file = new File(path);
		
		assertTrue(file.exists(), "File " + fileName + " does not exist.");
	}
	
	/**
	 * Confirms that the metaDataAry has been set by
	 * checking if the metaDataAry[2] contains the same
	 * information as the descipt varaible in this tester.
	 */
	@Test
	@Order(2)
	public void updateMetaAryTest() {
		metaInstance.updateDataMap();
		
		boolean bool = metaInstance.getDataMap().get("deck_descript").equals(descript);
		
		assertTrue(bool, "Descriptions did not match. Possible updateMetaAry error: " +
				"\n " + metaInstance.getDataMap().get("deck_descript") + " != " + descript);
	}
	
	@Test
	@Order(3)
	public void setDeckMetaFmFileTest() {
		String folder = DirectoryMgr.getMediaPath('t');
		String fileName = "testDeckMetaData.met";
		String path = folder + fileName;
		
		metaInstance.updateDataMap();
		FlashCardOps.getInstance().setMetaInFile(metaInstance, path);
		
		boolean failed = false;
		this.metaMapOne = metaInstance.getDataMap();
		this.metaMapTwo = getMetaFromFile(path);

		
		for(int i = 0; i < metaMapOne.size(); i++) {

			if( metaMapTwo.isEmpty()
					&& ! metaMapOne.get(keyAry[i]).equals(metaMapTwo.get(keyAry[i]))) {
				failed = true;
			}
		}
		
		assertTrue(failed == false, "The saved file is not correct.");
	}
	
	
	/**
	 * An intersting problem with Singletons. Apparently, calling getInstance does not
	 * necessarily mean you will have the same instance. ie. if we use getINstance to
	 * create a pointer, it does not point to the same instance that was created in initialization().
	 *
	 * This also causes problems with saving an instance of that class from within that
	 * singleton class. :/
	 */
	//@Test
	//@Order(4)
	public void thereIsReturnDataTest() {
		String folder = DirectoryMgr.getMediaPath('t');
		String fileName = "testDeckMetaData.met";
		String path = folder + fileName;
		
		DeckMetaData meta = DeckMetaData.getInstance();
		
		System.out.println();
		System.out.println("is meta the same object " + (meta == metaInstance));
		System.out.println("is meta object desicriptor holding data: " + meta.getDescript().equals(descript));
		System.out.println("is metaInstance descirptor holding data: " + metaInstance.getDescript().equals(descript));
		System.out.println(" descript: " + meta.getDescript());
		System.out.println();
		//	setTestMetaData(meta);
		meta.updateDataMap();
	//	this.metaAryOne = meta.getDeckMetaDataAry();
		
	//	meta.saveDeckMetaData("testDeckMetaData");
		FlashCardOps.getInstance().setMetaInFile(meta, path);
		
		
		boolean succeded = false;
	//	DeckMetaData.MetaObj metaObj = meta.getMetaObj();
		FlashCardOps.getInstance().setMetaObjFromFile(path);
		
	//	this.metaAryTwo =
		
	//	// deck description element
	//	succeded = metaAryTwo[2].equals(descript);
		
		System.out.println("MetaAryTwo length: " + metaMapTwo.size());
		
	//	for(int i = 0; i < metaAryTwo.length; i++) {
	//		System.out.println("one: <" + metaAryOne[i] + ">, two: <" + metaAryTwo[i] + ">");
	//	}
		
		
		
		assertTrue(succeded == true, "The descriptions were not the same. " +
				"Descript variable: <" + descript + ">, stored file returned: <" + metaMapTwo.get("deck_descript") + ">");
	}
	
	// If an error occurs, comment out DeckMetaModel FxNotify
	// And ensure that there is a DeckName provided. IE line 209ish in
	// save action of DeckMetaModel. Also, ensure that you are connected
	// to the internet.
	@Test
	@Order(5)
	public void saveAction_SavesDescriptors() {
		
		String folder = DirectoryMgr.getMediaPath('t');
		String deckName = "testDeckMetaData";

		FlashCardOps.getInstance().setDeckFileName(deckName);
		String path = folder + deckName + ".met";
		
		DeckMetaModel model = new DeckMetaModel();
	//	setTestMetaData(meta);
		model.doAction(metaInstance);
		
		this.metaMapTwo = getMetaFromFile(path);
		boolean succeded = false;
		succeded = metaMapTwo.get("descript").equals(descript);
		
		assertTrue(succeded == true, "The descriptions were not the same");
	}
	
	/**
	 * Builds MetaData programatically from file. File already has data.
	 *
	 */
	@Test
	@Order(6)
	public void updateInfo_DoesNotDeleteDescriptors() {
		
		// 1) upload data to meta
		setTestMetaData(metaInstance);
		
		// 2) ensure clean model
		DeckMetaModel model = new DeckMetaModel();
		
		// 3) upload descriptor to model
		// and save
		model.doAction(metaInstance);
		
		// 4) Save metaData to file
		String folder = DirectoryMgr.getMediaPath('t');
		String fileName = "testDeckMetaData.met";
		String path = folder + fileName;
		
		// 5) Set the data so that it exists with descriptors
		updateDeckInfo(metaInstance);
		
		// 6) Check if descriptors are deleted by
		// CreateFlash save action
		this.metaMapTwo = getMetaFromFile(path);
		
		boolean failed = ! metaMapTwo.get("deck_descript").equals("THis is a description") & (metaMapTwo.get("num_imgs").equals("5"));
		
		assertTrue(failed == false, "Data was changed");
	}
	
	/* ROBOT TESTER */
	
	@Test
	@Order(7)
	public void formSavesData() throws Exception {
		
		bobTheBot.robotSetup();
		
		// erase the files data
		// 4) Save metaData to file
		String folder = DirectoryMgr.getMediaPath('t');
		String metaFileName = FlashCardOps.getInstance().getMetaFileName();
		String path = folder + metaFileName;
		
		System.out.println("path and deckFileName: " + path);
		
		File metaFile = new File(path);
		if (metaFile.exists()) {
			metaFile.delete();
		}
		
		// click on create button
		Point2D xy = FlashMonkeyMain.getCreateButtonXY();
		bobTheBot.getBob().clickOn(xy);
		sleep(200);
		
		// click on sell/metaData button
		xy = CreateFlash.getInstance().getSellButtonXY();
		bobTheBot.getBob().clickOn(xy);
		sleep(300);
		
		
		
		// fill out form data
		CreateFlash cfp = CreateFlash.getInstance();
		xy = cfp.getFormMinXY();
		double x = xy.getX() + 150;
		double y = xy.getY() + 320;
		double val = 50;
		xy = new Point2D(x,y);
		
		System.out.println("\n\n XY: " + xy.getX() + ", " + xy.getY() + " \n\n");
		bobTheBot.getBob().clickOn(xy);
		write(descript);
		sleep(50);
		
		bobTheBot.getBob().clickOn(x, y += val);
		write(deckSchool);
		sleep(50);
		
		bobTheBot.getBob().clickOn(x, y += val);
		write(deckBook);
		sleep(50);
		
		bobTheBot.getBob().clickOn(x, y += val);
		write(deckProf);
		sleep(50);
		
		bobTheBot.getBob().clickOn(x, y += val);
		write(deckClass);
		sleep(50);
		
		bobTheBot.getBob().clickOn(x, y += val);
		write(subj);
		sleep(50);
		
		bobTheBot.getBob().clickOn(x, y += val);
		write(subjSubCat);
		sleep(50);
		
		bobTheBot.getBob().clickOn(x, y += val);
		write(lang);
		sleep(1000);
		
		// click on submit
		bobTheBot.getBob().clickOn(x, y + 75);
		//robot.type(KeyCode.ENTER);
		sleep(3000);
		
	//	check that metaData was saved
		metaInstance.close();
		metaInstance = DeckMetaData.getInstance();
		
		this.metaMapTwo = getMetaFromFile(path);
		boolean succeded = false;
		succeded =  metaMapTwo.get("deck_descript").equals(descript) &
					metaMapTwo.get("creator_email").equals(origAuthor);
		
		
	}
	
	/**
	 * Tests if the MetaDataPane Starts with data in the fields
	 */
	@Test
	@Order(8)
	public void fieldsHaveData() throws Exception {
		
		bobTheBot.robotSetup();
		
		// click on create button
		Point2D xy = FlashMonkeyMain.getCreateButtonXY();
		bobTheBot.getBob().clickOn(xy);
		sleep(4000);
		
		System.out.println("deckMetaFileName: " + FlashCardOps.getInstance().getMetaFileName());
		
		// click on sell/metaData button
		xy = CreateFlash.getInstance().getSellButtonXY();
		bobTheBot.getBob().clickOn(xy);
		sleep(3000);
		
		DeckMetaModel model = CreateFlash.getInstance().getMetaModelObj();
		String descript = model.getDescriptor().getDeckDescript();
		
		boolean bool = descript.equals(this.descript);
		
		/* UNPLUG THE ROBOT */
		tearDown();
		assertTrue(bool, "Descript field does not match ModelDescriptor");
	}
	
	
	
	/**
	 * Uses App methods, duplicates updateDeckInfo in CreateFlash.
	 * @param meta
	 */
	public void updateDeckInfo(DeckMetaData meta) {
		System.out.println("MetaDataTester.updateDeckInfo() called");
		
		//LOGGER.debug("before retrieveDEckMetaData(), meta array is: {}", meta.toString());
		
		meta.setDataFmFile();
		
		//LOGGER.debug("After retrieveDeckMetaData() from file, and before update, meta array is: {}", meta.toString());
		// reset deck meta to 0;
		//meta.reset();
		//FlashCardOps fcOps = FlashCardOps.getInstance();
		ArrayList<FlashCardMM> creatorList = getListFromFile();
		//startSize = creatorList.size();
		
		
		meta.setNumCard(creatorList.size() - 1);
		CreateFlash.inventoryMeta(creatorList, meta);
		// set metadata to array
		meta.updateDataMap();
		// save metadata to file
		//meta.saveDeckMetaData(ReadFlash.getInstance().getDeckName());
		FlashCardOps.getInstance().setMetaInFile(meta, "testDeckMetaData");
		//LOGGER.debug("After meta.saveDeckMetaData() meta array is: {}", meta.toString());
	}
	
	
	private void setTestMetaData(DeckMetaData meta1) {
		meta1.setLastDate(lastDate);
		meta1.setCreateDate(createDate);
		meta1.setCreatorEmail(origAuthor);
		meta1.setDeckFileName("testDeckMetaData");
		meta1.setDescript(descript);
		meta1.setNumImg(numImg);
		meta1.setNumVideo(numVideo);
		meta1.setNumAudio(numAudio);
		meta1.setNumSessions(numSessions);
		meta1.setDeckBook(deckBook);
		meta1.setDeckClass(deckClass);
		meta1.setDeckProf(deckProf);
		meta1.setDeckSchool(deckSchool);
		meta1.setSubj(subjSubCat);
		meta1.setLang(lang);
		meta1.setCourseCode(courseCode);
		meta1.setDeckPhotoURL(photoUrl);
		meta1.setPrice(price);
	}
	

	private ObjectInputStream input;
	private ObjectOutputStream output;
	/**
	 * <p>Outputs an array of Strings to file</p>
	 * @param deckMetaMap The String[] containing a single deck's
	 *                metaData. The file stored is named
	 *                from the DeckName and ends with .met
	 * @param deckNameOnly ..
	 */
	public void setMetaInFile(HashMap<String, String> deckMetaMap, String deckNameOnly) {
		//LOGGER.debug("setMetaInFile called");
		File folder = new File(DirectoryMgr.getMediaPath('t'));
		String metaFileName = deckNameOnly + ".met";
		//LOGGER.debug("filePath: <{}>", folder + "/" + metaFileName);
		
		try {

			System.out.println("MetaDataTester called with ObjectOutputStream()");
			Thread.dumpStack();

			output = new ObjectOutputStream(new BufferedOutputStream
					(new FileOutputStream(folder + "/" + metaFileName), 512));
			
			output.writeObject(deckMetaMap);

		}
		catch(EOFException e) {
			//System.out.println(e.getMessage() + "\n" + e.getStackTrace());
			//e.printStackTrace();
		}
		catch(FileNotFoundException e) {
			//LOGGER.warn(e.getMessage() + "\n" + e.getStackTrace());
			e.printStackTrace();
		}
		catch(IOException e) {
			//LOGGER.warn(e.getMessage() + "\n" + e.getStackTrace());
			e.printStackTrace();
		}
		catch(Exception e ) {
			//LOGGER.warn("Unknown Exception: " + "\n" + e.getStackTrace());
			e.printStackTrace();
		}
		finally {
			closeOutStream(output, null);
		}
	}
	
	/**
	 * @return Returns the flashCard list from the currently selected file
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<FlashCardMM> getListFromFile()
	{
		File folder = new File(DirectoryMgr.getMediaPath('t'));
		String filePath = folder + "/" + "History 2024.dec";
		File filePathName = new File(filePath);
		//LOGGER.info("calling getListFromFile, filePath: " + check.getPath() );
		//if( agrList.getLinkObj() != null)
		//{
		if(filePathName.exists()) {
			//LOGGER.debug("fileName exists");
			return connectBinaryIn(filePath);
		}
		else {
			//LOGGER.debug("fileName does not exist, creating new arrayList");
			return new ArrayList();
		}
	}
	
	/**
	 * Inputs from a local binary/serialized file to an ArrayList.
	 * @param fileName
	 * @return
	 */
	protected ArrayList<FlashCardMM> connectBinaryIn(String fileName) {
		
		ArrayList<FlashCardMM> flashListMM = new ArrayList();
		
		try {
			//new Thread(
			File folder = new File(DirectoryMgr.getMediaPath('t'));
			String filePath = folder + "/" + "History 2024.dec";
			FileInputStream fileIn = new FileInputStream(filePath);
			
			input = new ObjectInputStream(new BufferedInputStream(fileIn));
			flashListMM.clear();
			
			
			
			while(flashListMM.add((FlashCardMM)input.readObject()));
			
			
			
		} catch(ClassNotFoundException e) {
		
		
		
		} catch(EOFException e) {
			//LOGGER.warn("\tEnd of file Exception: connectBinaryIn() Line 1134 FlashCard");
			
		} catch(FileNotFoundException e) {
			//LOGGER.warn("\tFile Not Found exception: connectBinaryIn() Line 1134 FlashCard");
			// System.out.println(e.getMessage());
			//e.printStackTrace();
			
		} catch(IOException e) {
			//LOGGER.warn("\t****** IO Exception in FlashCard.connectBinaryIn() *******");
			//e.printStackTrace();
			
			
		} catch(Exception e) {
			// Set errorMsg in FlashMonkeyMain and let the ReadFlash class
			// handle the error in createReadScene()
			//        FlashMonkeyMain.setErrorMsg(" --   Oooph my bad!!!   --\n" +
			//                "The flash card deck you chose \n\"" + ReadFlash.getDeckName()
			//                + "\"\n will not work with this version of FlashMonkey" );
			
			//LOGGER.warn("\tUnknown Exception in connectBinaryIn: ");
			e.printStackTrace();
			
		} finally {
			closeInStream(input, null);
		}
		
		return flashListMM;
	}
	
	/**
	 * Returns a string[] from the file in the Param
	 * @param saveFile the full path name of the file to be
	 *                 retrieved.
	 * @return A String[] from the file in the Param.
	 */
	public HashMap<String, String> getMetaFromFile(String saveFile) {
		
		try {
			System.out.println("MetaDataTester.getMetaFromFile() called");
			FileInputStream fileIn = new FileInputStream(saveFile);
			
			input = new ObjectInputStream(new BufferedInputStream(fileIn));
			
			return (HashMap<String, String>) input.readObject();
		} catch (EOFException e) {
			// end of file exception. Do nothing. this is expected.
			
		} catch(FileNotFoundException e) {
			//LOGGER.warn("\tFile Not Found exception:  getObjFmFile() Line 986 FlashCard");
			//LOGGER.warn(e.getMessage());
			e.printStackTrace();
			
		} catch(IOException e) {
			//LOGGER.warn("\t****** IO Exception in FlashCard. getObjFmFile() *******");
			e.printStackTrace();
			
		} catch(Exception e) {
			
			//LOGGER.warn("\tUnknown Exception in getMetaFromFile: ");
			e.printStackTrace();
			
		} finally {
			closeInStream(input, null);
			//    return null;
		}
		return null;
	}
	
	//DESCRIPTION: THis method is used to close the output stream.
	private void closeOutStream(ObjectOutputStream closeMe, String message)
	{
		try
		{
			//System.out.println("closeOutStream() method in FlashCard ");
			closeMe.close();
		}
		catch(NullPointerException e)
		{
			if (message == null)
			{
				// do nothing
			}
			
			else
			{
				//System.out.println(message);
			}
		}
		catch(EOFException e)
		{
			System.exit(0);
		}
		catch(IOException e)
		{
			System.exit(0);
		}
	}
	
	/**
	 * Closes the input stream
	 * @param closeMe
	 * @param message
	 */
	private void closeInStream(ObjectInputStream closeMe, String message) {
		try {
			//System.out.println("closeInStream() method in FlashCard");
			closeMe.close();
		}
		catch(NullPointerException e)
		{
			if (message == null) {
				// do nothing
			}
			else {
				//System.out.println(message);
			}
		}
		catch(EOFException e) {

			System.exit(0);
		}
		catch(IOException e) {

			System.exit(0);
		}
	}
}
