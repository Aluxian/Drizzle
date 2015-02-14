package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Shot;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Provides shots by the users that the signed in user is following.
 */
public class FollowingShotsProvider extends ItemsProvider<Shot> {

    @Override
    protected ApiRequest<List<Shot>> getListRequest() {
        return Dribbble.listShotsFromFollowing();
    }

    @Override
    protected ApiRequest<List<Shot>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Shot>>() {});
    }

}
