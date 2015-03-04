package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.LikeItem;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.adapters.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.api.models.Like;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class LikesAdapter extends MultiTypeInfiniteAdapter<Like> {

    public LikesAdapter(ItemsProvider<Like> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(LikeItem.ITEM_TYPE);
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Like> items) {
        return Mapper.map(items, LikeItem::new);
    }

}
