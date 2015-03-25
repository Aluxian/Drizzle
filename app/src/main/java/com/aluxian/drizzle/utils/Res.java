package com.aluxian.drizzle.utils;

import android.content.Context;
import android.util.TypedValue;

public class Res {

    private Context mContext;

    public static Res from(Context context) {
        return new Res(context);
    }

    public Res(Context context) {
        mContext = context;
    }

    public int getColor(int resId) {
        return mContext.getResources().getColor(resId);
    }

    public int getColorAttr(int attribute) {
        TypedValue typedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(attribute, typedValue, true);
        return typedValue.data;
    }

}
