package com.aluxian.drizzle.api.models;

import java.util.Date;

public final class Team extends Model {

    public final int id;
    public final String name;
    public final String username;
    public final String htmlUrl;
    public final String avatarUrl;
    public final String bio;
    public final String location;
    public final Links links;
    public final int bucketsCount;
    public final int followersCount;
    public final int followingsCount;
    public final int likesCount;
    public final int membersCount;
    public final int projectsCount;
    public final int shotsCount;
    public final String type;
    public final boolean pro;
    public final String bucketsUrl;
    public final String followersUrl;
    public final String followingUrl;
    public final String likesUrl;
    public final String membersUrl;
    public final String shotsUrl;
    public final String teamShotsUrl;
    public final Date createdAt;
    public final Date updatedAt;

    public Team(int id, String name, String username, String htmlUrl, String avatarUrl, String bio, String location, Links links,
                int bucketsCount, int followersCount, int followingsCount, int likesCount, int membersCount, int projectsCount,
                int shotsCount, String type, boolean pro, String bucketsUrl, String followersUrl, String followingUrl, String likesUrl,
                String membersUrl, String shotsUrl, String teamShotsUrl, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.htmlUrl = htmlUrl;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.location = location;
        this.links = links;
        this.bucketsCount = bucketsCount;
        this.followersCount = followersCount;
        this.followingsCount = followingsCount;
        this.likesCount = likesCount;
        this.membersCount = membersCount;
        this.projectsCount = projectsCount;
        this.shotsCount = shotsCount;
        this.type = type;
        this.pro = pro;
        this.bucketsUrl = bucketsUrl;
        this.followersUrl = followersUrl;
        this.followingUrl = followingUrl;
        this.likesUrl = likesUrl;
        this.membersUrl = membersUrl;
        this.shotsUrl = shotsUrl;
        this.teamShotsUrl = teamShotsUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;

        Team team = (Team) o;
        return id == team.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
