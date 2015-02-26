package com.aluxian.drizzle.adapters.multi;

/**
 * Holds data about an item type used in a MultiTypeAdapter.
 *
 * @param <VH> The item's ViewHolder.
 */
public class MultiTypeItemType<VH extends MultiTypeBaseItem.ViewHolder> {

    public final Class itemClass;
    public final Class<VH> viewHolderClass;
    public final Integer layoutId;

    public MultiTypeItemType(Class itemClass, Class<VH> viewHolderClass, Integer layoutId) {
        this.itemClass = itemClass;
        this.viewHolderClass = viewHolderClass;
        this.layoutId = layoutId;
    }

}
