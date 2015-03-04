package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.TagItem;
import com.aluxian.drizzle.multi.MultiTypeAdapter;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class TagsAdapter extends MultiTypeAdapter {

    public TagsAdapter(List<String> tags) {
        itemsList().addAll(Mapper.map(tags, TagItem::new));
    }

    @Override
    protected void onAddItemTypes() {
        addItemType(TagItem.ITEM_TYPE);
    }

}
