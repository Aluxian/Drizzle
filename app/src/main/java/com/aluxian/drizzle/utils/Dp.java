package com.aluxian.drizzle.utils;

import android.content.Context;
import android.util.TypedValue;

public class Dp {

    private int mUnits;

    public Dp(int units) {
        mUnits = units;
    }

    public float toPx(Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mUnits, context.getResources().getDisplayMetrics());
    }

}
