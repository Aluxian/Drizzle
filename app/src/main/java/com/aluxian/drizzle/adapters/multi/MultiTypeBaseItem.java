package com.aluxian.drizzle.adapters.multi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Base class for items that will appear in a MultiTypeAdapter.
 *
 * @param <VH> The type of the item's ViewHolder.
 */
public class MultiTypeBaseItem<VH extends MultiTypeBaseItem.ViewHolder> {

    /**
     * Binds the item to its view. This method is used by MultiTypeAdapter to bind ViewHolders.
     *
     * @param holder   The ViewHolder to bind.
     * @param position The position of the item in the adapter that calls this method.
     */
    @SuppressWarnings("unchecked")
    public void bindViewHolder(ViewHolder holder, int position) {
        onBindViewHolder((VH) holder, position);
    }

    /**
     * Binds the item to its view. This method is implemented by subclassing items to do the actual binding.
     *
     * @param holder   The ViewHolder to bind.
     * @param position The position of the item in the adapter that calls this method.
     */
    protected void onBindViewHolder(VH holder, int position) {}

    /**
     * @param position The position of the item in the adapter that calls this method.
     * @return The id of this item.
     */
    public int getId(int position) {
        return position;
    }

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
