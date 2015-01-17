package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides shots from the /shots endpoint, where items are filtered by the list, timeframe and sort parameters.
 */
public class FilteredShotsProvider extends ShotsProvider {

    public final Params.List listParam;
    public Params.Timeframe timeframeParam = Params.Timeframe.NOW;
    public Params.Sort sortParam = Params.Sort.POPULAR;

    public FilteredShotsProvider(Params.List listParam) {
        this.listParam = listParam;
    }

    @Override
    public List<Shot> load() throws IOException, BadRequestException, TooManyRequestsException {
        if (mLastResponse == null) {
            mLastResponse = Dribbble.listShots(listParam, timeframeParam, sortParam).execute();

            if (mLastResponse != null) {
                return mLastResponse.data;
            }
        } else if (mLastResponse.nextPageUrl != null) {
            mLastResponse = Dribbble.listNextPage(mLastResponse.nextPageUrl).execute();

            if (mLastResponse != null) {
                return mLastResponse.data;
            }
        }

        return new ArrayList<>();
    }

    @Override
    public List<Shot> refresh() throws IOException, BadRequestException, TooManyRequestsException {
        mLastResponse = Dribbble.listShots(listParam, timeframeParam, sortParam).useCache(false).execute();
        return mLastResponse.data;
    }

    @Override
    public boolean hasItemsAvailable() {
        return Dribbble.listShots(listParam, timeframeParam, sortParam).canLoadImmediately();
    }

}
