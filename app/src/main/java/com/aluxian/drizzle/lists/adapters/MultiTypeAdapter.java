package com.aluxian.drizzle.lists.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom implementation of RecyclerView.Adapter which supports different item view types. Subclasses need to fill up the two lists or
 * just provide a constructor to do it.
 */
public abstract class MultiTypeAdapter<VH extends MultiTypeAdapter.BaseItem.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<ItemType<? extends VH>> mItemTypes = new ArrayList<>();
    protected List<BaseItem<VH>> mItems = new ArrayList<>();

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext()).inflate(mItemTypes.get(viewType).layoutId, parent, false);
            Constructor<? extends VH> constructor = mItemTypes.get(viewType).viewHolderClass.getConstructor(View.class);

            return constructor.newInstance(view);
        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid item type supplied", e);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        final AdapterView.OnItemClickListener clickListener = mItemTypes.get(holder.getItemViewType()).clickListener;

        if (clickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(null, v, position, getItemId(position));
                }
            });
        }

        mItems.get(position).bindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        Class itemClass = mItems.get(position).getClass();

        for (int i = 0; i < mItemTypes.size(); i++) {
            if (itemClass.equals(mItemTypes.get(i).itemClass)) {
                return i;
            }
        }

        return -1;
    }

    public static class ItemType<VH> {

        public final Class itemClass;
        public final Class<VH> viewHolderClass;
        public final AdapterView.OnItemClickListener clickListener;
        public final Integer layoutId;

        public ItemType(Class itemClass, Class<VH> viewHolderClass, AdapterView.OnItemClickListener clickListener, Integer layoutId) {
            this.itemClass = itemClass;
            this.viewHolderClass = viewHolderClass;
            this.clickListener = clickListener;
            this.layoutId = layoutId;
        }

    }

    public static interface BaseItem<VH extends BaseItem.ViewHolder> {

        /**
         * Binds the values of the item to its view.
         */
        public void bindViewHolder(VH viewHolder);

        /**
         * @return The id of this item.
         */
        public int getId();

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

}
