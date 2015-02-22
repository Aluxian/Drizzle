package com.aluxian.drizzle.recycler.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Base class for items that will appear in a MultiTypeAdapter.
 *
 * @param <VH> The type of the item's ViewHolder.
 */
public abstract class MultiTypeBaseItem<VH extends MultiTypeBaseItem.ViewHolder> {

    /**
     * Binds the item to its view. This method is used by MultiTypeAdapter to bind ViewHolders.
     *
     * @param holder The ViewHolder to bind.
     */
    @SuppressWarnings("unchecked")
    public void bindViewHolder(MultiTypeBaseItem.ViewHolder holder) {
        onBindViewHolder((VH) holder);
    }

    /**
     * Binds the item to its view. This method is implemented by subclassing items to do the actual binding.
     *
     * @param holder The ViewHolder to bind.
     */
    protected abstract void onBindViewHolder(VH holder);

    /**
     * @param position The position of the item in the adapter that calls this method.
     * @return The id of this item.
     */
    public abstract int getId(int position);

    /**
     * Custom ViewHolder that also holds a reference to the context of itemView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /** The context of itemView. */
        public final Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
        }

    }

}
