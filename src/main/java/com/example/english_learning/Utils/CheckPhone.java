package com.example.english_learning.Utils;

public class CheckPhone {
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        // Regex chỉ cho phép @gmail.com
        String regex = "^0\\d{9}$";
        return phone.matches(regex);
    }
}
