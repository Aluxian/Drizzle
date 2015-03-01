package com.aluxian.drizzle.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.multi.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.MultiTypeHeader;
import com.aluxian.drizzle.adapters.multi.MultiTypeInfiniteAdapter;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.adapters.multi.MultiTypeStyleableItem;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.models.User;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Mapper;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.aluxian.drizzle.utils.transformations.PaletteTransformation;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserActivityAdapter extends MultiTypeInfiniteAdapter<Shot> {

    private User mUser;

    public UserActivityAdapter(User user, ItemsProvider<Shot> itemsProvider,
                               AdapterHeaderListener headerListener, StatusListener statusListener) {
        super(itemsProvider, statusListener);
        mUser = user;
        itemsList().add(new HeaderItem(user, headerListener));
        notifyItemInserted(0);
    }

    @Override
    protected void onAddItemTypes() {
        super.onAddItemTypes();
        addItemType(new MultiTypeItemType<>(HeaderItem.class, HeaderItem.ViewHolder.class, R.layout.item_header_user));
        addItemType(new MultiTypeItemType<>(ShotItem.class, ShotsAdapter.ShotItem.ViewHolder.class, R.layout.item_shot));
    }

    @Override
    protected List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> mapLoadedItems(List<Shot> items) {
        return Mapper.map(items, shot -> new ShotItem(shot.cloneAndUpdate(json -> json.add("user", mUser.toJsonObject()))));
    }

    public static class HeaderItem extends MultiTypeStyleableItem<HeaderItem.ViewHolder> implements MultiTypeHeader {

        private final User user;
        private AdapterHeaderListener mHeaderListener;

        public HeaderItem(User user, AdapterHeaderListener headerListener) {
            this.user = user;
            mHeaderListener = headerListener;
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, int position) {
            if (TextUtils.isEmpty(user.location)) {
                ((View) holder.userLocation.getParent()).setVisibility(View.GONE);
            } else {
                holder.userLocation.setText(user.location);
            }

            if (TextUtils.isEmpty(user.bio)) {
                holder.userDescription.setVisibility(View.GONE);
            } else {
                holder.userDescription.setText(Html.fromHtml(user.bio));
                holder.userDescription.setMovementMethod(LinkMovementMethod.getInstance());
            }

            holder.userName.setText(user.name);

            CountableInterpolator interpolator = new CountableInterpolator(holder.itemView.getContext());
            holder.userShots.setText(interpolator.apply(user.shotsCount, R.string.stats_shots, R.string.stats_shot));
            holder.userProjects.setText(interpolator.apply(user.projectsCount, R.string.stats_projects, R.string.stats_project));
            holder.userFollowers.setText(interpolator.apply(user.followersCount, R.string.stats_followers, R.string.stats_follower));

            Picasso.with(holder.userAvatar.getContext())
                    .load(user.avatarUrl)
                    .transform(new CircularTransformation())
                    .transform(PaletteTransformation.instance())
                    .into(holder.userAvatar, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.userAvatar.postDelayed(() -> {
                                UberSwatch swatch = new UberSwatch(PaletteTransformation.getPalette(holder.userAvatar));
                                mHeaderListener.onHeaderLoaded(swatch, holder.header.getHeight());
                                onSetStyle(holder, swatch);
                            }, 100);
                        }

                        @Override
                        public void onError() {}
                    });
        }

        @Override
        public int getId(int position) {
            return user.id;
        }

        @Override
        protected void onSetStyle(ViewHolder holder, UberSwatch swatch) {
            holder.userName.setTextColor(swatch.titleTextColor);
            holder.userLocation.setTextColor(swatch.bodyTextColor);
            holder.userDescription.setTextColor(swatch.bodyTextColor);
            holder.userDescription.setLinkTextColor(swatch.bodyTextColor);
            holder.userLocationIcon.getDrawable().setTint(swatch.bodyTextColor);
            holder.header.setBackgroundColor(swatch.rgb);

            holder.actionButton.setColorNormal(swatch.rgb);
            holder.actionButton.setColorPressed(swatch.titleTextColor);
            holder.actionButton.setColorRipple(swatch.bodyTextColor);
            holder.actionButton.getDrawable().setTint(swatch.titleTextColor);
        }

        public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

            @InjectView(R.id.user_name) TextView userName;
            @InjectView(R.id.user_avatar) ImageView userAvatar;
            @InjectView(R.id.user_description) TextView userDescription;
            @InjectView(R.id.user_location_icon) ImageView userLocationIcon;
            @InjectView(R.id.user_location) TextView userLocation;
            @InjectView(R.id.fab) FloatingActionButton actionButton;
            @InjectView(R.id.header) View header;

            @InjectView(R.id.user_shots) TextView userShots;
            @InjectView(R.id.user_projects) TextView userProjects;
            @InjectView(R.id.user_followers) TextView userFollowers;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }

        }

    }

    public static class ShotItem extends ShotsAdapter.ShotItem {

        public ShotItem(Shot shot) {
            super(shot);
        }

        @Override
        protected void adaptMargins(ViewHolder holder, int position) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

            if (position % 2 == 0) {
                params.setMarginStart(Dp.PX_04);
                params.setMarginEnd(Dp.PX_08);
            } else {
                params.setMarginStart(Dp.PX_08);
                params.setMarginEnd(Dp.PX_04);
            }

            holder.itemView.requestLayout();
        }

    }

}
