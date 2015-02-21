package com.aluxian.drizzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.models.User;
import com.aluxian.drizzle.api.providers.UserShotsProvider;
import com.aluxian.drizzle.lists.UserAdapter;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.views.CustomEdgeRecyclerView;
import com.aluxian.drizzle.views.toolbar.NativeToolbar;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.Gson;

public class UserActivity extends Activity implements UserAdapter.HeaderListener {

    public static final String EXTRA_USER_DATA = "user_data";

    private User mUser;
    private CustomEdgeRecyclerView mRecyclerView;
    private NativeToolbar mToolbar;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mUser = new Gson().fromJson(getIntent().getStringExtra(EXTRA_USER_DATA), User.class);

        // Load the toolbar
        mToolbar = (NativeToolbar) findViewById(R.id.toolbar);
        setActionBar(mToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);

        mToolbar.setTitle(mUser.name);
        mToolbar.getTitleTextView().setAlpha(0);

        mRecyclerView = (CustomEdgeRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Header
                if (position == 0) {
                    return 2;
                } // else if is last item in list, span = 1 to show loader

                return 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new UserAdapter(mUser, this, new UserShotsProvider(mUser.id)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);

        if (mUser.links.web == null) {
            menu.removeItem(R.id.action_link_website);
        }

        if (mUser.links.twitter == null) {
            menu.removeItem(R.id.action_link_twitter);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isTaskRoot()) {
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    finish();
                }

                return true;

            case R.id.action_link_website:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mUser.links.web)));
                return true;

            case R.id.action_link_twitter:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mUser.links.twitter)));
                return true;

            case R.id.action_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TITLE, mUser.name);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mUser.name);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, mUser.htmlUrl);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_shot, mUser.name)));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAdapterLoadingFinished(boolean successful) {

    }

    @Override
    public void onAdapterLoadingError(boolean hasItems) {

    }

    @Override
    public void onHeaderLoaded(Palette.Swatch swatch, int height) {
        mRecyclerView.post(() -> {
            mRecyclerView.setTopEdgeColor(swatch.getTitleTextColor());
            mRecyclerView.setBottomEdgeColor(swatch.getRgb());
        });

        View toolbarBackground = findViewById(R.id.toolbar_background);
        toolbarBackground.setBackgroundColor(swatch.getRgb());
        toolbarBackground.getBackground().setAlpha(0);
        toolbarBackground.setElevation(0);

        float full = height - mToolbar.getHeight();

        mRecyclerView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                float fraction = scrollY / full;

                if (fraction > 1.0) {
                    fraction = 1.0f;
                } else if (fraction < 0) {
                    fraction = 0;
                }

                toolbarBackground.getBackground().setAlpha((int) (fraction * 255));
                toolbarBackground.setElevation(fraction * Dp.PX_4);
                mToolbar.getTitleTextView().setAlpha(fraction);
            }

            @Override
            public void onDownMotionEvent() {}

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {}
        });
    }

}
