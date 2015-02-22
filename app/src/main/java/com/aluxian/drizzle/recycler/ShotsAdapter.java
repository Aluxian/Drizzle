package com.aluxian.drizzle.recycler;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.ShotActivity;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Log;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeRefreshLayout.OnRefreshListener {

    /** A list with all the displayed items. */
    protected List<Shot> mShotsList = new ArrayList<>();

    /** Whether the adapter is currently loading items. */
    protected boolean mIsLoadingItems;

    /** A ShotsProvider instance used to load the appropriate shots for this adapter's fragment. */
    protected ItemsProvider<Shot> mItemsProvider;

    /** A callbacks instance (the adapter's fragment). */
    protected AdapterListener mAdapterListener;

    public ShotsAdapter(AdapterListener adapterListener, ItemsProvider<Shot> itemsProvider) {
        mAdapterListener = adapterListener;
        mItemsProvider = itemsProvider;

        // Load the first items
        new LoadItemsIfRequiredTask(0).execute();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot, parent, false));
    }

    protected int positionOffset() {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            Shot shot = mShotsList.get(position + positionOffset());
            ItemViewHolder holder = (ItemViewHolder) viewHolder;

            //holder.viewsCount.setText(String.valueOf(shot.viewsCount));
            holder.viewsCount.setText(String.valueOf(shot.viewsCount));
            holder.likesCount.setText(String.valueOf(shot.likesCount));

            holder.image.setOnClickListener(view -> onShotClick(holder.image.getContext(), shot));

            Picasso.with(holder.image.getContext())
                    .load(shot.images.normal)
                    .placeholder(R.color.slate)
                    .into(holder.image);

            // Adapt margin size
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            if (position % 2 == positionOffset() + 1) {
                params.setMarginStart(Dp.toPx(4));
                params.setMarginEnd(Dp.toPx(8));
            } else {
                params.setMarginStart(Dp.toPx(8));
                params.setMarginEnd(Dp.toPx(4));
            }
            holder.itemView.requestLayout();

            holder.gifBadge.setVisibility(shot.images.normal.endsWith(".gif") ? View.VISIBLE : View.INVISIBLE);

            // If position is near the end of the list, load more items from the API
            new LoadItemsIfRequiredTask(position).execute();
        }
    }

    protected void onShotClick(Context context, Shot shot) {
        Intent intent = new Intent(context, ShotActivity.class);
        intent.putExtra(ShotActivity.EXTRA_SHOT_DATA, new Gson().toJson(shot));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mShotsList.size();
    }

    @Override
    public void onRefresh() {
        new RefreshItemsTask().execute();
    }

    public ItemsProvider<Shot> getShotsProvider() {
        return mItemsProvider;
    }

    protected class LoadItemsIfRequiredTask extends AsyncTask<Void, Void, List<Shot>> {

        /** The position of the item that executed the task. */
        private final int mPosition;

        protected LoadItemsIfRequiredTask(int position) {
            mPosition = position;
        }

        @Override
        protected void onPreExecute() {
            if (!mIsLoadingItems && mShotsList.size() - mPosition <= Config.LOAD_ITEMS_THRESHOLD) {
                mIsLoadingItems = true;
            } else {
                cancel(true);
                //preloadImages(mPosition + 1);
            }
        }

        @Override
        protected List<Shot> doInBackground(Void... params) {
            try {
                return mItemsProvider.load();
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
                mAdapterListener.onAdapterLoadingError(mShotsList.size() > 0);
            }

            mIsLoadingItems = false;
            new Handler().postDelayed(() -> mAdapterListener.onAdapterLoadingFinished(response != null), 500);
            //preloadImages(mPosition + 1);
        }

    }

    /**
     * Start downloading the images of the following items.
     *
     * @param startPosition The position of the first item to be preloaded.
     */
    private void preloadImages(int startPosition) {
        for (int position = startPosition; position < startPosition + 6 && position < mShotsList.size(); position++) {
            //Picasso.with(mContext).load(mShotsList.get(position).images.normal).fetch();
        }
    }

    private class RefreshItemsTask extends AsyncTask<Void, Void, List<Shot>> {

        @Override
        protected void onPreExecute() {
            if (!mIsLoadingItems) {
                Log.d("Refreshing items");
                mIsLoadingItems = true;
            } else {
                cancel(false);
                mAdapterListener.onAdapterLoadingFinished(false);
            }
        }

        @Override
        protected List<Shot> doInBackground(Void... params) {
            try {
                return mItemsProvider.refresh();
            } catch (IOException | BadRequestException | TooManyRequestsException e) {
                Log.d(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Shot> response) {
            if (response != null) {
                mShotsList.clear();
                mShotsList.addAll(response);
                notifyDataSetChanged();
            } else {
                mAdapterListener.onAdapterLoadingError(mShotsList.size() > 0);
            }

            mIsLoadingItems = false;
            new Handler().postDelayed(() -> mAdapterListener.onAdapterLoadingFinished(response != null), 500);
        }

    }

    /**
     * A ViewHolder used by the items of this adapter.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.cover_image) ImageView image;
        @InjectView(R.id.gif_badge) ImageView gifBadge;

        @InjectView(R.id.views_count) TextView viewsCount;
        @InjectView(R.id.likes_count) TextView likesCount;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

    /**
     * Callbacks interface that users of this adapter must implement.
     */
    public static interface AdapterListener {

        /**
         * Called when the adapter finishes refreshing or loading items.
         *
         * @param successful Whether the adapter was successful and items were added to the list.
         */
        public void onAdapterLoadingFinished(boolean successful);

        /**
         * Called when there's an error while loading results.
         *
         * @param hasItems Whether the adapter has at least 1 item.
         */
        public void onAdapterLoadingError(boolean hasItems);

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