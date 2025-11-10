package com.druv.scheduler;

import java.security.NoSuchAlgorithmException;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String plaintext, String hashed) {
        try {
            return BCrypt.checkpw(plaintext, hashed);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
