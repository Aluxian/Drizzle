package com.aluxian.drizzle.utils;

import android.support.v7.graphics.Palette;

public class Utils {

    public static Palette.Swatch getSwatch(Palette palette) {
        Palette.Swatch swatch = palette.getDarkMutedSwatch();

        if (swatch == null) swatch = palette.getDarkVibrantSwatch();
        if (swatch == null) swatch = palette.getMutedSwatch();
        if (swatch == null) swatch = palette.getVibrantSwatch();
        if (swatch == null) swatch = palette.getLightMutedSwatch();
        if (swatch == null) swatch = palette.getLightVibrantSwatch();

        return swatch;
    }

}
