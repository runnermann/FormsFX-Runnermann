package forms.utility;

import authcrypt.UserData;
import authcrypt.user.EncryptedPerson;
import fileops.DirectoryMgr;
import forms.Descriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * <p>ParentClass to Student and Professor descriptors</p>
 *
 * <p> On distributed systems where data may remain on public
 * information systems such as computers in a university lab.
 * We do not leave the users personal information exposed. </p>
 *
 * <pre>
 * Update data scenarios
 *
 *     -1 Data is created on a system, and is available to be created
 *     on any system with the application.
 *     -2 Data is encrypted and uploaded to the DB.
 *     -3 Personal Data is removed from the local system.
 *     -4 To edit personal data, Data is downloaded from the
 *     DB to the local system, decrytped, and placed in the form
 *     for the user to modify.
 *     -5 * PFI (if needed) such as the SSN is not exposed in the form nor
 *     downloaded.
 *     * PFI Personal Financial Information is not planed to be used by any
 *     system as of 2020-07-21
 * </pre>
 */
public abstract class PersonDescriptor implements Descriptor<EncryptedPerson> {
	
	
	// Logging reporting level is set in src/main/resources/logback.xml
	// Use for detailed logging and use setLevel(Level.debug)
	// Slows app performance significantly!!!
	// private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PersonDescriptor.class);
	// Other wise use for normal logging
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonDescriptor.class);
	
	
	// String properties
	
	// The personDescript obj.
	private StringProperty personDescript;
	private StringProperty firstName;
	private StringProperty lastName;
	private StringProperty middleName;
	private StringProperty origEmail;
	private StringProperty currentEmail;
	//private StringProperty joinDate;
	private StringProperty taxID;
	private StringProperty phone;
	private StringProperty age;
	private StringProperty photoLink;
	private StringProperty institution;
	
	
	/**
	 * No args constructor
	 */
	public PersonDescriptor() {
		LOGGER.debug("called PersonDescriptor no args constructor");
		//EncryptedPerson cryptedUser = new EncryptedPerson();
	};
	
	
	// Person data from form entries
	public String getPersonDescript() { return personDescript.get(); }
	public String getFirstName() { return firstName.get(); }
	public String getLastName() { return lastName.get(); }
	public String getMiddleName() { return middleName.get(); }
	public String getOrigEmail() { return origEmail.get(); }
	public String getCurrentEmail() { return currentEmail.get(); }
	public String getTaxID() { return taxID.get(); }
	public String getPhone() { return phone.get(); }
	public String getAge() { return age.get(); }
	public String getPhotoLink() { return photoLink.get(); }
	public String getInstitution() { return institution.get(); }
	
	
	
	// person properties
	public StringProperty personDescriptProperty() { return personDescript; }
	public StringProperty firstNameProperty() { return firstName; } // in MetaParent
	public StringProperty lastNameProperty()  { return lastName; }
	public StringProperty middleNameProperty() { return middleName; }
	public StringProperty origEmailProperty() { return origEmail; } // in MetaParent
	public StringProperty currentEmailProperty() { return currentEmail; }
	public StringProperty taxIDProperty() { return taxID; }
	public StringProperty phoneProperty() { return phone; }
	public StringProperty ageProperty() { return age; }
	public StringProperty photoLinkProperty() { return photoLink; }
	public StringProperty institutionProperty() { return institution; }
	
	
	
	// Prevent multiple upload and downloads
	// iF person data has been set, set to true.
	public boolean personDataIsSet = false;
	
	
	
	
	
	@Override
	public void setProperitesDefault() {
		LOGGER.debug("called setPropertiesDefault() ");
		
		personDescript = new SimpleStringProperty("");
		firstName = new SimpleStringProperty(UserData.getFirstName());
		lastName = new SimpleStringProperty("");
		middleName  = new SimpleStringProperty("");
		// once orig_email is set, it cannot be changed.
		//origEmail = new SimpleStringProperty(UserData.getUserName());
		currentEmail = new SimpleStringProperty("");
		//private StringProperty joinDate;
		//	taxID;
		phone = new SimpleStringProperty("");;
		age = new SimpleStringProperty("0");
		//	photoLink = new SimpleStringProperty("");;
		institution = new SimpleStringProperty("");;
	}
	
	@Override
	public void setProperties(final EncryptedPerson u) {
		LOGGER.debug("called setProperties with args");
		
		personDescript = new SimpleStringProperty(u.getDescript());
		firstName = new SimpleStringProperty(u.getFirstName());
		lastName = new SimpleStringProperty(u.getLastName());
		middleName = new SimpleStringProperty(u.getMiddleName());
		// once orig_email is set, it cannot be changed.
		//origEmail = new SimpleStringProperty(u.getCurrentUserEmail());
		currentEmail = new SimpleStringProperty(u.getCurrentUserEmail());
		phone = new SimpleStringProperty(u.getPhone());
		age = new SimpleStringProperty(u.getAge());
		institution = new SimpleStringProperty(u.getInstitution());
	}
	
	/**
	 * Compares the DB version with local version using
	 * date. Sets the data to the most recent version.
	 */
	@Override
	public abstract void setMostRecent();
	
	/**
	 *
	 * @return the timeStamp in millis of a local meta file
	 * 	 * if exists. If not returns 0.
	 */
	@Override
	public long getLocalDataDate() {
		String folder = DirectoryMgr.getMediaPath('t');

		String personFileName = authcrypt.UserData.getUserName().hashCode()+ "p" + ".met";
		File check = new File(folder + "/" + personFileName);
		// if the file exists and has not been
		// retrieved from file previously for this object.
		if(check.exists()) {
			return check.lastModified();
		}
		return 0;
	}
	
	/**
	 * Fetches this decks metaData from the database if it
	 * exists, and sets the meta object to it. Otherwise leaves
	 * it as is.
	 */
	/*@Override
	public void getRemoteData() {
		UserData user = new UserData();
		// get meta from DB
		String response = "";
		//response = Report.getInstance().queryGetUserEcryptedData(studentHash, tableName);
	//	response = DBFetch.USER_ENCRYPTED_DATA.query(studentHash, tableName);
		if(response.length() > 10) {
//			LOGGER.debug("response from DB length is GREATER than 10... processing response.");
			// Parse the response, set metaAry,
			// & set the DeckMetaData object.
			EncryptedPerson data = new EncryptedPerson();
			String[] dataStr = parseResponse(response);
			data.setDataAry(dataStr);
			data.set(dataStr);
		} else {
//			LOGGER.debug("response from DB length is LESS than 10... NOT processing response.");
		}
	}
	
	 */
	
	/**
	 * Clears the form and sets to default
	 * entries.
	 */
	@Override
	public void clear() {
		personDescript.setValue("");
		lastName.setValue("");
		middleName.setValue("");
		// first name is provided by parent
		currentEmail.setValue("");
		phone.setValue("");
		age.setValue("");
		institution.setValue("");
	}

}
