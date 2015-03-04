package com.aluxian.drizzle.adapters.items;

import android.view.View;
import android.widget.ProgressBar;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.multi.MultiTypeItemType;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.multi.items.MultiTypeStyleableItem;
import com.aluxian.drizzle.utils.UberSwatch;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoadingItem extends MultiTypeStyleableItem<LoadingItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(LoadingItem.class,
            ViewHolder.class, R.layout.item_loading);

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {}

    @Override
    protected void onSetStyle(ViewHolder holder, UberSwatch swatch) {
        holder.progressBar.getIndeterminateDrawable().setTint(swatch.rgb);
    }

    public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

        @InjectView(R.id.progress_bar) public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

}
