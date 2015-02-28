package com.aluxian.drizzle.api.providers;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Attachment;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class AttachmentsProvider extends ItemsProvider<Attachment> {

    /** The id of the shot whose attachments to provide. */
    private final int mShotId;

    public AttachmentsProvider(int shotId) {
        mShotId = shotId;
    }

    @Override
    protected ApiRequest<List<Attachment>> getListRequest() {
        return Dribbble.listAttachments(mShotId);
    }

    @Override
    protected ApiRequest<List<Attachment>> getNextPageRequest() {
        return Dribbble.listNextPage(mLastResponse.nextPageUrl, new TypeToken<List<Attachment>>() {});
    }

}
