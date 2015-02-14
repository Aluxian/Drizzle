package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Config;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Provides shots featured by the app.
 */
public class FeaturedShotsProvider extends ItemsProvider<Shot> {

    @Override
    protected ApiRequest<List<Shot>> getListRequest() {
        return Dribbble.listBucketShots(Config.FEATURED_BUCKET_ID);
    }

    @Override
    protected ApiRequest<List<Shot>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Shot>>() {});
    }

}
