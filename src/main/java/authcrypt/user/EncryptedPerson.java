package authcrypt.user;

import authcrypt.UserData;
import forms.FormData;
import java.io.Serializable;
import java.util.HashMap;

/**
 * This data object is encrypted where it needs to be.
 */
public class EncryptedPerson implements Serializable, FormData {
    
    // The returned ID from a query
    // store for next query
    private static long person_id;
    // last date this was updated
    private long   lastDate;
//    private String firstName;
    private String lastName;
    private String middleName;
    private String current_user_email;
    private String orig_user_email;
    private String school;
    private String age;
    private String phone;
    private String Descript;
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
        setAll(person.person_id, person.phone, person.getFirstName(), person.lastName, person.middleName, person.current_user_email,
                person.orig_user_email, person.school, person.age, person.Descript, person.institution, person.photoLink);
    }
    
    @Override
    public void init() {
        person_id = -1;
        lastDate = 0;
//        firstName = UserData.getFirstName();
        lastName = "";
        middleName = "";
        current_user_email = "";
        orig_user_email = "";
        school = "";
        age = "";
        phone = "";
        Descript = "";
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


    protected void setAll(long personID, String phn, String firstNm, String lastNm, String middle,
          String currentMail, String origMail, String school, String age, String descript,
          String institute, String photoLnk) {
        setPersonId(personID);
        setPhone(phn);
        setFirstName(firstNm);
        setLastName(lastNm);
        setMiddleName(middle);
        setCurrent_user_email(currentMail);
        setOrig_user_email(origMail);
        setSchool(school);
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
        return current_user_email;
    } // used to set data.

    // encrypt
    public void setCurrent_user_email(String current_user_email) {
        this.current_user_email = current_user_email;
    }
    // Encrypted
    public String getOrig_user_email() {
        return orig_user_email;
    }
    // Encrypt
    public void setOrig_user_email(String orig_user_email) {
        this.orig_user_email = orig_user_email;
    }
    
    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }

    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }

    public String getDescript() {
        return this.Descript;
    }
    public void setDescript(String descript) {
        Descript = descript;
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
