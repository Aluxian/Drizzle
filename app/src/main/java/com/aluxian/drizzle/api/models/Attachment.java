package com.aluxian.drizzle.api.models;

import java.util.Date;

public final class Attachment extends Model {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment attachment = (Attachment) o;
        return id == attachment.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
