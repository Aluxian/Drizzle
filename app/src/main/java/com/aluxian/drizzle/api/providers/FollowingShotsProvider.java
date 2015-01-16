package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Shot;

import java.io.IOException;
import java.util.List;

/**
 * Provides shots by the users that the signed in user is following.
 */
public class FollowingShotsProvider extends ShotsProvider {

    public FollowingShotsProvider(Dribbble dribbble) {
        super(dribbble);
    }

    @Override
    public List<Shot> load() throws IOException, BadRequestException, TooManyRequestsException {
        if (mLastResponse != null && mLastResponse.nextPageUrl != null) {
            mLastResponse = mDribbble.listNextPage(mLastResponse.nextPageUrl).execute();
        } else {
            mLastResponse = mDribbble.listFollowing().execute();
        }

        return mLastResponse.data;
    }

    @Override
    public List<Shot> refresh() throws IOException, BadRequestException, TooManyRequestsException {
        mLastResponse = mDribbble.listFollowing().useCache(false).execute();
        return mLastResponse.data;
    }

    @Override
    public boolean hasItemsAvailable() {
        return mDribbble.listFollowing().canLoadImmediately();
    }

}
