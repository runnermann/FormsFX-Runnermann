package campaign.db;

import authcrypt.user.EncryptedStud;
import com.github.jasync.sql.db.QueryResult;
import forms.FormData;
import forms.utility.Alphabet;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public enum DBInsert {
	
	STUDENT_ENCRYPTED_DATA() {
		
		@Override
		public boolean doInsert(EncryptedStud student) {
			String btw = "','";
			/*String statement = //" BEGIN; " +
					"INSERT INTO Student (first_name, last_name, middle_name, orig_email, current_email, phone, age, ip_connect, institution, descript, photo_link, education_level, major, minor, cv_link) " +
					" VALUES ('" +
							Alphabet.encrypt(student.getFirstName()) + btw +
							Alphabet.encrypt(student.getLastName())  + btw +
							student.getMiddleName() + btw +
							Alphabet.encrypt(authcrypt.UserData.getUserName()) + btw +
							Alphabet.encrypt(authcrypt.UserData.getUserName()) + btw +
							student.getPhone() + btw +
							student.getAge() + btw +
							"ip address" + btw +
							student.getInstitution() + btw +
							student.getDescript() + btw +
							"photo link" + btw + //student.getPhotoLink() +
							student.getEducationLevel() + btw +
							student.getMajor() + btw +
							student.getMinor() + btw +
							student.getCvLink() +
					"');";// " +
					//"COMMIT;";

			 */
			String statement = //" BEGIN; " +
					" INSERT INTO Person (first_name, last_name, middle_name, orig_email, current_email, phone, age, ip_connect, institution, descript, photo_link) " +
							" VALUES ('" +
							Alphabet.encrypt(student.getFirstName()) + btw +
							Alphabet.encrypt(student.getLastName())  + btw +
							student.getMiddleName() + btw +
							// currently the orig_email
							Alphabet.encrypt(authcrypt.UserData.getUserName()) + btw +
							Alphabet.encrypt(authcrypt.UserData.getUserName()) + btw +
							student.getPhone() + btw +
							student.getAge() + btw +
							"ip address" + btw +
							student.getInstitution() + btw +
							student.getDescript() + btw +
							student.getPhotoLink() +
							"'); " +
							// insert all (orig_email, education_level, major, minor, cv_link)
							"INSERT INTO Student  VALUES ('" +
							// currently the orig_email
							Alphabet.encrypt(authcrypt.UserData.getUserName()) + btw +
							student.getEducationLevel() + btw +
							student.getMajor() + btw +
							student.getMinor() + btw +
							student.getCvLink() +
							"'); " +
							"COMMIT;";
			
			System.out.println("doInsert: " + statement);
			
			boolean bool = query(statement);
			if(bool) {
				return true;
			}
			
			return false;
		}
	};

	private static final Logger LOGGER = LoggerFactory.getLogger(DBInsert.class);

	DBInsert() { /* NO ARGS CONSTRUCTOR */ }
	
	public abstract boolean doInsert(EncryptedStud student);
	private static boolean query(String statement) {

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
