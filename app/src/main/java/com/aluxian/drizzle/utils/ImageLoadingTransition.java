package com.aluxian.drizzle.utils;

import android.animation.ObjectAnimator;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

public class ImageLoadingTransition {

    public static ObjectAnimator apply(ImageView imageView) {
        AlphaSatColorMatrixEvaluator evaluator = new AlphaSatColorMatrixEvaluator();
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(evaluator.getColorMatrix());
        imageView.getDrawable().setColorFilter(filter);

        ObjectAnimator animator = ObjectAnimator.ofObject(filter, "colorMatrix", evaluator, evaluator.getColorMatrix());
        animator.addUpdateListener(animation -> imageView.getDrawable().setColorFilter(filter));
        animator.setDuration(1000);
        animator.start();

        return animator;
    }

}
