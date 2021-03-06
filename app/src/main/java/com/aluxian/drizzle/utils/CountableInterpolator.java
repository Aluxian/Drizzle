package com.aluxian.drizzle.utils;

import android.content.Context;
import android.content.res.Resources;

public class CountableInterpolator {

    private Resources mResources;

    public CountableInterpolator(Context context) {
        mResources = context.getResources();
    }

    public String apply(int count, int pluralString, int singularString) {
        if (count == 1) {
            return mResources.getString(singularString);
        } else {
            return mResources.getString(pluralString, count);
        }
    }

    public String apply(int count, int pluralString, int singularString, int zeroString) {
        if (count == 0) {
            return mResources.getString(zeroString);
        } else {
            return apply(count, pluralString, singularString);
        }
    }

}
