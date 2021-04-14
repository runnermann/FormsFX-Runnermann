package forms;


import authcrypt.user.EncryptedPerson;
import authcrypt.user.EncryptedStud;
import campaign.db.DBDelete;
import campaign.db.DBFetchUnique;
import campaign.db.DBInsert;
import ch.qos.logback.classic.Level;
import forms.utility.StudentDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.InvocationTargetException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("editedShapes")
public class StudentFormTest extends ApplicationTest {
    // private static final Logger LOGGER = LoggerFactory.getLogger(StudentFormTest.class);
    // LOGGING
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(StudentFormTest.class);

    // Insert data into the DB for the purpose of the tests.
        String description = "The description is here.";
        String lastName = "LastName";
        String firstName = "FirstName";
        String middle   = "M";
        String age      = "19";
        String phone    = "000.000.0000";
        String currentMail = "nameNotSet@flashmonkey.xyz";
        String origMail = "nameNotSet@flashmonkey.xyz";
        String photoLink = "photoLink/me.jpg";


        String institute= "Institute";
        String edLevel  = "EdLevel";
        String major    = "Major";
        String minor    = "Minor";
        String cVLink   = "CVLink";

    EncryptedPerson ePerson;
    EncryptedStud encryptedStud;
    StudentDescriptor studDescript;



    public void setUpStudent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ePerson = new EncryptedPerson();

        ePerson.setAll(-1, phone, firstName, lastName, middle, currentMail, origMail, age, description, institute, photoLink);
        encryptedStud = new EncryptedStud(ePerson, edLevel, major, minor, cVLink);
    }

    // Verify that the data is correctly set up.
    @Test
    @Order(0)
    public void testStudentInsert() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        setUpStudent();
        // Set the students data using the descriptor

        Assert.assertTrue(encryptedStud.getDescript().equals(description));
        Assert.assertTrue(encryptedStud.getLastName().equals(lastName));
        Assert.assertTrue(encryptedStud.getFirstName().equals(firstName));
        Assert.assertTrue(encryptedStud.getMiddleName().equals(middle));
        Assert.assertTrue(encryptedStud.getAge().equals(age));
        Assert.assertTrue(encryptedStud.getPhone().equals(phone));
        Assert.assertTrue(encryptedStud.getCurrentUserEmail().equals(currentMail));
        Assert.assertTrue(encryptedStud.getOrigUserEmail().equals(origMail));
        Assert.assertTrue(encryptedStud.getPhotoLink().equals(photoLink));
        Assert.assertTrue(encryptedStud.getInstitution().equals(institute));
        Assert.assertTrue(encryptedStud.getEducationLevel().equals(edLevel));
        Assert.assertTrue(encryptedStud.getMajor().equals(major));
        Assert.assertTrue(encryptedStud.getMinor().equals(minor));
        Assert.assertTrue(encryptedStud.getCvLink().equals(cVLink));
    }

    // Insert and retrieve the data from the DB
    @Test
    @Order(1)
    public void testStudentInsertDataBase() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        LOGGER.setLevel(Level.DEBUG);

        setUpStudent();
        // student should remain setup from previous test.
        // CLear the DB if the student already exists
        String[] args = {origMail};
        String[] result = DBFetchUnique.PERSON_ID.query(args);
        delete(result);

        // Insert data to the DB
        boolean bool = DBInsert.STUDENT_ENCRYPTED_DATA.doInsert(encryptedStud);
        Assert.assertTrue(bool == true);

        // clear encrypted student
        encryptedStud = new EncryptedStud();
        // extract data from DB and test for correct answer
        studDescript = new StudentDescriptor();

        result = DBFetchUnique.PERSON_ID.query(args);
        delete(result);

        Assert.assertFalse("student is null", encryptedStud == null);

        System.out.println(studDescript.getCurrentEmail() + " vs " + currentMail);

        Assert.assertTrue(studDescript.getPersonDescript().equals(description));
        Assert.assertTrue(studDescript.getAge().equals(age));
        Assert.assertTrue(studDescript.getCVLink().equals(cVLink));
        Assert.assertTrue(studDescript.getEducationLevel().equals(edLevel));
        Assert.assertTrue(studDescript.getMajor().equals(major));
        Assert.assertTrue(studDescript.getMinor().equals(minor));
        // original email is not retrievable once set, that is the users name
 //       Assert.assertTrue(studDescript.getOrigEmail().equals(origMail) );  //not set on return from DB
 //       Assert.assertTrue(studDescript.getCurrentEmail().equals(currentMail)); //not set on return from DB
 //       Assert.assertTrue(studDescript.getFirstName().equals(firstName));
        System.out.println("last name = " + lastName + " vs "+ studDescript.getLastName());
        Assert.assertTrue(studDescript.getLastName().equals(lastName));
        Assert.assertTrue(studDescript.getInstitution().equals(institute) );
        Assert.assertTrue(studDescript.getMiddleName().equals(middle));
        Assert.assertTrue(studDescript.getPhone().equals(phone) );
        Assert.assertTrue(studDescript.getPhotoLink().equals(photoLink) );
    }

    // Ensure the data is as expected

    // DELETE Data from the DB that is not neccessary for the next tests

    // Enter data into the Student form

    // Verify that the Encrypted Person is correct

    // verify that the Form properly displays data when it is opened a second time

    //


    private boolean delete(String[] result) {
        if(! result[0].equals("EMPTY") ) {
            LOGGER.debug("result: {}", result[0]);
            return DBDelete.PERSON_BY_ID.query(result[0]);
        }
        return false;
    }


}
