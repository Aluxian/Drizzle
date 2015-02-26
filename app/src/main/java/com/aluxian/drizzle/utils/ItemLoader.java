package com.aluxian.drizzle.utils;

import android.os.AsyncTask;

import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.providers.ItemsProvider;

import java.io.IOException;
import java.util.List;

public class ItemLoader<T> {

    private int mItemsThreshold;
    private boolean mPendingReload;
    private ItemsProvider<T> mItemsProvider;
    private Listener<T> mListener;
    private LoadTask mLoadTask;

    public ItemLoader(ItemsProvider<T> itemsProvider, Listener<T> listener, int itemsThreshold) {
        mItemsThreshold = itemsThreshold;
        mItemsProvider = itemsProvider;
        mListener = listener;
    }

    /**
     * Do the initial load of items.
     */
    public void loadFirstItems() {
        mLoadTask = new LoadTask();
        mLoadTask.execute();
    }

    /**
     * Notify the loader that the item at the given position has just been bound.
     *
     * @param position  The position of the item.
     * @param itemCount The total number of items in the adapter.
     */
    public void notifyBoundItemAt(int position, int itemCount) {
        if (itemCount - position <= mItemsThreshold && (mLoadTask == null || mLoadTask.getStatus() == AsyncTask.Status.FINISHED)) {
            mLoadTask = new LoadTask();
            mLoadTask.execute();
        }
    }

    /**
     * Called when items need to be reloaded.
     */
    public void reload() {
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
        }

        mPendingReload = true;
        mLoadTask = new LoadTask();
        mLoadTask.execute();
    }

    private class LoadTask extends AsyncTask<Void, Void, List<T>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mListener.onStartedLoading();
        }

        @Override
        protected List<T> doInBackground(Void... params) {
            try {
                if (mPendingReload) {
                    mPendingReload = false;
                    return mItemsProvider.refresh();
                } else {
                    return mItemsProvider.load();
                }
            } catch (IOException | BadRequestException | TooManyRequestsException e) {
                mListener.onItemsLoadError(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<T> response) {
            mListener.onFinishedLoading(response != null);

            if (response != null) {
                mListener.onItemsLoaded(response);
            }
        }

    }

    /**
     * Listen for loading status and result. Used by adapters.
     *
     * @param <T> The type of the items.
     */
    public static interface Listener<T> {

        /**
         * Called when more items are loaded.
         *
         * @param items The list of items that have just been loaded.
         */
        void onItemsLoaded(List<T> items);

        /**
         * Called when the loader starts downloading items.
         */
        void onStartedLoading();

        /**
         * Called when there's an error loading the items.
         *
         * @param e The error's Exception.
         */
        void onItemsLoadError(Exception e);

        /**
         * Called when the loader finishes downloading items.
         *
         * @param successful Whether the loading was successful (i.e. without errors).
         */
        void onFinishedLoading(boolean successful);

    }

}
