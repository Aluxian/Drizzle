package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for providers to load items from the Dribbble API.
 */
public abstract class ItemsProvider<T> {

    /** The last received response. */
    protected Dribbble.Response<List<T>> mLastResponse;

    /**
     * Load more items (either the default or the next ones).
     *
     * @return The list of items.
     * @throws IOException              For network related errors.
     * @throws BadRequestException      When the request is invalid.
     * @throws TooManyRequestsException When too many API requests in a short timeframe were made.
     */
    public List<T> load() throws IOException, BadRequestException, TooManyRequestsException {
        if (mLastResponse == null) {
            mLastResponse = getListRequest().execute();

            if (mLastResponse != null) {
                return mLastResponse.data;
            }
        } else if (mLastResponse.nextPageUrl != null) {
            mLastResponse = getNextPageRequest().execute();

            if (mLastResponse != null) {
                return mLastResponse.data;
            }
        }

        return new ArrayList<>();
    }

    /**
     * Download a fresh copy of the default items.
     *
     * @return The list of items.
     * @throws IOException              For network related errors.
     * @throws BadRequestException      When the request is invalid.
     * @throws TooManyRequestsException When too many API requests in a short timeframe were made.
     */
    public List<T> refresh() throws IOException, BadRequestException, TooManyRequestsException {
        mLastResponse = getListRequest().useCache(false).execute();
        return mLastResponse.data;
    }

    /**
     * @return Whether items are available immediately, i.e. they're cached and the user doesn't have to wait.
     */
    public boolean hasItemsAvailable() {
        return getListRequest().canLoadImmediately();
    }

    /**
     * @return An API request used to retrieve the default list of items.
     */
    protected abstract ApiRequest<List<T>> getListRequest();

    /**
     * @return An API request used to retrieve the next page if items.
     */
    protected abstract ApiRequest<List<T>> getNextPageRequest();

}
