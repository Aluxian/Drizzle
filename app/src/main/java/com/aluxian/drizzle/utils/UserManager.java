package com.aluxian.drizzle.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.aluxian.drizzle.MainActivity.PREF_API_AUTH_TOKEN;

public class UserManager {

    private SharedPreferences mSharedPrefs;

    public UserManager(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isSignedIn() {
        return mSharedPrefs.contains(PREF_API_AUTH_TOKEN);
    }

    public String getAccessToken() {
        if (mSharedPrefs.contains(PREF_API_AUTH_TOKEN)) {
            return mSharedPrefs.getString(PREF_API_AUTH_TOKEN, null);
        }

        return Config.API_CLIENT_TOKEN;
    }

}
