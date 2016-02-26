package com.rabby250.sdvxtracker.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;

public class EamPortal {

    public static final String BASE_URL
            = "https://p.eagate.573.jp";
    public static final String LOGIN_URL
            = BASE_URL + "/gate/p/login.html";

    private static final String CREDENTIAL_KONAMI_ID = "KID";
    private static final String CREDENTIAL_PASSWORD = "pass";
    private static final String CREDENTIAL_ONE_TIME_PASS = "OTP";

    private static final String QUERY_ERROR_TEXT = "div.error_text_box";

    public static Map<String, String> login(
            @NonNull final String konamiId,
            @NonNull final String password,
            @Nullable String oneTimePass)
            throws IOException {
        final Connection.Response response = Jsoup.connect(LOGIN_URL)
                .data(buildCredentials(konamiId, password, oneTimePass))
                .method(Connection.Method.POST).execute();
        if (response == null) {
            throw new IOException("Login failed: empty response");
        }
        /*
         * If login fails, we will be redirected back to the login page,
         * which now contains a message field describing the error.
         * For now we'll just simply use the message field to determine
         * whether we're logged in or not.
         */
        final Element errorText = response.parse()
                .select(QUERY_ERROR_TEXT).first();
        if (errorText != null) {
            return null;
        }
        return response.cookies();
    }

    private static String[] buildCredentials(
            @NonNull final String konamiId,
            @NonNull final String password,
            @Nullable String oneTimePass) {
        /*
         * All three fields are required to login successfully.
         * If user does not have an OTP, we still need to pass in
         * an empty String for logging in.
         */
        if (oneTimePass == null) {
            oneTimePass = "";
        }
        return new String[] {
                CREDENTIAL_KONAMI_ID, konamiId,
                CREDENTIAL_ONE_TIME_PASS, oneTimePass,
                CREDENTIAL_PASSWORD, password
        };
    }
}
