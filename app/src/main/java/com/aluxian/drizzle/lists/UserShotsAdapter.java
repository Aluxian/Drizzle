package com.aluxian.drizzle.lists;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.models.User;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.utils.CircularTransformation;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.PaletteTransformation;
import com.aluxian.drizzle.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserShotsAdapter extends ShotsAdapter {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private UserAdapterListener mUserAdapterListener;
    private User mUser;

    public UserShotsAdapter(User user, UserAdapterListener adapterListener, ItemsProvider<Shot> itemsProvider) {
        super(adapterListener, itemsProvider);
        mUserAdapterListener = adapterListener;
        mUser = user;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_user, parent, false));
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            holder.userLocation.setText(mUser.location);
            holder.userName.setText(mUser.name);

            if (TextUtils.isEmpty(mUser.bio)) {
                holder.userDescription.setVisibility(View.GONE);
            } else {
                holder.userDescription.setText(Html.fromHtml(mUser.bio));
            }

            holder.userDescription.setMovementMethod(LinkMovementMethod.getInstance());

            CountableInterpolator interpolator = new CountableInterpolator(holder.itemView.getContext());
            holder.userShots.setText(interpolator.apply(mUser.shotsCount, R.string.stats_shots, R.string.stats_shot));
            holder.userProjects.setText(interpolator.apply(mUser.projectsCount, R.string.stats_projects, R.string.stats_project));
            holder.userFollowers.setText(interpolator.apply(mUser.followersCount, R.string.stats_followers, R.string.stats_follower));

            Picasso.with(holder.userAvatar.getContext())
                    .load(mUser.avatarUrl)
                    .transform(new CircularTransformation())
                    .transform(PaletteTransformation.instance())
                            //.noFade()
                    .into(holder.userAvatar, new Callback() {
                        @Override
                        public void onSuccess() {
                            //ImageLoadingTransition.apply(preview);

                            holder.userAvatar.postDelayed(() -> {
                                Palette palette = PaletteTransformation.getPalette(holder.userAvatar);
                                Palette.Swatch swatch = Utils.getSwatch(palette);

                                holder.header.setBackgroundColor(swatch.getRgb());
                                mUserAdapterListener.onHeaderLoaded(swatch, holder.header.getHeight());

                                holder.userName.setTextColor(swatch.getTitleTextColor());
                                holder.userLocation.setTextColor(swatch.getBodyTextColor());
                                holder.userDescription.setTextColor(swatch.getBodyTextColor());
                                holder.userDescription.setLinkTextColor(swatch.getTitleTextColor());

                                holder.userLocationIcon.getDrawable().setTint(swatch.getBodyTextColor());
                            }, 100);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } else {
            super.onBindViewHolder(viewHolder, position);
        }
    }

    @Override
    protected int positionOffset() {
        return -1;
    }

    @Override
    protected void onShotClick(Context context, Shot shot) {
        shot.user = mUser;
        super.onShotClick(context, shot);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.user_name) TextView userName;
        @InjectView(R.id.user_avatar) ImageView userAvatar;
        @InjectView(R.id.user_description) TextView userDescription;
        @InjectView(R.id.user_location_icon) ImageView userLocationIcon;
        @InjectView(R.id.user_location) TextView userLocation;
        @InjectView(R.id.header) View header;

        @InjectView(R.id.user_shots) TextView userShots;
        @InjectView(R.id.user_projects) TextView userProjects;
        @InjectView(R.id.user_followers) TextView userFollowers;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

    public static interface UserAdapterListener extends AdapterListener {

        public void onHeaderLoaded(Palette.Swatch swatch, int height);

    }

}
