package forms.utility;

public class Utility {

    /**
     * Filters a single word string for correct capitalization.
     *      * @returns str if not null, with the first letter capitalized,
     * @param str The users first name
     * @return A new string with the first letter capitalized.
     * @throws IllegalArgumentException if str is empty
     */
    public static String firstCapitol( String str) throws IllegalArgumentException {
        if(str == null || str.isBlank()) {
            throw new IllegalArgumentException("Illegal argument, str cannot be blank");
        }
        str = str.trim();
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
