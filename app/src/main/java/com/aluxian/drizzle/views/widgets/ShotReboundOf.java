package com.aluxian.drizzle.views.widgets;

import android.content.Context;
import android.content.Intent;
import android.support.v7.graphics.Palette;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.ShotActivity;
import com.aluxian.drizzle.activities.UserActivity;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.PaletteTransformation;
import com.aluxian.drizzle.utils.Utils;
import com.aluxian.drizzle.views.FixedAspectRatioImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ShotReboundOf extends LinearLayout {

    private ImageView mShotPreview;
    private TextView mShotTitle;
    private TextView mUserDescription;
    private LinearLayout mContentLayout;

    public ShotReboundOf(Context context) {
        super(context);
        init(context);
    }

    public ShotReboundOf(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShotReboundOf(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        // 'Rebound of' header
        TextView reboundOfText = new TextView(context);
        reboundOfText.setLayoutParams(new LayoutParams(MATCH_PARENT, Dp.PX_48));
        reboundOfText.setGravity(Gravity.CENTER_VERTICAL);
        reboundOfText.setText(R.string.section_rebound_of);
        reboundOfText.setTextAppearance(context, android.R.style.TextAppearance_Material_Body2);
        addView(reboundOfText);

        // Content horizontal layout
        mContentLayout = new LinearLayout(context);
        mContentLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mContentLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(mContentLayout);

        // Add the shot preview
        mShotPreview = new FixedAspectRatioImageView(context);
        LayoutParams previewImageParams = new LayoutParams(WRAP_CONTENT, Dp.PX_72);
        previewImageParams.setMarginEnd(Dp.toPx(12));
        mShotPreview.setLayoutParams(previewImageParams);
        mContentLayout.addView(mShotPreview);

        // Layout for shot info
        LinearLayout infoLayout = new LinearLayout(context);
        infoLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        infoLayout.setOrientation(VERTICAL);
        mContentLayout.addView(infoLayout);

        // Shot title
        mShotTitle = new TextView(context);
        mShotTitle.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mShotTitle.setTextAppearance(context, android.R.style.TextAppearance_Material_Body2);
        infoLayout.addView(mShotTitle);

        // Shot author
        mUserDescription = new TextView(context);
        mUserDescription.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mUserDescription.setTextColor(getResources().getColor(R.color.text_light));
        infoLayout.addView(mUserDescription);
    }

    public void load(int shotId) {
        Dribbble.getShot(shotId).execute(new ApiRequest.Callback<Shot>() {
            @Override
            public void onSuccess(Dribbble.Response<Shot> response) {
                load(response.data);
            }

            @Override
            public void onError(Exception e) {
                Log.e(e);
            }
        });
    }

    public void color() {
        mShotPreview.postDelayed(() -> {
            Palette palette = PaletteTransformation.getPalette(mShotPreview);
            Palette.Swatch swatch = Utils.getSwatch(palette);

            if (swatch != null) {
                mContentLayout.setBackgroundColor(swatch.getRgb());
                mShotTitle.setTextColor(swatch.getTitleTextColor());
                mUserDescription.setTextColor(swatch.getBodyTextColor());
                mUserDescription.setLinkTextColor(swatch.getTitleTextColor());
            }
        }, 100);
    }

    public void load(Shot shot) {
        mShotTitle.setText(shot.title);

        mUserDescription.setText(shot.generateAuthorDescription(getContext()));
        mUserDescription.setMovementMethod(LinkMovementMethod.getInstance());

        OnClickListener listener = v -> {
            Intent intent = new Intent(getContext(), ShotActivity.class);
            intent.putExtra(ShotActivity.EXTRA_SHOT_DATA, new Gson().toJson(shot));
            getContext().startActivity(intent);
        };

        mShotPreview.setOnClickListener(listener);
        mContentLayout.setOnClickListener(listener);
        mShotTitle.setOnClickListener(listener);

        mUserDescription.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserActivity.class);
            intent.putExtra(UserActivity.EXTRA_USER_DATA, new Gson().toJson(shot.user));
            getContext().startActivity(intent);
        });

        Picasso.with(getContext())
                .load(shot.images.normal)
                .placeholder(R.color.slate)
                .transform(PaletteTransformation.instance())
                .into(mShotPreview, new Callback() {
                    @Override
                    public void onSuccess() {
                        color();
                        //ImageLoadingTransition.apply(mShotPreview);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

}
