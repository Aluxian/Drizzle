package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Like;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class LikesProvider extends ItemsProvider<Like> {

    /** The id of the shot whose likes to provide. */
    private final int mShotId;

    public LikesProvider(int shotId) {
        mShotId = shotId;
    }

    @Override
    protected ApiRequest<List<Like>> getListRequest() {
        return Dribbble.listLikes(mShotId);
    }

    @Override
    protected ApiRequest<List<Like>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Like>>() {});
    }

}
