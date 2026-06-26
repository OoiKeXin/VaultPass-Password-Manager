package my.edu.utar.assignment_1;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationUtilsTest {

    @Test
    public void email_isValid_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("user@example.com"));
        assertTrue(ValidationUtils.isValidEmail("test.name@college.com"));
    }

    @Test
    public void email_isInvalid_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("user@example")); // Missing .com
        assertFalse(ValidationUtils.isValidEmail("plainaddress")); // Not an email
        assertFalse(ValidationUtils.isValidEmail("@missinguser.com")); // Missing user
    }

    @Test
    public void email_withUpper_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("USER@EXAMPLE.COM"));
    }
}
