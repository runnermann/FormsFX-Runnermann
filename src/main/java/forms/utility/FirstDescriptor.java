package forms.utility;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * <p>Non standard Descriptor, used by
 * SignUp and SignIn models. This class should not be
 * used for anything else. For this class, we do not
 * want to expose users data to users
 * who are not logged in to the correct
 * account.</p>
 * <p>Form data is/should always be initialized to empty strings.
 * and accessors should not access the DB nor Files</p>
 */
public class FirstDescriptor {
	
	private StringProperty siFirstName = new SimpleStringProperty("");
	private StringProperty siOrigEmail = new SimpleStringProperty("");
	private StringProperty siAge = new SimpleStringProperty("");
	
	/**
	 * No args constructor
	 */
	public FirstDescriptor() { /* do nothing */ }
	
	/**
	 * SignIn and SignUp forms
	 * related
	 * @return firstName if available
	 */
	public String getSiFirstName() {
		return siFirstName.get();
	}
	/**
	 * SignIn and SignUp forms
	 * related
	 * @return origEmail if available
	 */
	public String getSiOrigEmail() {
		return siOrigEmail.get().toLowerCase();
	}
	/**
	 * SignIn and SignUp forms
	 * related
	 * @return age if available
	 */
	public String getSiAge() { return siAge.get(); }
	
	
	/**
	 * SignIn and SignUp forms
	 * related
	 * @return firstName property if available
	 */
	public StringProperty siFirstNameProperty() {
		return siFirstName;
	}
	
	/**
	 * SignIn and SignUp forms
	 * related
	 * @return origEmail property if available
	 */
	public StringProperty siOrigEmailProperty() {
		return siOrigEmail;
	}
	
	/**
	 * SignIn and SignUp forms
	 * related
	 * @return age property if available
	 */
	public StringProperty siAgeProperty() {
		return siAge;
	}
	
	public void clear() {
		siFirstName = new SimpleStringProperty("");
		siOrigEmail = new SimpleStringProperty("");
		siAge = new SimpleStringProperty("");
	}
}
