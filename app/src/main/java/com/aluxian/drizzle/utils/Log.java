package com.aluxian.drizzle.utils;

import android.text.TextUtils;

import com.aluxian.drizzle.BuildConfig;

/**
 * Custom logger implementation which wraps around Android's Log class.
 */
public class Log {

    /** Delimiter for printing several values on the same line. */
    private static final String DELIMITER = ", ";

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
            // Send the exceptions to Crashlytics
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
     * Converts all the given objects to string and joins them with a delimiter.
     *
     * @param messages Objects to concatenate.
     * @return The joined string.
     */
    private static String concatenate(Object[] messages) {
        if (messages.length == 0) {
            return "()";
        }

        return TextUtils.join(DELIMITER, messages);
    }

    /**
     * @return The calling class and method names to use as a logcat tag.
     */
    private static String tag() {
        StackTraceElement element = new Exception().getStackTrace()[2];

        String clazz = element.getClassName().replace(BuildConfig.APPLICATION_ID + ".", "");
        String method = element.getMethodName();
        int line = element.getLineNumber();

        return TAG_INIT + clazz + ":" + line + "#" + method;
    }

}
