package my.edu.utar.assignment_1;

import java.util.Random;

public class PasswordUtils {

    public static String generateRandomPassword(int length, boolean upper, boolean lower, boolean numbers, boolean symbols) {
        StringBuilder chars = new StringBuilder();
        if (upper) chars.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        if (lower) chars.append("abcdefghijklmnopqrstuvwxyz");
        if (numbers) chars.append("0123456789");
        if (symbols) chars.append("!@#$%^&*()");
        
        if (chars.length() == 0) return "";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
