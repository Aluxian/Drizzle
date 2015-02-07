package com.aluxian.drizzle;

import android.app.Application;
import android.os.StrictMode;

import com.aluxian.drizzle.activities.MainActivity;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.UserManager;

public class App extends Application {

    @Override
    public void onCreate() {
        // Set the strict mode policy
        if (BuildConfig.STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .setClassInstanceLimit(MainActivity.class, 100)
                    .detectAll().penaltyLog().build());
        }

        Dp.init(this);
        //ApiRequest.initCache(this);
        UserManager.init(this);
    }

}
