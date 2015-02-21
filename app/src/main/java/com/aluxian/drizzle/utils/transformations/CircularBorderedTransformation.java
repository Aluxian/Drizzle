package com.aluxian.drizzle.utils.transformations;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircularBorderedTransformation implements Transformation {

    private int mBorderSize;
    private int mBorderColor;

    public CircularBorderedTransformation(int borderSize, int borderColor) {
        mBorderSize = borderSize;
        mBorderColor = borderColor;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        float radius = source.getHeight() / 2;

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
        Canvas canvas = new Canvas(output);

        Paint bitmapPaint = new Paint();
        bitmapPaint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        bitmapPaint.setAntiAlias(true);

        Paint borderPaint = new Paint();
        borderPaint.setColor(mBorderColor);
        borderPaint.setAntiAlias(true);

        canvas.drawCircle(radius, radius, radius, borderPaint);
        canvas.drawCircle(radius, radius, radius - mBorderSize, bitmapPaint);

        source.recycle();
        return output;
    }

    @Override
    public String key() {
        return String.format("circularBordered(size=%s, color=%s)", mBorderSize, mBorderColor);
    }

}
