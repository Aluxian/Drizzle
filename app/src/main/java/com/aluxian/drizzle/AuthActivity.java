package com.aluxian.drizzle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.exceptions.AuthorizationException;
import com.aluxian.drizzle.api.models.Credentials;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;

import java.util.Set;
import java.util.UUID;

public class AuthActivity extends Activity {

    private static final String AUTHORIZE_URL = "https://dribbble.com/oauth/authorize?client_id="
            + Config.API_CLIENT_ID + "&scope=public%20write%20comment%20upload&state=";

    private String mState = UUID.randomUUID().toString();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new AuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(AUTHORIZE_URL + mState);
    }

    /**
     * WebViewClient that intercepts the callback url.
     */
    private class AuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(Config.API_CALLBACK_URL)) {
                Uri uri = Uri.parse(url);
                Set<String> params = uri.getQueryParameterNames();

                if (!params.contains("state") || !mState.equals(uri.getQueryParameter("state"))) {
                    Toast.makeText(AuthActivity.this, getString(R.string.auth_invalid_attempt), Toast.LENGTH_LONG).show();
                } else if (params.contains("error")) {
                    Log.e(new AuthorizationException(url));

                    if (!uri.getQueryParameter("error").equals("access_denied")) {
                        Toast.makeText(AuthActivity.this, getString(R.string.auth_unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    new ExchangeTokenAsyncTask(uri.getQueryParameter("code")).execute();
                }

                finish();
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }

            return true;
        }

    }

    private class ExchangeTokenAsyncTask extends AsyncTask<Void, Void, Dribbble.Response<Credentials>> {

        private final String mCode;

        public ExchangeTokenAsyncTask(String code) {
            mCode = code;
        }

        @Override
        protected Dribbble.Response<Credentials> doInBackground(Void... params) {
            return Dribbble.oauthToken(mCode).execute();
        }

        @Override
        protected void onPostExecute(Dribbble.Response<Credentials> response) {
            PreferenceManager.getDefaultSharedPreferences(AuthActivity.this)
                    .edit().putString(MainActivity.PREF_API_AUTH_TOKEN, response.data.accessToken).apply();
        }

    }

}
