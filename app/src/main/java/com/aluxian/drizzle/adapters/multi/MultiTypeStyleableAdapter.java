package com.aluxian.drizzle.adapters.multi;

import com.aluxian.drizzle.utils.UberSwatch;

/**
 * A MultiTypeAdapter that supports styleable items.
 */
public abstract class MultiTypeStyleableAdapter extends MultiTypeAdapter {

    /** The currently set colors. */
    protected UberSwatch mSwatch;

    /**
     * Set the colour palette that the items should use.
     *
     * @param swatch The colour palette to use.
     */
    public void setColors(UberSwatch swatch) {
        mSwatch = swatch;

        // Update existing items
        for (int i = 0; i < itemsList().size(); i++) {
            MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder> item = itemsList().get(i);

            if (item instanceof MultiTypeStyleableItem && !(item instanceof MultiTypeHeader)) {
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onBindViewHolder(MultiTypeBaseItem.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder> item = itemsList().get(position);

        // Set the colour palette
        if (mSwatch != null && item instanceof MultiTypeStyleableItem) {
            MultiTypeStyleableItem styleableItem = (MultiTypeStyleableItem) item;

            if (styleableItem.getSwatch() == null || styleableItem.getSwatch() != mSwatch) {
                styleableItem.setColors(holder, mSwatch);
            }
        }
    }

}
