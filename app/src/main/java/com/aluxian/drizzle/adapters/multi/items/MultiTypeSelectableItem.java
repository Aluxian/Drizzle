package com.aluxian.drizzle.adapters.multi.items;

/**
 * Base class for items in {@link com.aluxian.drizzle.adapters.multi.adapters.MultiTypeSelectableAdapter}.
 *
 * @param <VH> The type of the item's ViewHolder.
 */
public abstract class MultiTypeSelectableItem<VH extends MultiTypeBaseItem.ViewHolder> extends MultiTypeStyleableItem<VH> {

    /**
     * Called when the item's selection state changes. This method is used by
     * {@link com.aluxian.drizzle.adapters.multi.adapters.MultiTypeSelectableAdapter} to change the palette.
     *
     * @param holder   The ViewHolder to bind.
     * @param selected Whether the item is selected.
     */
    @SuppressWarnings("unchecked")
    public final void selectionStateChanged(ViewHolder holder, boolean selected) {
        onSelectionStateChanged((VH) holder, selected);
    }

    /**
     * Called when the item's selection state changes. This method is implemented by subclassing items to do the actual changes.
     *
     * @param holder   The ViewHolder to bind.
     * @param selected Whether the item is selected.
     */
    protected abstract void onSelectionStateChanged(VH holder, boolean selected);

}
