package com.aluxian.drizzle.adapters.items;

import android.support.v7.widget.RecyclerView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Dp;

public class ShotItemUserActivity extends ShotItem {

    /** The {@link com.aluxian.drizzle.adapters.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(ShotItemUserActivity.class,
            ViewHolder.class, R.layout.item_shot);

    public ShotItemUserActivity(Shot shot) {
        super(shot);
    }

    @Override
    protected void adaptMargins(ViewHolder holder, int position) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

        if (position % 2 == 0) {
            params.setMarginStart(Dp.PX_04);
            params.setMarginEnd(Dp.PX_08);
        } else {
            params.setMarginStart(Dp.PX_08);
            params.setMarginEnd(Dp.PX_04);
        }

        holder.itemView.requestLayout();
    }

}
