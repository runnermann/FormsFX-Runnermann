package metadata;

import authcrypt.UserData;
import ch.qos.logback.classic.Level;
import fileops.DirectoryMgr;
//import flashmonkey.CreateFlash;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import flashmonkey.ReadFlash;
import forms.FormData;
import forms.utility.Alphabet;
import org.slf4j.LoggerFactory;
import type.testtypes.TestList;

import java.io.File;
import java.io.Serializable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Contains the data describing a deck. And contains the methods to store
 * and retrieve locally and in the database.</p>
 *
 * <p>Singleton Class with close. Close when not in use, Close previous session
 * when switching between decks or to reset variables and objects.</p>
 *
 * <p><b>CAUTION:</b> New objects of a class are created when they are imported
 * from a file. Being a Singleton does not change this. A DeckMetaData Object is
 * stored as files and imported when data is changed. </p>
 *
 * @TODO Remove DataAry and related methods. Adds extra effort when there are changes
 */
public class DeckMetaData implements FormData {
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckMetaData.class);
	/* VERSION */
	public static final long VERSION = FlashMonkeyMain.VERSION;
	/* SINGLETON */
	private volatile static DeckMetaData CLASS_INSTANCE = null;
	/* OTHER */
	private int deckTestInt;

	
	// flag
	private static boolean fileExists;
	// This decks metaData
	private HashMap<String, String> deckMetaDataMap;

		private static final ArrayList<String> SCORES = new ArrayList<>(1);
		
		private long lastDate = 0;
		private String createDate;
		private String creatorEmail;
		private String deckFileName;
		private String descript;
		private String deckImgName;
		private int numImg;
		private int numVideo;
		private int numAudio;
		private int numCard;
		private int numSessions;
		private String deckBook;
		private String deckClass;
		private String deckProf;
		private String deckSchool;
		private String subj;
		private String cat;
		private String lang;
		private String lastScore;
		private String testTypes;
		private String courseCode;
		private String deckPhotoURL;


		
//		public MetaObj() {
			/* no args constructor */
