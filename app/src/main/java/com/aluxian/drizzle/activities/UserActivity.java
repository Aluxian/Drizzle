package com.aluxian.drizzle.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.models.User;
import com.aluxian.drizzle.utils.AlphaSatColorMatrixEvaluator;
import com.aluxian.drizzle.utils.PaletteTransformation;
import com.aluxian.drizzle.views.CustomEdgeScrollView;
import com.aluxian.drizzle.views.widgets.ShotAttachments;
import com.aluxian.drizzle.views.widgets.ShotComments;
import com.aluxian.drizzle.views.widgets.ShotReboundOf;
import com.aluxian.drizzle.views.widgets.ShotRebounds;
import com.aluxian.drizzle.views.widgets.ShotSummary;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class UserActivity extends Activity {

    public static final String EXTRA_USER_DATA = "user_data";

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot);

        User user = new Gson().fromJson(getIntent().getStringExtra(EXTRA_USER_DATA), User.class);

        // Load the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);

        ImageView avatarBackground = (ImageView) findViewById(R.id.shot_preview);
        /*new Picasso.Builder(this)
                .indicatorsEnabled(true)
                .build()
                .load(shot.images.largest())
                .transform(PaletteTransformation.instance())
                .noFade()
                .into(avatarBackground, new Callback() {
                    @Override
                    public void onSuccess() {
                        AlphaSatColorMatrixEvaluator evaluator = new AlphaSatColorMatrixEvaluator();
                        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(evaluator.getColorMatrix());
                        avatarBackground.getDrawable().setColorFilter(filter);

                        ObjectAnimator animator = ObjectAnimator.ofObject(filter, "colorMatrix", evaluator, evaluator.getColorMatrix());
                        animator.addUpdateListener(animation -> avatarBackground.getDrawable().setColorFilter(filter));
                        animator.setDuration(1000);
                        animator.start();
                    }

                    @Override
                    public void onError() {

                    }
                });

        avatarBackground.postDelayed(() -> {
            Palette palette = PaletteTransformation.getPalette(avatarBackground);
            Palette.Swatch swatch = palette.getMutedSwatch();

            if (swatch == null) swatch = palette.getVibrantSwatch();
            if (swatch == null) swatch = palette.getDarkMutedSwatch();
            if (swatch == null) swatch = palette.getDarkVibrantSwatch();
            if (swatch == null) swatch = palette.getLightMutedSwatch();
            if (swatch == null) swatch = palette.getLightVibrantSwatch();

            if (swatch != null) {
                shotSummary.color(swatch);

                CustomEdgeScrollView scrollView = (CustomEdgeScrollView) findViewById(R.id.scroll_view);
                scrollView.setEdgeColor(swatch.getRgb());

                View toolbarBackground = findViewById(R.id.toolbar_background);
                toolbarBackground.setBackgroundColor(swatch.getRgb());
                toolbarBackground.setAlpha(0);

                View statusBarBackground = findViewById(R.id.status_bar_background);
                statusBarBackground.setBackgroundColor(swatch.getRgb());
                statusBarBackground.setAlpha(0);

                float full = avatarBackground.getHeight() - statusBarBackground.getHeight();

                scrollView.setOnScrollChangedListener(() -> {
                    float alpha = scrollView.getScrollY() / full;
                    toolbarBackground.setAlpha(alpha);
                    statusBarBackground.setAlpha(alpha);
                });
            }
        }, 2000);*/
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
