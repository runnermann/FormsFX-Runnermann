package forms.utility;

import authcrypt.UserData;
import campaign.db.DBFetchToMapAry;
import fileops.DirectoryMgr;
import fileops.utility.Utility;
import flashmonkey.FlashCardOps;
import forms.Descriptor;
import forms.searchables.CSVUtil;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import metadata.DeckMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains the description data for the deck. Works with
 * the MetaDataModel and MetaDataPane. BrCodege between
 * Form and metadata.DeckMetaData.
 */
public class MetaDescriptor implements Descriptor<DeckMetaData> {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MetaDescriptor.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(MetaDescriptor.class);

      // The deck description
      private final StringProperty price = new SimpleStringProperty("0");
      private final StringProperty numStars = new SimpleStringProperty("0");
      private final StringProperty deckDescript = new SimpleStringProperty("");
      private final StringProperty deckSchool = new SimpleStringProperty("");
      private final StringProperty deckBook = new SimpleStringProperty("");
      private final StringProperty deckProf = new SimpleStringProperty("");
      private final StringProperty deckClass = new SimpleStringProperty("");
      private final StringProperty subjCat = new SimpleStringProperty("");
      private final StringProperty subjSubCat = new SimpleStringProperty("");
      private final StringProperty deckLanguage = new SimpleStringProperty("");
      private final StringProperty courseCode = new SimpleStringProperty("");
      private final StringProperty deckImgNameProperty = new SimpleStringProperty("");
      private final IntegerProperty numCards = new SimpleIntegerProperty(0);
      // slider switches in deckMetaPane
      private final BooleanProperty shareDeck = new SimpleBooleanProperty(false);
      private final BooleanProperty sellDeck = new SimpleBooleanProperty(false);
      // Institutions - Professors - Course Codes
      private ObjectProperty<CSVUtil.SchoolObj> selectedTut;
      private ListProperty<CSVUtil.SchoolObj> allTuts;


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
      //  - 1. If not exists anywhere
      //      create it from new with blank data
      //  - 2. If if exists in both locations compare
      //      each set and use the most recent version.
      //  - 3. Otherwise it only exists in one location.

      /**
       * Constructor
       */
      public MetaDescriptor() {
            super();
            LOGGER.debug("Calling MetaDescriptor Constructor");
            //LOGGER.setLevel(Level.DEBUG);
            this.setMostRecent();
      }


      /* MetaData from Form Entries */
      // Note price is a string because of issues with
      // form.
      public String getPrice() {
            return price.get();
      }

      public String getNumStars() {
            return numStars.get();
      }

      public String getDeckDescript() {
            return deckDescript.get();
      }

      public String getDeckSchool() {
            return deckSchool.get();
      }

      public String getDeckBook() {
            return deckBook.get();
      }

      public String getDeckProf() {
            return deckProf.get();
      }

      public String getDeckClass() {
            return deckClass.get();
      }

      public String getSubj() {
            return subjCat.get();
      }

      public String getSubjSubCat() {
            return subjSubCat.get();
      }

      public String getDeckLanguage() {
            return deckLanguage.get();
      }

      public String getCourseCode() {
            return courseCode.get();
      }

      public String getDeckImgName() {
            return deckImgNameProperty.get();
      }

      public void setDeckImgNameProperty(String imgName) {
            deckImgNameProperty.set(imgName);
      }

      public Integer getNumCards() {
            return numCards.get();
      }

      // slider switches
      public Boolean getShareDeck() {
            return shareDeck.get();
      }

      public Boolean getSellDeck() {
            return sellDeck.get();
      }

      // institute university / college
      public ListProperty<CSVUtil.SchoolObj> tutsProperty() {
            return allTuts;
      }

      public ObjectProperty<CSVUtil.SchoolObj> selectedTutProperty() {
            return selectedTut;
      }


      public CSVUtil.SchoolObj getSelectedTut() {
            return selectedTut.get();
      }

      public void setSelectedTut(String school) {
            this.selectedTut.set(new CSVUtil.SchoolObj(school, "", ""));
      }

      public ObservableList<CSVUtil.SchoolObj> getAllTuts() {
            return allTuts.get();
      }

      public void setAllTuts(ObservableList<CSVUtil.SchoolObj> allTuts) {
            this.allTuts.set(allTuts);
      }


      /* metadata properties */
      public StringProperty priceProperty() {
            return price;
      }

      public StringProperty numStarsProperty() {
            return numStars;
      }

      public StringProperty deckDescriptProperty() {
            return deckDescript;
      }

