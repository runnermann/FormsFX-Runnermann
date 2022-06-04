package authcrypt.user;

import authcrypt.UserData;
import forms.FormData;
import java.util.HashMap;

//import static fileops.FileNaming.hashToHex;

/**
 * This data object is encrypted where it needs to be.
 * Note that Security sensitive classes should not be
 * serializable. Removed 08-10-2021
 */
public class EncryptedPerson implements FormData {
    
    // The returned ID from a query
    // store for next query
    private static long person_id;
    // last date this was updated
    private long   lastDate;
//    private String firstName;
    private String lastName;
    private String middleName;
    private String currentUserEmail;
    private String origUserEmail;
    private String age;
    private String phone;
    private String descript;
    private String institution;
    private String photoLink;

    //@TODO check login is correct in encrypted user.
    public EncryptedPerson() {
        init();
 //       if(login == 8675309) {

 //       }
    }

    /**
     * copyConstructor
     * @param person
     */
    public EncryptedPerson(EncryptedPerson person) {
        setAll(person.person_id, person.phone, person.getFirstName(), person.lastName, person.middleName, person.currentUserEmail,
                person.origUserEmail, person.age, person.descript, person.institution, person.photoLink);
    }
    
    @Override
    public void init() {
        person_id = -1;
        lastDate = 0;
//        firstName = UserData.getFirstName();
        lastName = "";
        middleName = "";
        currentUserEmail = "";
        origUserEmail = "";
        age = "";
        phone = "";
        descript = "";
        institution = "";
        photoLink = "";
    }
    

    /**
     * Sets this class instance to null
     */
    @Override
    public void close() {
        //@TODO finish stub
        /* STUB */
    }

    
    // *** SETTERS GETTERS AND OTHER *** //


    public void setAll(long personID, String phn, String firstNm, String lastNm, String middle,
          String currentMail, String origMail, String age, String descript,
          String institute, String photoLnk) {
        setPersonId(personID);
        setPhone(phn);
        setFirstName(firstNm);
        setLastName(lastNm);
        setMiddleName(middle);
        setCurrentUserEmail(currentMail);
        setOrigUserEmail(origMail);
        setAge(age);
        setDescript(descript);
        setInstitution(institute);
        setPhotoLink(photoLnk);
    }
    
    public static long getPersonId() {
        return person_id;
    }
    public static void setPersonId(long id) {
        person_id = id;
    }
    
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return UserData.getFirstName();
    }
    public void setFirstName(String firstN) {
        UserData.setFirstName(firstN);
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    
    // Stored in plain text
    public String getCurrentUserEmail() {
        return currentUserEmail;
    } // used to set data.

    // encrypt
    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
    }
    // Encrypted
    public String getOrigUserEmail() {
        return origUserEmail;
    }
    // Encrypt
    public void setOrigUserEmail(String origUserEmail) {
        this.origUserEmail = origUserEmail;
    }

    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }

    public String getDescript() {
        return this.descript;
    }
    public void setDescript(String descript) {
        this.descript = descript;
    }
    
    public String getInstitution() {
        return this.institution;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    public String getPhotoLink() {
        return photoLink;
    }
    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
    
    @Override
    public long getLastDate() {
        return lastDate;
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
    public boolean setDataMap(HashMap<String, String> dataAry) {
        //@TODO finish stub
        /* STUB */
        return false;
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
    /*
    //@Override
    public String[] getData(String filePathName) throws FileNotFoundException {
        //@TODO finish stub
        return new String[0];
    }

    //@Override
    public boolean setDataAryFmFile() {
        //@TODO finish stub
        return false;
    }
     */
}
