package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Shot;

import java.io.IOException;
import java.util.List;

/**
 * Base class for providers used by an adapter to load items from the Dribbble API.
 */
public abstract class ShotsProvider {

    /** The last received response. */
    protected Dribbble.Response<List<Shot>> mLastResponse;

    /**
     * Load more items (either the default or the next ones).
     *
     * @return The list of items.
     * @throws IOException              For network related errors.
     * @throws BadRequestException      When the request is invalid.
     * @throws TooManyRequestsException When too many API requests in a short timeframe were made.
     */
    public abstract List<Shot> load() throws IOException, BadRequestException, TooManyRequestsException;

    /**
     * Download a fresh copy of the default items.
     *
     * @return The list of items.
     * @throws IOException              For network related errors.
     * @throws BadRequestException      When the request is invalid.
     * @throws TooManyRequestsException When too many API requests in a short timeframe were made.
     */
    public abstract List<Shot> refresh() throws IOException, BadRequestException, TooManyRequestsException;

    /**
     * @return Whether items are available immediately, i.e. they're cached and the user doesn't have to wait.
     */
    public abstract boolean hasItemsAvailable();

}
