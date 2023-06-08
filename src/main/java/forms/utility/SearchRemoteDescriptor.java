package forms.utility;

import campaign.db.DBFetchMulti;
import com.dlsc.formsfx.model.structure.Section;
import flashmonkey.FlashCardOps;
import forms.FormData;
import forms.searchables.CSVUtil;
import forms.Descriptor;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Descriptor used by DeckSearchModel. Searches DB for
 * decks by criteria set in form.
 * Uses MetaDescriptor, but we are not interested in any local decks
 * with this search thus we do not call Super(). super() will set deck
 * data to local users current deck.
 */
public class SearchRemoteDescriptor implements FormData, Descriptor<SearchRemoteDescriptor> {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SearchRemoteDescriptor.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(SearchRemoteDescriptor.class);

      // A person
      private StringProperty friend; // author
      // The deck's id
      private StringProperty deckKey;
      // Not later than date
      private LongProperty nltDate;
      // Not earlier than date
      private LongProperty netDate;
      // Institutions - Professors - Course Codes
      private ObjectProperty<CSVUtil.SchoolObj> selectedTut;
      private ListProperty<CSVUtil.SchoolObj> allTuts;
      private ListProperty<String> professors;
      private ObjectProperty<String> selectedProf;
      private ListProperty<String> courseCodes;
      private ObjectProperty<String> selectedCourseCode;
      // Subject, section & subsection ie physics - mechanics - momentum
      private ObjectProperty<String> selectedSubject;
      private ListProperty<String> subjects;
      private ObjectProperty<String> selectedSection;
      private ListProperty<String> sections;
      private ObjectProperty<String> selectedSubSection;
      private ListProperty<String> subSections;


      /**
       * NoArgs Constructor
       */
      public SearchRemoteDescriptor() {
            setProperitesDefault();
      }

      /* Search Form Entries */
      public String getFriendDescript() {
            return friend.get();
      }

      public String getDeckKey() {
            return deckKey.get();
      }

      public long getnltDate() {
            return nltDate.get();
      }

      public long getnetDate() {
            return netDate.get();
      }

      //public ListProperty getProfessors() {return professors.get(); }
      //  public String getCourseCode() { return courseCodes.get(); }
      public String getSelectedSubject() {
            return selectedSubject.get();
      }

      /* Search properties */
      public StringProperty friendProperty() {
            return friend;
      }

      public StringProperty deckKeyProperty() {
            return deckKey;
      }

      public LongProperty nltDateProperty() {
            return nltDate;
      }

      public LongProperty netDateProperty() {
            return netDate;
      }

      /* search by institute and prof/course code */
      public ListProperty<String> professorsProperty() {
            return professors;
      }

      public ObjectProperty<String> selectedProfProperty() {
            return selectedProf;
      }

      public ListProperty<String> courseCodesProperty() {
            return courseCodes;
      }

