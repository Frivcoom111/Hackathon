package com.portal.util;

public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidRa(String ra) {
        if (ra == null || ra.isBlank()) return false;
        return ra.matches("\\d{5,20}");
    }

    public static boolean isValidCpf(String cpf) {
        if (cpf == null) return false;
        String digits = cpf.replaceAll("[^\\d]", "");

        if (digits.length() != 11) return false;
        if (digits.chars().distinct().count() == 1) return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (digits.charAt(i) - '0') * (10 - i);
        int first = (sum * 10 % 11) % 10;
        if (first != digits.charAt(9) - '0') return false;

        sum = 0;
        for (int i = 0; i < 10; i++) sum += (digits.charAt(i) - '0') * (11 - i);
        int second = (sum * 10 % 11) % 10;
        return second == digits.charAt(10) - '0';
    }

    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null) return false;
        String digits = cnpj.replaceAll("[^\\d]", "");

        if (digits.length() != 14) return false;
        if (digits.chars().distinct().count() == 1) return false;

        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < 12; i++) sum += (digits.charAt(i) - '0') * weights1[i];
        int first = sum % 11 < 2 ? 0 : 11 - sum % 11;
        if (first != digits.charAt(12) - '0') return false;

        sum = 0;
        for (int i = 0; i < 13; i++) sum += (digits.charAt(i) - '0') * weights2[i];
        int second = sum % 11 < 2 ? 0 : 11 - sum % 11;
        return second == digits.charAt(13) - '0';
    }

    /** 14 dígitos → XX.XXX.XXX/XXXX-XX */
    public static String formatCnpj(String cnpj) {
        if (cnpj == null) return "";
        String d = cnpj.replaceAll("[^\\d]", "");
        if (d.length() != 14) return cnpj;
        return d.substring(0, 2) + "." + d.substring(2, 5) + "." +
               d.substring(5, 8) + "/" + d.substring(8, 12) + "-" + d.substring(12);
    }

    /** 10 dígitos (fixo) → (XX) XXXX-XXXX
     *  11 dígitos (celular) → (XX) XXXXX-XXXX */
    public static String formatPhone(String phone) {
        if (phone == null) return "";
        String d = phone.replaceAll("[^\\d]", "");
        if (d.length() == 11) {
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 7) + "-" + d.substring(7);
        } else if (d.length() == 10) {
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 6) + "-" + d.substring(6);
        }
        return phone;
    }
}
