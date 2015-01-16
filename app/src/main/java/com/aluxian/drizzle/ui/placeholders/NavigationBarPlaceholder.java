package com.aluxian.drizzle.ui.placeholders;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.aluxian.drizzle.R;

public class NavigationBarPlaceholder extends View {

    private int mHeight;

    public NavigationBarPlaceholder(Context context) {
        super(context);
        init(context);
    }

    public NavigationBarPlaceholder(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NavigationBarPlaceholder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        TypedArray a = context.getTheme().obtainStyledAttributes(R.style.Drizzle, new int[]{android.R.attr.navigationBarColor});

        try {
            setBackgroundColor(a.getColor(0, 0));
        } finally {
            a.recycle();
        }

        Resources resources = context.getResources();
        mHeight = resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), mHeight);
    }

}
