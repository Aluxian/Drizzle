package com.aluxian.drizzle.utils;

import android.text.TextUtils;

import com.aluxian.drizzle.BuildConfig;
import com.aluxian.drizzle.api.ApiRequest;
import com.google.gson.Gson;

/**
 * Custom logger implementation which wraps around Android's Log class.
 */
@SuppressWarnings("UnusedDeclaration")
public class Log {

    /** Delimiter for printing several values on the same line. */
    private static final String DELIMITER = " ";

    /** String to add to the beginning of the tag. Used to differentiate dev's messages from the system's. */
    private static final String TAG_INIT = "/ ";

    /**
     * Log verbose messages.
     *
     * @param messages Objects whose values to write to logcat (all on the same line).
     */
    public static void v(Object... messages) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(tag(), concatenate(messages));
        }
    }

    /**
     * Log debug messages.
     *
     * @param messages Objects whose values to write to logcat (all on the same line).
     */
    public static void d(Object... messages) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag(), concatenate(messages));
        }
    }

    /**
     * Log info messages.
     *
     * @param messages Objects whose values to write to logcat (all on the same line).
     */
    public static void i(Object... messages) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(tag(), concatenate(messages));
        }
    }

    /**
     * Log warn messages.
     *
     * @param messages Objects whose values to write to logcat (all on the same line).
     */
    public static void w(Object... messages) {
        if (BuildConfig.DEBUG) {
            android.util.Log.w(tag(), concatenate(messages));
        }
    }

    /**
     * Log error messages.
     *
     * @param throwables Throwable objects to print to logcat.
     */
    public static void e(Throwable... throwables) {
        if (BuildConfig.DEBUG) {
            for (Throwable throwable : throwables) {
                android.util.Log.e(tag(), throwable.getLocalizedMessage(), throwable);
            }
        } else {
            // TODO: Send the exceptions to Crashlytics
            /*for (Throwable throwable : throwables) {
                Crashlytics.logException(throwable);
            }*/
        }
    }

    /**
     * Log wtf messages.
     *
     * @param messages Objects whose values to write to logcat (all on the same line).
     */
    public static void wtf(Object... messages) {
        if (BuildConfig.DEBUG) {
            android.util.Log.wtf(tag(), concatenate(messages));
        }
    }

    /**
     * Converts all the given objects to strings and joins them with a delimiter.
     *
     * @param messages Objects to concatenate.
     * @return The joined string.
     */
    private static String concatenate(Object[] messages) {
        if (messages.length == 0) {
            return "()";
        }

        Gson gson = ApiRequest.GSON;

        // Convert objects that don't override the toString() method to JSON
        for (int i = 0; i < messages.length; i++) {
            try {
                if (messages[i] == null || !messages[i].getClass().getMethod("toString").getDeclaringClass().equals(Object.class)) {
                    continue;
                }
            } catch (NoSuchMethodException e) {
                Log.e(e);
            }

            messages[i] = messages[i].toString() + "=" + gson.toJson(messages[i]);
        }

        return TextUtils.join(DELIMITER, messages);
    }

    /**
     * @return The calling class and method names to use as the logcat tag.
     */
    private static String tag() {
        StackTraceElement element = new Exception().getStackTrace()[2];

        String clazz = element.getClassName().replace(BuildConfig.APPLICATION_ID + ".", "");
        String method = element.getMethodName();
        int line = element.getLineNumber();

        return TAG_INIT + clazz + "::" + method + "@L" + line;
    }

}
