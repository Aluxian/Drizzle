package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.DividerItem;
import com.aluxian.drizzle.adapters.items.DrawerHeaderItem;
import com.aluxian.drizzle.adapters.items.DrawerItem;
import com.aluxian.drizzle.adapters.items.SpacingItem;
import com.aluxian.drizzle.adapters.items.SubHeaderItem;
import com.aluxian.drizzle.multi.adapters.MultiTypeSelectableAdapter;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.fragments.DrawerFragment;

public class DrawerAdapter extends MultiTypeSelectableAdapter {

    /**
     * @param drawerCallbacks A callback for item clicks.
     */
    public DrawerAdapter(DrawerFragment.DrawerCallbacks drawerCallbacks) {
        drawerCallbacks.onLoadDrawerItems(itemsList());
        notifyDataSetChanged();
    }

    /**
     * Changes the visibility of some items that have specific requirements on the auth state.
     *
     * @param authenticated Whether the user is authenticated.
     */
    public void updateItems(boolean authenticated) {
        for (int i = 0; i < itemsList().size(); i++) {
            MultiTypeBaseItem baseItem = itemsList().get(i);

            if (baseItem instanceof DrawerItem) {
                DrawerItem item = (DrawerItem) baseItem;

                if (item.requiresAuthState()) {
                    item.onAuthStateChanged(authenticated);
                    notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    protected void onAddItemTypes() {
        addItemType(DrawerHeaderItem.ITEM_TYPE);
        addItemType(SpacingItem.ITEM_TYPE);
        addItemType(DrawerItem.ITEM_TYPE);
        addItemType(SubHeaderItem.ITEM_TYPE);
        addItemType(DividerItem.ITEM_TYPE);
    }

}
