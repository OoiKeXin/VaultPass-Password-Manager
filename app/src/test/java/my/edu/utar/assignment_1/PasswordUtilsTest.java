package my.edu.utar.assignment_1;

import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordUtilsTest {

    @Test
    public void generatePassword_isCorrectLength() {
        int length = 10;
        String password = PasswordUtils.generateRandomPassword(length, true, true, true, true);
        assertEquals(length, password.length());
    }

    @Test
    public void generatePassword_containsOnlyNumbers() {
        String password = PasswordUtils.generateRandomPassword(20, false, false, true, false);
        assertTrue(password.matches("[0-9]+"));
    }

    @Test
    public void generatePassword_isEmptyWhenNoOptionsSelected() {
        String password = PasswordUtils.generateRandomPassword(10, false, false, false, false);
        assertEquals("", password);
    }
}
