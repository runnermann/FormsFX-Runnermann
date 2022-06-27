package forms.utility;

import authcrypt.user.EncryptedStud;
import campaign.db.DBFetchToMapAry;
import ch.qos.logback.classic.Level;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentDescriptor extends PersonDescriptor {

      // Logging reporting level is set in src/main/resources/logback.xml
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(StudentDescriptor.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(StudentDescriptor.class);
      // String properties
      private StringProperty educationLevel;
      private StringProperty major;
      private StringProperty minor;
      private StringProperty cvLink;
      private EncryptedStud student;

      // Prevent multiple upload and downlaods
      // iF meta has been set, set to true.
      public boolean studentIsSet = false;


      /**
       * Constructor
       */
      public StudentDescriptor() {
            super();

            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("called studentDescriptor constructor");
            this.setProperitesDefault();
            boolean hasData = this.setToRemoteData();
            if (hasData) {
                  this.setProperties(student);
            }
      }

      // Student info from form entries
      public String getEducationLevel() {
            return educationLevel.get();
      }

      public String getMajor() {
            return major.get();
      }

      public String getMinor() {
            return minor.get();
      }

      public String getCVLink() {
            return cvLink.get();
      }

      // student properties
      public StringProperty educationLevelProperty() {
            return educationLevel;
      }

      public StringProperty majorProperty() {
            return major;
      }

      public StringProperty minorProperty() {
            return minor;
      }

      public StringProperty cvLinkProperty() {
            return cvLink;
      }

      @Override
      public void setProperitesDefault() {
            LOGGER.debug("called setPropertiesDefault");
            // Set PersonDescriptor to default
            super.setProperitesDefault();
            educationLevel = new SimpleStringProperty("");
            major = new SimpleStringProperty("");
            minor = new SimpleStringProperty("");
            cvLink = new SimpleStringProperty("");
      }


      /**
       * There is no reason to store user information
       * locally. See notes; As of 2020-09-16
       * Sets the data to the most recent.
       */
      @Override
      public void setMostRecent() {
            /* not used */
      }


      //@Override
      public void setProperties(final EncryptedStud student) {
            LOGGER.debug("called setProperties(...) with args. student lastname: {}", student.getLastName());
            // set person (the parent) properties
            super.setProperties(student);
            educationLevel = new SimpleStringProperty(student.getEducationLevel());
            major = new SimpleStringProperty(student.getMajor());
            minor = new SimpleStringProperty(student.getMinor());
            cvLink = new SimpleStringProperty(student.getCvLink());
      }

      /**
       * @return the timeStamp in millis of a local meta file
       * * if exists. If not returns 0.
       */
      @Override
      public long getLocalDataDate() {
            return 0;
      }

      /**
       * A students data is not stored locally as of
       * 2020-09-16
       * //Sets the studentData object to the local file
       * //for this object if it exists and updates
       * //the Data Array. Otherwise leaves
       * //the meta object as is.
       */
      @Override
      public void setToLocalData() {
            /* not used */
      }

      /**
       * Fetches this decks metaData from the database if it
       * exists, and sets the meta object to it. Otherwise leaves
       * it as is.
       */
      @Override
      public boolean setToRemoteData() {
            student = new EncryptedStud();
            LOGGER.debug("called getRemoteData()");
            // @TODO complete getRemoteData, need studentHash in query

            // get meta from DB
            //String response = "";

            //String[] args = {Alphabet.encrypt(authcrypt.UserData.getUserName())};
            String[] args = {authcrypt.UserData.getUserName()}; // may cause an error
            ArrayList<HashMap<String, String>> response = DBFetchToMapAry.STUDENT_ENCRYPTED_DATA.query(args); // check

            if (response == null || response.get(0).get("empty").equals("true")) {
                  LOGGER.debug("response from DB length is EMPTY... NOT processing response.");
                  return false;
            } else {
                  LOGGER.debug("response from DB length contains data... processing response.");
                  student.set(response.get(0));
                  return true;
            }
      }

      /**
       * Clears the form and sets to default
       * entries.
       */
      @Override
      public void clear() {
            educationLevel.setValue("");
            major.setValue("");
            minor.setValue("");
            cvLink.setValue("");
      }
}
