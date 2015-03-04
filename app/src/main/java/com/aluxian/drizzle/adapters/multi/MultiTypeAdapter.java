package com.aluxian.drizzle.adapters.multi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom implementation of RecyclerView.Adapter which supports different item view types.
 */
public abstract class MultiTypeAdapter extends RecyclerView.Adapter<MultiTypeBaseItem.ViewHolder> {

    /** A list that holds the {@link com.aluxian.drizzle.adapters.multi.MultiTypeItemType}s supported by the adapter. */
    private List<MultiTypeItemType<? extends MultiTypeBaseItem.ViewHolder>> mItemTypes = new ArrayList<>();

    /** A list that holds the adapter's items. */
    private List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mItems = new ArrayList<>();

    /**
     * Load the item types.
     */
    protected MultiTypeAdapter() {
        onAddItemTypes();
    }

    /**
     * Adds an ItemType to the list with the ItemTypes supported by this adapter.
     *
     * @param itemType An ItemType to add.
     */
    protected void addItemType(MultiTypeItemType<? extends MultiTypeBaseItem.ViewHolder> itemType) {
        mItemTypes.add(itemType);
    }

    /**
     * Subclasses need to add the supported ItemTypes inside this method.
     */
    protected abstract void onAddItemTypes();

    /**
     * @return The list that holds this adapter's items.
     */
    public List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> itemsList() {
        return mItems;
    }

    @Override
    public MultiTypeBaseItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            // Use reflection to instantiate the corresponding ViewHolder
            View view = LayoutInflater.from(parent.getContext()).inflate(mItemTypes.get(viewType).layoutId, parent, false);
            return mItemTypes.get(viewType).viewHolderClass.getConstructor(View.class).newInstance(view);
        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid item type supplied", e);
        }
    }

    @Override
    public void onBindViewHolder(MultiTypeBaseItem.ViewHolder holder, int position) {
        // Call the item-specific bind method
        mItems.get(position).bindViewHolder(holder, position);
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

        throw new IllegalArgumentException("Item not supported by this adapter " + getClass().getSimpleName());
    }

}
