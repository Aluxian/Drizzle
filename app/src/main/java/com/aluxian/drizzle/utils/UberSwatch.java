package com.aluxian.drizzle.utils;

import android.graphics.Color;
import android.support.v7.graphics.Palette;

public class UberSwatch {

    private final Palette.Swatch swatch;

    public final int rgb;
    public final int titleTextColor;
    public final int bodyTextColor;
    public final int accentColor;
    public final int hoverColor;

    public static UberSwatch from(Palette palette) {
        return new UberSwatch(palette);
    }

    private UberSwatch(Palette palette) {
        this.swatch = getSwatch(palette);
        this.rgb = swatch.getRgb();
        this.titleTextColor = swatch.getTitleTextColor();
        this.bodyTextColor = swatch.getBodyTextColor();
        this.accentColor = generateAccent(rgb);
        this.hoverColor = generateHover(rgb);
    }

    private Palette.Swatch getSwatch(Palette palette) {
        Palette.Swatch swatch = palette.getDarkMutedSwatch();
        if (swatch == null) swatch = palette.getDarkVibrantSwatch();
        if (swatch == null) swatch = palette.getMutedSwatch();
        if (swatch == null) swatch = palette.getVibrantSwatch();
        if (swatch == null) swatch = palette.getLightMutedSwatch();
        if (swatch == null) swatch = palette.getLightVibrantSwatch();
        return swatch;
    }

    private int generateAccent(int rgb) {
        float[] hsv = new float[3];
        Color.colorToHSV(rgb, hsv);

        if (hsv[2] <= 0.85) {
            hsv[2] += 0.15;
        } else if (hsv[2] >= 0.15) {
            hsv[2] -= 0.15;
        }

        return Color.HSVToColor(hsv);
    }

    private int generateHover(int rgb) {
        float[] hsv = new float[3];
        Color.colorToHSV(rgb, hsv);

        if (hsv[2] <= 0.85) {
            hsv[2] += 0.25;
        } else if (hsv[2] >= 0.15) {
            hsv[2] -= 0.25;
        }

        return Color.HSVToColor(hsv);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UberSwatch)) return false;

        UberSwatch uberSwatch = (UberSwatch) o;
        return swatch.equals(uberSwatch.swatch);
    }

    @Override
    public int hashCode() {
        return swatch.hashCode();
    }

}
