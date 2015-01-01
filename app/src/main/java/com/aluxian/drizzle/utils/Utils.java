package com.aluxian.drizzle.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateUtils;
import android.util.TypedValue;

import com.aluxian.drizzle.api.Dribbble;
import com.anupcowkur.reservoir.Reservoir;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class Utils {

    private Context mContext;

    public Utils(Context context) {
        mContext = context;
    }

    /**
     * Parses the value of a Link header in order to extract the url with rel="next".
     *
     * @param linkHeader The value of the Link header to parse.
     * @return The url with rel="next" or null if not found.
     */
    public static String extractNextUrl(String linkHeader) {
        if (linkHeader != null) {
            String[] links = linkHeader.split(",");

            for (String link : links) {
                String[] segments = link.split(";");
                if (segments.length < 2) {
                    continue;
                }

                String[] relPart = segments[1].trim().split("=");
                if (relPart.length < 2) {
                    continue;
                }

                String relValue = relPart[1];
                if (relValue.startsWith("\"") && relValue.endsWith("\"")) {
                    relValue = relValue.substring(1, relValue.length() - 1);
                }

                if ("next".equals(relValue)) {
                    String linkPart = segments[0].trim();

                    if (linkPart.startsWith("<") && linkPart.endsWith(">")) {
                        return linkPart.substring(1, linkPart.length() - 1);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Check whether there is a valid cached value for the given key. A value is valid if it's neither null nor expired.
     *
     * @param key The key whose value to check.
     * @return Whether the value that corresponds to the given key is still valid.
     */
    public static boolean hasValidCache(String key) {
        try {
            String cached = Reservoir.get(key, String.class);

            if (cached != null) {
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                Dribbble.Response response = gson.fromJson(cached, Dribbble.Response.class);

                // Make sure it's not expired; also, there should be more than 5 seconds remaining until expiry time
                // to make sure it doesn't expire immediately after the check is made
                if (new Date().getTime() - response.receivedAt < Config.CACHE_TIMEOUT - 5 * DateUtils.SECOND_IN_MILLIS) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            // Value not found
        } catch (Exception e) {
            Log.e(e);
        }

        return false;
    }

    /**
     * Convert DP units to PX units.
     *
     * @param dp The amount to convert.
     * @return The amount in pixels.
     */
    public float dpToPx(int dp) {
        return dpToPx(dp, mContext);
    }

    /**
     * Convert DP units to PX units.
     *
     * @param dp      The amount to convert.
     * @param context The context used to get the device's DisplayMetrics.
     * @return The amount in pixels.
     */
    public static float dpToPx(int dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Extracts the value of an attribute from the current theme.
     *
     * @param attr The id of the attribute.
     * @return The value of the attribute.
     */
    public float getThemeAttr(int attr) {
        return getThemeAttr(attr, mContext);
    }

    /**
     * Extracts the value of an attribute from the current theme.
     *
     * @param attr    The id of the attribute.
     * @param context The context used to get the theme.
     * @return The value of the attribute.
     */
    public static float getThemeAttr(int attr, Context context) {
        TypedArray attrs = context.getTheme().obtainStyledAttributes(new int[]{attr});
        float value = attrs.getDimension(0, 0);
        attrs.recycle();
        return value;
    }

}
