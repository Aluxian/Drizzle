package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Bucket;
import com.aluxian.drizzle.api.models.Shot;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ReboundsProvider extends ItemsProvider<Shot> {

    /** The id of the shot whose rebounds to provide. */
    private final int mShotId;

    public ReboundsProvider(int shotId) {
        mShotId = shotId;
    }

    @Override
    protected ApiRequest<List<Shot>> getListRequest() {
        return Dribbble.listRebounds(mShotId);
    }

    @Override
    protected ApiRequest<List<Shot>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Shot>>() {});
    }

}
