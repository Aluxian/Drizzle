package com.aluxian.drizzle.views.widgets;

import android.content.Context;
import android.content.Intent;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.AttachmentActivity;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Attachment;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.views.CustomEdgeHorizontalScrollView;
import com.aluxian.drizzle.views.FixedAspectRatioImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ShotAttachments extends LinearLayout {

    private TextView mTitleView;
    private LinearLayout mScrollViewContainer;
    private CustomEdgeHorizontalScrollView mScrollView;

    public ShotAttachments(Context context) {
        super(context);
        init(context);
    }

    public ShotAttachments(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShotAttachments(Context context, AttributeSet attrs, int defStyleAttr) {
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
        if (shot.attachmentsCount == 0) {
            setVisibility(GONE);
            return;
        }

        CountableInterpolator countableInterpolator = new CountableInterpolator(getContext());
        String title = countableInterpolator.apply(shot.attachmentsCount, R.string.section_attachments, R.string.section_attachment);

        mTitleView.setText(title);

        Dribbble.listAttachments(shot.id).execute(new ApiRequest.Callback<List<Attachment>>() {
            @Override
            public void onSuccess(Dribbble.Response<List<Attachment>> response) {
                for (int i = 0; i < response.data.size(); i++) {
                    ImageView imageView = new FixedAspectRatioImageView(getContext());
                    LayoutParams params = new LayoutParams(WRAP_CONTENT, Dp.PX_72);

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

                    Attachment attachment = response.data.get(i);

                    imageView.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), AttachmentActivity.class);
                        intent.putExtra(AttachmentActivity.EXTRA_ATTACHMENT_DATA, new Gson().toJson(attachment));
                        getContext().startActivity(intent);
                    });

                    Picasso.with(getContext())
                            .load(attachment.thumbnailUrl)
                            .placeholder(R.color.slate)
                            .into(imageView);
                }
            }

            @Override
            public void onError(Exception e) {
                setVisibility(GONE);
                Log.e(e);
            }
        });
    }

    public void color(Palette.Swatch swatch) {
        mScrollView.setEdgeColor(swatch.getRgb());
    }

}
