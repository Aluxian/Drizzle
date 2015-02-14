package com.aluxian.drizzle.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class Dp {

    public static final int PX_4 = toPx(4);
    public static final int PX_8 = toPx(8);
    public static final int PX_16 = toPx(16);
    public static final int PX_48 = toPx(48);
    public static final int PX_56 = toPx(56);
    public static final int PX_72 = toPx(72);

    public static int toPx(float units) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, units, Resources.getSystem().getDisplayMetrics()));
    }

}
