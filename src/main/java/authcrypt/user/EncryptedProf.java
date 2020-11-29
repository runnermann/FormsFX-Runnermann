package authcrypt.user;

import fileops.DirectoryMgr;
import flashmonkey.FlashCardOps;
import forms.FormData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;

public class EncryptedProf extends EncryptedPerson implements Serializable, FormData {
	
	private String profEmail;
	private String educationLevel;
	private String position;
	private int    numStars;
	private String website;
	private String cvLink;
	private int    pay;
	private String recruiterInfo;
	
	public EncryptedProf() {
		super.init();
		this.init();
	}

	public EncryptedProf(EncryptedPerson person, String profEmail, String educationLevel, String position, int numStars,
						 String website, String cvLink, int pay, String recruiterInfo) {
		super(person);
		setAll(profEmail, educationLevel, position, numStars, website, cvLink, pay, recruiterInfo);
	}

	public EncryptedProf(EncryptedProf orig) {
		super.setAll(orig.getPersonId(), orig.getPhone(), orig.getFirstName(), orig.getLastName(), orig.getMiddleName(), orig.getCurrentUserEmail(),
				orig.getOrig_user_email(), orig.getSchool(), orig.getAge(), orig.getDescript(), orig.getInstitution(), orig.getPhotoLink());
		setAll(orig.profEmail, orig.educationLevel, orig.position, orig.numStars, orig.website, orig.cvLink, orig.pay, orig.recruiterInfo);
	}
	
	@Override
	public void init() {
		profEmail = "";
		educationLevel = "";
		position = "";
		numStars = 0;
		website = "";
		cvLink = "";
		pay = 0;
		recruiterInfo = "";
	}

	/**
	 * Sets this class instance to null
	 */
	@Override
	public void close() {
		//@TODO finish stub
		/* STUB */
	}
	

	
	// ******* GETTERS AND SETTERS ******* //

	public static EncryptedProf getProfFmFile() {
		String personFileName = authcrypt.UserData.getUserName().hashCode()+ "p" + ".met";
		EncryptedProf s = new EncryptedProf((EncryptedProf) FlashCardOps.getInstance().getFO().getObjectFmFile(personFileName));
		return s;
	}

	public void setAll(String profEmail, String educationLevel, String position, int numStars,
					   String website, String cvLink, int pay, String recruiterInfo) {
		setProfEmail(profEmail);
		setProfEmail(profEmail);
		setEducationLevel(educationLevel);
		setPosition(position);
		setNumStars(numStars);
		setWebsite(website);
		setCvLink(cvLink);
		setPay(pay);
		setRecruiterInfo(recruiterInfo);
	}
	
	public String getProfEmail() {
		return profEmail;
	}
	public void setProfEmail(String profEmail) {
		this.profEmail = profEmail;
	}
	
	public String getEducationLevel() {
		return educationLevel;
	}
	public void setEducationLevel(String educationLevel) {
		this.educationLevel = educationLevel;
	}
	
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	
	public int getNumStars() {
		return numStars;
	}
	public void setNumStars(int numStars) {
		this.numStars = numStars;
	}
	
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	
	public String getCvLink() {
		return cvLink;
	}
	public void setCvLink(String cvLink) {
		this.cvLink = cvLink;
	}
	
	public int getPay() {
		return pay;
	}
	public void setPay(int pay) {
		this.pay = pay;
	}
	
	public String getRecruiterInfo() {
		return recruiterInfo;
	}
	public void setRecruiterInfo(String recruiterInfo) {
		this.recruiterInfo = recruiterInfo;
	}


	// **** DATA ARRAY RELATED *** //

	/**
	 * Sets this object form the dataAry from this object;
	 * @return If the dataAry is missing the Description or
	 * the lastDate is 0 returns false signifying a failure.
	 */
	@Override
	public boolean set() {
		//@TODO finish stub
		/* STUB */
		return false;
	}

	/**
	 * Sets this instances variables from the dataAry
	 * @param str
	 */
	@Override
	public void set(HashMap<String, String> str) {
		//@TODO finish stub
		/* STUB */
	}

	/**
	 * Sets this objects dataAry to the array in the param.
	 * @param dataAry
	 */
	@Override
	public void setDataMap(HashMap<String, String> dataAry) {
		//@TODO finish stub
		/* STUB */
	}

	/**
	 * Creates a String[] of data of the current deck.
	 * To include a recent tests score, update the testNode
	 * with the latest test before using this method.
	 */
	@Override
	public void updateDataMap() {
		//@TODO finish stub
		/* STUB */
	}

	/**
	 * Returns the deckdata array created by this class.
	 * @return
	 */
	@Override
	public HashMap<String, String> getDataMap() {
		//@TODO finish stub
		return new HashMap<>();
	}

/*	@Override
	public String[] getData(String filePathName) throws FileNotFoundException {
		//@TODO finish stub
		return new String[0];
	}

	@Override
	public boolean setDataAryFmFile() {
		//@TODO finish stub
		return false;
	}
 */
}
