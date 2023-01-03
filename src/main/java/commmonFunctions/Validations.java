package commmonFunctions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {

    /**
     * Checks if password and confirm password are same.
     *
     * @param password
     * @param confirmPassword
     * @return
     */
    public static Boolean isPasswordAndConfirmPasswordSame(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the password and confirm password matches the pattern.
     *
     * @param password
     * @param confirmPassword
     * @return
     */
    public static Boolean doesPasswordMatchRegex(String password, String confirmPassword) {
        Pattern regexPattern = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,16}");
        Matcher passwordMatcher = regexPattern.matcher(password);
        Matcher confirmPasswordMatcher = regexPattern.matcher(confirmPassword);
        if (passwordMatcher.find() && confirmPasswordMatcher.find()) {
            return true;
        }
        return false;
    }
}
