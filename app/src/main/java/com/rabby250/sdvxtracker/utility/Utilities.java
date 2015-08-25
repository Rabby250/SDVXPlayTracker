package com.rabby250.sdvxtracker.utility;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.regex.Pattern;

public class Utilities {

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
}
