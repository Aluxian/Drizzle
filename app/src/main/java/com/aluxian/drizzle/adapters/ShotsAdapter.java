package com.aluxian.drizzle.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.ShotActivity;
import com.aluxian.drizzle.adapters.multi.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Mapper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShotsAdapter extends MultiTypeInfiniteAdapter<Shot> {

    private ItemsProvider<Shot> mItemsProvider;

    public ShotsAdapter(ItemsProvider<Shot> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
        mItemsProvider = itemsProvider;
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(new MultiTypeItemType<>(ShotItem.class, ShotItem.ViewHolder.class, R.layout.item_shot));
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Shot> items) {
        return Mapper.map(items, ShotItem::new);
    }

    public ItemsProvider<Shot> getShotsProvider() {
        return mItemsProvider;
    }

    public static class ShotItem extends MultiTypeBaseItem<ShotItem.ViewHolder> implements View.OnClickListener {

        protected final Shot shot;

        public ShotItem(Shot shot) {
            this.shot = shot;
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, int position) {
            holder.viewsCount.setText(String.valueOf(shot.viewsCount));
            holder.likesCount.setText(String.valueOf(shot.likesCount));
            holder.image.setOnClickListener(this);

            Picasso.with(holder.context)
                    .load(shot.images.normal)
                    .placeholder(R.color.slate)
                    .into(holder.image);

            adaptMargins(holder, position);

            // Hide or show the GIF badge
            holder.gifBadge.setVisibility(shot.isGif() ? View.VISIBLE : View.INVISIBLE);
        }

        /**
         * Adapt the margin sizes for this item.
         *
         * @param holder   The item's ViewHolder.
         * @param position The item's position.
         */
        protected void adaptMargins(ViewHolder holder, int position) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

            if (position % 2 == 1) {
                params.setMarginStart(Dp.PX_04);
                params.setMarginEnd(Dp.PX_08);
            } else {
                params.setMarginStart(Dp.PX_08);
                params.setMarginEnd(Dp.PX_04);
            }

            if (position == 0 || position == 1) {
                params.topMargin = Dp.PX_08;
            } else {
                params.topMargin = Dp.PX_04;
            }

            holder.itemView.requestLayout();
        }

        @Override
        public int getId(int position) {
            return shot.id;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ShotActivity.class);
            intent.putExtra(ShotActivity.EXTRA_SHOT_DATA, new Gson().toJson(shot));
            v.getContext().startActivity(intent);
        }

        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

            @InjectView(R.id.cover_image) ImageView image;
            @InjectView(R.id.gif_badge) ImageView gifBadge;

            @InjectView(R.id.views_count) TextView viewsCount;
            @InjectView(R.id.likes_count) TextView likesCount;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }

        }

    }

}
