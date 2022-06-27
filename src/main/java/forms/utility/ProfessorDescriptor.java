package forms.utility;

import authcrypt.user.EncryptedProf;
import fileops.DirectoryMgr;
import fileops.utility.Utility;
import flashmonkey.FlashCardOps;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * <p> On distributed systems where data may remain on public
 * * information systems such as computers in a university library.
 * * We do not leave the users personal information exposed. </p>
 * * <pre>
 *  *     -1 Data is created on a system, and is available to be created
 *  *     on any system with the application.
 *  *     -2 Data is encrypted and uploaded to the DB.
 *  *     -3 Personal Data is removed from the local system.
 *  *     -4 To edit personal data, Data is downloaded from the
 *  *     DB to the local system, decrytped, and placed in the from
 *  *     for the user to modify.
 *  *     -5 Information such as the SSN is not exposed in the form nor
 *  *     downloaded.
 *  * </pre>
 */
public class ProfessorDescriptor extends PersonDescriptor {

      // Logging reporting level is set in src/main/resources/logback.xml
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ProfessorDescriptor.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorDescriptor.class);

      // String properties
      private StringProperty profEmail;
      private StringProperty educationLevel;
      private StringProperty position;
      private StringProperty numStars;
      private StringProperty website;
      private StringProperty cvLink;
      private StringProperty pay;
      private StringProperty recruiterInfo;
      private EncryptedProf prof;

      // flag set by DB response
      boolean hasData;

      /**
       * Constructor
       */
      public ProfessorDescriptor() {
            super(); // not sure this is neccessary
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("called ProfessorDescriptor constructor");
            this.setMostRecent();
            hasData = false;
      }

      // Professor data from form entries
      public String getProfEmail() {
            return profEmail.get();
      }

      public String getEducationLevel() {
            return educationLevel.get();
      }

      public String getPostion() {
            return position.get();
      }

      public String getNumStars() {
            return numStars.get();
      }

      public String getWebSite() {
            return website.get();
      }

      public String getCVLink() {
            return cvLink.get();
      }

      public String getPay() {
            return pay.get();
      }

      public String getRecruiterInfo() {
            return recruiterInfo.get();
      }

      // Professor Properties
      public StringProperty profEmailProperty() {
            return this.profEmail;
      }

      public StringProperty educationLevelProperty() {
            return this.educationLevel;
      }

      public StringProperty positionProperty() {
            return this.position;
      }

      public StringProperty numStarsProperty() {
            return this.numStars;
      }

      public StringProperty websiteProperty() {
            return this.website;
      }

      public StringProperty cvLinkProperty() {
            return this.cvLink;
      }

      public StringProperty payProperty() {
            return this.pay;
      }

      public StringProperty recreiterInfoProperty() {
            return this.recruiterInfo;
      }

      @Override
      public void setProperitesDefault() {
            super.setProperitesDefault();
            profEmail = new SimpleStringProperty("");
            educationLevel = new SimpleStringProperty("");
            position = new SimpleStringProperty("");
            recruiterInfo = new SimpleStringProperty("");
            website = new SimpleStringProperty("");
            cvLink = new SimpleStringProperty("");
            pay = new SimpleStringProperty("0");
            numStars = new SimpleStringProperty("0");
      }

      //@Override
      public void setProperties(final EncryptedProf p) {
            super.setProperties(p);
            profEmail = new SimpleStringProperty(p.getProfEmail());
            educationLevel = new SimpleStringProperty(p.getEducationLevel());
            position = new SimpleStringProperty(p.getPosition());
            recruiterInfo = new SimpleStringProperty(p.getRecruiterInfo());
            website = new SimpleStringProperty(p.getWebsite());
            cvLink = new SimpleStringProperty(p.getCvLink());
            pay = new SimpleStringProperty(p.getPay());
            numStars = new SimpleStringProperty(p.getNumStars());
      }

      /**
       * Sets the data to the most recent.
       */
      @Override
      public void setMostRecent() {

            LOGGER.debug("called setMostRecent()");
            // Sets the Professor object to the DB if
            // it exists && the user is connected.
            long remoteDate = 0;// = meta.getLastDate() * -1;
            if (Utility.isConnected()) {
                  hasData = setToRemoteData();
                  if (!hasData) {
                        remoteDate = prof.getLastDate() * -1;
                  }
            }

            long localDate = getLocalDataDate();
            if (hasData) {
                  long num = remoteDate + localDate;
                  // If this files metadata does not exist yet
                  // chose to set from default.
                  if (num + localDate == 0) {
                        //System.out.print("Chose default");
                        setProperitesDefault();
                  } else if (num > 0) {
                        setToLocalData();
                        //System.out.println("Chose local ");
                        setProperties(prof);
                  } else { // (num < 0)
                        // meta is already set to the DB
                        //System.out.println("Chose remote ");
                        setProperties(prof);
                  }
            } else {
                  setProperitesDefault();
            }
      }

      /**
       * @return the timeStamp in millis of a local meta file
       * * if exists. If not returns 0.
       */
      @Override
      public long getLocalDataDate() {
            return super.getLocalDataDate();
      }

      /**
       * Sets the professorData object to the local file
       * for this object if it exists and updates
       * the Data Array. Otherwise leaves
       * the meta object as is.
       */
      @Override
      public void setToLocalData() {
            String folder = DirectoryMgr.getMediaPath('z');

            String metaFileName = FlashCardOps.getInstance().getMetaFileName();
            File check = new File(folder + "/" + metaFileName);
            // if the file exists and has not been
            // retrieved from file previously for this object.
            if (check.exists()) {
                  prof = new EncryptedProf(EncryptedProf.getProfFmFile());
            }
      }

      /**
       * Fetches this decks metaData from the database if it
       * exists, and sets the meta object to it. Otherwise leaves
       * it as is.
       */
      @Override
      public boolean setToRemoteData() {

            // @TODO complete getRemoteData, need professorHash in query

            // UserData user = new UserData();
            // get meta from DB
            String[] response;// = "";
            //response = Report.getInstance().queryGetUserEcryptedData(professorHash, tableName);
	/*
		response = DBFetch.USER_ENCRYPTED_DATA.query(professorHash, tableName);
		if(response.length() > 10) {
			LOGGER.debug("response from DB length is GREATER than 10... processing response.");
			// Parse the response, set metaAry,
			// & set the DeckMetaData object.
			EncryptedPerson data = new EncryptedPerson();
			//String[] dataStr = parseResponse(response);
			data.setDataAry(response);
			data.set(response);
			return true;
		} else {
			LOGGER.debug("response from DB length is LESS than 10... NOT processing response.");
			return false;
		}
		
	 */
            return false;
      }


      @Override
      public void clear() {
            profEmail.setValue("");
            educationLevel.setValue("");
            position.setValue("");
            numStars.setValue("0");
            website.setValue("");
            cvLink.setValue("");
            pay.setValue("0.0");
            recruiterInfo.setValue("");
      }

}

