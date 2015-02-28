package com.aluxian.drizzle.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.multi.MultiTypeAdapter;
import com.aluxian.drizzle.adapters.multi.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Mapper;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TagsAdapter extends MultiTypeAdapter {

    public TagsAdapter(List<String> tags) {
        itemsList().addAll(Mapper.map(tags, TagItem::new));
    }

    @Override
    protected void onAddItemTypes() {
        addItemType(new MultiTypeItemType<>(TagItem.class, TagItem.ViewHolder.class, R.layout.item_tag));
    }

    public static class TagItem extends MultiTypeBaseItem<TagItem.ViewHolder> {

        private final String tag;

        public TagItem(String tag) {
            this.tag = tag;
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(tag);
        }

        @Override
        public int getId(int position) {
            return position;
        }

        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

            @InjectView(R.id.title) TextView name;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }

        }

    }

}
