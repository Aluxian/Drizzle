package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.ShotItemUserActivity;
import com.aluxian.drizzle.adapters.items.UserHeaderItem;
import com.aluxian.drizzle.multi.adapters.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.models.User;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.multi.traits.MultiTypeHeader;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class UserActivityAdapter extends MultiTypeInfiniteAdapter<Shot> {

    private User mUser;

    public UserActivityAdapter(User user, ItemsProvider<Shot> itemsProvider,
                               MultiTypeHeader.StateListener headerListener, StatusListener statusListener) {
        super(itemsProvider, statusListener);
        mUser = user;
        itemsList().add(new UserHeaderItem(user, headerListener));
        notifyItemInserted(0);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(UserHeaderItem.ITEM_TYPE);
        addItemType(ShotItemUserActivity.ITEM_TYPE);
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> convertLoadedItems(List<Shot> lst) {
        return Mapper.map(lst, shot -> new ShotItemUserActivity(shot.cloneAndUpdate(json -> json.add("user", mUser.toJsonObject()))));
    }

}
