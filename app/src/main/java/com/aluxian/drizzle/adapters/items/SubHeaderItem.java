package com.aluxian.drizzle.adapters.items;

import android.view.View;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SubHeaderItem extends MultiTypeBaseItem<SubHeaderItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.adapters.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(SubHeaderItem.class,
            ViewHolder.class, R.layout.item_subheader);

    /** The resource id of the title. */
    private final int titleResId;

    public SubHeaderItem(int titleResId) {
        this.titleResId = titleResId;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(titleResId);
    }

    @Override
    public int getId(int position) {
        return titleResId;
    }

    public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

        @InjectView(R.id.title) TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

}
