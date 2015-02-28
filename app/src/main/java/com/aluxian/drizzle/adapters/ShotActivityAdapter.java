package com.aluxian.drizzle.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.UserActivity;
import com.aluxian.drizzle.adapters.multi.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.MultiTypeHeader;
import com.aluxian.drizzle.adapters.multi.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.adapters.multi.MultiTypeStyleableItem;
import com.aluxian.drizzle.api.models.Comment;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.AttachmentsProvider;
import com.aluxian.drizzle.api.providers.BucketsProvider;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.api.providers.LikesProvider;
import com.aluxian.drizzle.api.providers.ReboundsProvider;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.LocaleManager;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.Mapper;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.aluxian.drizzle.utils.transformations.PaletteTransformation;
import com.aluxian.drizzle.views.CustomEdgeRecyclerView;
import com.aluxian.drizzle.views.ShotPreviewGifImageView;
import com.aluxian.drizzle.views.widgets.ShotReboundOf;
import com.aluxian.drizzle.views.widgets.ShotSummary;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.droidsonroids.gif.GifDrawable;

public class ShotActivityAdapter extends MultiTypeInfiniteAdapter<Comment> {

    public ShotActivityAdapter(Shot shot, Shot reboundOfShot, ItemsProvider<Comment> itemsProvider,
                               AdapterHeaderListener headerListener, StatusListener statusListener) {
        super(itemsProvider, statusListener);
        itemsList().add(0, new HeaderItem(shot, reboundOfShot, headerListener));
        notifyItemInserted(0);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(new MultiTypeItemType<>(HeaderItem.class, HeaderItem.ViewHolder.class, R.layout.item_header_shot));
        addItemType(new MultiTypeItemType<>(CommentItem.class, CommentItem.ViewHolder.class, R.layout.item_comment));
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Comment> items) {
        return Mapper.map(items, CommentItem::new);
    }

    public static class HeaderItem extends MultiTypeStyleableItem<HeaderItem.ViewHolder> implements MultiTypeHeader {

        private final Shot shot;
        private final Shot reboundOfShot;
        private AdapterHeaderListener headerListener;

