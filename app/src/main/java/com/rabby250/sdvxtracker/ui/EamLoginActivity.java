package com.rabby250.sdvxtracker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.rabby250.sdvxtracker.R;
import com.rabby250.sdvxtracker.content.EamPortal;
import com.rabby250.sdvxtracker.utility.Utilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class EamLoginActivity extends AppCompatActivity {

    // RESULT_OK if logged in successfully and cookie generated
    // RESULT_CANCELED upon onBackPressed()
    public static final int RESULT_FAILED = 1;

    // Starts sync service after login
    public static final String INTENT_ACTION_EAM_LOGIN_SYNC
            = Utilities.INTENT_PREFIX + ".action.EAM_LOGIN_SYNC";
    public static final String INTENT_EXTRA_COOKIES
            = Utilities.INTENT_PREFIX + ".extra.COOKIES";

    private static final String URL_PATH_LOGIN
            = "/gate/p/login.html";
    private static final String URL_PATH_LOGIN_COMPLETE
            = "/gate/p/login_complete.html";

    private static final String COOKIE_WEBSITE = "p.eagate.573.jp";
    private static final String COOKIE_NAME = "M573SSID";

    private WebView mLoginView;
    private ProgressBar mProgressBar;

    private Intent mLoginResult;

    private WebViewClient mLoginClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(
                final WebView view, final String url) {
            /*
             * The redirection flow after logging in is listed below:
             * 1. login.html?login=success&&___REDIRECT=1
             * 2. login_complete.html (note that if the login session
             *    was still active, users will be redirected to this
             *    page directly instead of having to login again)
             * In our case, we will look for step 2 since it covers
             * both new and existing login sessions.
             */
            try {
                final String path = new URL(url).getPath();
                if (URL_PATH_LOGIN_COMPLETE.equalsIgnoreCase(path)) {
                    // NEVER, EVER output authCookies to Logcat
                    setLoginResult(Utilities.getCookie(
                            COOKIE_WEBSITE, COOKIE_NAME));
                    finish();
                    return true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void onPageStarted(
                final WebView view, final String url,
                final Bitmap favicon) {
            showProgressBar();
        }

        @Override
        public void onPageFinished(
                final WebView view, final String url) {
            hideProgressBar();
        }
    };

    @RequiresPermission(Manifest.permission.INTERNET)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eam_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        showProgressBar();

        mLoginView = (WebView) findViewById(R.id.login_page);
        mLoginView.setWebViewClient(mLoginClient);
        // JavaScript required for reCAPTCHA
        mLoginView.getSettings().setJavaScriptEnabled(true);
        mLoginView.loadUrl(EamPortal.BASE_URL + URL_PATH_LOGIN);
    }

    @Override
    public void finish() {
        if (mLoginResult != null) {
            setResult(RESULT_OK, mLoginResult);
        }
        // TODO: if the action was to start service, start it now
        super.finish();
    }

    private void setLoginResult(@Nullable final String authCookie) {
        if (TextUtils.isEmpty(authCookie)) {
            return;
        }
        final HashMap<String, String> cookies = new HashMap<>();
        cookies.put(COOKIE_NAME, authCookie);
        if (mLoginResult == null) {
            mLoginResult = new Intent();
        } // Add result replacement notice if required
        mLoginResult.putExtra(INTENT_EXTRA_COOKIES, cookies);
    }

    // TODO: add back and refresh button on menu

    private void showProgressBar() {
        // Fade-in animation can be added here if required
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        // Fade-out animation can be added here if required
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
