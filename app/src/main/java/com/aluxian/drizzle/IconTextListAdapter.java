package com.aluxian.drizzle;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class IconTextListAdapter extends BaseAdapter {

    private Context context;
    private LinkedHashMap<Integer, Integer> items;

    public IconTextListAdapter(Context context, LinkedHashMap<Integer, Integer> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_list_item, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            imageView.setColorFilter(context.getResources().getColor(R.color.text_drawer), PorterDuff.Mode.MULTIPLY);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        Collection<Integer> values = items.values();
        imageView.setImageResource(values.toArray(new Integer[values.size()])[position]);

        TextView textView = (TextView) convertView.findViewById(R.id.title);
        Set<Integer> keys = items.keySet();
        int id = keys.toArray(new Integer[keys.size()])[position];
        textView.setText(context.getString(id));

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
