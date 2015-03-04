package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.ShotItem;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.multi.adapters.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class ShotsAdapter extends MultiTypeInfiniteAdapter<Shot> {

    private ItemsProvider<Shot> mItemsProvider;

    public ShotsAdapter(ItemsProvider<Shot> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
        mItemsProvider = itemsProvider;
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(ShotItem.ITEM_TYPE);
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> convertLoadedItems(List<Shot> items) {
        return Mapper.map(items, ShotItem::new);
    }

    public ItemsProvider<Shot> getShotsProvider() {
        return mItemsProvider;
    }

}
