package com.aluxian.drizzle.views;

import android.content.Context;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.EdgeEffect;

import com.aluxian.drizzle.utils.Log;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomEdgeRecyclerView extends ObservableRecyclerView {

    private EdgeEffect mTopEdgeEffect;
    private EdgeEffect mBottomEdgeEffect;

    public CustomEdgeRecyclerView(Context context) {
        super(context);
    }

    public CustomEdgeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEdgeRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEdgeColor(int color) {
        ensureEdgeEffect();
        mTopEdgeEffect.setColor(color);
        mBottomEdgeEffect.setColor(color);
    }

    public void setTopEdgeColor(int color) {
        ensureEdgeEffect();
        mTopEdgeEffect.setColor(color);
    }

    public void setBottomEdgeColor(int color) {
        ensureEdgeEffect();
        mBottomEdgeEffect.setColor(color);
    }

    private void ensureEdgeEffect() {
        if (mTopEdgeEffect == null) {
            try {
                Method topGlowMethod = RecyclerView.class.getDeclaredMethod("ensureTopGlow");
                Method bottomGlowMethod = RecyclerView.class.getDeclaredMethod("ensureBottomGlow");

                topGlowMethod.setAccessible(true);
                bottomGlowMethod.setAccessible(true);

                topGlowMethod.invoke(this);
                bottomGlowMethod.invoke(this);

                Field topGlowField = RecyclerView.class.getDeclaredField("mTopGlow");
                Field bottomGlowField = RecyclerView.class.getDeclaredField("mBottomGlow");

                topGlowField.setAccessible(true);
                bottomGlowField.setAccessible(true);

                EdgeEffectCompat topEffectCompat = (EdgeEffectCompat) topGlowField.get(this);
                EdgeEffectCompat bottomEffectCompat = (EdgeEffectCompat) bottomGlowField.get(this);

                Field edgeEffectField = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
                edgeEffectField.setAccessible(true);

                mTopEdgeEffect = (EdgeEffect) edgeEffectField.get(topEffectCompat);
                mBottomEdgeEffect = (EdgeEffect) edgeEffectField.get(bottomEffectCompat);
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                Log.e(e);
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldL, int oldT) {
        try {
            super.onScrollChanged(l, t, oldL, oldT);
        } catch (NullPointerException e) {
            Log.e(e);
        }
    }

}
