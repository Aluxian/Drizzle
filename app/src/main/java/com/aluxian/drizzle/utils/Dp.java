package com.aluxian.drizzle.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Dp {

    private static DisplayMetrics mDisplayMetrics;

    public static void init(Context context) {
        mDisplayMetrics = context.getResources().getDisplayMetrics();
    }

    public static int toPx(float units) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, units, mDisplayMetrics);
    }

}
