package com.rabby250.sdvxtracker.utility;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;

import java.util.regex.Pattern;

public class Utilities {

    public static final String INTENT_PREFIX
            = "com.rabby250.sdvxtracker.intent";

    private static Pattern REGEX_FIND_NAN_CHARS
            = Pattern.compile("\\D");
    private static Pattern REGEX_FIND_WHITESPACE
            = Pattern.compile("\\s");

    public static int extractNumber(
            @Nullable String input, int fallback) {
        if (input == null) {
            return fallback;
        }
        input = REGEX_FIND_NAN_CHARS.matcher(input).replaceAll("");
        if (TextUtils.isEmpty(input)) {
            return fallback;
        }
        return Integer.parseInt(input);
    }

    public static String removeWhitespace(@Nullable String input) {
        if (input == null) {
            return null;
        }
        return REGEX_FIND_WHITESPACE.matcher(input).replaceAll("");
    }

    public static String getCookie(
            @Nullable String website, @Nullable String name) {
        if (TextUtils.isEmpty(website) || TextUtils.isEmpty(name)) {
            return null;
        }
        final String cookies
                = CookieManager.getInstance().getCookie(website);
        if (TextUtils.isEmpty(cookies)) {
            return null;
        }
        // getCookie() returns in format "NAME=VALUE [; NAME=VALUE]"
        String[] cookie;
        for (String cookieString : cookies.split("; ")) {
            cookie = cookieString.split("=", 2);
            if (name.equalsIgnoreCase(cookie[0])) {
                return cookie[1];
            }
        }
        return null;
    }
}
