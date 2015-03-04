package com.aluxian.drizzle.adapters.multi.adapters;

import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeSelectableItem;
import com.aluxian.drizzle.utils.Log;

/**
 * Implementation of {@link com.aluxian.drizzle.adapters.multi.adapters.MultiTypeStyleableAdapter} which supports single item selection.
 */
public abstract class MultiTypeSelectableAdapter extends MultiTypeStyleableAdapter {

    /** The position of the selected item. */
    private int mSelectedPosition = -1;

    /**
     * Mark the item on the given position as selected.
     *
     * @param position The position of the item to mark as selected.
     */
    public void selectItem(int position) {
        Log.d("selecting item " + position);

        int previousSelectedPosition = mSelectedPosition;
        mSelectedPosition = position;

        if (previousSelectedPosition > -1) {
            notifyItemChanged(previousSelectedPosition);
        }

        if (mSelectedPosition > -1) {
            notifyItemChanged(position);
        }
    }

    @Override
    public void onBindViewHolder(MultiTypeBaseItem.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder> item = itemsList().get(position);

        // Set the selection state
        if (item instanceof MultiTypeSelectableItem) {
            MultiTypeSelectableItem selectableItem = (MultiTypeSelectableItem) item;
            selectableItem.selectionStateChanged(holder, position == mSelectedPosition);
            Log.d("changing selection state on " + position + " to " + String.valueOf(position == mSelectedPosition));
        }
    }

}
