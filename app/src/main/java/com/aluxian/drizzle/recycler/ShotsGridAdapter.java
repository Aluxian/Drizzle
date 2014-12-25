package com.aluxian.drizzle.recycler;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.ShotActivity;
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

import retrofit.client.Response;

public class ShotsGridAdapter extends RecyclerView.Adapter<ShotsGridAdapter.ViewHolder> {

    private List<Shot> shotsList = new ArrayList<>();
    private ParsedResponse<List<Shot>> lastResponse;
    private boolean isLoadingItems;

    private Activity activity;
    private Params.List listParam;
    private Params.Timeframe timeframeParam;
    private Params.Sort sortParam;

    public ShotsGridAdapter(Activity activity, Params.List list, Params.Timeframe timeframe, Params.Sort sort) {
        this.activity = activity;
        this.listParam = list;
        this.timeframeParam = timeframe;
        this.sortParam = sort;

        // Load first items
        loadItemsIfRequired(0);
    }

    @Override
    public ShotsGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Shot shot = shotsList.get(position);

        Resources resources = activity.getResources();
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

        // If position is near the end of the list, load more items
        loadItemsIfRequired(position);
    }

    @Override
    public int getItemCount() {
        return shotsList.size();
    }

    public void setTimeframeParam(Params.Timeframe timeframeParam) {
        this.timeframeParam = timeframeParam;
    }

    public void setSortParam(Params.Sort sortParam) {
        this.sortParam = sortParam;
    }

    public void loadItemsIfRequired(int position) {
        Log.v("loadItemsIfRequired(" + position + ")");

        if (!isLoadingItems && shotsList.size() - position <= Config.LOAD_ITEMS_THRESHOLD) {
            Log.d("loading more items");
            isLoadingItems = true;

            new AsyncTask<Void, Void, ParsedResponse<List<Shot>>>() {
                @Override
                protected ParsedResponse<List<Shot>> doInBackground(Void... params) {
                    if (lastResponse != null && lastResponse.nextPageUrl != null) {
                        return Dribbble.request(lastResponse.nextPageUrl, new TypeToken<List<Shot>>() {});
                    } else {
                        Response response = Dribbble.api.listShots(listParam.apiValue, timeframeParam.apiValue, sortParam.apiValue);
                        return Dribbble.parse(response, new TypeToken<List<Shot>>() {});
                    }
                }

                @Override
                protected void onPostExecute(ParsedResponse<List<Shot>> response) {
                    shotsList.addAll(response.data);
                    lastResponse = response;

                    notifyItemRangeInserted(shotsList.size() - response.data.size(), response.data.size());
                    isLoadingItems = false;

                    Log.d("now there are " + shotsList.size() + " items");
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

        public ViewHolder(View v) {
            super(v);

            card = (CardView) v;
            image = (ImageView) v.findViewById(R.id.image);

            //viewsIcon = (ImageView) v.findViewById(R.id.views_icon);
            commentsIcon = (ImageView) v.findViewById(R.id.comments_icon);
            likesIcon = (ImageView) v.findViewById(R.id.likes_icon);

            //viewsCount = (TextView) v.findViewById(R.id.views_count);
            commentsCount = (TextView) v.findViewById(R.id.comments_count);
            likesCount = (TextView) v.findViewById(R.id.likes_count);
        }

    }

}
