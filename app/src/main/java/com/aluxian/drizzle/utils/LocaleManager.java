package com.aluxian.drizzle.utils;

import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleManager {

    private static final Locale defaultLocale = Locale.getDefault();

    public static void set(Locale locale) {
        Configuration systemConf = Resources.getSystem().getConfiguration();
        systemConf.locale = locale;
        Resources.getSystem().updateConfiguration(systemConf, Resources.getSystem().getDisplayMetrics());
        Locale.setDefault(locale);
    }

    public static void reset() {
        set(defaultLocale);
    }

}
