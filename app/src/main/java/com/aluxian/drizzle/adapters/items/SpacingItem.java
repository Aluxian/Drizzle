package com.aluxian.drizzle.adapters.items;

import android.view.View;
import android.view.ViewGroup;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;

public class SpacingItem extends MultiTypeBaseItem<SpacingItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.adapters.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(SpacingItem.class,
            ViewHolder.class, R.layout.item_spacing);

    private final int width;
    private final int height;

    public SpacingItem(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = width;
        params.height = height;
    }

    public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

}
