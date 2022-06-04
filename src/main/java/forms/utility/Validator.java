package forms.utility;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Validator {
	
	private Pattern pattern;
	private String errorMessage;

	private Validator(String pattern, String errorMessage) throws PatternSyntaxException {
		this.errorMessage = errorMessage;
		this.pattern = Pattern.compile(pattern);
		//if(pattern.matches(input));
	}
	
	
	/**
	 * Multi-language Validate isAlphaNumeric
	 * @param message
	 * @return
	 */
	public static Validator forWord(String message) {
		return new Validator("[0-9\\p{L}]*", message);
	}
	
	
	public static Validator forEmail(String message) {
		return new Validator("^(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-z0-9](?:[a-zA-Z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$", message);
	}
	
	public static Validator forURL(String message) {
		return new Validator("(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})", message);
	}
	
	public static Validator forPassword(String message) {
		return new Validator("[0-9!#$%&?@\\p{L}]*", message);
	}
	
	
	/*public static void main(String[] args) {
		// Regex tested to recognize aribic and chineese letters
		// and to not match
		String regex = "[0-9!#$%&?@\\p{L}]*";
		Pattern pattern;
		String aribic = "كلالكلابتذهبإلىالجن"; // passes
		
		pattern = Pattern.compile(regex);
		boolean result = pattern.matcher(aribic).matches();
		
		//System.out.println("Result of String: " + aribic + " with regex: " + regex + " isMatch(): " + result);
	}*/
	
	private class Word {
		
		private Word class_instance;
		private long expire;
		private String word;
		
		private Word() {
			// clear register of current word
			word = "!aword& -+=";
			// release memory and reassign
			word = new String();
		}
		
//		working on encryption of password, authcrypt.user data, and ??? for encryption of decks???
		
		synchronized Word getClassInstance() {
			//if(class_instance == null) {
			this.class_instance = new Word();
			//}
			return class_instance;
		}
		
		synchronized String validate(String word) {
			
			// hash the word
			
			// compare with stored hash and stored salt
			
			// return hash of this class??? and returned hash and store for local
			// confirmation and decrypt/encrypt.
			return "blablabla";
		}
		
		
		
		
	}
}
