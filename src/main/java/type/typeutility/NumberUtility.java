package type.typeutility;

public abstract class NumberUtility {

    /**
     * Checks if the String is a number and handles
     * the error with a message, or returns
     * the original String.
     * @param inStr
     * @return If the string provided is a valid Double
     * returns the String, else returns an error message.
     */
    public static String isNumber(String inStr) {
        try {
            Double.parseDouble(inStr);
        }
        catch(NumberFormatException e) {
            return "Please enter a valid number";
        }

        return inStr;
    }
}
