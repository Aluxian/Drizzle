package com.aluxian.drizzle.lists.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.lists.DrawerListItem;

import java.util.List;

import static com.aluxian.drizzle.lists.DrawerListItem.TYPE_ICON_TEXT;
import static com.aluxian.drizzle.lists.DrawerListItem.TYPE_SUBHEADER;

public class DrawerListAdapter extends BaseAdapter {

    private List<DrawerListItem> mItems;

    public DrawerListAdapter(List<DrawerListItem> items) {
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            int layoutId = new int[]{R.layout.item_icon_text, R.layout.item_subheader, R.layout.item_divider}[itemViewType];
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        }

        switch (itemViewType) {
            case TYPE_ICON_TEXT:
                ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
                imageView.setImageResource(mItems.get(position).iconResourceId);

            case TYPE_SUBHEADER:
                TextView textView = (TextView) convertView.findViewById(R.id.title);
                textView.setText(context.getString(mItems.get(position).titleResourceId));
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_ICON_TEXT;
    }

}
