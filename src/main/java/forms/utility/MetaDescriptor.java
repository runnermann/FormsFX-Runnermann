package forms.utility;

import authcrypt.UserData;
import campaign.db.DBFetchToMapAry;
import campaign.db.DBFetchUnique;
import fileops.DirectoryMgr;
import flashmonkey.ReadFlash;
import forms.Descriptor;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import metadata.DeckMetaData;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains the description data for the deck. Works with
 * the MetaDataModel and MetaDataPane. BrCodege between
 * Form and metadata.DeckMetaData.
 */
public class MetaDescriptor  implements Descriptor<DeckMetaData> {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MetaDescriptor.class);
	
	// The deck description
	private StringProperty deckDescript;
	private StringProperty deckSchool;
	private StringProperty deckBook;
	private StringProperty deckProf;
	private StringProperty deckClass;
	private StringProperty subjCat;
	private StringProperty subjSubCat;
	private StringProperty deckLanguage;
	private StringProperty courseCode;
	private StringProperty deckPhotURL;
	private IntegerProperty numCards;
	
	// Prevent multiple upload and downlaods
	// iF meta has been set, set to true.
	public boolean metaIsSet = false;
//	private DeckMetaData meta;
	
	// Three scenarios,
	//  - 1 the deck is just being created
	//  - 2 the deck exists locally and there is a
	//      local metaData File
	//  - 3 The deck exists but is not on this current
	//      users system
	//
	// Check if metadata exists in the DB
	// Check if metaData exists in a local file
	//  - 1. If not present anywhere
	//      create it from new with blank data
	//  - 2. If if exists in both locations compare
	//      each set and use the most recent version.
	//  - 3. Otherwise it only exists in one location.
	
	/**
	 * Constructor
	 */
	public MetaDescriptor() {
		super();
		//this.meta = new DeckMetaData();
		this.setMostRecent();
	}
	
	/* Deck Meta Data Form Entries */
	public String getDeckDescript() { return deckDescript.get(); }
	public String getDeckSchool() { return deckSchool.get(); }
	public String getDeckBook() { return deckBook.get();}
	public String getDeckProf() { return deckProf.get();}
	public String getDeckClass() { return deckClass.get();}
	public String getSubj() { return subjCat.get();}
	public String getSubjSubCat() {return subjSubCat.get();}
	public String getDeckLanguage() {return deckLanguage.get();}
	public String getCourseCode() {return courseCode.get();}
	public String getPhotoURL() {return deckPhotURL.get();}
	public Integer getNumCards() {return numCards.get();}
	
	/* deck metadata properties */
	public StringProperty deckDescriptProperty() {
		return deckDescript;
	}
	public StringProperty deckSchoolProperty() {
		return deckSchool;
	}
	public StringProperty deckBookProperty() {
		return deckBook;
	}
	public StringProperty deckProfProperty() {
		return deckProf;
	}
	public StringProperty deckClassProperty() {
		return deckClass;
	}
	public StringProperty deckSubjProperty() {
		return subjCat;
	}
	public StringProperty deckSubjSubCatProperty() {
		return subjSubCat;
	}
	public StringProperty deckLanguageProperty() {
		return deckLanguage;
	}
	public StringProperty courseCodeProperty() {return courseCode; }
	public StringProperty deckPhotoURLProperty() { return deckPhotURL;}
	
	
	
	@Override
	public void setProperitesDefault() {
		
		//System.out.println("setPropertiesDefault()");
		deckDescript = new SimpleStringProperty("");
		deckSchool = new SimpleStringProperty("");
		deckBook = new SimpleStringProperty("");
		deckProf = new SimpleStringProperty("");
		deckClass = new SimpleStringProperty("");
		subjCat = new SimpleStringProperty("");
		subjSubCat = new SimpleStringProperty("");
		deckLanguage = new SimpleStringProperty("");
		courseCode = new SimpleStringProperty("");
		deckPhotURL = new SimpleStringProperty("");
		numCards = new SimpleIntegerProperty(0);
	}
	
	/**
	 * Sets the variable properties for this
	 * descriptor
	 * @param m
	 */
	@Override
	public void setProperties(final DeckMetaData m) {
		
		//System.out.println("Called setProperties() \n Printing meta: " + m.toString());
		
		deckDescript = new SimpleStringProperty(m.getDescript());
		deckSchool = new SimpleStringProperty(m.getDeckSchool());
		deckBook = new SimpleStringProperty(m.getDeckBook());
		deckProf = new SimpleStringProperty(m.getDeckProf());
		deckClass = new SimpleStringProperty(m.getDeckClass());
		subjCat = new SimpleStringProperty(m.getSubj());
		subjSubCat = new SimpleStringProperty(m.getCat());
		deckLanguage = new SimpleStringProperty(m.getLang());
		courseCode = new SimpleStringProperty(m.getCourseCode());
		deckPhotURL = new SimpleStringProperty(m.getDeckPhotoURL());
		numCards = new SimpleIntegerProperty(Integer.parseInt(m.getNumCard()));
	}
	
	
	/**
	 * Compares the DB version with local version using
	 * date. Sets the data to the most recent version.
	 */
	@Override
	public void setMostRecent() {
		//System.out.println("called setMostRecent()");
		DeckMetaData meta = DeckMetaData.getInstance();
		long localDate = getLocalDataDate();
		
		// Sets the meta object to the DB if
		// it exists && the user is connected.
		if(fileops.Utility.isConnected()) {
			setToRemoteData() ;
		}
		
		long remoteDate = meta.getLastDate() * -1;
		long num = remoteDate + localDate;
		
		System.out.println("remoteDate: " + remoteDate + " localDate " + localDate);
		// If this files metadata does not exist yet
		// chose to set from default.
		if(num + localDate == 0) {
			//System.out.print("Chose default");
			setProperitesDefault();
		}
		// If the num value is greater, then chose local
		else if(num > 0 ) {
			setToLocalData();
			//System.out.println("Chose local ");
			setProperties(meta);
			
		}
		// Else the db has the most recent version.
		else  { // (num < 0)
			// meta is already set to the DB
			//System.out.println("Chose remote ");
			setProperties(meta);
		}
		
		//System.out.println(); // clear buffer
	}
	
	
	/**
	 *
	 * @return the timeStamp in millis of a local meta file
	 * 	 * if exists. If not returns 0.
	 */
	@Override
	public long getLocalDataDate() {
		String folder = DirectoryMgr.getMediaPath('t');
		String metaFileName = ReadFlash.getInstance().getDeckName() + ".met";
		File check = new File(folder + "/" + metaFileName);
		
		if(check.exists()) {
			return check.lastModified();
		}
		return 0;
	}
	
	
	/**
	 * Sets the metaData object to the local file
	 * for this object if it exists and updates
	 * the Data Array. Otherwise leaves
	 * the meta object as is.
	 */
	@Override
	public void setToLocalData() {
		String folder = DirectoryMgr.getMediaPath('t');
		String metaFileName = ReadFlash.getInstance().getDeckName() + ".met";
		File check = new File(folder + "/" + metaFileName);
		// if the file exists and has not been
		// retrieved from file previously for this object.
		if(check.exists() && !metaIsSet) {
			// retrieve the local file and set it
			// to the metadata object.
			DeckMetaData meta = DeckMetaData.getInstance();
			//System.out.println("meatDescriptor getLocalMeta and the metaIsSet flag is NOT set");
			meta.setDataFmFile();
			meta.updateDataMap();
		} else {
			LOGGER.warn("metaDescriptor getLocalMeta and the metaIsSet flag is set or file does not exist");
		}
	}
	
	/**
	 * Fetches this decks metaData from the database if it
	 * exists, and sets the meta object to it. Otherwise leaves
	 * it as is.
	 */
	@Override
	public void setToRemoteData() {
		UserData user = new UserData();
		// get meta from DB
		ArrayList<HashMap<String, String>> response;// = new String[1];
		String args = "WHERE user_email = '" + Alphabet.encrypt(user.getUserName()) +
						"' AND deck_name = '" + ReadFlash.getInstance().getDeckName() + "'";
		//response = Report.getInstance().queryGetDeckMetaData(user.getUserName(), ReadFlash.getInstance().getDeckName());
		response = DBFetchToMapAry.DECKS_METADATA_QUERY.query(args);

		if(response.get(0).get("user_email") != null) {
			LOGGER.debug("response from DB length is GREATER than 10... processing response.");
			// Parse the response, set metaAry,
			// & set the DeckMetaData object.
			DeckMetaData meta = DeckMetaData.getInstance();
			//String[] metaStr = parseResponse(response);
			meta.setDataMap(response.get(0));
			meta.set(response.get(0));
		} else {
			LOGGER.debug("response from DB length is LESS than 10... NOT processing response.");
		}
	}
	
	/**
	 * Parses the String provided in the param
	 * using the ",".
	 * @param response
	 * @return An array containing the elements of the
	 * response provided in the param.
	 */
	//@Override
	//public String[] parseResponse(String[] response) {

	//	System.out.println("response: " + response);
