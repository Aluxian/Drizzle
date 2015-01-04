package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Shot;

import java.io.IOException;
import java.util.List;

/**
 * Provides shots from the /shots endpoint, where items are filtered by the list, timeframe and sort parameters.
 */
public class FilteredShotsProvider implements ShotsProvider {

    /** The last received response. */
    private Dribbble.Response<List<Shot>> mLastResponse;

    /** Dribbble ApiRequest generator instance. */
    private Dribbble mDribbble;

    // Parameters
    public final Params.List listParam;
    public Params.Timeframe timeframeParam = Params.Timeframe.NOW;
    public Params.Sort sortParam = Params.Sort.POPULAR;

    public FilteredShotsProvider(Dribbble dribbble, Params.List listParam) {
        this.mDribbble = dribbble;
        this.listParam = listParam;
    }

    @Override
    public List<Shot> load() throws IOException, BadRequestException, TooManyRequestsException {
        if (mLastResponse != null && mLastResponse.nextPageUrl != null) {
            mLastResponse = mDribbble.listNextPage(mLastResponse.nextPageUrl).execute();
        } else {
            mLastResponse = mDribbble.listShots(listParam, timeframeParam, sortParam).execute();
        }

        return mLastResponse.data;
    }

    @Override
    public List<Shot> refresh() throws IOException, BadRequestException, TooManyRequestsException {
        mLastResponse = mDribbble.listShots(listParam, timeframeParam, sortParam).useCache(false).execute();
        return mLastResponse.data;
    }

    @Override
    public boolean hasItemsAvailable() {
        return mDribbble.listShots(listParam, timeframeParam, sortParam).canLoadImmediately();
    }

}
