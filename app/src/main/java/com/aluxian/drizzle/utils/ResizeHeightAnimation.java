package com.aluxian.drizzle.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation for resizing a view vertically.
 */
public class ResizeHeightAnimation extends Animation {

    private float mFromHeight;
    private float mToHeight;

    private View mView;

    public ResizeHeightAnimation(View view, float toHeight) {
        mFromHeight = view.getHeight();
        mToHeight = toHeight;
        mView = view;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        mView.getLayoutParams().height = (int) ((mToHeight - mFromHeight) * interpolatedTime + mFromHeight);
        mView.requestLayout();
    }

}
