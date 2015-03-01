package com.aluxian.drizzle.api.models;

import java.util.Date;

public final class Like extends Model {

    public final int id;
    public final Date createdAt;
    public final User user;

    public Like(int id, Date createdAt, User user) {
        this.id = id;
        this.createdAt = createdAt;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Like like = (Like) o;
        return id == like.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
