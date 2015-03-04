package com.aluxian.drizzle.adapters.items;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.ShotActivity;
import com.aluxian.drizzle.multi.MultiTypeItemType;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Dp;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ReboundItem extends MultiTypeBaseItem<ReboundItem.ViewHolder> implements View.OnClickListener {

    /** The {@link com.aluxian.drizzle.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(ReboundItem.class,
            ViewHolder.class, R.layout.item_rebound);

    protected final Shot reboundShot;
    protected final Shot shot;

    public ReboundItem(Shot reboundShot, Shot shot) {
        this.reboundShot = reboundShot;
        this.shot = shot;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {
        holder.image.setOnClickListener(this);

        Picasso.with(holder.context)
                .load(shot.images.normal)
                .placeholder(R.color.slate)
                .into(holder.image);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        if (position == 0) {
            params.leftMargin = Dp.PX_16;
        } else {
            params.leftMargin = Dp.PX_08;
        }

//            if (position == response.data.size() - 1) {
//                params.setMarginEnd(Dp.PX_16);
//            }
    }

    @Override
    public int getId(int position) {
        return shot.id;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ShotActivity.class);
        intent.putExtra(ShotActivity.EXTRA_SHOT_DATA, shot.toJson());
        intent.putExtra(ShotActivity.EXTRA_REBOUND_OF, reboundShot.toJson());
        v.getContext().startActivity(intent);
    }

    public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

        @InjectView(R.id.image) ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

}
