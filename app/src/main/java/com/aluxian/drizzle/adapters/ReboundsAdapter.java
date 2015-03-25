package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.ReboundItem;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.multi.adapters.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class ReboundsAdapter extends MultiTypeInfiniteAdapter<Shot> {

    private Shot mReboundShot;

    public ReboundsAdapter(Shot reboundShot, ItemsProvider<Shot> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
        mReboundShot = reboundShot;
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(ReboundItem.ITEM_TYPE);
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> convertLoadedItems(List<Shot> lst) {
        return Mapper.map(lst, shot -> new ReboundItem(mReboundShot, shot.cloneAndUpdate(json -> {
            json.add("user", mReboundShot.user.toJsonObject());

            if (mReboundShot.team != null) {
                json.add("team", mReboundShot.team.toJsonObject());
            }
        })));
    }

}
