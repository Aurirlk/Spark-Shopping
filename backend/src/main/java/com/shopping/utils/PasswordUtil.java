package com.shopping.utils;

import cn.dev33.satoken.secure.BCrypt;

/**
 * 密码工具类
 * 使用 SaToken BCrypt 加密验证
 */
public class PasswordUtil {

    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        try { return BCrypt.checkpw(rawPassword, encodedPassword); }
        catch (Exception e) { return false; }
    }
}
