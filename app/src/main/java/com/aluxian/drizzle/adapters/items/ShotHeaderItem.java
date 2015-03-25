package com.aluxian.drizzle.adapters.items;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.AttachmentsAdapter;
import com.aluxian.drizzle.adapters.BucketsAdapter;
import com.aluxian.drizzle.adapters.LikesAdapter;
import com.aluxian.drizzle.adapters.ReboundsAdapter;
import com.aluxian.drizzle.adapters.TagsAdapter;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Like;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.AttachmentsProvider;
import com.aluxian.drizzle.api.providers.BucketsProvider;
import com.aluxian.drizzle.api.providers.LikesProvider;
import com.aluxian.drizzle.api.providers.ReboundsProvider;
import com.aluxian.drizzle.multi.MultiTypeItemType;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.multi.items.MultiTypeStyleableItem;
import com.aluxian.drizzle.multi.traits.MultiTypeHeader;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.utils.UserManager;
import com.aluxian.drizzle.utils.transformations.PaletteTransformation;
import com.aluxian.drizzle.views.CustomEdgeRecyclerView;
import com.aluxian.drizzle.views.ShotPreviewGifImageView;
import com.aluxian.drizzle.views.widgets.ShotReboundOf;
import com.aluxian.drizzle.views.widgets.ShotSummary;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.droidsonroids.gif.GifDrawable;

import static com.aluxian.drizzle.multi.adapters.MultiTypeInfiniteAdapter.StatusListener;

public class ShotHeaderItem extends MultiTypeStyleableItem<ShotHeaderItem.ViewHolder> implements MultiTypeHeader {

