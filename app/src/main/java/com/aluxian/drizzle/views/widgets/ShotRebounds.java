package com.aluxian.drizzle.views.widgets;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.ShotActivity;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.views.CustomEdgeHorizontalScrollView;
import com.aluxian.drizzle.views.FixedAspectRatioImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ShotRebounds extends LinearLayout {

    private TextView mTitleView;
    private LinearLayout mScrollViewContainer;

    private CustomEdgeHorizontalScrollView mScrollView;
    private UberSwatch swatch;

    public ShotRebounds(Context context) {
        super(context);
        init(context);
    }

    public ShotRebounds(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShotRebounds(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        // Add the title
        mTitleView = new TextView(context);
        mTitleView.setTextAppearance(context, android.R.style.TextAppearance_Material_Body2);
        LayoutParams titleParams = new LayoutParams(MATCH_PARENT, Dp.PX_48);
        titleParams.setMarginStart(Dp.PX_16);
        titleParams.setMarginEnd(Dp.PX_16);
        mTitleView.setLayoutParams(titleParams);
        mTitleView.setGravity(Gravity.CENTER_VERTICAL);
        addView(mTitleView);

        // Add the ScrollView
        mScrollView = new CustomEdgeHorizontalScrollView(context);
        mScrollView.setLayoutParams(new LayoutParams(MATCH_PARENT, Dp.PX_72));
        mScrollView.setHorizontalScrollBarEnabled(false);
        addView(mScrollView);

        // Add the scroll view images container
        mScrollViewContainer = new LinearLayout(context);
        mScrollViewContainer.setLayoutParams(new HorizontalScrollView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        mScrollView.addView(mScrollViewContainer);
    }

    public void load(Shot shot) {
        if (shot.reboundsCount == 0) {
            setVisibility(GONE);
            return;
        }

        CountableInterpolator countableInterpolator = new CountableInterpolator(getContext());
        String title = countableInterpolator.apply(shot.reboundsCount, R.string.section_rebounds, R.string.section_rebound);

        mTitleView.setText(title);

        Dribbble.listRebounds(shot.id).execute(new ApiRequest.Callback<List<Shot>>() {
            @Override
            public void onSuccess(Dribbble.Response<List<Shot>> response) {
                int height = Dp.PX_72;

                for (int i = 0; i < response.data.size(); i++) {
                    ImageView imageView = new FixedAspectRatioImageView(getContext());
                    LayoutParams params = new LayoutParams(WRAP_CONTENT, height);

                    if (i == 0) {
                        params.setMarginStart(Dp.PX_16);
                    } else {
                        params.setMarginStart(Dp.PX_8);
                    }

                    if (i == response.data.size() - 1) {
                        params.setMarginEnd(Dp.PX_16);
                    }

                    imageView.setLayoutParams(params);
                    mScrollViewContainer.addView(imageView);

                    Shot reboundShot = response.data.get(i);

                    imageView.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), ShotActivity.class);
                        intent.putExtra(ShotActivity.EXTRA_SHOT_DATA, new Gson().toJson(reboundShot));
                        intent.putExtra(ShotActivity.EXTRA_REBOUND_OF, new Gson().toJson(shot));
                        getContext().startActivity(intent);
                    });

                    Picasso.with(getContext())
                            .load(reboundShot.images.largest())
                            .placeholder(R.color.slate)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    //ImageLoadingTransition.apply(imageView);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
            }

            @Override
            public void onError(Exception e) {
                setVisibility(GONE);
                Log.e(e);
            }
        });
    }

    public void color(UberSwatch swatch) {
        this.swatch = swatch;
        mScrollView.setEdgeColor(swatch.rgb);
    }

}
