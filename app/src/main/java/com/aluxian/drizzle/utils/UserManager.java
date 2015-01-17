package com.aluxian.drizzle.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aluxian.drizzle.activities.MainActivity;

import static com.aluxian.drizzle.activities.MainActivity.PREF_API_AUTH_TOKEN;

public class UserManager {

    private static UserManager mInstance;
    private SharedPreferences mSharedPrefs;

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
        mSharedPrefs.edit().remove(PREF_API_AUTH_TOKEN).apply();
    }

    /**
     * Store the given access token to be used in the future for authenticated requests.
     *
     * @param accessToken The access token to store.
     */
    public void putAccessToken(String accessToken) {
        mSharedPrefs.edit().putString(MainActivity.PREF_API_AUTH_TOKEN, accessToken).apply();
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

}
