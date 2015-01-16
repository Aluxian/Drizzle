package com.aluxian.drizzle.ui.toolbar;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.aluxian.drizzle.R;

public class SearchWidget extends RelativeLayout {

    /** Whether the view is currently shown. */
    private boolean mShown;

    /** Used to show/hide the soft keyboard. */
    private InputMethodManager mInputMethodManager;

    /** The input field where the user will search. */
    private EditText mEditText;

    /** A button used to clear the EditText. */
    private ImageButton mClearButton;

    /** A reference to the native toolbar of the EnhancedToolbar. */
    private NativeToolbar mNativeToolbar;

    public SearchWidget(Context context) {
        super(context);
    }

    public SearchWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        mEditText = (EditText) findViewById(R.id.input);

        mClearButton = (ImageButton) findViewById(R.id.clear);
        mClearButton.setOnClickListener(v -> mEditText.setText(""));
        mClearButton.setTranslationX(mClearButton.getWidth());

        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNativeToolbar = ((EnhancedToolbar) getParent()).getNativeToolbar();
    }

    /**
     * @return Whether the search view is visible or not.
     */
    public boolean isShownInToolbar() {
        return mShown;
    }

    /**
     * Shows the search view animating all the necessary changes.
     */
    public void show() {
        mShown = true;

        setVisibility(View.VISIBLE);
        mEditText.animate().alpha(1);
        mClearButton.animate().translationX(0);

        mNativeToolbar.hideTitleView();
        mNativeToolbar.hideMenuView();

        mEditText.requestFocus();
        mEditText.postDelayed(() -> mInputMethodManager.showSoftInput(mEditText, 0), 500);
    }

    /**
     * Hides the search view animating all the necessary changes.
     */
    public void hide() {
        mShown = false;

        mEditText.animate().alpha(0).withEndAction(() -> {
            mEditText.setText("");
            setVisibility(View.GONE);
        });

        mClearButton.animate().translationX(mClearButton.getWidth());

        mNativeToolbar.showTitleView();
        mNativeToolbar.showMenuView();

        mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

}
