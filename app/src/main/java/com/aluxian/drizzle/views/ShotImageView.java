package com.aluxian.drizzle.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ShotImageView extends ImageView {

    public ShotImageView(Context context) {
        super(context);
    }

    public ShotImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShotImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int calculatedHeight = originalWidth * 3 / 4;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(calculatedHeight, MeasureSpec.EXACTLY));
    }

}
