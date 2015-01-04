package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Shot;

import java.io.IOException;
import java.util.List;

/**
 * Provides shots featured by the app.
 */
public class FeaturedShotsProvider implements ShotsProvider {

    @Override
    public List<Shot> load() throws IOException, BadRequestException, TooManyRequestsException {
        return null;
    }

    @Override
    public List<Shot> refresh() throws IOException, BadRequestException, TooManyRequestsException {
        return null;
    }

    @Override
    public boolean hasItemsAvailable() {
        return false;
    }

}
