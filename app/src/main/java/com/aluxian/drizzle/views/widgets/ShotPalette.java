package com.aluxian.drizzle.views.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aluxian.drizzle.utils.Dp;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ShotPalette extends LinearLayout {

    private boolean mLoaded;

    public ShotPalette(Context context) {
        super(context);
    }

    public ShotPalette(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShotPalette(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void load(List<String> colors) {
        if (mLoaded) {
            return;
        }
        mLoaded = true;

        setWeightSum(colors.size());

        for (String color : colors) {
            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(0, MATCH_PARENT, 1));
            view.setBackgroundColor(Color.parseColor(color));
            addView(view);
        }
    }

}