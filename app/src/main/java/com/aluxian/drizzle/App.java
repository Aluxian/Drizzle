package com.aluxian.drizzle;

import android.app.Application;
import android.os.StrictMode;

import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.UserManager;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.Date;

public class App extends Application {

    public static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();

    @Override
    public void onCreate() {
        // Set the strict mode policy
        if (BuildConfig.STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

        // Init the cache for http requests
        App.OK_HTTP_CLIENT.setCache(new Cache(new File(getCacheDir(), App.class.getName()), Config.CACHE_SIZE));

        // Create a UserManager instance
        UserManager.init(this);
    }

}
