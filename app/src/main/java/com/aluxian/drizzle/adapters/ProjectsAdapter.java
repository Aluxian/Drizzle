package com.aluxian.drizzle.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.multi.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.api.models.Project;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Mapper;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProjectsAdapter extends MultiTypeInfiniteAdapter<Project> {

    public ProjectsAdapter(ItemsProvider<Project> itemsProvider, StatusListener statusListener) {
        super(itemsProvider, statusListener);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(new MultiTypeItemType<>(ProjectItem.class, ProjectItem.ViewHolder.class, R.layout.item_project));
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Project> items) {
        return Mapper.map(items, ProjectItem::new);
    }

    public static class ProjectItem extends MultiTypeBaseItem<ProjectItem.ViewHolder> {

        private final Project project;

        public ProjectItem(Project project) {
            this.project = project;
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(holder.context)
                    .load(project.user.avatarUrl)
                    .transform(new CircularTransformation())
                    .placeholder(R.drawable.round_placeholder)
                    .into(holder.avatar);

            CountableInterpolator countableInterpolator = new CountableInterpolator(holder.context);
            String shots = countableInterpolator.apply(project.user.shotsCount, R.string.stats_shots, R.string.stats_shot);

            holder.name.setText(project.name);
            holder.author.setText(holder.context.getResources().getString(R.string.word_by) + " " + project.user.name);
            holder.footer.setText(shots);

            holder.itemView.setOnClickListener(view -> {
//                Intent intent = new Intent(holder.context, UserActivity.class);
//                intent.putExtra(UserActivity.EXTRA_USER_DATA, new Gson().toJson(bucket.user));
//                holder.context.startActivity(intent);
            });
        }

        @Override
        public int getId(int position) {
            return project.id;
        }

        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

            @InjectView(R.id.avatar) ImageView avatar;
            @InjectView(R.id.title) TextView name;
            @InjectView(R.id.description) TextView author;
            @InjectView(R.id.footer) TextView footer;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }

        }

    }

}
