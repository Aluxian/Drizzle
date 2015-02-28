package com.aluxian.drizzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.AdapterHeaderListener;
import com.aluxian.drizzle.adapters.ShotActivityAdapter;
import com.aluxian.drizzle.adapters.multi.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.ShotCommentsProvider;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.views.CustomEdgeRecyclerView;
import com.aluxian.drizzle.views.toolbar.NativeToolbar;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.Gson;

public class ShotActivity extends Activity implements AdapterHeaderListener, MultiTypeInfiniteAdapter.StatusListener {

    public static final String EXTRA_SHOT_DATA = "shot_data";
    public static final String EXTRA_REBOUND_OF = "rebound_of";

    private Shot shot;
    private NativeToolbar mToolbar;
    private CustomEdgeRecyclerView mRecyclerView;
    private ShotActivityAdapter mAdapter;
    private Menu mMenu;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot);

        shot = new Gson().fromJson(getIntent().getStringExtra(EXTRA_SHOT_DATA), Shot.class);
        Shot reboundShot = null;

        if (getIntent().hasExtra(EXTRA_REBOUND_OF)) {
            reboundShot = new Gson().fromJson(getIntent().getStringExtra(EXTRA_REBOUND_OF), Shot.class);
        }

        // Load the toolbar
        mToolbar = (NativeToolbar) findViewById(R.id.toolbar);
        setActionBar(mToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);

        mToolbar.setTitle(shot.title);
        mToolbar.getTitleTextView().setAlpha(0);

        mRecyclerView = (CustomEdgeRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShotActivityAdapter(shot, reboundShot, new ShotCommentsProvider(shot.id), this, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shot, menu);
        mMenu = menu;
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

            case R.id.action_share_link:
                String by = " " + getResources().getString(R.string.word_by) + " ";
                String title = shot.title + by + shot.user.name;

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TITLE, title);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shot.htmlUrl);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_shot, shot.title)));

                return true;

            case R.id.action_share_image:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHeaderLoaded(UberSwatch swatch, int height) {
        mRecyclerView.post(() -> mRecyclerView.setEdgeColor(swatch.rgb));
        mAdapter.setColors(swatch);

        mMenu.findItem(R.id.action_share_link).getIcon().setTint(swatch.rgb);
        mMenu.findItem(R.id.action_share_image).getIcon().setTint(swatch.rgb);

        View toolbarBackground = findViewById(R.id.toolbar_background);
        toolbarBackground.setBackgroundColor(swatch.rgb);
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

    @Override
    public void onAdapterLoadingFinished(boolean successful) {

    }

    @Override
    public void onAdapterLoadingError(Exception e, boolean hasItems) {

    }

}
