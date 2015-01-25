package com.aluxian.drizzle.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.exceptions.AuthorizationException;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Credentials;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.UserManager;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class AuthActivity extends Activity {

    private static final String AUTHORIZE_URL = "https://dribbble.com/oauth/authorize?client_id="
            + Config.API_CLIENT_ID + "&scope=public%20write%20comment%20upload&state=";

    /** A random string to prevent request forgery attacks. */
    private String mState = UUID.randomUUID().toString();

    MenuItem loadingItem;

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Load the toolbar
        setActionBar((Toolbar) findViewById(R.id.toolbar));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Clean previous session data
        CookieManager.getInstance().removeAllCookies(null);

        // Initialise the WebView
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new AuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSaveFormData(false);
        webView.setOnLongClickListener(v -> true);
        webView.loadUrl(AUTHORIZE_URL + mState);

        Log.d("Loading authorization url: " + AUTHORIZE_URL + mState);
    }

    /**
     * WebViewClient that intercepts the callback url and parses it.
     */
    private class AuthWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            loadingItem.setVisible(false);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("Loading url: " + url);

            if (url.startsWith(Config.API_CALLBACK_URL)) {
                Uri uri = Uri.parse(url);
                Set<String> params = uri.getQueryParameterNames();

                if (!params.contains("state") || !uri.getQueryParameter("state").equals(mState)) {
                    Toast.makeText(AuthActivity.this, getString(R.string.auth_invalid_attempt), Toast.LENGTH_LONG).show();
                    finish();
                } else if (params.contains("error")) {
                    if (!uri.getQueryParameter("error").equals("access_denied")) {
                        Toast.makeText(AuthActivity.this, getString(R.string.auth_unexpected_error), Toast.LENGTH_LONG).show();
                    }

                    finish();
                    Log.e(new AuthorizationException(url));
                } else {
                    new ExchangeTokenAsyncTask().execute(uri.getQueryParameter("code"));
                }

                return true;
            }

            loadingItem.setVisible(true);
            return false;
        }

    }

    /**
     * Background task that exchanges the OAuth authorization code for an access token.
     */
    private class ExchangeTokenAsyncTask extends AsyncTask<String, Void, Dribbble.Response<Credentials>> {

        @Override
        protected Dribbble.Response<Credentials> doInBackground(String... params) {
            Dribbble.Response<Credentials> response = null;
            long startedAt = System.currentTimeMillis();

            try {
                response = Dribbble.oauthToken(params[0]).execute();
            } catch (IOException | BadRequestException | TooManyRequestsException e) {
                Log.d(e);
            }

            synchronized (this) {
                while (System.currentTimeMillis() - startedAt < 2000) {
                    try {
                        wait(100);
                    } catch (InterruptedException e) {
                        Log.e(e);
                    }
                }
            }

            // no internet message /error listener
            return response;
        }

        @Override
        protected void onPostExecute(Dribbble.Response<Credentials> response) {
            if (response != null) {
                Log.d("Token exchange successful, saving token to UserManager");
                UserManager.getInstance().putAccessToken(response.data.accessToken);
            } else {
                Toast.makeText(AuthActivity.this, getString(R.string.auth_unexpected_error), Toast.LENGTH_LONG).show();
            }

            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.auth, menu);
        loadingItem = menu.findItem(R.id.loading);
        loadingItem.setActionView(R.layout.inflate_indeterminate_progress);
        ((ProgressBar) loadingItem.getActionView().findViewById(R.id.progress_bar)).getIndeterminateDrawable().setTint(Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
