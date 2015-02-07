package com.aluxian.drizzle.views.widgets;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Comment;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.CircularTransformation;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.views.FixedAspectRatioImageView;
import com.aluxian.drizzle.views.SquareImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ShotComments extends LinearLayout {

    private TextView mTitleView;
    private LinearLayout mCommentsContainer;

    public ShotComments(Context context) {
        super(context);
        init(context);
    }

    public ShotComments(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShotComments(Context context, AttributeSet attrs, int defStyleAttr) {
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

        // Add the comments container
        mCommentsContainer = new LinearLayout(context);
        mCommentsContainer.setOrientation(VERTICAL);
        mCommentsContainer.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        mCommentsContainer.setHorizontalScrollBarEnabled(false);
        addView(mCommentsContainer);

        // Load placeholder data for edit mode
        if (isInEditMode()) {
            mTitleView.setText("2 response");
        }
    }

    public void load(Shot shot) {
        if (shot.commentsCount == 0) {
            setVisibility(GONE);
            return;
        }

        mTitleView.setText(shot.commentsCount + (shot.commentsCount == 1 ? " response" : " responses"));
        Dribbble.listComments(shot.id).execute(new ApiRequest.Callback<List<Comment>>() {
            @Override
            public void onSuccess(Dribbble.Response<List<Comment>> response) {
                for (Comment comment : response.data) {
                    View divider = LayoutInflater.from(getContext()).inflate(R.layout.divider, mCommentsContainer, false);
                    ((LayoutParams) divider.getLayoutParams()).setMarginStart(Dp.toPx(72));
                    mCommentsContainer.addView(divider);

                    // Add content layout
                    int contentMargin = Dp.toPx(16);
                    LinearLayout commentContent = new LinearLayout(getContext());
                    LinearLayout.LayoutParams contentParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    contentParams.setMargins(contentMargin, contentMargin, contentMargin, contentMargin);
                    commentContent.setLayoutParams(contentParams);
                    mCommentsContainer.addView(commentContent);

                    // Add user avatar
                    SquareImageView userAvatar = new SquareImageView(getContext());
                    LayoutParams avatarParams = new LayoutParams(Dp.toPx(40), WRAP_CONTENT);
                    avatarParams.setMarginEnd(Dp.toPx(16));
                    userAvatar.setLayoutParams(avatarParams);
                    commentContent.addView(userAvatar);

                    Picasso.with(getContext())
                            .load(comment.user.avatarUrl)
                            .transform(new CircularTransformation())
                            .into(userAvatar);

                    // Add body layout
                    LinearLayout bodyLayout = new LinearLayout(getContext());
                    bodyLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                    bodyLayout.setGravity(Gravity.CENTER_VERTICAL);
                    bodyLayout.setOrientation(VERTICAL);
                    commentContent.addView(bodyLayout);

                    // Author name
                    TextView authorName = new TextView(getContext());
                    authorName.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                    authorName.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body2);
                    authorName.setText(comment.user.name);
                    bodyLayout.addView(authorName);

                    // Comment body
                    TextView commentBody = new TextView(getContext());
                    LayoutParams bodyParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    bodyParams.topMargin = Dp.toPx(8);
                    //bodyParams.bottomMargin = Dp.toPx(8);
                    commentBody.setLayoutParams(bodyParams);
                    commentBody.setText(Html.fromHtml(comment.body));
                    commentBody.setMovementMethod(LinkMovementMethod.getInstance());
                    bodyLayout.addView(commentBody);

                    // Comment info
                    CharSequence time = DateUtils.getRelativeTimeSpanString(comment.createdAt.getTime());
                    String likes = "";

                    if (comment.likesCount > 0) {
                        likes = ", " + comment.likesCount + (comment.likesCount == 1 ? " like" : " likes");
                    }

                    TextView infoText = new TextView(getContext());
                    infoText.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                    infoText.setText(time + likes);
                    infoText.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Caption);
                    bodyLayout.addView(infoText);
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