      public StringProperty deckSchoolProperty() {
            return new SimpleStringProperty(selectedTut.getValue().getName());
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

      public StringProperty courseCodeProperty() {
            return courseCode;
      }

      public StringProperty deckPhotoNameProperty() {
            return deckImgNameProperty;
      }

      public BooleanProperty shareDeckProperty() {
            return shareDeck;
      }

      public BooleanProperty sellDeckProperty() {
            return sellDeck;
      }


      @Override
      public void setProperitesDefault() {

            LOGGER.debug("setPropertiesDefault()");
            // Institution selector
            allTuts = new SimpleListProperty<>(FXCollections.observableArrayList());
            allTuts.setAll(FlashCardOps.getInstance().getSchoolObjs());
            selectedTut = new SimpleObjectProperty<>();

      }

      /**
       * Sets the variable properties for this
       * descriptor. Used at intialization of form to populate data
       * with existing data.
       *
       * @param m
       */
      @Override
      public void setProperties(final DeckMetaData m) {
            LOGGER.debug("Called setProperties() \n Printing meta: " + m.toString());

            deckDescript.set(m.getDescript());
            deckSchool.set(m.getDeckSchool());
            deckBook.set(m.getDeckBook());
            deckProf.set(m.getDeckProf());
            deckClass.set(m.getDeckClass());
            subjCat.set(m.getSubj());
            subjSubCat.set(m.getCat());
            deckLanguage.set(m.getLang());
            courseCode.set(m.getCourseCode());
            deckImgNameProperty.set(m.getDeckPhotoName());
            numCards.set(Integer.parseInt(m.getNumCard()));
            price.set(m.getPrice());
            numStars.set(m.getNumStars());
            shareDeck.set(m.isShareDistro());
            sellDeck.set(m.isSellDeck());
            allTuts = new SimpleListProperty<>(FXCollections.observableArrayList());
            allTuts.setAll(FlashCardOps.getInstance().getSchoolObjs());
            selectedTut = new SimpleObjectProperty<>();
            // selectedTut.setValue(new CSVUtil.SchoolObj(m.getDeckSchool(), "", ""));
      }


      /**
       * Compares the DB version with local version using
       * date. Sets the data to the most recent version.
       */
      @Override
      public void setMostRecent() {
            //LOGGER.debug("called setMostRecent()");
            DeckMetaData meta = DeckMetaData.getInstance();
            long localDate = getLocalDataDate();
            // Sets the meta object to the DB if
            // it exists && the user is connected.
            if (Utility.isConnected()) {
                  setToRemoteData(); // returns a boolean if needed.
            }
            long remoteDate = meta.getLastDate() * -1;
            long num = remoteDate + localDate;

            LOGGER.debug("\tremoteDate: " + remoteDate + " localDate " + localDate);
            // If this files metadata does not exist yet
            // chose to set from default.
            if (num + localDate == 0) {
                  setProperitesDefault();
            }
            // If the num value is greater, then chose local
            else if (num > 0) {
                  setToLocalData();
                  //setProperties(meta);
            }
            // Else the db has the most recent version and data is already set.
            setProperties(meta);
            //}

            //System.out.println(); // clear buffer
      }


      /**
       * @return the timeStamp in millis of a local meta file
       * * if exists. If not returns 0.
       */
      @Override
      public long getLocalDataDate() {
            String folder = DirectoryMgr.getMediaPath('t');
            String deckStr = FlashCardOps.getInstance().getDeckLabelName();
            String metaFileName = deckStr + ".met";
            File check = new File(folder + "/" + metaFileName);

            if (check.exists()) {
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
            String deckStr = FlashCardOps.getInstance().getDeckLabelName();
            String metaFileName = deckStr + ".met";
            File check = new File(folder + "/" + metaFileName);
            // if the file exists and has not been
            // retrieved from file previously for this object.
            if (check.exists() && !metaIsSet) {
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
      public boolean setToRemoteData() {
            //	UserData user = new UserData();
            // get meta from DB
            ArrayList<HashMap<String, String>> response;// = new String[1];
            String args = "WHERE user_email = '" + Alphabet.encrypt(UserData.getUserName()) +
                "' AND deck_name = '" + FlashCardOps.getInstance().getDeckLabelName() + "'";

            response = DBFetchToMapAry.DECK_METADATA_SINGLE.query(args);

            LOGGER.debug("response, num Array Elements: {}", response.size());

            if (response == null || response.get(0).get("empty").equals("true")) {
                  LOGGER.debug("response from DB is empty... NOT processing response.");
                  return false;
            } else {
                  LOGGER.debug("response from DB length is not empty... processing response.");
                  // Parse the response, set metaAry,
                  // & set the DeckMetaData object.
                  DeckMetaData meta = DeckMetaData.getInstance();
                  meta.setDataMap(response.get(0));

                  meta.set(response.get(0));
                  return true;
            }
      }

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
            deckImgNameProperty.setValue("");
            price.setValue("0");
            numStars.setValue("0");
            sellDeck.setValue(false);
            shareDeck.setValue(false);
      }


      /**
       * Ensure that the query contains an existing id!
       * @param args
       */
	/*public static void main(String[] args) {
		DeckMetaData meta = DeckMetaData.getInstance();
		DeckMetaData otherMeta = DeckMetaData.getInstance();
		System.out.println("meta == otherMeta " + (meta == otherMeta));
	}*/
}
