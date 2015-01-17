package com.aluxian.drizzle;

import android.app.Application;
import android.os.StrictMode;

import com.aluxian.drizzle.activities.MainActivity;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.UserManager;
import com.iainconnor.objectcache.DiskCache;

import java.io.IOException;

public class App extends Application {

    @Override
    public void onCreate() {
        UserManager.init(this);

        // Set the strict mode policy
        if (BuildConfig.STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .setClassInstanceLimit(MainActivity.class, 100)
                    .detectAll().penaltyLog().build());
        }

        // Set the cache that ApiRequest objects can use
        try {
            ApiRequest.diskCache(new DiskCache(getCacheDir(), BuildConfig.VERSION_CODE, Config.CACHE_SIZE));
        } catch (IOException e) {
            Log.e(e);
        }
    }

}
