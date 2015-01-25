package com.aluxian.drizzle.utils;

import android.content.Context;
import android.util.TypedValue;

public class Utils {

    public static int dpToPx(float units, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, units, context.getResources().getDisplayMetrics());
    }

}
