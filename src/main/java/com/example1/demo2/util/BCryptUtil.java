package com.example1.demo2.util;
import org.mindrot.jbcrypt.BCrypt;

//哈希加密密码工具
public class BCryptUtil {
    private static final int STRENGTH = 10;

    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(STRENGTH));
    }

    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}