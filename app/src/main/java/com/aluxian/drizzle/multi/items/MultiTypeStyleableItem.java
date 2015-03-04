package com.aluxian.drizzle.multi.items;

import com.aluxian.drizzle.utils.UberSwatch;

/**
 * Base class for items in {@link com.aluxian.drizzle.multi.adapters.MultiTypeStyleableAdapter}.
 *
 * @param <VH> The type of the item's ViewHolder.
 */
public abstract class MultiTypeStyleableItem<VH extends MultiTypeBaseItem.ViewHolder> extends MultiTypeBaseItem<VH> {

    /** The colours currently applied to this item. */
    private UberSwatch mSwatch;

    /**
     * Called when the adapter style changes. The item should apply the new colours to its views.
     * This method is used by {@link com.aluxian.drizzle.multi.adapters.MultiTypeStyleableAdapter} to change the palette.
     *
     * @param holder The ViewHolder to bind.
     * @param swatch The colours to apply.
     */
    @SuppressWarnings("unchecked")
    public final void setStyle(ViewHolder holder, UberSwatch swatch) {
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
