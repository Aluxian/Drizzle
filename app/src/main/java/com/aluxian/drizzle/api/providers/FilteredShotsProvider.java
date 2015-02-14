package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.api.models.Shot;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Provides shots from the /shots endpoint, where items are filtered by the list, timeframe and sort parameters.
 */
public class FilteredShotsProvider extends ItemsProvider<Shot> {

    public final Params.List listParam;
    public Params.Timeframe timeframeParam = Params.Timeframe.NOW;
    public Params.Sort sortParam = Params.Sort.POPULAR;

    public FilteredShotsProvider(Params.List listParam) {
        this.listParam = listParam;
    }

    @Override
    protected ApiRequest<List<Shot>> getListRequest() {
        return Dribbble.listShots(listParam, timeframeParam, sortParam);
    }

    @Override
    protected ApiRequest<List<Shot>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Shot>>() {});
    }

}
