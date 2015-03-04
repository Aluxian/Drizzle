package com.aluxian.drizzle.adapters.items;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.multi.MultiTypeItemType;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;

public class DividerItem extends MultiTypeBaseItem<MultiTypeBaseItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(DividerItem.class,
            ViewHolder.class, R.layout.item_divider_horizontal);

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {}

}