/*
		for(String s : response) {
			System.out.println(s);
		}
		System.out.println("\n");



		// First element
		String orig_email = Alphabet.decrypt(response[0]);
*/
		// split the remainder into words
		//String[] fmSplit = sub.split(",");
		//String[] finalAry = new String[fmSplit.length + 1];

		// parse loop and add to finalAry
		//for(int i = 0; i < fmSplit.length; i++) {
		//	finalAry[i] = fmSplit[i ].strip();
		//	System.out.println("\t" + i + ") " + finalAry[i]);
		//}

		// add orig_email to the end of the array
		//finalAry[fmSplit.length] = orig_email;

		//System.out.println("Metadata.parseResponse() orig_email: " + orig_email);

		//System.out.println("\n\nresult finalAry");
		//for(String s : finalAry){
		//	System.out.println(s);
		//}
		//System.out.println();

		//return finalAry;
	//}
	
	
	
	/**
	 * Clears the form and sets to default
	 * entries.
	 */
	@Override
	public void clear() {
		
		deckDescript.setValue("");
		deckBook.setValue("");
		deckClass.setValue("");
		deckLanguage.setValue("English");
		deckProf.setValue("");
		deckSchool.setValue("");
		subjCat.setValue("");
		subjSubCat.setValue("");
		courseCode.setValue("");
		deckPhotURL.setValue("");
	}
	
	
	
	/**
	 * Ensure that the query contains an existing id!
	 * @param args
	 */
	public static void main(String[] args) {
		//Report rep = Report.getInstance();
		//String  response = rep.queryGetDeckMetaData("idk@idk.com", "History 2054");
		
		//System.out.println(response);
		
		DeckMetaData meta = DeckMetaData.getInstance();
		
		DeckMetaData otherMeta = DeckMetaData.getInstance();
		
		System.out.println("meta == otherMeta " + (meta == otherMeta));
	}
}
