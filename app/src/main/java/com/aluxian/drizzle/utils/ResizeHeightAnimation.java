package com.aluxian.drizzle.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeHeightAnimation extends Animation {

    private final int mStartHeight;
    private final int mTargetHeight;
    private final View mTargetView;

    public ResizeHeightAnimation(View targetView, int targetHeight) {
        mTargetView = targetView;
        mStartHeight = targetView.getHeight();
        mTargetHeight = targetHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        mTargetView.getLayoutParams().height = (int) (mStartHeight + (mTargetHeight - mStartHeight) * interpolatedTime);
        mTargetView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}
