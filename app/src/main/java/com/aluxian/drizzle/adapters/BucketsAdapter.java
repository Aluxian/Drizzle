package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.BucketItem;
import com.aluxian.drizzle.multi.adapters.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.api.models.Bucket;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class BucketsAdapter extends MultiTypeInfiniteAdapter<Bucket> {

    public BucketsAdapter(ItemsProvider<Bucket> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(BucketItem.ITEM_TYPE);
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> convertLoadedItems(List<Bucket> items) {
        return Mapper.map(items, BucketItem::new);
    }

}
