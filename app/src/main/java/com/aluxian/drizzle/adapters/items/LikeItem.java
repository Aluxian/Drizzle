package com.aluxian.drizzle.adapters.items;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.UserActivity;
import com.aluxian.drizzle.multi.MultiTypeItemType;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.api.models.Like;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LikeItem extends MultiTypeBaseItem<LikeItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(LikeItem.class,
            ViewHolder.class, R.layout.item_like);

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
            intent.putExtra(UserActivity.EXTRA_USER_DATA, like.user.toJson());
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
