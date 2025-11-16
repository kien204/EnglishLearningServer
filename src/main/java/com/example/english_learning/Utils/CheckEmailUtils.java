package com.example.english_learning.Utils;

public class CheckEmailUtils {

    public static boolean isValidGmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        // Regex chỉ cho phép @gmail.com
        String regex = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
        return email.matches(regex);
    }

}
