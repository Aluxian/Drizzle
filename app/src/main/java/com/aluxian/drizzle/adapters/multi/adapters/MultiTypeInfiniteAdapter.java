package com.aluxian.drizzle.adapters.multi.adapters;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewGroup;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.items.LoadingItem;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.recycler.ItemLoader;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;

import java.util.List;

/**
 * A {@link com.aluxian.drizzle.adapters.multi.MultiTypeBaseAdapter} that uses an
 * {@link com.aluxian.drizzle.api.providers.ItemsProvider} to load items indefinitely.
 *
 * @param <T> The type of data items that the {@link com.aluxian.drizzle.api.providers.ItemsProvider} provides.
 */
public abstract class MultiTypeInfiniteAdapter<T> extends MultiTypeStyleableAdapter
        implements ItemLoader.Listener<T>, SwipeRefreshLayout.OnRefreshListener {

    private int mLoadingItemPosition = -1;
    private boolean mShowLoadingIndicatorWhenEmpty;

    private ItemLoader<T> mItemLoader;
    private StatusListener mStatusListener;
    private boolean mPendingRefresh;

    public MultiTypeInfiniteAdapter(ItemsProvider<T> itemsProvider, StatusListener statusListener) {
        super();
        mStatusListener = statusListener;

        mItemLoader = new ItemLoader<>(itemsProvider, this, Config.LOAD_ITEMS_THRESHOLD);
        mItemLoader.loadFirstItems();
    }

    /**
     * @param show Whether the loading indicator should be shown when the list is empty.
     */
    public void showLoadingIndicatorWhenEmpty(boolean show) {
        mShowLoadingIndicatorWhenEmpty = show;
    }

    @Override
    protected void onAddItemTypes() {
        addItemType(LoadingItem.ITEM_TYPE);
    }

    @Override
    public MultiTypeBaseItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MultiTypeBaseItem.ViewHolder holder = super.onCreateViewHolder(parent, viewType);

        if (holder instanceof LoadingItem.ViewHolder) {
            int tint = parent.getContext().getResources().getColor(R.color.accent);
            ((LoadingItem.ViewHolder) holder).progressBar.getIndeterminateDrawable().setTint(tint);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(MultiTypeBaseItem.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        new Handler().post(() -> mItemLoader.notifyBoundItemAt(position, getItemCount()));
    }

    @Override
    public void onItemsLoaded(List<T> items) {
        if (mPendingRefresh) {
            mPendingRefresh = false;
            itemsList().clear();

            if (items.size() > 0) {
                itemsList().addAll(mapLoadedItems(items));
                notifyDataSetChanged();
            }
        } else if (items.size() > 0) {
            itemsList().addAll(mapLoadedItems(items));
            notifyItemRangeInserted(itemsList().size() - items.size(), items.size());
        }
    }

    @Override
    public void onRefresh() {
        mPendingRefresh = true;
        mItemLoader.reload();
    }

    /**
     * Converts the given list of data items into a list of {@link com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem} objects.
     *
     * @param items The list of items to map.
     * @return A list of {@link com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem} objects.
     */
    protected abstract List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<T> items);

    @Override
    public void onStartedLoading() {
        if (mShowLoadingIndicatorWhenEmpty || itemsList().size() > 0) {
            mLoadingItemPosition = itemsList().size();
            itemsList().add(new LoadingItem());
            notifyItemInserted(mLoadingItemPosition);
        }
    }

    @Override
    public void onItemsLoadError(Exception e) {
        mStatusListener.onAdapterLoadingError(e, itemsList().size() > 1);
        Log.e(e);
    }

    @Override
    public void onFinishedLoading(boolean successful) {
        if (mLoadingItemPosition > -1) {
            itemsList().remove(mLoadingItemPosition);
            notifyItemRemoved(mLoadingItemPosition);
            mLoadingItemPosition = -1;
        }

        mStatusListener.onAdapterLoadingFinished(successful);
    }

    /**
     * Listen for loading status and errors. Used by a {@code RecyclerView}'s host.
     */
    public static interface StatusListener {

        /**
         * Called when the adapter finishes refreshing or loading items.
         *
         * @param successful Whether the adapter was successful and items were added to the list.
         */
        void onAdapterLoadingFinished(boolean successful);

        /**
         * Called when there's an error while loading results.
         *
         * @param e        The error's Exception.
         * @param hasItems Whether the adapter has at least 1 (data) item.
         */
        void onAdapterLoadingError(Exception e, boolean hasItems);

    }

}
