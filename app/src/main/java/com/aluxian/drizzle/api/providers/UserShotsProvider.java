package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Shot;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class UserShotsProvider extends ItemsProvider<Shot> {

    /** The id of the user. */
    private final int mUserId;

    public UserShotsProvider(int userId) {
        mUserId = userId;
    }

    @Override
    protected ApiRequest<List<Shot>> getListRequest() {
        return Dribbble.listUserShots(mUserId);
    }

    @Override
    protected ApiRequest<List<Shot>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Shot>>() {});
    }

}
