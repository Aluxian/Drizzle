package com.aluxian.drizzle;

import android.app.Application;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.iainconnor.objectcache.DiskCache;

import java.io.IOException;

public class App extends Application {

    @Override
    public void onCreate() {
        try {
            ApiRequest.diskCache(new DiskCache(getCacheDir(), BuildConfig.VERSION_CODE, Config.CACHE_SIZE));
        } catch (IOException e) {
            Log.e(e);
        }
    }

}
