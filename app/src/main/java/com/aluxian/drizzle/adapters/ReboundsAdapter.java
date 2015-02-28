package com.aluxian.drizzle.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

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

public class ReboundsAdapter extends MultiTypeInfiniteAdapter<Shot> {

    private Shot mReboundShot;

    public ReboundsAdapter(Shot reboundShot, ItemsProvider<Shot> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
        mReboundShot = reboundShot;
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(new MultiTypeItemType<>(ReboundItem.class, ReboundItem.ViewHolder.class, R.layout.item_rebound));
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Shot> items) {
        return Mapper.map(items, item -> {
            item.user = mReboundShot.user;
            item.team = mReboundShot.team;
            return new ReboundItem(mReboundShot, item);
        });
    }

    public static class ReboundItem extends MultiTypeBaseItem<ReboundItem.ViewHolder> implements View.OnClickListener {

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
            intent.putExtra(ShotActivity.EXTRA_SHOT_DATA, new Gson().toJson(shot));
            intent.putExtra(ShotActivity.EXTRA_REBOUND_OF, new Gson().toJson(reboundShot));
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

}
