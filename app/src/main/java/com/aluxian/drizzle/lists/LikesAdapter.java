package com.aluxian.drizzle.lists;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.UserActivity;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Like;
import com.aluxian.drizzle.api.providers.LikesProvider;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.Log;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.ViewHolder> {

    /** A list with all the displayed items. */
    private List<Like> mLikesList = new ArrayList<>();

    /** Whether the adapter is currently loading items. */
    private boolean mIsLoadingItems;

    /** An ItemsProvider instance used to load the appropriate items for this adapter's fragment. */
    private LikesProvider mLikesProvider;

    /** A Context, required by some tasks. */
    private Context mContext;

    public LikesAdapter(Context context, LikesProvider likesProvider) {
        mContext = context;
        mLikesProvider = likesProvider;

        // Load the first items
        new LoadItemsIfRequiredTask(0).execute();
    }

    @Override
    public LikesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Like like = mLikesList.get(position);

        Picasso.with(holder.userAvatar.getContext())
                .load(like.user.avatarUrl)
                .transform(new CircularTransformation())
                .into(holder.userAvatar);

        holder.userName.setText(like.user.name);

        CountableInterpolator countableInterpolator = new CountableInterpolator(holder.itemView.getContext());
        String shots = countableInterpolator.apply(like.user.shotsCount, R.string.stats_shots, R.string.stats_shot);
        String followers = countableInterpolator.apply(like.user.followersCount, R.string.stats_followers, R.string.stats_follower);

        holder.userDescription.setText(shots + ", " + followers);

//        if (position % 2 == 0) {
//            convertView.setBackgroundResource(R.color.bg_light);
//        } else {
//            convertView.setBackground(null);
//        }

        holder.itemView.setOnClickListener(view -> {
            /*Intent intent = new Intent(activity, ShotActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, holder.image, "robot");
            activity.startActivity(intent, options.toBundle());*/

            Intent intent = new Intent(mContext, UserActivity.class);
            intent.putExtra(UserActivity.EXTRA_USER_DATA, new Gson().toJson(like.user));
            mContext.startActivity(intent);
        });

        // If position is near the end of the list, load more items from the API
        new LoadItemsIfRequiredTask(position).execute();
    }

    @Override
    public int getItemCount() {
        return mLikesList.size();
    }

    private class LoadItemsIfRequiredTask extends AsyncTask<Void, Void, List<Like>> {

        /** The position of the item that executed the task. */
        private final int mPosition;

        private LoadItemsIfRequiredTask(int position) {
            mPosition = position;
        }

        @Override
        protected void onPreExecute() {
            if (!mIsLoadingItems && mLikesList.size() - mPosition <= Config.LOAD_ITEMS_THRESHOLD) {
                mIsLoadingItems = true;
            } else {
                cancel(true);
                //preloadImages(mPosition + 1);
            }
        }

        @Override
        protected List<Like> doInBackground(Void... params) {
            try {
                return mLikesProvider.load();
            } catch (IOException | BadRequestException | TooManyRequestsException e) {
                Log.d(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Like> response) {
            if (response != null) {
                mLikesList.addAll(response);
                notifyItemRangeInserted(mLikesList.size() - response.size(), response.size());
            }

            mIsLoadingItems = false;
            //preloadImages(mPosition + 1);
        }

    }

    /**
     * Start downloading the images of the following items.
     *
     * @param startPosition The position of the first item to be preloaded.
     */
    private void preloadImages(int startPosition) {
        for (int position = startPosition; position < startPosition + 6 && position < mLikesList.size(); position++) {
            Picasso.with(mContext).load(mLikesList.get(position).user.avatarUrl).fetch();
        }
    }

    /**
     * A ViewHolder used by the items of this adapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView userAvatar;

        public final TextView userName;
        public final TextView userDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            userAvatar = (ImageView) itemView.findViewById(R.id.user_avatar);

            userName = (TextView) itemView.findViewById(R.id.user_name);
            userDescription = (TextView) itemView.findViewById(R.id.user_description);
        }

    }

}
