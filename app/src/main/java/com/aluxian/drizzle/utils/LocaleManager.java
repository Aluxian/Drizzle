package com.aluxian.drizzle.utils;

import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleManager {

    /** An instance of the default Locale. */
    private static final Locale defaultLocale = Locale.getDefault();

    /**
     * Change the active Locale.
     *
     * @param locale The new Locale.
     */
    public static void set(Locale locale) {
        Configuration systemConf = Resources.getSystem().getConfiguration();
        systemConf.locale = locale;
        Resources.getSystem().updateConfiguration(systemConf, Resources.getSystem().getDisplayMetrics());
        Locale.setDefault(locale);
    }

    /**
     * Set the Locale back to the default one.
     */
    public static void reset() {
        set(defaultLocale);
    }

}
