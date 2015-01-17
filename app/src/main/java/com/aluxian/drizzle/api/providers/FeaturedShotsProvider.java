package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides shots featured by the app.
 */
public class FeaturedShotsProvider extends ShotsProvider {

    @Override
    public List<Shot> load() throws IOException, BadRequestException, TooManyRequestsException {
        if (mLastResponse == null) {
            mLastResponse = Dribbble.listBucketShots(Config.FEATURED_BUCKET_ID).execute();
            return mLastResponse.data;
        } else if (mLastResponse.nextPageUrl != null) {
            mLastResponse = Dribbble.listNextPage(mLastResponse.nextPageUrl).execute();
            return mLastResponse.data;
        }

        return new ArrayList<>();
    }

    @Override
    public List<Shot> refresh() throws IOException, BadRequestException, TooManyRequestsException {
        mLastResponse = Dribbble.listBucketShots(Config.FEATURED_BUCKET_ID).useCache(false).execute();
        return mLastResponse.data;
    }

    @Override
    public boolean hasItemsAvailable() {
        return Dribbble.listBucketShots(Config.FEATURED_BUCKET_ID).canLoadImmediately();
    }

}
