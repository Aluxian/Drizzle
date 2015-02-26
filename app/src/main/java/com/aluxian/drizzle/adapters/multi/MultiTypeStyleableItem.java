package com.aluxian.drizzle.adapters.multi;

import android.support.v7.graphics.Palette;

/**
 * Base class implementation for items that care about style.
 *
 * @param <VH> The type of the item's ViewHolder.
 */
public abstract class MultiTypeStyleableItem<VH extends MultiTypeBaseItem.ViewHolder> extends MultiTypeBaseItem<VH> {

    /** The color Swatch currently applied to this item. */
    private Palette.Swatch mSwatch;

    /**
     * Called when the adapter style changes. The item should apply the new colours to its views.
     * This method is used by MultiTypeAdapter to change the palette.
     *
     * @param holder The ViewHolder to bind.
     * @param swatch The new Swatch of colors.
     */
    @SuppressWarnings("unchecked")
    public void setColors(ViewHolder holder, Palette.Swatch swatch) {
        mSwatch = swatch;
        onSetColors((VH) holder, swatch);
    }

    /**
     * Called when the adapter style changes. The item should apply the new colours to its views.
     * This method is implemented by subclassing items to do the actual changes.
     *
     * @param holder The ViewHolder to bind.
     * @param swatch The new Swatch of colors.
     */
    protected abstract void onSetColors(VH holder, Palette.Swatch swatch);

    /**
     * @return The color Swatch currently applied to this item.
     */
    public Palette.Swatch getSwatch() {
        return mSwatch;
    }

}
