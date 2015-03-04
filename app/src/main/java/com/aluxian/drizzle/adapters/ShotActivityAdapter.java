package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.CommentItem;
import com.aluxian.drizzle.adapters.items.ShotHeaderItem;
import com.aluxian.drizzle.adapters.listeners.HeaderLoadListener;
import com.aluxian.drizzle.adapters.multi.adapters.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.api.models.Comment;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class ShotActivityAdapter extends MultiTypeInfiniteAdapter<Comment> {

    public ShotActivityAdapter(Shot shot, Shot reboundOfShot, ItemsProvider<Comment> itemsProvider,
                               HeaderLoadListener headerListener, StatusListener statusListener) {
        super(itemsProvider, statusListener);
        itemsList().add(0, new ShotHeaderItem(shot, reboundOfShot, headerListener));
        notifyItemInserted(0);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(ShotHeaderItem.ITEM_TYPE);
        addItemType(CommentItem.ITEM_TYPE);
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Comment> items) {
        return Mapper.map(items, CommentItem::new);
    }

}
