package com.aluxian.drizzle.adapters.items;

import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.multi.items.MultiTypeSelectableItem;

public abstract class AuthStateDependantItem<VH extends MultiTypeBaseItem.ViewHolder> extends MultiTypeSelectableItem<VH> {

    private Boolean mRequiredAuthState;
    private boolean mAuthenticated;

    public AuthStateDependantItem(Boolean requiredAuthState) {
        mRequiredAuthState = requiredAuthState;
    }

    /**
     * @return Whether this item's visibility depends on the user's authentication state.
     */
    public boolean requiresAuthState() {
        return mRequiredAuthState != null;
    }

    /**
     * Called when the auth state changes to update the view.
     *
     * @param authenticated Whether the user is authenticated.
     */
    public void onAuthStateChanged(boolean authenticated) {
        mAuthenticated = authenticated;
    }

    @Override
    protected void onBindViewHolder(VH holder, int position) {
        if (mRequiredAuthState != null) {
            changeVisibility(holder, mRequiredAuthState == mAuthenticated);
        }
    }

    /**
     * Changed the visibility of the item.
     *
     * @param holder  The ViewHolder of the item.
     * @param visible Whether the view should be visible.
     */
    protected abstract void changeVisibility(VH holder, boolean visible);

}