        public HeaderItem(Shot shot, Shot reboundOfShot, AdapterHeaderListener headerListener) {
            this.shot = shot;
            this.reboundOfShot = reboundOfShot;
            this.headerListener = headerListener;
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, int position) {
            loadRebounds(holder);
            loadAttachments(holder);

            holder.reboundOf.setVisibility(View.GONE);

            if (reboundOfShot != null) {
                holder.reboundOf.setVisibility(View.VISIBLE);
                holder.reboundOf.load(reboundOfShot);
            }

            holder.summary.load(shot);

            holder.likes.setTag(shot.id);
            holder.buckets.setTag(shot.id);
            holder.tags.setTag(shot.id);

            CountableInterpolator countableInterpolator = new CountableInterpolator(holder.context);

            holder.likes.setText(countableInterpolator.apply(shot.likesCount, R.string.stats_likes, R.string.stats_like));
            holder.buckets.setText(countableInterpolator.apply(shot.bucketsCount, R.string.stats_buckets, R.string.stats_bucket));
            holder.views.setText(countableInterpolator.apply(shot.viewsCount, R.string.stats_views, R.string.stats_view));
            holder.tags.setText(countableInterpolator.apply(shot.tags.size(), R.string.stats_tags, R.string.stats_tag));

            if (!TextUtils.isEmpty(shot.description)) {
                holder.description.setMovementMethod(LinkMovementMethod.getInstance());
                holder.description.setText(Html.fromHtml(shot.description));
            } else {
                holder.description.setVisibility(View.GONE);
            }

            String title = countableInterpolator.apply(shot.commentsCount, R.string.section_responses, R.string.section_response);
            holder.commentsSubheader.setText(title);

            Picasso.with(holder.context)
                    .load(shot.images.largest())
                    .transform(PaletteTransformation.instance())
                    .placeholder(R.color.slate)
                    .into(holder.preview, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.preview.postDelayed(() -> {
                                if (shot.isGIF()) {
                                    holder.gifLoader.setVisibility(View.VISIBLE);

                                    new AsyncTask<Void, Void, GifDrawable>() {
                                        @Override
                                        protected GifDrawable doInBackground(Void... params) {
                                            try {
                                                Request request = new Request.Builder().url(shot.images.largest()).build();
                                                Response response = new OkHttpClient().newCall(request).execute();
                                                return new GifDrawable(response.body().bytes());
                                            } catch (IOException e) {
                                                Log.e(e);
                                            }

                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(GifDrawable gif) {
                                            if (gif != null) {
                                                holder.preview.setImageDrawable(gif);
                                                holder.gifLoader.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }.execute();
                                }

                                UberSwatch swatch = new UberSwatch(PaletteTransformation.getPalette(holder.preview));
                                headerListener.onHeaderLoaded(swatch, holder.preview.getHeight());
                                onSetStyle(holder, swatch);
                            }, 100);
                        }

                        @Override
                        public void onError() {}
                    });
        }

        private void loadRebounds(ViewHolder holder) {
            if (shot.reboundsCount == 0) {
                holder.reboundsContainer.setVisibility(View.GONE);
                return;
            }

            CountableInterpolator countableInterpolator = new CountableInterpolator(holder.context);
            String title = countableInterpolator.apply(shot.reboundsCount, R.string.section_rebounds, R.string.section_rebound);
            holder.reboundsHeader.setText(title);

            holder.reboundsRecycler.setAdapter(new ReboundsAdapter(shot, new ReboundsProvider(shot.id), new StatusListener() {
                @Override
                public void onAdapterLoadingFinished(boolean successful) {
                    // TODO
                }

                @Override
                public void onAdapterLoadingError(Exception e, boolean hasItems) {
                    Log.e(e);
                }
            }));
        }

        private void loadAttachments(ViewHolder holder) {
            if (shot.attachmentsCount == 0) {
                holder.attachmentsContainer.setVisibility(View.GONE);
                return;
            }

            CountableInterpolator countableInterpolator = new CountableInterpolator(holder.context);
            String title = countableInterpolator.apply(shot.attachmentsCount, R.string.section_attachments, R.string.section_attachment);
            holder.attachmentsHeader.setText(title);

            holder.attachmentsRecycler.setAdapter(new AttachmentsAdapter(new AttachmentsProvider(shot.id), new StatusListener() {
                @Override
                public void onAdapterLoadingFinished(boolean successful) {
                    // TODO
                }

                @Override
                public void onAdapterLoadingError(Exception e, boolean hasItems) {
                    Log.e(e);
                }
            }));
        }

        @Override
        public int getId(int position) {
            return shot.id;
        }

        @Override
        protected void onSetStyle(ViewHolder holder, UberSwatch swatch) {
            holder.description.setLinkTextColor(swatch.rgb);
            holder.preview.setBackgroundColor(swatch.rgb);

            holder.summary.color(swatch);

            holder.reboundsRecycler.setEdgeColor(swatch.rgb);
            holder.attachmentsRecycler.setEdgeColor(swatch.rgb);

            holder.likes.setOnClickListener(v -> {
                LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(holder.context).inflate(R.layout.dialog_list, null);
                dialogLayout.setBackgroundColor(swatch.rgb);

                TextView titleView = (TextView) dialogLayout.findViewById(R.id.title);
                titleView.setTextColor(swatch.titleTextColor);
                titleView.setText(R.string.dialog_title_likes);

                LikesAdapter likesAdapter = new LikesAdapter(new LikesProvider((int) holder.likes.getTag()), new StatusListener() {
                    @Override
                    public void onAdapterLoadingFinished(boolean successful) {

                    }

                    @Override
                    public void onAdapterLoadingError(Exception e, boolean hasItems) {

                    }
                });

                likesAdapter.setColors(swatch);
                likesAdapter.showLoadingIndicatorWhenEmpty(true);

                CustomEdgeRecyclerView recyclerView = (CustomEdgeRecyclerView) dialogLayout.findViewById(R.id.list);
                recyclerView.setLayoutManager(new LinearLayoutManager(holder.context));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(likesAdapter);

                recyclerView.postDelayed(() -> recyclerView.setEdgeColor(swatch.rgb), 500);
                new AlertDialog.Builder(holder.context, R.style.Drizzle_Widget_Dialog).setView(dialogLayout).show();
            });

            holder.buckets.setOnClickListener(v -> {
                LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(holder.context).inflate(R.layout.dialog_list, null);
                dialogLayout.setBackgroundColor(swatch.rgb);

                TextView titleView = (TextView) dialogLayout.findViewById(R.id.title);
                titleView.setTextColor(swatch.titleTextColor);
                titleView.setText(R.string.dialog_title_buckets);

                BucketsAdapter bucketsAdapter = new BucketsAdapter(new BucketsProvider((int) holder.likes.getTag()), new StatusListener() {
                    @Override
                    public void onAdapterLoadingFinished(boolean successful) {

                    }

                    @Override
                    public void onAdapterLoadingError(Exception e, boolean hasItems) {

                    }
                });

                bucketsAdapter.setColors(swatch);
                bucketsAdapter.showLoadingIndicatorWhenEmpty(true);

                CustomEdgeRecyclerView recyclerView = (CustomEdgeRecyclerView) dialogLayout.findViewById(R.id.list);
                recyclerView.setLayoutManager(new LinearLayoutManager(holder.context));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(bucketsAdapter);

                recyclerView.postDelayed(() -> recyclerView.setEdgeColor(swatch.rgb), 500);
                new AlertDialog.Builder(holder.context, R.style.Drizzle_Widget_Dialog).setView(dialogLayout).show();
            });

            holder.tags.setOnClickListener(v -> {
                LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(holder.context).inflate(R.layout.dialog_list, null);
                dialogLayout.setBackgroundColor(swatch.rgb);

                TextView titleView = (TextView) dialogLayout.findViewById(R.id.title);
                titleView.setTextColor(swatch.titleTextColor);
                titleView.setText(R.string.dialog_title_tags);

                CustomEdgeRecyclerView recyclerView = (CustomEdgeRecyclerView) dialogLayout.findViewById(R.id.list);
                recyclerView.setLayoutManager(new LinearLayoutManager(holder.context));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(new TagsAdapter(shot.tags));

                recyclerView.postDelayed(() -> recyclerView.setEdgeColor(swatch.rgb), 500);
                new AlertDialog.Builder(holder.context, R.style.Drizzle_Widget_Dialog).setView(dialogLayout).show();
            });
        }

        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

            @InjectView(R.id.shot_preview) ShotPreviewGifImageView preview;
            @InjectView(R.id.gif_loader) ProgressBar gifLoader;
            @InjectView(R.id.shot_summary) ShotSummary summary;
            @InjectView(R.id.shot_rebound_of) ShotReboundOf reboundOf;

            @InjectView(R.id.rebounds) View reboundsContainer;
            @InjectView(R.id.rebounds_header) TextView reboundsHeader;
            @InjectView(R.id.rebounds_recycler) CustomEdgeRecyclerView reboundsRecycler;

            @InjectView(R.id.attachments) View attachmentsContainer;
            @InjectView(R.id.attachments_header) TextView attachmentsHeader;
            @InjectView(R.id.attachments_recycler) CustomEdgeRecyclerView attachmentsRecycler;

            @InjectView(R.id.shot_description) TextView description;
            @InjectView(R.id.subheader_comments) TextView commentsSubheader;

            @InjectView(R.id.shot_likes) TextView likes;
            @InjectView(R.id.shot_buckets) TextView buckets;
            @InjectView(R.id.shot_views) TextView views;
            @InjectView(R.id.shot_projects) TextView tags;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);

