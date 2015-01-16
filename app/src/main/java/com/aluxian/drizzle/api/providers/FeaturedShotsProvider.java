package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.models.Shot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides shots featured by the app.
 */
public class FeaturedShotsProvider extends ShotsProvider {

    public FeaturedShotsProvider(Dribbble dribbble) {
        super(dribbble);
    }

    @Override
    public List<Shot> load() throws IOException, BadRequestException, TooManyRequestsException {
        return new ArrayList<>();
    }

    @Override
    public List<Shot> refresh() throws IOException, BadRequestException, TooManyRequestsException {
        return new ArrayList<>();
    }

    @Override
    public boolean hasItemsAvailable() {
        return false;
    }

}
