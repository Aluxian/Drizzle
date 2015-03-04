package com.aluxian.drizzle.adapters.items;

import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.listeners.HeaderLoadListener;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeStyleableItem;
import com.aluxian.drizzle.adapters.multi.traits.MultiTypeHeader;
import com.aluxian.drizzle.api.models.User;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.aluxian.drizzle.utils.transformations.PaletteTransformation;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserHeaderItem extends MultiTypeStyleableItem<UserHeaderItem.ViewHolder> implements MultiTypeHeader {

    /** The {@link com.aluxian.drizzle.adapters.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(UserHeaderItem.class,
            ViewHolder.class, R.layout.item_header_user);

    private final User user;
    private HeaderLoadListener mHeaderListener;

    public UserHeaderItem(User user, HeaderLoadListener headerListener) {
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
                            UberSwatch swatch = UberSwatch.from(PaletteTransformation.getPalette(holder.userAvatar));
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
