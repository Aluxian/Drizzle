package com.aluxian.drizzle.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.EdgeEffect;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.aluxian.drizzle.utils.Log;

import java.lang.reflect.Field;

public class CustomEdgeHorizontalScrollView extends HorizontalScrollView {

    private EdgeEffect mEdgeEffectLeft;
    private EdgeEffect mEdgeEffectRight;

    public CustomEdgeHorizontalScrollView(Context context) {
        super(context);
    }

    public CustomEdgeHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEdgeHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEdgeColor(int color) {
        if (mEdgeEffectLeft == null) {
            try {
                Field edgeFieldLeft = HorizontalScrollView.class.getDeclaredField("mEdgeGlowLeft");
                Field edgeFieldRight = HorizontalScrollView.class.getDeclaredField("mEdgeGlowRight");

                edgeFieldLeft.setAccessible(true);
                edgeFieldRight.setAccessible(true);

                mEdgeEffectLeft = (EdgeEffect) edgeFieldLeft.get(this);
                mEdgeEffectRight = (EdgeEffect) edgeFieldRight.get(this);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.e(e);
            }
        }

        mEdgeEffectLeft.setColor(color);
        mEdgeEffectRight.setColor(color);
    }

}
