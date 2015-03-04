package com.aluxian.drizzle.api.models;

import java.util.Date;

public final class Project extends Model {

    public final int id;
    public final String name;
    public final String description;
    public final int shotsCount;
    public final Date createdAt;
    public final User user;

    public Project(int id, String name, String description, int shotsCount, Date createdAt, User user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shotsCount = shotsCount;
        this.createdAt = createdAt;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;

        Project project = (Project) o;
        return id == project.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
