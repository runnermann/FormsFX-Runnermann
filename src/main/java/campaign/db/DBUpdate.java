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
        public boolean doUpdate(EncryptedStud student, String whereStatement) {

            LOGGER.debug("DBUpdate.doUpdate() called");
 // (Message, column "person" of relation "student" does not exist), (Position, 21), (File, analyze.c), (Line, 2346), (Routine, transformUpdateTargetList)])
            String btw = "'";
            String statement = "BEGIN; " +
                    " UPDATE Person SET " +
                    " first_name = '" + Alphabet.encrypt(student.getFirstName()) + btw +
                    ", last_name = '" + Alphabet.encrypt(student.getLastName())  + btw +
                    ", middle_name = '" + student.getMiddleName() + btw +
                    ", age = '" + student.getAge() + btw +
                    ", phone = '" + student.getPhone() + btw +
                    ", current_email = '" + Alphabet.encrypt(authcrypt.UserData.getUserName()) + btw +
                    ", institution = '" + student.getInstitution() + btw +
                    ", descript = '" + student.getDescript() + btw +
                    ", photo_link = '" + "photo link" + "' " +
                    whereStatement + ";" +
                    " UPDATE Student SET " +
                    " education_level = '" + student.getEducationLevel() + btw +
                    ", major = '" + student.getMajor() + btw +
                    ", minor = '" + student.getMinor() + btw +
                    ", cv_link = '" + student.getCvLink() + btw +
                    whereStatement + ";" +
                    " COMMIT;";

            System.out.println("sending statement: {}" + statement);

            return query(statement);
        }
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(DBUpdate.class);

    public abstract boolean doUpdate(EncryptedStud student, String whereStatement);
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
