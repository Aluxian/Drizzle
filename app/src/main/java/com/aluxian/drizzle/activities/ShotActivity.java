package com.aluxian.drizzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.ShotCommentsProvider;
import com.aluxian.drizzle.lists.ShotAdapter;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.views.CustomEdgeRecyclerView;
import com.aluxian.drizzle.views.toolbar.NativeToolbar;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.Gson;

public class ShotActivity extends Activity implements ShotAdapter.Listener {

    public static final String EXTRA_SHOT_DATA = "shot_data";
    public static final String EXTRA_REBOUND_OF = "rebound_of";

    private Shot shot;
    private NativeToolbar mToolbar;
    private CustomEdgeRecyclerView mRecyclerView;

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
        mRecyclerView.setAdapter(new ShotAdapter(shot, reboundShot, new ShotCommentsProvider(shot.id), this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shot, menu);
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

            case R.id.action_share:
                String by = " " + getResources().getString(R.string.word_by) + " ";
                String title = shot.title + by + shot.user.name;

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TITLE, title);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shot.htmlUrl);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_shot, shot.title)));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHeaderLoaded(Palette.Swatch swatch, int height) {
        mRecyclerView.post(() -> mRecyclerView.setEdgeColor(swatch.getRgb()));

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
