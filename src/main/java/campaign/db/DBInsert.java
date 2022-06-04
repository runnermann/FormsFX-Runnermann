package campaign.db;

import authcrypt.UserData;
import authcrypt.user.EncryptedStud;
import ch.qos.logback.classic.Level;
import com.github.jasync.sql.db.QueryResult;
import flashmonkey.Timer;
import forms.utility.Alphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public enum DBInsert {
	
	STUDENT_ENCRYPTED_DATA() {
		@Override
		public boolean doInsert(EncryptedStud student) {
			//LOGGER.setLevel(Level.DEBUG);
			LOGGER.debug("orig_email: {} ", student.getOrigUserEmail());
			LOGGER.debug("userName: {}", authcrypt.UserData.getUserName());

			String statement = //" BEGIN; " +
					" INSERT INTO Person (first_name, last_name, middle_name, orig_email, current_email, phone, age, ip_connect, institution, descript, photo_link) " +
							" VALUES ('" +
							Alphabet.encrypt(student.getFirstName()) + btw +
							Alphabet.encrypt(student.getLastName())  + btw +
							student.getMiddleName() + btw +
							Alphabet.encrypt(student.getOrigUserEmail()) + btw +
							Alphabet.encrypt(student.getOrigUserEmail()) + btw +
							student.getPhone() + btw +
							student.getAge() + btw +
							"ip address" + btw +
							student.getInstitution() + btw +
							student.getDescript() + btw +
							student.getPhotoLink() +
							"'); " +

							"INSERT INTO Student  VALUES ('" +
							// currently the orig_email
							Alphabet.encrypt(student.getOrigUserEmail()) + btw +
							student.getEducationLevel() + btw +
							student.getMajor() + btw +
							student.getMinor() + btw +
							student.getCvLink() +
							"'); " +
							"COMMIT;";
			
			return query(statement);
		}
	},
	/**
	 * Note: Does not use EncryptedStudent.
	 */
	SESSION_NOTE() {
		private Timer fmTimer = Timer.getClassInstance();
		@Override
		public boolean doInsert(EncryptedStud student) {

		    String name = UserData.getUserName() != null ? Alphabet.encrypt(UserData.getUserName()) : "not set yet";

			String statement = "INSERT INTO sessions (uhash, event_localtime, note) " +
					" VALUES ('" +
					name + btw +
					fmTimer.getBeginTime() + btw +
					fmTimer.getNote() +
					"'); ";

			return query((statement));
		}

	};;

	String btw = "','";
	// LOGGING
	private static final Logger LOGGER = LoggerFactory.getLogger(DBInsert.class);
	//private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DBInsert.class);

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
			LOGGER.warn("WARNING: DB ExecutionException, {}\n{}" + e.getMessage(), e.getStackTrace());
		} catch (InterruptedException e) {
			LOGGER.warn("WARNING: DB InterruptedException, {}\n{}" + e.getMessage(), e.getStackTrace());
		}
		LOGGER.debug("returning an empty string");
		return false;
	}
}
