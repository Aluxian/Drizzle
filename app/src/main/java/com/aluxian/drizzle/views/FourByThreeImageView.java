package com.aluxian.drizzle.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class FourByThreeImageView extends ImageView {

    public FourByThreeImageView(Context context) {
        super(context);
    }

    public FourByThreeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FourByThreeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int calculatedHeight = originalWidth * 3 / 4;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(calculatedHeight, View.MeasureSpec.EXACTLY));
    }

}
