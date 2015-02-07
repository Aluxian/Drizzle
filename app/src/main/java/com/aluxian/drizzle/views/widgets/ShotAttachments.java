package com.aluxian.drizzle.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Attachment;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.views.FixedAspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ShotAttachments extends LinearLayout {

    private TextView mTitleView;
    private LinearLayout mScrollViewContainer;

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
        LayoutParams titleParams = new LayoutParams(MATCH_PARENT, Dp.toPx(48));
        titleParams.setMarginStart(Dp.toPx(16));
        titleParams.setMarginEnd(Dp.toPx(16));
        mTitleView.setLayoutParams(titleParams);
        mTitleView.setGravity(Gravity.CENTER_VERTICAL);
        addView(mTitleView);

        // Add the ScrollView
        HorizontalScrollView scrollView = new HorizontalScrollView(context);
        scrollView.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        scrollView.setHorizontalScrollBarEnabled(false);
        addView(scrollView);

        // Add the scroll view images container
        mScrollViewContainer = new LinearLayout(context);
        mScrollViewContainer.setLayoutParams(new HorizontalScrollView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        scrollView.addView(mScrollViewContainer);

        // Load placeholder data for edit mode
        if (isInEditMode()) {
            mTitleView.setText("2 attachments");
        }
    }

    public void load(Shot shot) {
        if (shot.attachmentsCount == 0) {
            setVisibility(GONE);
            return;
        }

        mTitleView.setText(shot.attachmentsCount + (shot.attachmentsCount == 1 ? " attachment" : " attachments"));
        Dribbble.listAttachments(shot.id).execute(new ApiRequest.Callback<List<Attachment>>() {
            @Override
            public void onSuccess(Dribbble.Response<List<Attachment>> response) {
                int height = Dp.toPx(72);

                for (int i = 0; i < response.data.size(); i++) {
                    ImageView imageView = new FixedAspectRatioImageView(getContext());
                    LayoutParams params = new LayoutParams(WRAP_CONTENT, height);

                    if (i > 0) {
                        params.setMarginStart(Dp.toPx(8));
                    }

                    imageView.setLayoutParams(params);
                    mScrollViewContainer.addView(imageView);

                    Picasso.with(getContext())
                            .load(response.data.get(i).thumbnailUrl)
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

}
