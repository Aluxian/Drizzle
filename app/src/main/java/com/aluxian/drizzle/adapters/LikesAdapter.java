package com.aluxian.drizzle.adapters;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.UserActivity;
import com.aluxian.drizzle.adapters.multi.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.api.models.Like;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Mapper;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LikesAdapter extends MultiTypeInfiniteAdapter<Like> {

    public LikesAdapter(ItemsProvider<Like> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(new MultiTypeItemType<>(LikeItem.class, LikeItem.ViewHolder.class, R.layout.item_like));
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Like> items) {
        return Mapper.map(items, LikeItem::new);
    }

    public static class LikeItem extends MultiTypeBaseItem<LikeItem.ViewHolder> {

        private final Like like;

        public LikeItem(Like like) {
            this.like = like;
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(holder.context)
                    .load(like.user.avatarUrl)
                    .transform(new CircularTransformation())
                    .placeholder(R.drawable.round_placeholder)
                    .into(holder.avatar);

            CountableInterpolator countableInterpolator = new CountableInterpolator(holder.context);
            String shots = countableInterpolator.apply(like.user.shotsCount, R.string.stats_shots, R.string.stats_shot);
            String followers = countableInterpolator.apply(like.user.followersCount, R.string.stats_followers, R.string.stats_follower);

            holder.name.setText(like.user.name);
            holder.description.setText(shots + ", " + followers);

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(holder.context, UserActivity.class);
                intent.putExtra(UserActivity.EXTRA_USER_DATA, new Gson().toJson(like.user));
                holder.context.startActivity(intent);
            });
        }

        @Override
        public int getId(int position) {
            return like.id;
        }

        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

            @InjectView(R.id.user_avatar) ImageView avatar;
            @InjectView(R.id.user_name) TextView name;
            @InjectView(R.id.user_description) TextView description;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }

        }

    }

}
