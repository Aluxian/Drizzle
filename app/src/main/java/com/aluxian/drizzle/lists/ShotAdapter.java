package com.aluxian.drizzle.lists;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.UserActivity;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Comment;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.LikesProvider;
import com.aluxian.drizzle.api.providers.ShotCommentsProvider;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.Utils;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.aluxian.drizzle.utils.transformations.PaletteTransformation;
import com.aluxian.drizzle.views.CustomEdgeRecyclerView;
import com.aluxian.drizzle.views.widgets.ShotAttachments;
import com.aluxian.drizzle.views.widgets.ShotReboundOf;
import com.aluxian.drizzle.views.widgets.ShotRebounds;
import com.aluxian.drizzle.views.widgets.ShotSummary;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_COMMENT = 1;

    /** A list with all the displayed items. */
    private List<Comment> mCommentsList = new ArrayList<>();

    /** Whether the adapter is currently loading items. */
    private boolean mIsLoadingItems;

    /** An ItemsProvider used to load items for this adapter. */
    private ShotCommentsProvider mCommentsProvider;

    private Shot mShot;
    private Shot mReboundOfShot;

    private HeaderListener mHeaderListener;
    private Palette.Swatch mSwatch;
    private boolean mHeaderLoaded;

    public ShotAdapter(Shot shot, Shot reboundOfShot, ShotCommentsProvider commentsProvider, HeaderListener headerListener) {
        mShot = shot;
        mReboundOfShot = reboundOfShot;

        mCommentsProvider = commentsProvider;
        mHeaderListener = headerListener;

        // Load the first items
        new LoadItemsIfRequiredTask(0).execute();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_shot, parent, false));
        }

        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_COMMENT;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            if (mHeaderLoaded) {
                return;
            }

            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            holder.rebounds.load(mShot);
            holder.attachments.load(mShot);

            holder.reboundOf.setVisibility(View.GONE);

            if (mReboundOfShot != null) {
                holder.reboundOf.setVisibility(View.VISIBLE);
                holder.reboundOf.load(mReboundOfShot);
            }/* else {
                Dribbble.getShotExtra(shot.id).execute(new ApiRequest.Callback<Shot.Extra>() {
                    @Override
                    public void onSuccess(Dribbble.Response<Shot.Extra> response) {
                        if (response.data.reboundOf != null) {
                            reboundOf.setVisibility(View.VISIBLE);
                            reboundOf.load(response.data.reboundOf);
                        }

                        ShotPalette shotPalette = (ShotPalette) findViewById(R.id.shot_palette);
                        shotPalette.load(response.data.colours);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(e);
                    }
                });
            }*/

            holder.summary.load(mShot);

            CountableInterpolator countableInterpolator = new CountableInterpolator(holder.itemView.getContext());

            holder.likes.setText(countableInterpolator.apply(mShot.likesCount, R.string.stats_likes, R.string.stats_like));
            holder.buckets.setText(countableInterpolator.apply(mShot.bucketsCount, R.string.stats_buckets, R.string.stats_bucket));
            holder.views.setText(countableInterpolator.apply(mShot.viewsCount, R.string.stats_views, R.string.stats_view));
            holder.tags.setText(countableInterpolator.apply(mShot.tags.size(), R.string.stats_tags, R.string.stats_tag));

            if (!TextUtils.isEmpty(mShot.description)) {
                holder.description.setMovementMethod(LinkMovementMethod.getInstance());
                holder.description.setText(Html.fromHtml(mShot.description));
            } else {
                holder.description.setVisibility(View.GONE);
            }

            String title = countableInterpolator.apply(mShot.commentsCount, R.string.section_responses, R.string.section_response);
            holder.commentsSubheader.setText(title);

            Picasso.with(holder.preview.getContext())
                    .load(mShot.images.largest())
                    .transform(PaletteTransformation.instance())
                            //.noFade()
                    .into(holder.preview, new Callback() {
                        @Override
                        public void onSuccess() {
                            //ImageLoadingTransition.apply(preview);

                            holder.preview.postDelayed(() -> {
                                Palette palette = PaletteTransformation.getPalette(holder.preview);
                                mSwatch = Utils.getSwatch(palette);

                                if (mSwatch != null) {
                                    if (mHeaderLoaded) {
                                        return;
                                    }

                                    holder.description.setLinkTextColor(mSwatch.getRgb());
                                    //.color(swatch.getRgb());
                                    holder.preview.setBackgroundColor(mSwatch.getRgb());

                                    holder.rebounds.color(mSwatch);
                                    holder.attachments.color(mSwatch);

                                    mHeaderLoaded = true;
                                    holder.summary.color(mSwatch);
                                    mHeaderListener.onHeaderLoaded(mSwatch, holder.preview.getHeight());

                                    holder.likes.setOnClickListener(v -> {
                                        LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(holder.likes.getContext())
                                                .inflate(R.layout.dialog_fans, null);
                                        dialogLayout.setBackgroundColor(mSwatch.getRgb());

                                        TextView titleView = (TextView) dialogLayout.findViewById(R.id.title);
                                        titleView.setTextColor(Color.WHITE);

                                        CustomEdgeRecyclerView recyclerView = (CustomEdgeRecyclerView) dialogLayout.findViewById(R.id.list);
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.postDelayed(() -> recyclerView.setEdgeColor(mSwatch.getRgb()), 500);

                                        recyclerView.setLayoutManager(new LinearLayoutManager(holder.likes.getContext()));
                                        recyclerView.setAdapter(new LikesAdapter(holder.likes.getContext(), new LikesProvider(mShot.id)));

                                        new AlertDialog.Builder(holder.likes.getContext(), R.style.Drizzle_Widget_Dialog)
                                                .setView(dialogLayout)
                                                .show();
                                    });
                                }
                            }, 100);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } else if (viewHolder instanceof CommentViewHolder) {
            CommentViewHolder holder = (CommentViewHolder) viewHolder;
            Comment comment = mCommentsList.get(position - 1);

            if (position % 2 == 0) {
                holder.itemView.setBackgroundResource(R.color.bg_light);
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE);
            }

            View.OnClickListener authorClickListener = (v) -> {
                Intent intent = new Intent(holder.itemView.getContext(), UserActivity.class);
                intent.putExtra(UserActivity.EXTRA_USER_DATA, new Gson().toJson(comment.user));
                holder.itemView.getContext().startActivity(intent);
            };

            holder.avatar.setOnClickListener(authorClickListener);
            holder.author.setOnClickListener(authorClickListener);

            Picasso.with(holder.avatar.getContext())
                    .load(comment.user.avatarUrl)
                    .transform(new CircularTransformation())
                    .into(holder.avatar);

            holder.author.setText(comment.user.name);

            holder.body.setText(Html.fromHtml(comment.body));
            holder.body.setMovementMethod(LinkMovementMethod.getInstance());

            if (mSwatch != null) {
                holder.body.setLinkTextColor(mSwatch.getRgb());
            }

            CharSequence time = DateUtils.getRelativeTimeSpanString(comment.createdAt.getTime());
            String likes = "";

            if (comment.likesCount > 0) {
                CountableInterpolator countableInterpolator = new CountableInterpolator(holder.footer.getContext());
                likes = ", " + countableInterpolator.apply(comment.likesCount, R.string.stats_likes, R.string.stats_like);
            }

            holder.footer.setText(time + likes);

//        if (position % 2 == 0) {
//            convertView.setBackgroundResource(R.color.bg_light);
//        } else {
//            convertView.setBackground(null);
//        }

//        holder.itemView.setOnClickListener(view -> {
//            /*Intent intent = new Intent(activity, ShotActivity.class);
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, holder.image, "robot");
//            activity.startActivity(intent, options.toBundle());*/
//
//            Intent intent = new Intent(mContext, UserActivity.class);
//            intent.putExtra(UserActivity.EXTRA_USER_DATA, new Gson().toJson(like.user));
//            mContext.startActivity(intent);
//        });
        }

        // If position is near the end of the list, load more items from the API
        new LoadItemsIfRequiredTask(position).execute();
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    private class LoadItemsIfRequiredTask extends AsyncTask<Void, Void, List<Comment>> {

        /** The position of the item that executed the task. */
        private final int mPosition;

        private LoadItemsIfRequiredTask(int position) {
            mPosition = position;
        }

        @Override
        protected void onPreExecute() {
            if (!mIsLoadingItems && mCommentsList.size() - mPosition <= Config.LOAD_ITEMS_THRESHOLD) {
                mIsLoadingItems = true;
            } else {
                cancel(true);
            }
        }

        @Override
        protected List<Comment> doInBackground(Void... params) {
            try {
                return mCommentsProvider.load();
            } catch (IOException | BadRequestException | TooManyRequestsException e) {
                Log.d(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Comment> response) {
            if (response != null) {
                mCommentsList.addAll(response);
                notifyItemRangeInserted(mCommentsList.size() - response.size(), response.size());
            }

            mIsLoadingItems = false;
        }

    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.shot_preview) ImageView preview;
        @InjectView(R.id.shot_summary) ShotSummary summary;
        @InjectView(R.id.shot_rebound_of) ShotReboundOf reboundOf;
        @InjectView(R.id.shot_rebounds) ShotRebounds rebounds;
        @InjectView(R.id.shot_attachments) ShotAttachments attachments;
        @InjectView(R.id.shot_description) TextView description;
        @InjectView(R.id.subheader_comments) TextView commentsSubheader;

        @InjectView(R.id.shot_likes) TextView likes;
        @InjectView(R.id.shot_buckets) TextView buckets;
        @InjectView(R.id.shot_views) TextView views;
        @InjectView(R.id.shot_tags) TextView tags;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.comment_avatar) ImageView avatar;
        @InjectView(R.id.comment_author) TextView author;
        @InjectView(R.id.comment_body) TextView body;
        @InjectView(R.id.comment_footer) TextView footer;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

    public static interface HeaderListener {

        /**
         * Called after the header view is loaded for the first time.
         *
         * @param swatch The generated colour swatch for the shot preview.
         * @param height The height of the entire header view.
         */
        public void onHeaderLoaded(Palette.Swatch swatch, int height);

    }

}
