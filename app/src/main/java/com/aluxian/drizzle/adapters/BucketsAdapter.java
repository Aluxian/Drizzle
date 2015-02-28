package com.aluxian.drizzle.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.multi.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.api.models.Bucket;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Mapper;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BucketsAdapter extends MultiTypeInfiniteAdapter<Bucket> {

    public BucketsAdapter(ItemsProvider<Bucket> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(new MultiTypeItemType<>(BucketItem.class, BucketItem.ViewHolder.class, R.layout.item_bucket));
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Bucket> items) {
        return Mapper.map(items, BucketItem::new);
    }

    public static class BucketItem extends MultiTypeBaseItem<BucketItem.ViewHolder> {

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
//                intent.putExtra(UserActivity.EXTRA_USER_DATA, new Gson().toJson(bucket.user));
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

}
