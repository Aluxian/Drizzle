package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.adapters.items.ProjectItem;
import com.aluxian.drizzle.adapters.multi.adapters.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.api.models.Project;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.Mapper;

import java.util.List;

public class ProjectsAdapter extends MultiTypeInfiniteAdapter<Project> {

    public ProjectsAdapter(ItemsProvider<Project> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(ProjectItem.ITEM_TYPE);
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Project> items) {
        return Mapper.map(items, ProjectItem::new);
    }

}
