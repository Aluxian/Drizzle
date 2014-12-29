package com.aluxian.drizzle.lists;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.api.ParsedResponse;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShotsAdapter extends RecyclerView.Adapter<ShotsAdapter.ViewHolder> {

    private List<Shot> mShotsList = new ArrayList<>();
    private ParsedResponse<List<Shot>> mLastResponse;
    private boolean mIsLoadingItems;

    private Activity mActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Params.List mListParam;
    private Params.Timeframe mTimeframeParam;
    private Params.Sort mSortParam;

    public ShotsAdapter(Activity activity, SwipeRefreshLayout swipeRefreshLayout,
                        Params.List list, Params.Timeframe timeframe, Params.Sort sort) {
        mActivity = activity;
        mSwipeRefreshLayout = swipeRefreshLayout;

        mListParam = list;
        mTimeframeParam = timeframe;
        mSortParam = sort;

        // Load first mItems
        loadItemsIfRequired(0);
    }

    @Override
    public ShotsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Shot shot = mShotsList.get(position);

        Resources resources = mActivity.getResources();
        int iconColor = resources.getColor(R.color.card_actions);

        //holder.viewsIcon.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
        holder.commentsIcon.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
        holder.likesIcon.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);

        //holder.viewsCount.setText(String.valueOf(shot.viewsCount));
        holder.commentsCount.setText(String.valueOf(shot.commentsCount));
        holder.likesCount.setText(String.valueOf(shot.likesCount));

        /*holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ShotActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, holder.image, "robot");
                activity.startActivity(intent, options.toBundle());
            }
        });*/

        Picasso.with(holder.image.getContext())
                .load(shot.images.normal)
                .placeholder(R.drawable.loading_placeholder)
                .into(holder.image);

        // If position is near the end of the list, load more mItems
        loadItemsIfRequired(position);
    }

    @Override
    public int getItemCount() {
        return mShotsList.size();
    }

    public void setTimeframeParam(Params.Timeframe mTimeframeParam) {
        this.mTimeframeParam = mTimeframeParam;
    }

    public void setSortParam(Params.Sort mSortParam) {
        this.mSortParam = mSortParam;
    }

    public Params.Timeframe getTimeframeParam() {
        return mTimeframeParam;
    }

    public Params.Sort getSortParam() {
        return mSortParam;
    }

    public void loadItemsIfRequired(int position) {
        Log.v("loadItemsIfRequired(" + position + ")");

        if (!mIsLoadingItems && mShotsList.size() - position <= Config.LOAD_ITEMS_THRESHOLD) {
            Log.d("loading more items");
            mIsLoadingItems = true;

            new AsyncTask<Void, Void, ParsedResponse<List<Shot>>>() {

                @Override
                protected ParsedResponse<List<Shot>> doInBackground(Void... params) {
                    if (mLastResponse != null && mLastResponse.nextPageUrl != null) {
                        return new ApiRequest<List<Shot>>()
                                .responseType(new TypeToken<List<Shot>>() {})
                                .url(mLastResponse.nextPageUrl)
                                .execute();
                    } else {
                        return Dribbble.listShots(mListParam, mTimeframeParam, mSortParam).execute();
                    }
                }

                @Override
                protected void onPostExecute(ParsedResponse<List<Shot>> response) {
                    mShotsList.addAll(response.data);
                    mLastResponse = response;
                    mIsLoadingItems = false;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }, 500);

                    notifyItemRangeInserted(mShotsList.size() - response.data.size(), response.data.size());
                    Log.d("now there are " + mShotsList.size() + " items");
                }

            }.execute();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final CardView card;
        public final ImageView image;

        //public final ImageView viewsIcon;
        public final ImageView commentsIcon;
        public final ImageView likesIcon;

        //public final TextView viewsCount;
        public final TextView commentsCount;
        public final TextView likesCount;

        public ViewHolder(View itemView) {
            super(itemView);

            card = (CardView) itemView;
            image = (ImageView) itemView.findViewById(R.id.image);

            //viewsIcon = (ImageView) v.findViewById(R.id.views_icon);
            commentsIcon = (ImageView) itemView.findViewById(R.id.comments_icon);
            likesIcon = (ImageView) itemView.findViewById(R.id.likes_icon);

            //viewsCount = (TextView) v.findViewById(R.id.views_count);
            commentsCount = (TextView) itemView.findViewById(R.id.comments_count);
            likesCount = (TextView) itemView.findViewById(R.id.likes_count);
        }

    }

}
