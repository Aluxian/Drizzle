package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Comment;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ShotCommentsProvider extends ItemsProvider<Comment> {

    /** The id of the shot whose comments to provide. */
    private final int mShotId;

    public ShotCommentsProvider(int shotId) {
        mShotId = shotId;
    }

    @Override
    protected ApiRequest<List<Comment>> getListRequest() {
        return Dribbble.listComments(mShotId);
    }

    @Override
    protected ApiRequest<List<Comment>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Comment>>() {});
    }

}
