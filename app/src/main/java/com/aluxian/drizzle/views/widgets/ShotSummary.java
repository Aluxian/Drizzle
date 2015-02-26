package com.aluxian.drizzle.views.widgets;

import android.content.Context;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.UserActivity;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.utils.transformations.CircularBorderedTransformation;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ShotSummary extends LinearLayout {

    private ImageView mUserAvatar;
    private ImageView mTeamAvatar;

    private TextView mShotTitle;
    private TextView mAuthorDescription;

    private Shot mShot;

    public ShotSummary(Context context) {
        super(context);
        init(context);
    }

    public ShotSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShotSummary(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER_VERTICAL);

        int userAvatarSize = Dp.PX_56;
        int teamAvatarSize = Dp.toPx(20);

        // Avatar container
        RelativeLayout avatarContainer = new RelativeLayout(context);
        avatarContainer.setLayoutParams(new LayoutParams(userAvatarSize, userAvatarSize));
        addView(avatarContainer);

        // User avatar
        mUserAvatar = new ImageView(context);
        mUserAvatar.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        avatarContainer.addView(mUserAvatar);

        // Team avatar
        mTeamAvatar = new ImageView(context);
        RelativeLayout.LayoutParams previewParams = new RelativeLayout.LayoutParams(teamAvatarSize, teamAvatarSize);
        previewParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        previewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        mTeamAvatar.setLayoutParams(previewParams);
        avatarContainer.addView(mTeamAvatar);

        // Layout for shot info
        LinearLayout infoLayout = new LinearLayout(context);
        LinearLayout.LayoutParams infoParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        infoParams.setMarginStart(Dp.PX_16);
        infoLayout.setLayoutParams(infoParams);
        infoLayout.setOrientation(VERTICAL);
        addView(infoLayout);

        // Shot title
        mShotTitle = new TextView(context);
        mShotTitle.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mShotTitle.setTextAppearance(context, android.R.style.TextAppearance_Material_Title);
        infoLayout.addView(mShotTitle);

        // Shot author
        mAuthorDescription = new TextView(context);
        mAuthorDescription.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mAuthorDescription.setTextAppearance(context, android.R.style.TextAppearance_Material_Subhead);
        infoLayout.addView(mAuthorDescription);
    }

    public void load(Shot shot) {
        mShotTitle.setText(shot.title);

        mAuthorDescription.setText(shot.generateAuthorDescription(getContext()));
        mAuthorDescription.setMovementMethod(LinkMovementMethod.getInstance());

        mUserAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserActivity.class);
            intent.putExtra(UserActivity.EXTRA_USER_DATA, new Gson().toJson(shot.user));
            getContext().startActivity(intent);
        });

        Picasso.with(getContext())
                .load(shot.user.avatarUrl)
                .transform(new CircularTransformation())
                .placeholder(R.drawable.round_placeholder)
                .into(mUserAvatar);

        mShot = shot;
    }

    public void color(UberSwatch swatch) {
        setBackgroundColor(swatch.rgb);
        mShotTitle.setTextColor(swatch.titleTextColor);
        mAuthorDescription.setTextColor(swatch.bodyTextColor);

        mAuthorDescription.setLinkTextColor(swatch.bodyTextColor);

        if (mShot.team != null) {
            Picasso.with(getContext())
                    .load(mShot.team.avatarUrl)
                    .transform(new CircularBorderedTransformation(Dp.toPx(6), swatch.rgb))
                    .into(mTeamAvatar);
        }
    }

}