//		}
		
		public void initMetaObj() {
			//System.out.println("Constructor called in DeckMetaData");
			//exists = true;
			lastDate = System.currentTimeMillis();
			createDate = ""; // the beginning of time
			creatorEmail = "";
			deckFileName = "";
			descript = "";
			deckImgName = "";
			numImg = 0;
			numVideo = 0;
			numAudio = 0;
			numCard = 0;
			numSessions = 0;
			deckBook = "";
			deckClass = "";
			deckProf = "";
			deckSchool = "";
			subj = "";
			cat = "";
			lang = "";
			testTypes = "90210";
			//scores.add(0, new ScoreNode());
			lastScore = "";
			courseCode = "";
			deckPhotoURL = "";
			
			// set flag
			fileExists = false;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(" lastDate: " + lastDate +
					"\n createDate: " + createDate +
					"\n creatorEmail: " + creatorEmail +
					"\n deckFileName: " + deckFileName +
					"\n descript: " + descript +
					"\n deckImgName: " + deckImgName +
					"\n numImg: " + numImg +
					"\n numVideo: " + numVideo +
					"\n numAudio: " + numAudio +
					"\n numSessions: " + numSessions +
					"\n deckBook: " + deckBook +
					"\n deckClass: " + deckClass +
					"\n deckProf: " + deckProf +
					"\n deckSchool: " + deckSchool +
					"\n subj: " + subj +
					"\n cat: " + cat +
					"\n lang: " + lang +
					"\n lastScore: " + lastScore +
					"\n testTypes: " + testTypes +
					"\n scores: " +
					"\n courseCode: " + courseCode +
					"\n deckPhotoURL: " + deckPhotoURL);
			if(SCORES.size() > 0) {
				for (String s : SCORES) {
					sb.append(s);
				}
			} else {
				sb.append("0/0/0");
			}
			return sb.toString();
		}
	
	
	// ************ METHODS ****************
	
	/**
	 * No args Constructor. Initializes
	 * this object to default values.
	 */
	private DeckMetaData() {
		init();
	}
	
	// Double-checked locking for singleton class
	public static synchronized DeckMetaData getInstance() {
		
		LOGGER.setLevel(Level.DEBUG);
		if(CLASS_INSTANCE == null) {
			synchronized (DeckMetaData.class) {
				if (CLASS_INSTANCE == null) {
					CLASS_INSTANCE = new DeckMetaData();
				}
			}
		}
		return CLASS_INSTANCE;
	}
	
	
	
	@Override
	public void init() {
		//metaObj = new MetaObj();
		initMetaObj();
		deckMetaDataMap = new HashMap<String, String>();
		// set flag
		fileExists = false;
		
		//LOGGER.debug("init() called, metaObj should exist? {}", metaObj != null);
	}
	
	@Override
	public boolean set() {
		
		System.out.println("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());
		//int count = 0;
		//for(String s : str) {
		//	System.out.println(count++ + ") " + s);
		//}
		//System.out.println();
		
		boolean dateIsZero = Long.parseLong(deckMetaDataMap.get("last_date").trim()) == 0;
		boolean descriptIsEmpty = deckMetaDataMap.get("deck_descript").isEmpty();
		if( dateIsZero || descriptIsEmpty ) {
			return false;
		}
		set(this.deckMetaDataMap);
		return true;
	}
	
	/**
	 * Sets this instances variables from the parsed DB response
	 * @param map The Parsed DB response or file.
	 */
	@Override
	public void set(HashMap<String, String> map) {
		
		System.out.println("DeckMetaData.set(metaDataAry) called ");
		//System.out.println("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());
		
		System.out.println();
		// remove first character which is a "["
		setLastDate(Long.parseLong(map.get("last_date").trim().substring(1)));
		//setCreateDate(str[1]);
		setDescript(map.get("deck_descript"));
		setDeckSchool(map.get("deck_school"));
		setDeckBook(map.get("deck_book"));
		setDeckClass(map.get("deck_class"));
		setDeckProf(map.get("deck_prof"));
		setSubj(map.get("subj"));
		setCat(map.get("section"));
		setLang(map.get("deck_language"));
		setLastScore(map.get("session_score"));
		setCreatorEmail(Alphabet.decrypt(map.get("creator_email")));
	}
	
	
	/**
	 * Sets this class instance to null
	 */
	@Override
	public void close() {
		//this.scores = null;
		CLASS_INSTANCE = null;
	}
	
	/**
	 * Sets this dataMap metaDataAry to the array in the param.
	 * @param dataMap
	 */
	@Override
	public void setDataMap(HashMap<String, String> dataMap) {
		if(deckMetaDataMap.get("creator_email").equals("")) {
			LOGGER.warn("WARNING: deckMetaDataMap has not been set");
		//	this.deckMetaDataAry = null;
		//	System.exit(0);
		} else {
			this.deckMetaDataMap = dataMap;
		}
	}
	
	@Override
	public HashMap<String, String> getDataMap() {
		return this.deckMetaDataMap;
	}
	
	/**
	 * Creates a String[] of metaData of the current deck.
	 * To include the recent test score, update the testNode
	 * with the latest test before using this method.
	 */
	@Override
	public void updateDataMap() {
		LOGGER.debug("updateMetaData() called");
		//System.out.println("DeckMetaData updateMetaAry() called.");
	//	LOGGER.debug("is metaObj null: {}", this.metaObj == null);
	//	LOGGER.debug("this.metaObj: {}", this.metaObj.toString());
		
		deckMetaDataMap.put("last_date", Long.toString(this.lastDate));
		deckMetaDataMap.put("deck_descript", getDescript());
		deckMetaDataMap.put("creator_email", getCreatorEmail());
		deckMetaDataMap.put("user_email", UserData.getUserName());
		deckMetaDataMap.put("deck_school", getDeckSchool());
		deckMetaDataMap.put("deck_book", getDeckBook());
		deckMetaDataMap.put("deck_class", getDeckClass());
		deckMetaDataMap.put("deck_prof", getDeckProf());
		deckMetaDataMap.put("subj", getSubj());
		deckMetaDataMap.put("section", getCat());
		deckMetaDataMap.put("deck_language", getLang());
		deckMetaDataMap.put("deck_name", ReadFlash.getInstance().getDeckName());
		deckMetaDataMap.put("test_types", Integer.toString(deckTestInt));
		deckMetaDataMap.put("num_cards", getNumCard());
		deckMetaDataMap.put("num_imgs", Integer.toString(numImg));
		deckMetaDataMap.put("num_video", Integer.toString(numVideo));
		deckMetaDataMap.put("num_audio", Integer.toString(numAudio));
		deckMetaDataMap.put("session_score", calcLastScore());
		deckMetaDataMap.put("deck_photoid", getDeckPhotoURL());
		deckMetaDataMap.put("course_code", getCourseCode());
		
		LOGGER.debug("showing lastDate: {}", lastDate);
	}

	/**
	 * Sets this decks metadata stored in a file to
	 * this object
	 */
//	@Override
	public boolean setDataFmFile() {
		String folder = DirectoryMgr.getMediaPath('t');
		String fileName = ReadFlash.getInstance().getDeckName() + ".met";
		String path = folder + fileName;
		File file = new File(path);

		LOGGER.debug("setDataFmFile() called");
		LOGGER.debug("filePathName: <{}>", path);

		if(file.exists()) {
			FlashCardOps.getInstance().getFO().setMetaObjFromFile(path);
			//set();
			return true;
		}
		return false;
	}
	
	/**
	 * Fetches the decks metadata from the DB
	 * if it exists
	 * @return Returns the string containing the
	 * query results. Else returns "-99";
	 */
//	public String fetchDeckMetaData() {
//
//	}

	/**
	 * Saves the metaData stored in this object
	 * to file. Do not include ".met".
	 */
	
	// SEEMS TO CREATE ISSUES WITH SERIALIZATION AND
	// A SINGLETON OBJECT. WHEN SINGLETON OBJECTS
	// ARE DESERIALIZED, NEW OBJECTS ARE CREATED, THUS
	// YOU HAVE TWO SINGLETONS.
	
	// THIS SEEMS TO BE AN ISSUE WITH SAVING FROM WITHIN
	// THE CLASS.
	
//	public void saveDeckMetaData(String deckName) {
//		System.out.println("saveDeckMetaData called: ");
//		//System.out.println(this.arrayToString());
//		FlashCardOps.getInstance().getFO().setMetaInFile(this.deckMetaDataAry, deckName);
//	}
	
	/**
	 * Increments the count for the media based on the
	 * media type. IE Video, image, or audio.
	 * May need to use reset() prior to counting an
	 * inventory.
	 * @param fileName
	 */
	public void mediaCounter(String fileName) {
		//System.out.println("called metaData.mediaCounter()");

		if(fileName == null || fileName.isEmpty()) {
			return;
		}

		int num = fileName.lastIndexOf('.');
		String ending = fileName.substring(num);
		//System.out.println("ending: " + ending);

		switch(ending) {
			case ".JPG":
			case ".jpg":
			case ".PNG":
			case ".png": {
				imgPlus();
				break;
			}
			case ".flv":
			case ".MPEG-4":
			case ".mpeg":
			case ".mp4":
			case ".mv4": {
				vidPlus();
				break;
			}
			case "wav":
			case "mp3": {
				audPlus();
				break;
			}
		}
	}

	private void imgPlus() {
		this.numImg++;
	}

	private void vidPlus() {
		this.numVideo++;
	}

	private void audPlus() {
		this.numAudio++;
	}

	public void reset() {
		this.numAudio = 0;
		this.numVideo = 0;
		this.numImg = 0;
		this.testTypes = "90120";
	}

	public int getNumSessions() {
		return this.numSessions;
	}
	
	public String getTestTypes() {
		return this.testTypes;
	}

	public void setNumSessions(int numSessions) {
		this.numSessions = numSessions;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String description) {
		this.descript = description;
	}

	public String getDeckImgName() {
		return deckImgName;
	}

	public void setDeckImgName(String deckImgName) {
		this.deckImgName = deckImgName;
	}

	public String getDeckBook() {
		return deckBook;
	}

	public void setDeckBook(String deckBook) {
		this.deckBook = deckBook;
	}

	public String getDeckClass() {
		return deckClass;
	}

	public void setDeckClass(String deckClass) {
		this.deckClass = deckClass;
	}

	public String getDeckProf() {
		return this.deckProf;
	}

	public void setDeckProf(String deckProf) {
		this.deckProf = deckProf;
	}

	public String getSubj() {
		return this.subj;
	}

	public void setSubj(String subj) {
		this.subj = subj;
	}

	public String getCat() {
		return this.cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}

	public String getLang() {
		return this.lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getDeckPhotoURL() {
		return this.deckPhotoURL;
	}

	public void setDeckPhotoURL(String photoURL) {
		deckPhotoURL = photoURL;
	}

	public String getCourseCode() {
		return this.courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	/**
	 * Clears the current local array, and sets scores to the
	 * scores set in the parameter.
	 *
	 * @param scores
	 */
	public void setScores(ArrayList<String> scores) {
		if(SCORES.size() > 1) {
			SCORES.clear();
			SCORES.addAll(scores);
		}
	}


	/**
	 * Converts the deckTestInt to a String[] of the
	 * testTypes that this deck contains.
	 * @return a String[] containing the test types by name.
	 */
	public String[] readTestTypes() {
		ArrayList<String> testTypes = new ArrayList<>();
		for(int i = 0, idx = 0; i < (TestList.TEST_TYPES.length - 1); i++) {
			if(((deckTestInt >> i) & 1) == 1) { testTypes.set(idx++ , TestList.TEST_TYPES[i].getName()); }
		}

		return (String[]) testTypes.toArray();
	}

	/**
	 * Counts and adds the test types for report.
	 * @param cardTest
	 */
	public void addTestType(int cardTest) {
        deckTestInt |= cardTest;
	}

	public String getNumImg() {
		return Integer.toString(this.numImg);
	}

	public void setNumImg(int numImg) {
		this.numImg = numImg;
	}

	public String getNumVideo() {
		return Integer.toString(this.numVideo);
	}

	public void setNumVideo(int numVideo) {
		this.numVideo = numVideo;
	}

	public String getNumAudio() {
		return Integer.toString(this.numAudio);
	}

	public void setNumAudio(int numAudio) {
		this.numAudio = numAudio;
	}

	public String getNumCard() {
		return Integer.toString(this.numCard);
	}

	public void setNumCard(int numCard) {
		this.numCard = numCard;
	}

	public final ArrayList<String> getScores() {
		return SCORES;
	}

	public String calcLastScore() {
		if(SCORES.size() < 1) {
			addTestData(0, 0);
		}
		return SCORES.get(SCORES.size() - 1);
	}

	public String getLastScore() { return this.lastScore; }

	public String getDeckSchool() {
		return this.deckSchool;
	}

	public void setDeckSchool(String deckSchool) {
		this.deckSchool = deckSchool;
	}

	public void setDeckFileName(String deckFileName) {
		if(deckFileName != null && deckFileName.length() > 0) {
			this.deckFileName = deckFileName;
		}
	}

	public String getDeckFileName() {
		return this.deckFileName;
	}
	
	@Override
	public long getLastDate() {
		return this.lastDate;
	}
	
	public void setLastDate(long lastDate) {
		this.lastDate = lastDate;
	}
	
	public void setLastScore(String lastScore) { this.lastScore = lastScore; }

	public String getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreatorEmail() {
		return this.creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	// OTHER METHODS

	

	public void addTestData(int numAns, int score) {
		SCORES.add(new ScoreNode(this.numCard, numAns, score).getScore());
	}
	

	// ******* INNER CLASS ******** //

	public class ScoreNode implements Serializable {
		
		/* VERSION */
		public static final long VERSION = FlashMonkeyMain.VERSION;
		
		long dateInMillis = 0;
		int numQs = 0;
		int numAns = 0;
		int score = 0;

		/**
		 * A node containing user scores and date.
		 * Does not provide a score, rather provides
		 * data about the numbers of a taken test.
		 * @param numQs
		 * @param numAns
		 * @param score
		 */
		ScoreNode( int numQs, int numAns, int score) {
			this.dateInMillis = System.currentTimeMillis();
			this.numQs = numQs;
			this.numAns = numAns;
			this.score = score;
		}

		ScoreNode getScoreNode() {
			return this;
		}

		double getDateInMillis() {
			return this.dateInMillis;
		}

		/**
		 * @return Returns a string containing the
		 * 		 numbRight, numAnswered, the numQuestions
		 * 		 in a deck, and the date taken.
		 */
		public String getScore() {
			LocalDateTime ldt = Instant.ofEpochMilli(dateInMillis)
					.atZone(ZoneId.systemDefault()).toLocalDateTime();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

			String dtg = ldt.format(formatter);

			return score + "/" + numAns + "/" + numQs + "  " + dtg;
		}
	}
}
