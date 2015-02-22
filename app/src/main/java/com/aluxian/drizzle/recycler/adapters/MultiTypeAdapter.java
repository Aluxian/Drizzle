package com.aluxian.drizzle.recycler.adapters;

import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.aluxian.drizzle.recycler.adapters.MultiTypeBaseItem.ViewHolder;

/**
 * Custom implementation of RecyclerView.Adapter which supports different item view types.
 */
public abstract class MultiTypeAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<MultiTypeItemType<? extends ViewHolder>> mItemTypes;
    private List<MultiTypeBaseItem<? extends ViewHolder>> mItems;
    private Palette.Swatch mSwatch;

    public MultiTypeAdapter() {
        mItemTypes = getItemTypes();
        mItems = new ArrayList<>();
    }

    /**
     * Set the colour palette that the items should use.
     *
     * @param swatch The Swatch of colours to use.
     */
    public void setColors(Palette.Swatch swatch) {
        mSwatch = swatch;
    }

    /**
     * @return A list with the ItemTypes supported by this adapter.
     */
    protected abstract List<MultiTypeItemType<? extends ViewHolder>> getItemTypes();

    /**
     * @return The list that holds this adapter's items.
     */
    protected List<MultiTypeBaseItem<? extends ViewHolder>> itemsList() {
        return mItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            // Use reflection to instantiate the corresponding ViewHolder
            View view = LayoutInflater.from(parent.getContext()).inflate(mItemTypes.get(viewType).layoutId, parent, false);
            Constructor<? extends ViewHolder> constructor = mItemTypes.get(viewType).viewHolderClass.getConstructor(View.class);
            ViewHolder viewHolder = constructor.newInstance(view);

            if (mSwatch != null && viewHolder instanceof MultiTypeStyleable) {
                ((MultiTypeStyleable) viewHolder).setColors(mSwatch);
            }

            return viewHolder;
        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid item type supplied", e);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Call the item-specific bind method
        mItems.get(position).bindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId(position);
    }

    @Override
    public int getItemViewType(int position) {
        Class itemClass = mItems.get(position).getClass();

        for (int i = 0; i < mItemTypes.size(); i++) {
            if (itemClass.equals(mItemTypes.get(i).itemClass)) {
                return i;
            }
        }

        throw new IllegalArgumentException("Item not supported by this adapter");
    }

}
