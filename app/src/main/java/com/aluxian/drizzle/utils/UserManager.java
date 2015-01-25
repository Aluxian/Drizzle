package com.aluxian.drizzle.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aluxian.drizzle.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.aluxian.drizzle.activities.MainActivity.PREF_API_AUTH_TOKEN;

public class UserManager {

    private static UserManager mInstance;
    private SharedPreferences mSharedPrefs;
    private List<AuthStateChangeListener> mListeners = new ArrayList<>();

    public static void init(Context context) {
        mInstance = new UserManager(context);
    }

    public static UserManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("UserManager hasn't been initialised, call init(Context) first");
        }

        return mInstance;
    }

    private UserManager(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return Whether there is an authenticated user, and thus a saved access token.
     */
    public boolean isAuthenticated() {
        return mSharedPrefs.contains(PREF_API_AUTH_TOKEN);
    }

    /**
     * Removes the stored access token.
     */
    public void clearAccessToken() {
        Log.d("Clearing access token");
        mSharedPrefs.edit().remove(PREF_API_AUTH_TOKEN).apply();
        stateChanged(false);
    }

    /**
     * Store the given access token to be used in the future for authenticated requests.
     *
     * @param accessToken The access token to store.
     */
    public void putAccessToken(String accessToken) {
        Log.d("Storing access token");
        mSharedPrefs.edit().putString(MainActivity.PREF_API_AUTH_TOKEN, accessToken).apply();
        stateChanged(true);
    }

    /**
     * @return The stored access token if it exists, otherwise the default (public) client token.
     */
    public String getAccessToken() {
        if (mSharedPrefs.contains(PREF_API_AUTH_TOKEN)) {
            return mSharedPrefs.getString(PREF_API_AUTH_TOKEN, null);
        }

        return Config.API_CLIENT_TOKEN;
    }

    @SuppressWarnings("Convert2streamapi")
    private void stateChanged(boolean authenticated) {
        for (AuthStateChangeListener listener : mListeners) {
            if (listener != null) {
                listener.onAuthenticationStateChanged(authenticated);
            }
        }
    }

    public void registerStateChangeListener(AuthStateChangeListener listener) {
        mListeners.add(listener);
    }

    public void unregisterStateChangeListener(AuthStateChangeListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Interface to be implemented by objects who wish to be notified by authentication state changes.
     */
    public static interface AuthStateChangeListener {

        /**
         * Called when the user signs in or out. May not always be called on the UI thread.
         *
         * @param authenticated Whether the user is signed in or not.
         */
        public void onAuthenticationStateChanged(boolean authenticated);

    }

}
