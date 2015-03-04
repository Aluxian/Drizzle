package com.aluxian.drizzle.adapters.items;

import android.view.View;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TagItem extends MultiTypeBaseItem<TagItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.adapters.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(TagItem.class,
            ViewHolder.class, R.layout.item_tag);

    private final String tag;

    public TagItem(String tag) {
        this.tag = tag;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(tag);
    }

    @Override
    public int getId(int position) {
        return position;
    }

    public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

        @InjectView(R.id.title) TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

}
