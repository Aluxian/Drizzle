package com.aluxian.drizzle.recycler;

import android.os.AsyncTask;

import com.aluxian.drizzle.api.providers.ItemsProvider;

import java.util.List;

/**
 * Helper that facilitates the use of {@link com.aluxian.drizzle.api.providers.ItemsProvider}s in adapters.
 *
 * @param <T> The type of items provided by the {@link com.aluxian.drizzle.api.providers.ItemsProvider}.
 */
public class ItemLoader<T> {

    private int mItemsThreshold;
    private ItemsProvider<T> mItemsProvider;
    private Listener<T> mListener;
    private LoadItemsTask<T> mLoadItemsTask;

    public ItemLoader(ItemsProvider<T> itemsProvider, Listener<T> listener, int itemsThreshold) {
        mItemsProvider = itemsProvider;
        mItemsThreshold = itemsThreshold;
        mListener = listener;
    }

    /**
     * Do the initial load of items.
     */
    public void loadFirstItems() {
        mLoadItemsTask = new LoadItemsTask<>(mItemsProvider, mListener);
        mLoadItemsTask.execute();
    }

    /**
     * Notify the loader that the item at the given position has just been bound.
     *
     * @param position  The position of the item.
     * @param itemCount The total number of items in the adapter.
     */
    public void notifyBoundItemAt(int position, int itemCount) {
        if ((mLoadItemsTask == null || mLoadItemsTask.getStatus() == AsyncTask.Status.FINISHED)
                && (itemCount - position <= mItemsThreshold) && !mItemsProvider.hasFinished()) {
            mLoadItemsTask = new LoadItemsTask<>(mItemsProvider, mListener);
            mLoadItemsTask.execute();
        }
    }

    /**
     * Called when items need to be reloaded.
     */
    public void reload() {
        if (mLoadItemsTask != null) {
            mLoadItemsTask.cancel(true);
        }

        mLoadItemsTask = new LoadItemsTask<>(mItemsProvider, mListener);
        mLoadItemsTask.setPendingReload(true);
        mLoadItemsTask.execute();
    }

    /**
     * Listen for loading status and result.
     *
     * @param <T> The type of the items.
     */
    public interface Listener<T> {

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
