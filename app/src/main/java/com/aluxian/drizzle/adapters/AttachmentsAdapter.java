package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.AttachmentItem;
import com.aluxian.drizzle.api.models.Attachment;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.multi.adapters.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class AttachmentsAdapter extends MultiTypeInfiniteAdapter<Attachment> {

    public AttachmentsAdapter(ItemsProvider<Attachment> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(AttachmentItem.ITEM_TYPE);
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> convertLoadedItems(List<Attachment>
                                                                                                             lst) {
        return Mapper.map(lst, AttachmentItem::new);
    }

}