    /** The {@link com.aluxian.drizzle.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(ShotHeaderItem.class,
            ViewHolder.class, R.layout.item_header_shot);

    private final Shot shot;
    private final Shot reboundOfShot;
    private StateListener headerListener;

    public ShotHeaderItem(Shot shot, Shot reboundOfShot, StateListener headerListener) {
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
        holder.buckets.setText(countableInterpolator.apply(shot.bucketsCount, R.string.stats_buckets,
                R.string.stats_bucket));
        holder.views.setText(countableInterpolator.apply(shot.viewsCount, R.string.stats_views, R.string.stats_view));
        holder.tags.setText(countableInterpolator.apply(shot.tags.size(), R.string.stats_tags, R.string.stats_tag));

        if (!TextUtils.isEmpty(shot.description)) {
            holder.description.setMovementMethod(LinkMovementMethod.getInstance());
            holder.description.setText(Html.fromHtml(shot.description));
        } else {
            holder.description.setVisibility(View.GONE);
        }

        String title = countableInterpolator.apply(shot.commentsCount, R.string.section_responses,
                R.string.section_response);
        holder.commentsSubheader.setText(title);

        if (shot.isGif()) {
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

        Picasso.with(holder.context)
                .load(shot.images.largest())
                .transform(PaletteTransformation.instance())
                .placeholder(R.color.slate)
                .into(holder.preview, new Callback() {
                    @Override
                    public void onSuccess() {
                        UberSwatch swatch = UberSwatch.from(PaletteTransformation.getPalette(holder.preview));
                        holder.preview.post(() -> headerListener.onHeaderLoaded(swatch, holder.preview.getHeight()));
                        onSetStyle(holder, swatch);
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
        String title = countableInterpolator.apply(shot.reboundsCount, R.string.section_rebounds,
                R.string.section_rebound);
        holder.reboundsHeader.setText(title);

        holder.reboundsRecycler.setAdapter(new ReboundsAdapter(shot, new ReboundsProvider(shot.id),
                new StatusListener() {
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
        String title = countableInterpolator.apply(shot.attachmentsCount, R.string.section_attachments,
                R.string.section_attachment);
        holder.attachmentsHeader.setText(title);

        holder.attachmentsRecycler.setAdapter(new AttachmentsAdapter(new AttachmentsProvider(shot.id),
                new StatusListener() {
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

        holder.reboundsRecycler.post(() -> holder.reboundsRecycler.setEdgeColor(swatch.rgb));
        holder.attachmentsRecycler.post(() -> {
            Log.d("setting color", holder.attachmentsRecycler, swatch.rgb);
            holder.attachmentsRecycler.setEdgeColor(swatch.rgb);
        });

        holder.likes.setOnClickListener(v -> {
            LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(holder.context).inflate(R.layout
                    .dialog_list, null);
            dialogLayout.setBackgroundColor(swatch.rgb);

            TextView titleView = (TextView) dialogLayout.findViewById(R.id.title);
            titleView.setTextColor(swatch.titleTextColor);
            titleView.setText(R.string.dialog_title_likes);

            LikesAdapter likesAdapter = new LikesAdapter(new LikesProvider((int) holder.likes.getTag()),
                    new StatusListener() {
                @Override
                public void onAdapterLoadingFinished(boolean successful) {

                }

                @Override
                public void onAdapterLoadingError(Exception e, boolean hasItems) {

                }
            });

            likesAdapter.setStyle(swatch);
            likesAdapter.showLoadingIndicatorWhenEmpty(true);

            CustomEdgeRecyclerView recyclerView = (CustomEdgeRecyclerView) dialogLayout.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.context));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(likesAdapter);

            recyclerView.postDelayed(() -> recyclerView.setEdgeColor(swatch.rgb), 500);
            new AlertDialog.Builder(holder.context, R.style.Drizzle_Widget_Dialog).setView(dialogLayout).show();
        });

        holder.buckets.setOnClickListener(v -> {
            LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(holder.context).inflate(R.layout
                    .dialog_list, null);
            dialogLayout.setBackgroundColor(swatch.rgb);

            TextView titleView = (TextView) dialogLayout.findViewById(R.id.title);
            titleView.setTextColor(swatch.titleTextColor);
            titleView.setText(R.string.dialog_title_buckets);

            BucketsAdapter bucketsAdapter = new BucketsAdapter(new BucketsProvider((int) holder.likes.getTag()),
                    new StatusListener() {
                @Override
                public void onAdapterLoadingFinished(boolean successful) {

                }

                @Override
                public void onAdapterLoadingError(Exception e, boolean hasItems) {

                }
            });

            bucketsAdapter.setStyle(swatch);
            bucketsAdapter.showLoadingIndicatorWhenEmpty(true);

            CustomEdgeRecyclerView recyclerView = (CustomEdgeRecyclerView) dialogLayout.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.context));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(bucketsAdapter);

            recyclerView.postDelayed(() -> recyclerView.setEdgeColor(swatch.rgb), 500);
            new AlertDialog.Builder(holder.context, R.style.Drizzle_Widget_Dialog).setView(dialogLayout).show();
        });

        if (shot.tags.size() > 0) {
            holder.tags.setOnClickListener(v -> {
                LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(holder.context).inflate(R.layout
                        .dialog_list, null);
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

        if (UserManager.getInstance().isAuthenticated()) {
            Dribbble.userLikesShot(shot.id).execute(new ApiRequest.Callback<Like>() {
                @Override
                public void onSuccess(Dribbble.Response<Like> response) {
                    holder.likeButton.getDrawable().setTint(swatch.rgb);
                    holder.likeButton.setTag(1);
                }

                @Override
                public void onError(Exception e) {
                    int color = Color.parseColor("#999999");
                    holder.likeButton.getDrawable().setTint(color);
                    holder.likeButton.setTag(0);
                }
            });
        }

        holder.likeButton.setOnClickListener(v -> {
            if (((int) holder.likeButton.getTag()) == 0) {
                Dribbble.likeShot(shot.id).execute(new ApiRequest.Callback<Like>() {
                    @Override
                    public void onSuccess(Dribbble.Response<Like> response) {
                        holder.likeButton.getDrawable().setTint(swatch.rgb);
                        holder.likeButton.setTag(1);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(e);
                    }
                });
            } else {
                Dribbble.unlikeShot(shot.id).execute(new ApiRequest.Callback<Object>() {
                    @Override
                    public void onSuccess(Dribbble.Response response) {
                        int color = Color.parseColor("#999999");
                        holder.likeButton.getDrawable().setTint(color);
                        holder.likeButton.setTag(0);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(e);
                    }
                });
            }
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

        @InjectView(R.id.shot_like) ImageView likeButton;
        @InjectView(R.id.shot_bucket) ImageView bucketButton;

        @InjectView(R.id.shot_likes) TextView likes;
        @InjectView(R.id.shot_buckets) TextView buckets;
        @InjectView(R.id.shot_views) TextView views;
        @InjectView(R.id.shot_tags) TextView tags;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);

            reboundsRecycler.setHasFixedSize(true);
            reboundsRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            attachmentsRecycler.setHasFixedSize(true);
            attachmentsRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                    false));
        }

    }

}
