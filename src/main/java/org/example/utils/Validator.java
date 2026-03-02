package org.example.utils;

import java.util.regex.Pattern;

public class Validator {

    // 简单的手机号正则表达式（11位数字）
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 校验手机号
     */
    public static boolean isPhoneValid(String phone) {
        if (phone == null) return false;
        return Pattern.matches(PHONE_REGEX, phone);
    }

    /**
     * 校验字符串是否为空（用于必填项校验）
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 校验密码长度（例如要求至少6位）
     */
    public static boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 6;
    }
}