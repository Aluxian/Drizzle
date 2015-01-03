package com.aluxian.drizzle.api.models;

import java.util.Date;
import java.util.List;

public final class Shot {

    public final int id;
    public final String title;
    public final String description;
    public final int width;
    public final int height;
    public final Images images;
    public final int viewsCount;
    public final int likesCount;
    public final int commentsCount;
    public final int attachmentsCount;
    public final int reboundsCount;
    public final int bucketsCount;
    public final Date createdAt;
    public final Date updatedAt;
    public final String htmlUrl;
    public final String attachmentsUrl;
    public final String bucketsUrl;
    public final String commentsUrl;
    public final String likesUrl;
    public final String projectsUrl;
    public final String reboundsUrl;
    public final List<String> tags;
    public final User user;
    public final Team team;

    public Shot(int id, String title, String description, int width, int height, Images images, int viewsCount, int likesCount, int
            commentsCount, int attachmentsCount, int reboundsCount, int bucketsCount, Date createdAt, Date updatedAt, String htmlUrl,
                String attachmentsUrl, String bucketsUrl, String commentsUrl, String likesUrl, String projectsUrl, String reboundsUrl,
                List<String> tags, User user, Team team) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.width = width;
        this.height = height;
        this.images = images;
        this.viewsCount = viewsCount;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.attachmentsCount = attachmentsCount;
        this.reboundsCount = reboundsCount;
        this.bucketsCount = bucketsCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.htmlUrl = htmlUrl;
        this.attachmentsUrl = attachmentsUrl;
        this.bucketsUrl = bucketsUrl;
        this.commentsUrl = commentsUrl;
        this.likesUrl = likesUrl;
        this.projectsUrl = projectsUrl;
        this.reboundsUrl = reboundsUrl;
        this.tags = tags;
        this.user = user;
        this.team = team;
    }

    public static final class Images {

        public final String hidpi;
        public final String normal;
        public final String teaser;

        public Images(String hidpi, String normal, String teaser) {
            this.hidpi = hidpi;
            this.normal = normal;
            this.teaser = teaser;
        }

    }

}
