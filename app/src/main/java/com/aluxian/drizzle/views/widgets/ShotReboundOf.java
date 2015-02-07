package com.aluxian.drizzle.views.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.PaletteTransformation;
import com.aluxian.drizzle.views.FixedAspectRatioImageView;
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
        reboundOfText.setLayoutParams(new LayoutParams(MATCH_PARENT, Dp.toPx(48)));
        reboundOfText.setGravity(Gravity.CENTER_VERTICAL);
        reboundOfText.setText("Rebound of");
        reboundOfText.setTextAppearance(context, android.R.style.TextAppearance_Material_Body2);
        addView(reboundOfText);

        // Content horizontal layout
        mContentLayout = new LinearLayout(context);
        mContentLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mContentLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(mContentLayout);

        // Add the shot preview
        mShotPreview = new FixedAspectRatioImageView(context);
        LayoutParams previewImageParams = new LayoutParams(WRAP_CONTENT, Dp.toPx(72));
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

        // Load placeholder data for edit mode
        if (isInEditMode()) {
            mShotPreview.setBackgroundColor(Color.GRAY);
            mShotTitle.setText("Philanthropy: CGI");
            mUserDescription.setText("by Nick Slater");
        }
    }

    public void load(Shot shot) {
        mShotTitle.setText(shot.title);
        mUserDescription.setText("by " + shot.user.name + (shot.team != null ? " for " + shot.team.name : ""));

        Picasso.with(getContext())
                .load(shot.images.normal)
                .placeholder(R.color.slate)
                .transform(PaletteTransformation.instance())
                .into(mShotPreview);

//        mShotPreview.postDelayed(() -> {
//            Palette palette = PaletteTransformation.getPalette(mShotPreview);
//            Palette.Swatch swatch = palette.getDarkMutedSwatch();
//
//            if (swatch == null) swatch = palette.getDarkVibrantSwatch();
//            if (swatch == null) swatch = palette.getMutedSwatch();
//            if (swatch == null) swatch = palette.getVibrantSwatch();
//            if (swatch == null) swatch = palette.getLightMutedSwatch();
//            if (swatch == null) swatch = palette.getLightVibrantSwatch();
//
//            if (swatch != null) {
//                mContentLayout.setBackgroundColor(swatch.getRgb());
//                mShotTitle.setTextColor(swatch.getTitleTextColor());
//                mUserDescription.setTextColor(swatch.getBodyTextColor());
//            }
//        }, 2000);
    }

}
