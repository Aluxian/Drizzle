package com.aluxian.drizzle;

import android.content.Context;

import net.orange_box.storebox.StoreBox;
import net.orange_box.storebox.annotations.method.KeyByString;

public interface Preferences {

    @KeyByString("intro_shown")
    boolean introActivityShown(boolean defValue);

    @KeyByString("intro_shown")
    void setIntroActivityShown(boolean value);

    @KeyByString("api_auth_token")
    String getApiAuthToken(String defValue);

    @KeyByString("api_auth_token")
    void setApiAuthToken(String value);

    /**
     * @param context The Context to use to create the instance.
     * @return A Preferences instance.
     */
    static Preferences get(Context context) {
        return StoreBox.create(context, Preferences.class);
    }

}
