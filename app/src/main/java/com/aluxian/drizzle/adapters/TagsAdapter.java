package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.TagItem;
import com.aluxian.drizzle.adapters.multi.MultiTypeBaseAdapter;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class TagsAdapter extends MultiTypeBaseAdapter {

    public TagsAdapter(List<String> tags) {
        itemsList().addAll(Mapper.map(tags, TagItem::new));
    }

    @Override
    protected void onAddItemTypes() {
        addItemType(TagItem.ITEM_TYPE);
    }

}
