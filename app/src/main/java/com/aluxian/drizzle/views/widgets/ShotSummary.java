package com.aluxian.drizzle.views.widgets;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.CircularBorderedTransformation;
import com.aluxian.drizzle.utils.CircularTransformation;
import com.aluxian.drizzle.utils.Dp;
import com.squareup.picasso.Picasso;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ShotSummary extends LinearLayout {

    private ImageView mUserAvatar;
    private ImageView mTeamAvatar;

    private TextView mShotTitle;
    private TextView mAuthorDescription;

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

        int userAvatarSize = Dp.toPx(56);
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
        infoParams.setMarginStart(Dp.toPx(16));
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

        // Load placeholder data for edit mode
        if (isInEditMode()) {
            mUserAvatar.setBackgroundColor(Color.GRAY);
            mShotTitle.setText("Philanthropy: CGI");
            mAuthorDescription.setText("by Nick Slater");
        }
    }

    private Shot mShot;

    public void load(Shot shot) {
        mShotTitle.setText(shot.title);
        mAuthorDescription.setText("by " + shot.user.name + (shot.team != null ? " for " + shot.team.name : ""));

        Picasso.with(getContext())
                .load(shot.user.avatarUrl)
                .transform(new CircularTransformation())
                .into(mUserAvatar);

        mShot = shot;
    }

    public void color(Palette.Swatch swatch) {
        setBackgroundColor(swatch.getRgb());
        mShotTitle.setTextColor(swatch.getTitleTextColor());
        mAuthorDescription.setTextColor(swatch.getBodyTextColor());

        if (mShot.team != null) {
            Picasso.with(getContext())
                    .load(mShot.team.avatarUrl)
                    .transform(new CircularBorderedTransformation(Dp.toPx(6), swatch.getRgb()))
                    .into(mTeamAvatar);
        }
    }

}
