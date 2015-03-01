package com.aluxian.drizzle.adapters.multi;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.recycler.ItemLoader;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.UberSwatch;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A MultiTypeAdapter that uses an ItemsProvider to load items infinitely.
 *
 * @param <T> The type of data items that the ItemsProvider provides.
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
        addItemType(new MultiTypeItemType<>(LoadingItem.class, LoadingItem.ViewHolder.class, R.layout.item_loading));
        //addItemType(new MultiTypeItemType<>(SpacingItem.class, SpacingItem.ViewHolder.class, R.layout.item_spacing));
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
     * Converts the given list of data items into a list of MultiTypeBaseItem objects.
     *
     * @param items The list of items to map.
     * @return A list of MultiTypeBaseItem objects.
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

    public static class LoadingItem extends MultiTypeStyleableItem<LoadingItem.ViewHolder> {

        @Override
        protected void onSetStyle(ViewHolder holder, UberSwatch swatch) {
            holder.progressBar.getIndeterminateDrawable().setTint(swatch.rgb);
        }

        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

            @InjectView(R.id.progress_bar) ProgressBar progressBar;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }

        }

    }

//    public static class SpacingItem extends MultiTypeBaseItem<SpacingItem.ViewHolder> {
//
//        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//            }
//
//        }
//
//    }

    /**
     * Listen for loading status and errors. Used by a RecyclerView's host.
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
