package com.aluxian.drizzle.api.models;

import java.util.Date;

public final class Comment extends Model {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;

        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
