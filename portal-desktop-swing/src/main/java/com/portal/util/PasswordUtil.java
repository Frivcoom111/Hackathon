package com.portal.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {

    public static boolean verify(String rawPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}
