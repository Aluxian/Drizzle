package com.aluxian.drizzle.api.models;

import java.util.Date;
import java.util.List;

public final class Comment {

    public final int id;
    public final String body;
    public final int likesCount;
    public final Date createdAt;
    public final User user;

    public Comment(int id, String body, int likesCount, Date createdAt, User user) {
        this.id = id;
        this.body = body;
        this.likesCount = likesCount;
        this.createdAt = createdAt;
        this.user = user;
    }

}
