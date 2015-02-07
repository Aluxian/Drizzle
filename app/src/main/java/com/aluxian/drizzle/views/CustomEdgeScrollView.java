package com.aluxian.drizzle.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.EdgeEffect;
import android.widget.ScrollView;

import com.aluxian.drizzle.utils.Log;

import java.lang.reflect.Field;

public class CustomEdgeScrollView extends ScrollView {

    private EdgeEffect mEdgeEffectTop;
    private EdgeEffect mEdgeEffectBottom;

    public CustomEdgeScrollView(Context context) {
        super(context);
    }

    public CustomEdgeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEdgeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEdgeColor(int color) {
        if (mEdgeEffectTop == null) {
            try {
                Field topEdgeField = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                Field bottomEdgeField = ScrollView.class.getDeclaredField("mEdgeGlowBottom");

                topEdgeField.setAccessible(true);
                bottomEdgeField.setAccessible(true);

                mEdgeEffectTop = (EdgeEffect) topEdgeField.get(this);
                mEdgeEffectBottom = (EdgeEffect) bottomEdgeField.get(this);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.e(e);
            }
        }

        mEdgeEffectTop.setColor(color);
        mEdgeEffectBottom.setColor(color);
    }

    public void setOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener listener) {
        getViewTreeObserver().addOnScrollChangedListener(listener);
    }

}
