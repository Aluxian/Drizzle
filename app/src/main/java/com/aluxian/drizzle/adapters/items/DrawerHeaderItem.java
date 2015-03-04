package com.aluxian.drizzle.adapters.items;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.ShotActivity;
import com.aluxian.drizzle.adapters.listeners.HeaderLoadListener;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.utils.transformations.PaletteTransformation;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DrawerHeaderItem extends MultiTypeBaseItem<DrawerHeaderItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.adapters.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(DrawerHeaderItem.class,
            ViewHolder.class, R.layout.item_header_drawer);

    private final HeaderLoadListener mHeaderListener;

    private static final Pattern PIXELS_COUNT_PATTERN = Pattern.compile("<strong>([0-9,]+)</strong> pixels dribbbled");

    public DrawerHeaderItem(HeaderLoadListener headerListener) {
        mHeaderListener = headerListener;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {
        // Load the cover image
        Dribbble.getDrawerCoverShot().execute(new ApiRequest.Callback<List<Shot>>() {
            @Override
            public void onSuccess(Dribbble.Response<List<Shot>> response) {
                Shot shot = response.data.get(0);

                Picasso.with(holder.context)
                        .load(shot.images.largest())
                        .transform(PaletteTransformation.instance())
                        .placeholder(R.color.slate)
                        .into(holder.cover, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.cover.postDelayed(() -> {
                                    Palette palette = PaletteTransformation.getPalette(holder.cover);
                                    UberSwatch swatch = UberSwatch.from(palette);
                                    mHeaderListener.onHeaderLoaded(swatch, 0);
                                }, 500);
                            }

                            @Override
                            public void onError() {

                            }
                        });

                holder.cover.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.context, ShotActivity.class);
                    intent.putExtra(ShotActivity.EXTRA_SHOT_DATA, shot.toJson());
                    holder.context.startActivity(intent);
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(e);
            }
        });

        // Load the dribbbled pixels count
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Request request = new Request.Builder().url("https://dribbble.com/privacy").build();
                    return new OkHttpClient().newCall(request).execute().body().string();
                } catch (IOException e) {
                    Log.e(e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(String body) {
                if (body != null) {
                    Matcher matcher = PIXELS_COUNT_PATTERN.matcher(body);
                    if (matcher.find()) {
                        String pixelCount = matcher.group(1);
                        holder.pixelsCount.setText(pixelCount);
                        holder.pixelsDescription.setVisibility(View.VISIBLE);
                    }
                }
            }
        }.execute();
    }

    public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

        @InjectView(R.id.cover_image) ImageView cover;

        @InjectView(R.id.pixels_count) TextView pixelsCount;
        @InjectView(R.id.pixels_description) TextView pixelsDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

}
