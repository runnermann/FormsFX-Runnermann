package campaign.db;

import authcrypt.user.EncryptedStud;
import com.github.jasync.sql.db.QueryResult;
import forms.utility.Alphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public enum DBUpdate {

    STUDENT_ENCRYPTED_DATA() {
        @Override
        public boolean doUpdate(EncryptedStud student, long studentID) {

            String btw = "','";
            String statement = "UPDATE Student SET" +
                    " person.first_name '" + Alphabet.encrypt(student.getFirstName()) + btw +
                    " person.last_name '" + Alphabet.encrypt(student.getLastName())  + btw +
                    " person.middle_name '" + student.getMiddleName() + btw +
                    " person.age '" + student.getAge() + btw +
                    " person.phone '" + student.getPhone() + btw +
                    " person.current_email '" + Alphabet.encrypt(authcrypt.UserData.getUserName()) + btw +
                    " person.institution '" + student.getInstitution() + btw +
                    " person.descript '" + student.getDescript() + btw +
                    " person.photo_link '" + "photo link" + btw +
                    " student.education_level '" + student.getEducationLevel() + btw +
                    " student.major '" + student.getMajor() + btw +
                    " student.minor '" + student.getMinor() + btw +
                    " student.cv_link '" + student.getCvLink() + btw +
                    " WHERE person_id =" + studentID + ";";

            System.out.println("doInsert: " + statement);

            boolean bool = query(statement);
            if(bool) {
                return true;
            }

            return false;

        }
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(DBUpdate.class);

    public abstract boolean doUpdate(EncryptedStud student, long studentID);
    public static boolean query(String statement) {
        DBConnect db = DBConnect.getInstance();
        try{
            CompletableFuture<QueryResult> future = db.getConnection()
                    .sendQuery(statement);
            future.get();
            return true;

        } catch (ExecutionException e) {
            LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
        } catch (InterruptedException e) {
            LOGGER.warn("WARNING: DBConnection ERROR, {}\n{}" + e.getMessage(), e.getStackTrace());
        }
        LOGGER.debug("returning an empty string");
        return false;
    }
}
