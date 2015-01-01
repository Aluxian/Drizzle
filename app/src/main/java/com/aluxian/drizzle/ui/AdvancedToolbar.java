package com.aluxian.drizzle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ActionMenuView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.Utils;

import java.lang.reflect.Field;

public class AdvancedToolbar extends Toolbar {

    // Toolbar views
    private TextView mTitleTextView;
    private ActionMenuView mMenuView;

    // SearchView
    private View mSearchView;
    private EditText mSearchViewEditText;
    private ImageButton mSearchViewClearButton;

    private boolean mSearchViewShown;
    private float mSearchViewTranslation;
    private float mSearchViewClearButtonTranslation;

    public AdvancedToolbar(Context context) {
        super(context);
    }

    public AdvancedToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvancedToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AdvancedToolbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TextView getTitleTextView() {
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

    public ActionMenuView getMenuView() {
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

    public void showTitleView() {
        getTitleTextView().animate()
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .translationY(0)
                .alpha(1);
    }

    public void hideTitleView() {
        getTitleTextView().animate()
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(200)
                .translationY(-mSearchViewTranslation)
                .alpha(0);
    }

    public void showMenuView() {
        getMenuView().setVisibility(VISIBLE);
        getMenuView().animate()
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .translationY(0)
                .alpha(1);
    }

    public void hideMenuView() {
        getMenuView().animate()
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(200)
                .translationY(-mSearchViewTranslation)
                .alpha(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        getMenuView().setVisibility(GONE);
                    }
                });
    }

    private void findSearchView() {
        if (mSearchView == null) {
            mSearchView = getRootView().findViewById(R.id.search_view);
            mSearchViewEditText = (EditText) mSearchView.findViewById(R.id.input);

            mSearchViewClearButton = (ImageButton) mSearchView.findViewById(R.id.clear);
            mSearchViewClearButtonTranslation = mSearchViewClearButton.getTranslationX();
            mSearchViewClearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSearchViewEditText.setText("");
                }
            });

            mSearchViewTranslation = Utils.getThemeAttr(android.R.attr.actionBarSize, getContext());
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
    }

    public void hideSearchView() {
        mSearchViewShown = false;
        findSearchView();

        mSearchViewEditText.setText("");
        mSearchViewEditText.animate()
                .alpha(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mSearchView.setVisibility(View.GONE);
                    }
                });

        mSearchViewClearButton.animate().translationX(mSearchViewClearButtonTranslation);

        showTitleView();
        showMenuView();

        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(mSearchViewEditText.getWindowToken(), 0);
    }

    /**
     * @return Whether the search view is visible or not.
     */
    public boolean isSearchViewShown() {
        return mSearchViewShown;
    }

}
