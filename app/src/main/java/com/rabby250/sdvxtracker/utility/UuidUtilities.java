package com.rabby250.sdvxtracker.utility;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

// For UTF-8 reference
@TargetApi(Build.VERSION_CODES.KITKAT)
public class UuidUtilities {

    private static final String UTF_8
            = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
            StandardCharsets.UTF_8.name() : "UTF-8";

    public static String encodeToUrl(@NonNull final UUID input) {
        // UUID -> Base64
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(input.getMostSignificantBits());
        buffer.putLong(input.getLeastSignificantBits());
        // Must set NO_WRAP, or we'll get extra line feeds (%0A's)
        final String base64String = Base64.encodeToString(
                buffer.array(), Base64.NO_WRAP);
        // Base64 -> URL
        try {
            return URLEncoder.encode(base64String, UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static UUID decode(@NonNull String input) {
        // URL -> Base64
        if (input.contains("%")) {
            try {
                input = URLDecoder.decode(input, UTF_8);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        // Base64 -> UUID
        final ByteBuffer buffer = ByteBuffer.wrap(
                Base64.decode(input, Base64.DEFAULT));
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
