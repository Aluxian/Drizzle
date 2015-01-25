package com.aluxian.drizzle.ui.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.utils.Log;

public class EnhancedToolbar extends RelativeLayout {

    /** Android's toolbar view. */
    private NativeToolbar mNativeToolbar;

    /** A view that display an EditText for searching. */
    private SearchWidget mSearchWidget;

    public EnhancedToolbar(Context context) {
        super(context);
        init(context);
    }

    public EnhancedToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EnhancedToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mNativeToolbar = (NativeToolbar) LayoutInflater.from(context).inflate(R.layout.inflate_native_toolbar, this, false);
        mSearchWidget = (SearchWidget) LayoutInflater.from(context).inflate(R.layout.inflate_search_widget, this, false);

        // Make the search widget have the same height as the toolbar
        LayoutParams searchViewParams = (LayoutParams) mSearchWidget.getLayoutParams();
        searchViewParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.native_toolbar);
        searchViewParams.leftMargin = mNativeToolbar.getContentInsetStart();
        mSearchWidget.setLayoutParams(searchViewParams);

        addView(mNativeToolbar);
        addView(mSearchWidget);
    }

    public NativeToolbar getNativeToolbar() {
        return mNativeToolbar;
    }

    public SearchWidget getSearchView() {
        return mSearchWidget;
    }

    /**
     * Shows the toolbar.
     *
     * @param animate Whether the changes should be animated.
     */
    public void show(boolean animate) {
        mNativeToolbar.show(animate);
    }

    /**
     * Hides the toolbar.
     *
     * @param animate Whether the changes should be animated.
     */
    public void hide(boolean animate) {
        mNativeToolbar.hide(animate);
    }

}
