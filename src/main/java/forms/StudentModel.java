package forms;


import authcrypt.UserData;
import authcrypt.user.EncryptedPerson;
import authcrypt.user.EncryptedStud;
import campaign.db.DBFetchUnique;
import campaign.db.DBInsert;
import campaign.db.DBUpdate;
import ch.qos.logback.classic.Level;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.Section;
import com.dlsc.formsfx.model.validators.RegexValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.model.validators.StringNumRangeValidator;
import flashmonkey.FlashMonkeyMain;
import forms.utility.Alphabet;
import forms.utility.StudentDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> On distributed systems where data may remain on public
 * * information systems such as computers in a university library,
 * * We do not leave the users personal information exposed. </p>
 * * <pre>
 *  *     -1 Data is created on a system, and is available to be created
 *  *     on any system with the application.
 *  *     -2 Data is encrypted and uploaded to the DB.
 *  *     -3 Personal Data is removed from the local system.
 *  *     -4 To edit personal data, Data is downloaded from the
 *  *     DB to the local system, decrytped, and placed in the form
 *  *     for the user to modify.
 *  *     -5 Information such as the SSN if stored, is not exposed in the form nor
 *  *     downloaded.
 *  * </pre>
 */
public class StudentModel extends ModelParent {
      // Logging reporting level is set in src/main/resources/logback.xml
      // Use for detailed logging and use setLevel(Level.debug)
      // Slows app performance significantly!!!
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(StudentModel.class);
      // Other wise use for normal logging
      private static final Logger LOGGER = LoggerFactory.getLogger(StudentModel.class);

      private StudentDescriptor descriptor;// = new StudentDescriptor();
      private final String allCountryRegex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";

      /**
       * Provides the form fields with validation and messaging to the user.
       */
      public void createForm() {
            descriptor = new StudentDescriptor();
            //descriptor.setToRemoteData();

            //LOGGER.setLevel(Level.DEBUG);

            // @TODO ensure that data is not displayed if the user has not passed login.

            LOGGER.info("StudentModel createForm() called");
            LOGGER.debug("firstName: " + descriptor.firstNameProperty());
            LOGGER.debug("lastName:  " + descriptor.lastNameProperty());

            formInstance = Form.of(
                    Group.of(
                        Field.ofStringType(descriptor.avatarNameProperty())
                             .label("label_avatarname")
                             .placeholder("avatarname_placeholder")
                             .required(false)
                             .validate(StringLengthValidator.between(2, 16, "3_20_error_message")),
                        // first name
                        Field.ofStringType(descriptor.firstNameProperty())
                            .label("label_firstname")
                            .placeholder("firstname_placeholder")
                            .required(true)
                            .validate(StringLengthValidator.between(2, 31, "3_20_error_message")),
                        // last name
                        Field.ofStringType(descriptor.lastNameProperty())
                            .label("label_lastname")
                            .placeholder("lastname_placeholder")
                            .required(true)
                            .validate(StringLengthValidator.between(3, 31, "3_20_error_message")),
                        // middle initial
                        Field.ofStringType(descriptor.middleNameProperty())
                            .label("label_middleinit")
                            .placeholder("middleinit_placeholder")
                            .validate(StringLengthValidator.upTo(1, "single_char_error")),
                        // age
                        Field.ofStringType(descriptor.ageProperty())
                            .label("label_age")
                            .required("required_error_message")
                            .placeholder("age_placeholder")
                            .validate(StringNumRangeValidator.between(1, 120, "omg_age_error")),
                        // current email  -- Move this to change log in information form.
						/*Field.ofStringType(descriptor.currentEmailProperty())
							.label("label_current_email")
							.placeholder("current_email_placeholder")
							.validatorActionSwitch(RegexValidator.forEmail("email_error_message")),
						 */
                        // phone
                        Field.ofStringType(descriptor.phoneProperty())
                            .label("label_phone")
                            .placeholder("phone_placeholder")
                            .validate(
                                StringLengthValidator.upTo(31, "upto_31_error_message"),
                                RegexValidator.forPattern(allCountryRegex, "format_error_message")
                            )
                    ),
                    Section.of(
                        // my description
                        Field.ofStringType(descriptor.personDescriptProperty())
                            .label("label_description")
                            .multiline(true)
                            .placeholder("person_descipt_placeholder")
                            .validate(StringLengthValidator.upTo(511, "upTo_510_error"))
                    ).title("personal_descript_title"),
                    Section.of(
                        // institution
                        Field.ofStringType(descriptor.institutionProperty())
                            .label("label_institute")
                            .placeholder("institute_placeholder")
                            .validate(StringLengthValidator.upTo(31, "upto_31_error_message")),
                        // Student education and cv link data
                        Field.ofStringType(descriptor.educationLevelProperty())
                            .label("label_ed_level")
                            .placeholder("ed_level_placeholder")
                            .validate(StringLengthValidator.upTo(31, "upto_31_error_message")),
                        Field.ofStringType(descriptor.majorProperty())
                            .label("label_major")
                            .placeholder("major_placeholder")
                            .validate(StringLengthValidator.upTo(31, "upto_31_error_message")),
                        Field.ofStringType(descriptor.minorProperty())
                            .label("label_minor")
                            .placeholder("minor_placeholder")
                            .validate(StringLengthValidator.upTo(31, "upto_31_error_message")),
                        Field.ofStringType(descriptor.cvLinkProperty())
                            .label("label_cvlink")
                            .placeholder("cvlink_placeholder")
                            .validate(StringLengthValidator.upTo(31, "upto_31_error_message"))
                    ).title("institute_ed_level_title")
                ).title("student_label")
                .i18n(rbs);
      }

