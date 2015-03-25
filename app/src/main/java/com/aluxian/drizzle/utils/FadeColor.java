package com.aluxian.drizzle.utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;

public class FadeColor {

    public static void apply(int from, int to, ValueAnimator.AnimatorUpdateListener updateListener) {
        ValueAnimator backgroundColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        backgroundColorAnimation.addUpdateListener(updateListener);
        backgroundColorAnimation.start();
    }

}
