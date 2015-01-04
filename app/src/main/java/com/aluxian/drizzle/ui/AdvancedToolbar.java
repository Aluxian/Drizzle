package com.aluxian.drizzle.ui;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ActionMenuView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.Utils;

import java.lang.reflect.Field;

public class AdvancedToolbar extends Toolbar {

    private InputMethodManager inputMethodManager;

    // Toolbar views
    private TextView mTitleTextView;
    private ActionMenuView mMenuView;

    // SearchView
    private View mSearchView;
    private EditText mSearchViewEditText;
    private ImageButton mSearchViewClearButton;

    private boolean mSearchViewShown;
    private float mSearchViewTranslationY;
    private float mSearchViewClearButtonTranslationX;

    public AdvancedToolbar(Context context) {
        super(context);
    }

    public AdvancedToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvancedToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

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

    private void showTitleView() {
        getTitleTextView().animate()
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .translationY(0)
                .alpha(1);
    }

    private void hideTitleView() {
        getTitleTextView().animate()
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(200)
                .translationY(-mSearchViewTranslationY)
                .alpha(0);
    }

    private void showMenuView() {
        getMenuView().setVisibility(VISIBLE);
        getMenuView().animate()
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .translationY(0)
                .alpha(1);
    }

    private void hideMenuView() {
        getMenuView().animate()
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(200)
                .translationY(-mSearchViewTranslationY)
                .alpha(0)
                .withEndAction(() -> getMenuView().setVisibility(GONE));
    }

    private void findSearchView() {
        if (mSearchView == null) {
            mSearchView = getRootView().findViewById(R.id.search_view);
            mSearchViewEditText = (EditText) mSearchView.findViewById(R.id.input);

            mSearchViewClearButton = (ImageButton) mSearchView.findViewById(R.id.clear);
            mSearchViewClearButtonTranslationX = mSearchViewClearButton.getTranslationX();
            mSearchViewClearButton.setOnClickListener(v -> mSearchViewEditText.setText(""));

            mSearchViewTranslationY = getResources().getDimensionPixelSize(R.dimen.toolbarHeight);

            inputMethodManager = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
        }
    }

    public void showSearchView() {
        mSearchViewShown = true;
        findSearchView();

        mSearchView.setVisibility(View.VISIBLE);
        mSearchViewEditText.animate().alpha(1);
        mSearchViewClearButton.animate().translationX(0);

        hideTitleView();
        hideMenuView();

        mSearchViewEditText.requestFocus();
        mSearchViewEditText.postDelayed(() -> inputMethodManager.showSoftInput(mSearchViewEditText, 0), 500);
    }

    public void hideSearchView() {
        mSearchViewShown = false;
        findSearchView();

        mSearchViewEditText.setText("");
        mSearchViewEditText.animate().alpha(0).withEndAction(() -> mSearchView.setVisibility(View.GONE));
        mSearchViewClearButton.animate().translationX(mSearchViewClearButtonTranslationX);

        showTitleView();
        showMenuView();

        inputMethodManager.hideSoftInputFromWindow(mSearchViewEditText.getWindowToken(), 0);
    }

    /**
     * @return Whether the search view is visible or not.
     */
    public boolean isSearchViewShown() {
        return mSearchViewShown;
    }

    public void show(boolean animate) {
        int actionBarSize = getResources().getDimensionPixelSize(R.dimen.toolbarHeight);
        int statusBarHeight = getResources().getDimensionPixelSize(R.dimen.statusBarHeight);
        moveTopMargin(animate, actionBarSize, statusBarHeight - actionBarSize);
    }

    public void hide(boolean animate) {
        int actionBarSize = getResources().getDimensionPixelSize(R.dimen.toolbarHeight);
        int statusBarHeight = getResources().getDimensionPixelSize(R.dimen.statusBarHeight);
        moveTopMargin(animate, -actionBarSize, statusBarHeight);
    }

    private void moveTopMargin(boolean animate, int delta, int initialMargin) {
        View wrapper = (View) getParent();

        if (animate) {
            Animation anim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) wrapper.getLayoutParams();
                    params.topMargin = (int) (delta * interpolatedTime) + initialMargin;
                    wrapper.setLayoutParams(params);
                }
            };

            anim.setDuration(200);
            startAnimation(anim);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) wrapper.getLayoutParams();
            params.topMargin = delta + initialMargin;
            wrapper.setLayoutParams(params);
        }
    }

}
