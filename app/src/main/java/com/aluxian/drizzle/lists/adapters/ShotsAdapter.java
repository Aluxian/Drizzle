package com.aluxian.drizzle.lists.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.ShotsProvider;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShotsAdapter extends RecyclerView.Adapter<ShotsAdapter.ViewHolder> implements SwipeRefreshLayout.OnRefreshListener {

    private List<Shot> mShotsList = new ArrayList<>();
    private boolean mIsLoadingItems;

    private ShotsProvider mShotsProvider;
    private Context mContext;
    private Callbacks mCallbacks;

    public ShotsAdapter(Context context, Callbacks callbacks, ShotsProvider shotsProvider) {
        mContext = context;
        mCallbacks = callbacks;
        mShotsProvider = shotsProvider;

        // Load first mItems
        loadItemsIfRequired(0);
    }

    @Override
    public ShotsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot, parent, false));
    }

    public ShotsProvider getShotsProvider() {
        return mShotsProvider;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Shot shot = mShotsList.get(position);

        Resources resources = mContext.getResources();
        int iconColor = resources.getColor(R.color.card_actions);

        //holder.viewsIcon.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
        holder.commentsIcon.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
        holder.likesIcon.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);

        //holder.viewsCount.setText(String.valueOf(shot.viewsCount));
        holder.commentsCount.setText(String.valueOf(shot.viewsCount));
        holder.likesCount.setText(String.valueOf(shot.likesCount));

        /*holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ShotActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, holder.image, "robot");
                activity.startActivity(intent, options.toBundle());
            }
        });*/

        if (!shot.images.normal.equals(holder.image.getTag())) {
            holder.image.setTag(shot.images.normal);
            Picasso.with(holder.image.getContext())
                    .load(shot.images.normal)
                    .placeholder(R.drawable.bg_placeholder)
                    .into(holder.image);
        }

        // If position is near the end of the list, load more items from the API
        loadItemsIfRequired(position);

        preloadViews(position + 1);
    }

    @Override
    public int getItemCount() {
        return mShotsList.size();
    }

    private void loadItemsIfRequired(int position) {
        if (!mIsLoadingItems && mShotsList.size() - position <= Config.LOAD_ITEMS_THRESHOLD) {
            mIsLoadingItems = true;

            new AsyncTask<Void, Void, List<Shot>>() {

                @Override
                protected List<Shot> doInBackground(Void... params) {
                    try {
                        return mShotsProvider.load();
                    } catch (IOException | BadRequestException | TooManyRequestsException e) {
                        Log.d(e);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(List<Shot> response) {
                    if (response != null) {
                        mShotsList.addAll(response);
                        notifyItemRangeInserted(mShotsList.size() - response.size(), response.size());
                    } else {
                        mCallbacks.onAdapterLoadingError(mShotsList.size() > 0);
                    }

                    mIsLoadingItems = false;
                    new Handler().postDelayed(() -> mCallbacks.onAdapterLoadingFinished(response != null), 500);

                    //preloadViews(position + 1);
                }

            }.execute();
        } else {
            //preloadViews(position + 1);
        }
    }

    /**
     * Start loading the images of the following items.
     *
     * @param startPosition The position of the first item to be preloaded.
     */
    private void preloadViews(int startPosition) {
        for (int position = startPosition; position < startPosition + 6 && position < mShotsList.size(); position++) {
            //ViewHolder holder = (ViewHolder) mRecyclerView.findViewHolderForPosition(position);
            //if (holder != null) {
            //    onBindViewHolder(holder, position);
            //} else {
            Picasso.with(mContext).load(mShotsList.get(position).images.normal);
            //}
        }
    }

    @Override
    public void onRefresh() {
        if (!mIsLoadingItems) {
            Log.d("refreshing items");
            mIsLoadingItems = true;

            new AsyncTask<Void, Void, List<Shot>>() {

                @Override
                protected List<Shot> doInBackground(Void... params) {
                    try {
                        return mShotsProvider.refresh();
                    } catch (IOException | BadRequestException | TooManyRequestsException e) {
                        Log.d(e);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(List<Shot> response) {
                    if (response != null) {
                        mShotsList = response;
                        notifyDataSetChanged();
                    } else {
                        mCallbacks.onAdapterLoadingError(mShotsList.size() > 0);
                    }

                    mIsLoadingItems = false;
                    new Handler().postDelayed(() -> mCallbacks.onAdapterLoadingFinished(response != null), 500);

                    Log.d("now there are " + mShotsList.size() + " items");
                }

            }.execute();
        } else {
            mCallbacks.onAdapterLoadingFinished(false);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final CardView card;
        public final ImageView image;

        //public final ImageView viewsIcon;
        public final ImageView commentsIcon;
        public final ImageView likesIcon;

        //public final TextView viewsCount;
        public final TextView commentsCount;
        public final TextView likesCount;

        public ViewHolder(View itemView) {
            super(itemView);

            card = (CardView) itemView;
            image = (ImageView) itemView.findViewById(R.id.image);

            //viewsIcon = (ImageView) v.findViewById(R.id.views_icon);
            commentsIcon = (ImageView) itemView.findViewById(R.id.comments_icon);
            likesIcon = (ImageView) itemView.findViewById(R.id.likes_icon);

            //viewsCount = (TextView) v.findViewById(R.id.views_count);
            commentsCount = (TextView) itemView.findViewById(R.id.comments_count);
            likesCount = (TextView) itemView.findViewById(R.id.likes_count);
        }

    }

    /**
     * Callbacks interface that users of this adapter must implement.
     */
    public static interface Callbacks {

        /**
         * Called when the adapter finishes refreshing or loading items.
         *
         * @param successful Whether the adapter was successful and items were added to the list.
         */
        void onAdapterLoadingFinished(boolean successful);

        /**
         * Called when there's an error while loading results.
         *
         * @param hasItems Whether the adapter has at least 1 item.
         */
        void onAdapterLoadingError(boolean hasItems);

    }

}

/*

                    // Remove
                    for (int i = 0; i < mShotsList.size(); i++) {
                        if (!newList.contains(mShotsList.get(i))) {
                            mShotsList.remove(i);
                            notifyItemRemoved(i);
                        }
                    }

                    int i = 0, j = 0;

                    while (i < oldList.size() && j < mShotsList.size()) {
                        if (oldList.get(i).equals(mShotsList.get(j))) {
                            i++;
                            j++;
                        } else {
                            notifyItemInserted(j);
                            j++;
                        }
                    }

                    while (i < oldList.size()) {
                        notifyItemRemoved(i);
                        i++;
                    }

                    while (j < mShotsList.size()) {
                        notifyItemInserted(j);
                        j++;
                    }*/