                reboundsRecycler.setHasFixedSize(true);
                reboundsRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

                attachmentsRecycler.setHasFixedSize(true);
                attachmentsRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            }

        }

    }

    public static class CommentItem extends MultiTypeStyleableItem<CommentItem.ViewHolder> {

        private final Comment comment;

        public CommentItem(Comment comment) {
            this.comment = comment;
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, int position) {
            if (position % 2 == 0) {
                holder.itemView.setBackgroundResource(R.color.bg_light);
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE);
            }

            View.OnClickListener authorClickListener = (v) -> {
                Intent intent = new Intent(holder.context, UserActivity.class);
                intent.putExtra(UserActivity.EXTRA_USER_DATA, new Gson().toJson(comment.user));
                holder.context.startActivity(intent);
            };

            holder.avatar.setOnClickListener(authorClickListener);
            holder.author.setOnClickListener(authorClickListener);

            Picasso.with(holder.context)
                    .load(comment.user.avatarUrl)
                    .transform(new CircularTransformation())
                    .placeholder(R.drawable.round_placeholder)
                    .into(holder.avatar);

            LocaleManager.set(Locale.US);
            CharSequence time = DateUtils.getRelativeTimeSpanString(comment.createdAt.getTime());
            String likes = "";
            LocaleManager.reset();

            if (comment.likesCount > 0) {
                CountableInterpolator countableInterpolator = new CountableInterpolator(holder.context);
                likes = ", " + countableInterpolator.apply(comment.likesCount, R.string.stats_likes, R.string.stats_like);
            }

            holder.author.setText(comment.user.name);
            holder.body.setText(Html.fromHtml(comment.body));
            holder.body.setMovementMethod(LinkMovementMethod.getInstance());
            holder.footer.setText(time + likes);
        }

        @Override
        public int getId(int position) {
            return comment.id;
        }

        @Override
        protected void onSetStyle(ViewHolder holder, UberSwatch swatch) {
            holder.body.setLinkTextColor(swatch.rgb);
        }

        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

            @InjectView(R.id.avatar) ImageView avatar;
            @InjectView(R.id.title) TextView author;
            @InjectView(R.id.description) TextView body;
            @InjectView(R.id.footer) TextView footer;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }

        }

    }

}
