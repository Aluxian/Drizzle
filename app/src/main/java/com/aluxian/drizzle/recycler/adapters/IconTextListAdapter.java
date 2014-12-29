package com.aluxian.drizzle.recycler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.utils.Log;

import java.util.ArrayList;
import java.util.Map;

public class IconTextListAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_ICON_TEXT = 0;
    private static final int VIEW_TYPE_SUBHEADER = 1;

    // Map<titleId, drawableId>
    private Map<Integer, Integer> items;

    public IconTextListAdapter(Map<Integer, Integer> items) {
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            int layoutId = new int[]{R.layout.item_icon_text, R.layout.item_subheader}[itemViewType];
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        }

        if (itemViewType == VIEW_TYPE_ICON_TEXT) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            imageView.setImageResource(new ArrayList<>(items.values()).get(position));
        }

        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(context.getString(new ArrayList<>(items.keySet()).get(position)));

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        // The resource id of the item's title string
        return new ArrayList<>(items.keySet()).get(position);
    }

    @Override
    public int getItemViewType(int position) {
        // If the drawable id is 0 it's a subheader, otherwise it's an icon-text item
        return new ArrayList<>(items.values()).get(position) == 0 ? VIEW_TYPE_SUBHEADER : VIEW_TYPE_ICON_TEXT;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == VIEW_TYPE_ICON_TEXT;
    }

}
