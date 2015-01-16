package com.aluxian.drizzle.ui.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;

import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.ResizeHeightAnimation;
import com.gc.materialdesign.views.ProgressBarIndeterminateDeterminate;

public class ProgressBarWidget extends ProgressBarIndeterminateDeterminate {

    /** The height of the progress bar. */
    private final int mHeight;

    /** Whether the progress bar is currently shown. */
    private boolean mShown;

    public ProgressBarWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHeight = (int) new Dp(3).toPx(context);
    }

    /**
     * Show the progress bar.
     *
     * @param animate Whether the change should be animated.
     */
    public void show(boolean animate) {
        mShown = true;

        if (animate) {
            startAnimation(new ResizeHeightAnimation(this, mHeight));
        } else {
            getLayoutParams().height = mHeight;
            requestLayout();
        }
    }

    /**
     * Hide the progress bar.
     *
     * @param animate Whether the change should be animated.
     */
    public void hide(boolean animate) {
        mShown = false;

        if (animate) {
            startAnimation(new ResizeHeightAnimation(this, 0));
        } else {
            getLayoutParams().height = 0;
            requestLayout();
        }
    }

    /**
     * @return Whether the progress bar is currently shown.
     */
    public boolean isShownInToolbar() {
        return mShown;
    }

}
