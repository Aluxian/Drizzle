package com.aluxian.drizzle.ui.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ActionMenuView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.ResizeHeightAnimation;

import java.lang.reflect.Field;

public class NativeToolbar extends Toolbar {

    /** The text view that displays the title. */
    private TextView mTitleTextView;

    /** The view that holds the action menu items. */
    private ActionMenuView mMenuView;

    /** The initial height of the toolbar. */
    private int mHeight;

    public NativeToolbar(Context context) {
        super(context);
    }

    public NativeToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NativeToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        mHeight = getHeight();
    }

    /**
     * @return The mTitleTextView object accessed through reflection from super.
     */
    private TextView getTitleTextView() {
        if (mTitleTextView == null) {
            try {
                Field field = Toolbar.class.getDeclaredField("mTitleTextView");
                field.setAccessible(true);
                mTitleTextView = (TextView) field.get(this);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                Log.e(e);
            }
        }

        return mTitleTextView;
    }

    /**
     * @return The mMenuView object accessed through reflection from super.
     */
    private ActionMenuView getMenuView() {
        if (mMenuView == null) {
            try {
                Field field = Toolbar.class.getDeclaredField("mMenuView");
                field.setAccessible(true);
                mMenuView = (ActionMenuView) field.get(this);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                Log.e(e);
            }
        }

        return mMenuView;
    }

    /**
     * Shows the title view with a fade-in-from-top animation.
     */
    public void showTitleView() {
        getTitleTextView().animate()
                .setInterpolator(new DecelerateInterpolator())
                .translationY(0)
                .alpha(1);
    }

    /**
     * Hides the title view with a fade-out-to-top animation.
     */
    public void hideTitleView() {
        getTitleTextView().animate()
                .setInterpolator(new AccelerateInterpolator())
                .translationY(-getHeight())
                .alpha(0);
    }

    /**
     * Shows the menu view with a fade-in-from-top animation.
     */
    public void showMenuView() {
        getMenuView().animate()
                .setInterpolator(new DecelerateInterpolator())
                .translationY(0)
                .alpha(1);
    }

    /**
     * Hides the menu view with a fade-out-to-top animation.
     */
    public void hideMenuView() {
        getMenuView().animate()
                .setInterpolator(new AccelerateInterpolator())
                .translationY(-getHeight())
                .alpha(0);
    }

    /**
     * Shows the toolbar.
     *
     * @param animate Whether the change should be animated.
     */
    public void show(boolean animate) {
        changeState(animate, true);
    }

    /**
     * Hides the toolbar.
     *
     * @param animate Whether the change should be animated.
     */
    public void hide(boolean animate) {
        changeState(animate, false);
    }

    /**
     * Animate the view's properties to hide it or show it.
     *
     * @param animate Whether the change should be animated.
     * @param visible Whether the view is made visible.
     */
    private void changeState(boolean animate, boolean visible) {
        View wrapper = (View) getParent();

        if (animate) {
            startAnimation(new ResizeHeightAnimation(this, visible ? mHeight : 0));
        } else {
            wrapper.getLayoutParams().height = visible ? mHeight : 0;
            wrapper.requestLayout();
        }
    }

}
