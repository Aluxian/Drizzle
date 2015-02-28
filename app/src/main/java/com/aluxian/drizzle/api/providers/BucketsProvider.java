package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Bucket;
import com.aluxian.drizzle.api.models.Like;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class BucketsProvider extends ItemsProvider<Bucket> {

    /** The id of the shot whose buckets to provide. */
    private final int mShotId;

    public BucketsProvider(int shotId) {
        mShotId = shotId;
    }

    @Override
    protected ApiRequest<List<Bucket>> getListRequest() {
        return Dribbble.listBuckets(mShotId);
    }

    @Override
    protected ApiRequest<List<Bucket>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Bucket>>() {});
    }

}
