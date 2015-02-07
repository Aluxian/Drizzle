package com.aluxian.drizzle.api.models;

import java.util.Date;

public final class Attachment {

    public final int id;
    public final String url;
    public final String thumbnailUrl;
    public final int size;
    public final String contentType;
    public final int viewsCount;
    public final Date createdAt;

    public Attachment(int id, String url, String thumbnailUrl, int size, String contentType, int viewsCount, Date createdAt) {
        this.id = id;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.size = size;
        this.contentType = contentType;
        this.viewsCount = viewsCount;
        this.createdAt = createdAt;
    }

}
