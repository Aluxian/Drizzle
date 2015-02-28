package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Project;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ProjectsProvider extends ItemsProvider<Project> {

    /** The id of the shot whose buckets to provide. */
    private final int mShotId;

    public ProjectsProvider(int shotId) {
        mShotId = shotId;
    }

    @Override
    protected ApiRequest<List<Project>> getListRequest() {
        return Dribbble.listProjects(mShotId);
    }

    @Override
    protected ApiRequest<List<Project>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Project>>() {});
    }

}
