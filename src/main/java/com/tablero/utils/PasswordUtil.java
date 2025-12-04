package com.tablero.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainText, String hashedPassword){
        return BCrypt.checkpw(plainText, hashedPassword);
    }
}
