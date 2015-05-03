package com.aluxian.drizzle.utils;

import android.content.Context;

import com.aluxian.drizzle.Preferences;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static UserManager mInstance;
    private Preferences mPrefs;
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
        mPrefs = Preferences.get(context);
    }

    /**
     * @return Whether there is an authenticated user, and thus a saved access token.
     */
    public boolean isAuthenticated() {
        return mPrefs.getApiAuthToken(null) != null;
    }

    /**
     * Removes the stored access token.
     */
    public void clearAccessToken() {
        Log.d("Clearing access token");
        mPrefs.setApiAuthToken(null);
        stateChanged(false);
    }

    /**
     * Store the given access token to be used in the future for authenticated requests.
     *
     * @param accessToken The access token to store.
     */
    public void putAccessToken(String accessToken) {
        Log.d("Storing access token");
        mPrefs.setApiAuthToken(accessToken);
        stateChanged(true);
    }

    /**
     * @return The stored access token if it exists, otherwise the default (public) client token.
     */
    public String getAccessToken() {
        return mPrefs.getApiAuthToken(Config.API_CLIENT_TOKEN);
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
    public interface AuthStateChangeListener {

        /**
         * Called when the user signs in or out. May not always be called on the UI thread.
         *
         * @param authenticated Whether the user is signed in or not.
         */
        void onAuthenticationStateChanged(boolean authenticated);

    }

}
