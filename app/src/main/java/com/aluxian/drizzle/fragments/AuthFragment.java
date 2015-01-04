package com.aluxian.drizzle.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.aluxian.drizzle.MainActivity;
import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.exceptions.AuthorizationException;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Credentials;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class AuthFragment extends Fragment {

    private static final String AUTHORIZE_URL = "https://dribbble.com/oauth/authorize?client_id="
            + Config.API_CLIENT_ID + "&scope=public%20write%20comment%20upload&state=";

    /** A random string to prevent request forgery attacks. */
    private String mState = UUID.randomUUID().toString();

    /** The callbacks instance (the Activity). */
    private Callbacks mCallbacks;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);

        WebView webView = (WebView) view.findViewById(R.id.webview);
        webView.setWebViewClient(new AuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(AUTHORIZE_URL + mState);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //noinspection ConstantConditions
        activity.getActionBar().setTitle(R.string.auth_title);

        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AuthFragment.Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * WebViewClient that intercepts url changes. When the callback url is detected, it is parsed. Other urls are handled by the system.
     */
    private class AuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(Config.API_CALLBACK_URL)) {
                Uri uri = Uri.parse(url);
                Set<String> params = uri.getQueryParameterNames();

                if (!params.contains("state") || !uri.getQueryParameter("state").equals(mState)) {
                    Toast.makeText(getActivity(), getString(R.string.auth_invalid_attempt), Toast.LENGTH_LONG).show();
                    mCallbacks.authFlowEnded();
                } else if (params.contains("error")) {
                    if (!uri.getQueryParameter("error").equals("access_denied")) {
                        Toast.makeText(getActivity(), getString(R.string.auth_unexpected_error), Toast.LENGTH_LONG).show();
                    }

                    Log.e(new AuthorizationException(url));
                    mCallbacks.authFlowEnded();
                } else {
                    new ExchangeTokenAsyncTask().execute(uri.getQueryParameter("code"));
                }
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }

            return true;
        }

    }

    /**
     * Background task that exchanges the OAuth authorization code for an access token while showing a progress dialog for the user.
     */
    private class ExchangeTokenAsyncTask extends AsyncTask<String, Void, Dribbble.Response<Credentials>> {

        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity(), R.style.Drizzle_Dialog);
            mDialog.setMessage("Connecting to Dribbble...");
            mDialog.show();

            // TODO: Show a fake determinate progress for better UX?
            // TODO: minimum loading time
        }

        @Override
        protected Dribbble.Response<Credentials> doInBackground(String... params) {
            try {
                return new Dribbble().oauthToken(params[0]).execute();
            } catch (IOException | BadRequestException | TooManyRequestsException e) {
                Log.d(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Dribbble.Response<Credentials> response) {
            if (response != null) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sharedPrefs.edit().putString(MainActivity.PREF_API_AUTH_TOKEN, response.data.accessToken).apply();
            } else {
                Toast.makeText(getActivity(), "Exchange error", Toast.LENGTH_LONG).show();
            }

            mDialog.dismiss();
            mCallbacks.authFlowEnded();
        }

    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface Callbacks {

        /**
         * Called when the authentication flow has ended and the activity should show another fragment.
         */
        void authFlowEnded();

    }

}
