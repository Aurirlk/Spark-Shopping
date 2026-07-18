package com.shopping.utils;

import cn.dev33.satoken.stp.StpUtil;

public class UserContext {

    public static void setUserId(Long userId) {
        // SaToken 管理登录状态，不再需要 ThreadLocal
    }

    public static Long getUserId() {
        try {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            return loginId != null ? Long.parseLong(loginId.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void clear() {
        // SaToken 自动管理
    }
}