      /**
       * There is no financially valuable information requested in the form, storing
       * the personal information is not as high of a concern, but is still a concern.
       * We encrypt it here.
       *
       * @param data
       */
      @Override
      public void formAction(FormData data) {
            authcrypt.user.EncryptedStud student = new authcrypt.user.EncryptedStud();
            getFormInstance().persist();

            student.setDescript(descriptor.getPersonDescript());
            student.setFirstName(descriptor.getFirstName());
            student.setLastName(descriptor.getLastName());
            student.setMiddleName(descriptor.getMiddleName());
            student.setAge(descriptor.getAge());
            student.setPhone(descriptor.getPhone());
            student.setInstitution(descriptor.getInstitution());
            student.setEducationLevel(descriptor.getEducationLevel());
            student.setMajor(descriptor.getMajor());
            student.setMinor(descriptor.getMinor());
            student.setCvLink(descriptor.getCVLink());
            student.setOrigUserEmail(descriptor.getOrigEmail().toLowerCase());
            student.setCurrentUserEmail(descriptor.getCurrentEmail().toLowerCase());
            student.setAvatarName(descriptor.getAvatarName().toLowerCase());

            // We are not storing personal information to the users system
            // It is sent to the cloud.
            if (doAction(student)) {
                  FlashMonkeyMain.closeActionWindow();
            } else {
                  LOGGER.warn("Student data creation Form failed to be sent to the database for userName: {}", descriptor.getOrigEmail());
            }
      }

      /**
       * sends studentData to the db. If the student exists
       * it attempts an update, otherwise it conducts an insert.
       * returns true if successful else false.
       */
      @Override
      public boolean doAction(final FormData data) {
            authcrypt.user.EncryptedStud studentData = (EncryptedStud) data;

            // do insert
            boolean bool = DBInsert.STUDENT_ENCRYPTED_DATA.doInsert(studentData);
            // If successful, skip, otherwise do update
            if (!bool) {
                  LOGGER.debug("student exists, do update. studentID: {}", EncryptedPerson.getPersonId());
                  // Update database. If the user is in the database and their id is NOT returned.
                  if (EncryptedPerson.getPersonId() == -1) {
                        String whereStatement = " WHERE orig_email = '" + Alphabet.encrypt(UserData.getUserName()) + "'";
                        LOGGER.debug("whereStatement" + whereStatement);
                        return DBUpdate.STUDENT_ENCRYPTED_DATA.doUpdate(studentData, whereStatement);
                  } else {
                        String whereStatement = " WHERE person_id = '" + EncryptedPerson.getPersonId() + "'";
                        LOGGER.debug("whereStatement" + whereStatement);
                        return DBUpdate.STUDENT_ENCRYPTED_DATA.doUpdate(studentData, whereStatement);
                  }
            }
            return bool;
      }


      @Override
      public StudentDescriptor getDescriptor() {
            return descriptor;
      }

      @Override
      public void formAction() {
            /* stub */
      }

      // REMOVE THIS.... It is not used.
//      private long fetchStudentID(EncryptedStud student) {
//            String[] strs = {student.getOrigUserEmail()};
//            String[] response = DBFetchUnique.STUDENT_ENCRYPTED_DATA.query(strs);
//            //String[] strAry = response.split(",");
//
//            LOGGER.debug("fetchStudentID.strAry");
//
//            if (response[0].equals("EMPTY")) {
//                  return -1;
//            } else {
//                  return Long.parseLong(response[0]);
//            }
//      }

}
