package com.aluxian.drizzle.utils;

import android.animation.TypeEvaluator;
import android.graphics.ColorMatrix;

/**
 * @source http://stackoverflow.com/a/27286385/1133344
 */
public class AlphaSatColorMatrixEvaluator implements TypeEvaluator {

    private ColorMatrix colorMatrix = new ColorMatrix();
    private float[] elements = new float[20];

    public ColorMatrix getColorMatrix() {
        return colorMatrix;
    }

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        // There are 3 phases so we multiply fraction by that amount
        float phase = fraction * 3;

        // Compute the alpha change over period [0, 2]
        float alpha = Math.min(phase, 2f) / 2f;
        elements[19] = (float) Math.round(alpha * 255);

        // We subtract to make the picture look darker, it will automatically clamp
        // This is spread over period [0, 2.5]
        int maxBlacker = 100;
        float blackening = (float) Math.round((1 - Math.min(phase, 2.5f) / 2.5f) * maxBlacker);
        elements[4] = elements[9] = elements[14] = -blackening;

        // Finally we desaturate over [0, 3], taken from ColorMatrix.SetSaturation
        float invSat = 1 - Math.max(0.2f, fraction);
        float R = 0.213f * invSat;
        float G = 0.715f * invSat;
        float B = 0.072f * invSat;

        elements[0] = R + fraction;
        elements[1] = G;
        elements[2] = B;
        elements[5] = R;
        elements[6] = G + fraction;
        elements[7] = B;
        elements[10] = R;
        elements[11] = G;
        elements[12] = B + fraction;

        colorMatrix.set(elements);
        return colorMatrix;
    }

}
