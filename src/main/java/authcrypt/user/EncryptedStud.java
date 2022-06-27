package authcrypt.user;

import authcrypt.UserData;
import flashmonkey.FlashCardOps;
import forms.FormData;

import java.util.HashMap;

/**
 * Note that Security sensitive classes should not be serializable
 */
public class EncryptedStud extends EncryptedPerson implements FormData {

      private String educationLevel;
      private String major;
      private String minor;
      private String cvLink;


      /**
       * No args constructor
       */
      public EncryptedStud() {
            super.init();
            init();
      }

      public EncryptedStud(EncryptedPerson person, String educationLevel, String major, String minor, String cvLink) {
            super(person);
            setEducationLevel(educationLevel);
            setMajor(major);
            setMinor(minor);
            setCvLink(cvLink);
      }

      public EncryptedStud(EncryptedStud orig) {
            super.setAll(getPersonId(), orig.getPhone(), orig.getFirstName(), orig.getLastName(), orig.getMiddleName(), orig.getCurrentUserEmail(),
                orig.getOrigUserEmail(), orig.getAge(), orig.getDescript(), orig.getInstitution(), orig.getPhotoLink());
            setAll(orig.educationLevel, orig.major, orig.minor, orig.cvLink);
      }

      @Override
      public void init() {
            educationLevel = "";
            major = "";
            minor = "";
            cvLink = "";
      }

      /**
       * Sets this class instance to null
       */
      @Override
      public void close() {
            //@TODO finish stub
            /* STUB */
      }


      // ****** GETTERS AND SETTERS ***** //

      public static EncryptedStud getStudentFmFile() {
            String personFileName = authcrypt.UserData.getUserName().hashCode() + "s.met"; // meta
            EncryptedStud s = new EncryptedStud((EncryptedStud) FlashCardOps.getInstance().getObjectFmFile(personFileName));
            return s;
      }

      public void setAll(String educationLevel, String major, String minor, String cvLink) {
            setEducationLevel(educationLevel);
            setMajor(major);
            setMinor(minor);
            setCvLink(cvLink);
      }

      public String getEducationLevel() {
            return educationLevel;
      }

      public void setEducationLevel(String educationLevel) {
            this.educationLevel = educationLevel;
      }

      public String getMajor() {
            return major;
      }

      public void setMajor(String major) {
            this.major = major;
      }

      public String getMinor() {
            return minor;
      }

      public void setMinor(String minor) {
            this.minor = minor;
      }

      public String getCvLink() {
            return cvLink;
      }

      public void setCvLink(String cvLink) {
            this.cvLink = cvLink;
      }

      // **** DATA ARRAY RELATED *** //

      /**
       * Sets this object from the dataAry from this object;
       *
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
       *
       * @param map
       */
      @Override
      public void set(HashMap<String, String> map) {
            setMiddleName(map.get("middle_name"));
            setPhone(map.get("phone"));
            setAge(map.get("age"));
            setInstitution(map.get("institution"));
            setDescript(map.get("descript"));
            setEducationLevel(map.get("education_level"));
            setMajor(map.get("major"));
            setMinor(map.get("minor"));
            setCvLink(map.get("cv_link"));
            setPersonId(Long.parseLong(map.get("person_id").replace("]", "")));
            UserData.setFirstName(map.get("first_name"));
            setLastName(map.get("last_name"));
            setPhotoLink(map.get("photo_link"));
            setPhone(map.get("phone"));
      }

      /**
       * Sets this objects dataAry to the array in the param.
       *
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
       *
       * @return
       */
      @Override
      public HashMap<String, String> getDataMap() {
            //@TODO finish stub
            return new HashMap<>();
      }


}
