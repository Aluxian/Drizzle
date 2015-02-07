package com.aluxian.drizzle.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

public class PaletteTransformation implements Transformation {

    private static final PaletteTransformation INSTANCE = new PaletteTransformation();
    private static final Map<Bitmap, Palette> CACHE = new WeakHashMap<>();

    public static PaletteTransformation instance() {
        return INSTANCE;
    }

    public static Palette getPalette(ImageView imageView) {
        return CACHE.get(((BitmapDrawable) imageView.getDrawable()).getBitmap());
    }

    private PaletteTransformation() {}

    @Override
    public Bitmap transform(Bitmap source) {
        Palette.generateAsync(source, palette -> CACHE.put(source, palette));
        return source;
    }

    @Override
    public String key() {
        return "palette";
    }

}
