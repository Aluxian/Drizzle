package com.aluxian.drizzle.adapters.items;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.multi.MultiTypeItemType;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.api.models.Bucket;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BucketItem extends MultiTypeBaseItem<BucketItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(BucketItem.class,
            ViewHolder.class, R.layout.item_bucket);

    private final Bucket bucket;

    public BucketItem(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(holder.context)
                .load(bucket.user.avatarUrl)
                .transform(new CircularTransformation())
                .placeholder(R.drawable.round_placeholder)
                .into(holder.avatar);

        CountableInterpolator countableInterpolator = new CountableInterpolator(holder.context);
        String shots = countableInterpolator.apply(bucket.user.shotsCount, R.string.stats_shots, R.string.stats_shot);

        holder.name.setText(bucket.name);
        holder.author.setText(holder.context.getResources().getString(R.string.word_by) + " " + bucket.user.name);
        holder.footer.setText(shots);

//            holder.itemView.setOnClickListener(view -> {
//                Intent intent = new Intent(holder.context, UserActivity.class);
//                intent.putExtra(UserActivity.EXTRA_USER_DATA, bucket.user.toJson());
//                holder.context.startActivity(intent);
//            });
    }

    @Override
    public int getId(int position) {
        return bucket.id;
    }

    public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

        @InjectView(R.id.avatar) ImageView avatar;
        @InjectView(R.id.title) TextView name;
        @InjectView(R.id.description) TextView author;
        @InjectView(R.id.footer) TextView footer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

}
