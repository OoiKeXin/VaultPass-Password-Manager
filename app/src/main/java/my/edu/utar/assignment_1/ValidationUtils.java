package my.edu.utar.assignment_1;

public class ValidationUtils {
    // A standard Regex that doesn't need Android's "Patterns" class
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches(EMAIL_REGEX) && email.toLowerCase().endsWith(".com");
    }
}
