package com.aluxian.drizzle.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

public class StatusBarPlaceholder extends View {

    /** The height of the view. */
    private int mHeight;

    public StatusBarPlaceholder(Context context) {
        super(context);
        init(context);
    }

    public StatusBarPlaceholder(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StatusBarPlaceholder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (!isInEditMode()) {
            Resources resources = context.getResources();
            mHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), mHeight);
    }

}
