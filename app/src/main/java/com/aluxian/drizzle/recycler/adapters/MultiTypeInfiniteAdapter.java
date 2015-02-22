package com.aluxian.drizzle.recycler.adapters;

import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ProgressBar;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.ItemLoader;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A MultiTypeAdapter that uses an ItemsProvider to load items infinitely.
 *
 * @param <T> The type of data items that the ItemsProvider provides.
 */
public abstract class MultiTypeInfiniteAdapter<T> extends MultiTypeAdapter implements ItemLoader.Listener<T> {

    private ItemLoader<T> mItemLoader;
    private LoadingItem.ViewHolder mLoadingItemViewHolder;

    public MultiTypeInfiniteAdapter(ItemsProvider<T> itemsProvider) {
        super();

        itemsList().add(new LoadingItem());
        notifyItemInserted(0);

        mItemLoader = new ItemLoader<>(itemsProvider, this, Config.LOAD_ITEMS_THRESHOLD);
        mItemLoader.firstLoad();
    }

    @Override
    protected List<MultiTypeItemType<? extends MultiTypeBaseItem.ViewHolder>> getItemTypes() {
        return Arrays.asList(new MultiTypeItemType<>(LoadingItem.class, LoadingItem.ViewHolder.class, R.layout.item_loading));
    }

    @Override
    public void onBindViewHolder(MultiTypeBaseItem.ViewHolder holder, int position) {
        if (holder instanceof LoadingItem.ViewHolder) {
            mLoadingItemViewHolder = (LoadingItem.ViewHolder) holder;
        }

        super.onBindViewHolder(holder, position);
        mItemLoader.notifyBoundItemAt(position, getItemCount());
    }

    @Override
    public void onItemsLoaded(List<T> items) {
        int startPos = itemsList().size() - 1;
        itemsList().addAll(startPos, mapItems(items));
        notifyItemRangeInserted(startPos, items.size());
    }

    /**
     * Converts the given list of data items into a list of MultiTypeBaseItem objects.
     *
     * @param items The list of items to map.
     * @return A list of MultiTypeBaseItem objects.
     */
    protected abstract List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapItems(List<T> items);

    @Override
    public void onStartedLoading() {
        if (mLoadingItemViewHolder != null) {
            mLoadingItemViewHolder.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFinishedLoading() {
        if (mLoadingItemViewHolder != null) {
            mLoadingItemViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    public static class LoadingItem extends MultiTypeBaseItem<LoadingItem.ViewHolder> {

        @Override
        protected void onBindViewHolder(ViewHolder holder) {}

        @Override
        public int getId(int position) {
            return 0;
        }

        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder implements MultiTypeStyleable {

            @InjectView(R.id.progress_bar) ProgressBar progressBar;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }

            @Override
            public void setColors(Palette.Swatch swatch) {
                progressBar.getIndeterminateDrawable().setTint(swatch.getRgb());
            }

        }

    }

}
