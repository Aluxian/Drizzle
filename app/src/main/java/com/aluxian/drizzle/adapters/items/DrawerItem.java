package com.aluxian.drizzle.adapters.items;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.utils.UberSwatch;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DrawerItem extends AuthStateDependantItem<DrawerItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.adapters.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(DrawerItem.class,
            ViewHolder.class, R.layout.item_icon_text);

    private final DrawerItemClickListener mDrawerItemClickListener;
    private final int drawableResId;
    private final int titleResId;

    public DrawerItem(int drawableResId, int titleResId, DrawerItemClickListener drawerItemClickListener) {
        this(drawableResId, titleResId, drawerItemClickListener, null);
    }

    public DrawerItem(int drawableResId, int titleResId, DrawerItemClickListener drawerItemClickListener, Boolean requiredAuthState) {
        super(requiredAuthState);
        this.drawableResId = drawableResId;
        this.titleResId = titleResId;
        mDrawerItemClickListener = drawerItemClickListener;
    }

    /**
     * Run the click listener.
     *
     * @param position The position of the item in the adapter.
     */
    public void onClick(int position) {
        mDrawerItemClickListener.onDrawerItemClick(position);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v -> mDrawerItemClickListener.onDrawerItemClick(position));
        holder.icon.setImageResource(drawableResId);
        holder.title.setText(titleResId);
    }

    @Override
    protected void changeVisibility(ViewHolder holder, boolean visible) {
        //holder.icon.setVisibility(visible ? View.VISIBLE : View.GONE);
        //holder.title.setVisibility(visible ? View.VISIBLE : View.GONE);

        holder.icon.getLayoutParams().height = holder.icon.getLayoutParams().height / 2;
        holder.title.getLayoutParams().height = holder.title.getLayoutParams().height / 2;
    }

    @Override
    public int getId(int position) {
        return titleResId;
    }

    @Override
    protected void onSetStyle(ViewHolder holder, UberSwatch swatch) {
        //holder.itemView.setTag(swatch);
    }

    @Override
    protected void onSelectionStateChanged(ViewHolder holder, boolean selected) {
        if (selected) {
            //UberSwatch swatch = (UberSwatch) holder.itemView.getTag();
            //holder.icon.getDrawable().setTint(swatch.rgb);
            //holder.title.setTextColor(swatch.rgb);
            int accentColor = holder.context.getResources().getColor(R.color.accent);
            holder.icon.getDrawable().setTint(accentColor);
            holder.title.setTextColor(accentColor);
        } else {
            int initialColor = holder.title.getLinkTextColors().getDefaultColor();
            holder.icon.getDrawable().setTint(initialColor);
            holder.title.setTextColor(initialColor);
        }
    }

    public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

        @InjectView(R.id.icon) ImageView icon;
        @InjectView(R.id.title) TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

    /**
     * Listen for clicks on the item.
     */
    public static interface DrawerItemClickListener {

        /**
         * Called when an item is clicked.
         *
         * @param position The position of the item in the adapter.
         */
        void onDrawerItemClick(int position);

    }

}
