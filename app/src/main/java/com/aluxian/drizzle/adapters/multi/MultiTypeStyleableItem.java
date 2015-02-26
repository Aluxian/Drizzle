package com.aluxian.drizzle.adapters.multi;

import com.aluxian.drizzle.utils.UberSwatch;

/**
 * Base class implementation for items that care about style.
 *
 * @param <VH> The type of the item's ViewHolder.
 */
public abstract class MultiTypeStyleableItem<VH extends MultiTypeBaseItem.ViewHolder> extends MultiTypeBaseItem<VH> {

    /** The colours currently applied to this item. */
    private UberSwatch mSwatch;

    /**
     * Called when the adapter style changes. The item should apply the new colours to its views.
     * This method is used by MultiTypeAdapter to change the palette.
     *
     * @param holder The ViewHolder to bind.
     * @param swatch The colours to apply.
     */
    @SuppressWarnings("unchecked")
    public void setColors(ViewHolder holder, UberSwatch swatch) {
        mSwatch = swatch;
        onSetStyle((VH) holder, swatch);
    }

    /**
     * Called when the adapter style changes. The item should apply the new colours to its views.
     * This method is implemented by subclassing items to do the actual changes.
     *
     * @param holder The ViewHolder to bind.
     * @param swatch The colours to apply.
     */
    protected abstract void onSetStyle(VH holder, UberSwatch swatch);

    /**
     * @return The colours currently applied to this item.
     */
    public UberSwatch getSwatch() {
        return mSwatch;
    }

}