      public ObjectProperty<String> SelectedCourseCodeProperty() {
            return selectedCourseCode;
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

      public String getSelectedProf() {
            return selectedProf.get();
      }

      public String getSelectedCourseCode() {
            return selectedCourseCode.get();
      }


      /* by subject related */
      public ListProperty<String> subjectsProperty() {
            return subjects;
      }

      public ObjectProperty<String> selectedSubjectProperty() {
            return selectedSubject;
      }

      public ListProperty<String> sectionsProperty() {
            return sections;
      }

      public ObjectProperty<String> selectedSectionProperty() {
            return selectedSection;
      }

      // a specific sub-topic
      public ListProperty<String> subSectionsProperty() {
            return subSections;
      }

      public ObjectProperty<String> selectedSubSectionsProperty() {
            return selectedSubSection;
      }

      public String getSelectedSubjectProperty() {
            return selectedSubject.get();
      }

      public String getSelectedSectionProperty() {
            return selectedSection.get();
      }

      public String getSelectedSubSectionProperty() {
            return selectedSubSection.get();
      }

      // for attributes, searches description
      // public ListProperty<String> attributesProperty() { }

      /* by author/friend */


      public void setSelectedTut(CSVUtil.SchoolObj defaultTut) {
            this.selectedTut.set(defaultTut);
      }

      public ObservableList<CSVUtil.SchoolObj> getAllTuts() {
            return allTuts.get();
      }

      public void setAllTuts(ObservableList<CSVUtil.SchoolObj> allTuts) {
            this.allTuts.set(allTuts);
      }

      public ObservableList<String> getProfessors() {
            return professors.get();
      }

      public ObservableList<String> getCourseCodes() {
            return courseCodes.get();
      }


      @Override
      public void setProperitesDefault() {
		/*
		ArrayList<String> bill = new ArrayList<>(1);
		bill.add("Dr.");
		bill.add("William");
		bill.add("Ledbetter");
		ArrayList<String> codes = new ArrayList<>(1);
		codes.add("TAMU-CE-573-SP-1985");
		ArrayList<String> subj = new ArrayList<>(1);
		subj.add("Civil Engineering");
		CSVUtil.SchoolObj schObj = new CSVUtil.SchoolObj("Texas A&M", "1 TAMU Ave, College Station, Tx 77840", "Texas");
		*/


            //super.setProperitesDefault();
            friend = new SimpleStringProperty("");
            deckKey = new SimpleStringProperty("");
            nltDate = new SimpleLongProperty(0);
            netDate = new SimpleLongProperty(0);
            //selectedSubject = new SimpleStringProperty("");


            // institutions and schools/colleges/universities
            // Gets data from the DB and provides info for display in
            // comboBox
            subjects = new SimpleListProperty<>(FXCollections.observableArrayList());
            professors = new SimpleListProperty<>(FXCollections.observableArrayList());
            courseCodes = new SimpleListProperty<>(FXCollections.observableArrayList());
            // gets data from file
            allTuts = new SimpleListProperty<>(FXCollections.observableArrayList());
            selectedTut = new SimpleObjectProperty<>();
            selectedProf = new SimpleObjectProperty<>();
            selectedCourseCode = new SimpleObjectProperty<>();

            // subject section subsection
            sections = new SimpleListProperty<>(FXCollections.observableArrayList());
            //subSections = new SimpleListProperty<>(FXCollections.observableArrayList(getSubSectionsList()));
            selectedSubject = new SimpleObjectProperty<>();
            selectedSection = new SimpleObjectProperty<>();
            //selectedSubSection = new SimpleObjectProperty<>("");
      }

      public final <T extends Boolean> Section setProfBarProperties(ObservableValue<T> value, Section section) {
            LOGGER.debug("Value: {}", value);

            if (value.getValue().equals(false)) {
                  LOGGER.debug("called");
                  allTuts.setAll(FlashCardOps.getInstance().getSchoolObjs());

                  if (subjects.isEmpty()) {
                        subjects = new SimpleListProperty<>(FXCollections.observableArrayList(getSubjsList()));
                  }
                  professors.setAll(FXCollections.observableArrayList(getProfsList()));
                  courseCodes.setAll(FXCollections.observableArrayList(getCourseCodesList()));
            }
            return section;
      }

      protected void setSubjBarProperties() {
            if (subjects.isEmpty()) {
                  subjects = new SimpleListProperty<>(FXCollections.observableArrayList(getSubjsList()));
            }
            sections = new SimpleListProperty<>(FXCollections.observableArrayList(getSectionsList()));
            //subSections = new SimpleListProperty<>(FXCollections.observableArrayList(getSubSectionsList()));
      }


      @Override
      public void setMostRecent() {
            /* call setProperties directly */
            /* not used */
      }

      @Override
      public long getLocalDataDate() {
            return 0;
      }

      @Override
      public void setToLocalData() {

      }

      @Override
      public boolean setToRemoteData() {
            return false;
      }


      @Override
      public void setProperties(SearchRemoteDescriptor fm) {

      }

      //@TODO implement getSubjs, getProf, getCodes, getSections in SearchRemoteDescriptor and DBFetchMulti

      /**
       * Parses the String response from the DB to an
       * ArrayList.
       *
       * @return Returns the list of subject from the DB.
       */
      private ArrayList<String> getSubjsList() {
            return DBFetchMulti.DECK_SUBJECTS.query("notUSed");
      }

      private ArrayList<String> getProfsList() {
            return DBFetchMulti.DECK_PROFS.query("notUsed");
      }

      private ArrayList<String> getCourseCodesList() {
            return DBFetchMulti.COURSE_CODES.query("notUsed");
      }

      private ArrayList<String> getSectionsList() {
            return DBFetchMulti.DECK_SECTIONS.query("notUsed");
      }

      private ArrayList<String> getSubSectionsList() {
            return DBFetchMulti.DECK_SUB_SECTIONS.query("notUsed");
      }

      // *** FORM DATA ***

      @Override
      public void clear() {
            friend.setValue("");
            deckKey.setValue("");
            //	professors.setValue("");
            //	courseCode.setValue("");
            selectedSubject.setValue("");
            nltDate.setValue(0);
            netDate.setValue(Long.MAX_VALUE);
      }


      // *** Overriden methods from FormData ***

      @Override
      public void init() {

      }

      /**
       * Sets this object form the DataAry from this object;
       *
       * @return If the dataAry is missing the Description or
       * the lastDate is 0 returns false signifying a failure.
       */
      @Override
      public boolean set() {
            return false;
      }

      /**
       * Sets this instances variables from the DataAry
       *
       * @param str
       */
      @Override
      public void set(HashMap<String, String> str) {

      }

      /**
       * Sets this class instance to null
       */
      @Override
      public void close() {

      }

      /**
       * Sets this objects metaDataAry to the array in the param.
       *
       * @param dataAry
       */
      @Override
      public boolean setDataMap(HashMap<String, String> dataAry) {
            return false;
      }

      /**
       * Creates a String[] of data of the current user.
       */
      @Override
      public void updateDataMap() {

      }

      /**
       * Returns the deckMetaData array created by this class.
       *
       * @return
       */
      @Override
      public HashMap<String, String> getDataMap() {
            return new HashMap<>();
      }

      /**
       * The last timeStamp from when this was updated
       *
       * @return
       */
      @Override
      public long getLastDate() {
            return 0;
      }
}
