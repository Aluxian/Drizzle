package com.aluxian.drizzle.api.models;

import java.util.Objects;

public final class Links extends Model {

    public final String web;
    public final String twitter;

    public Links(String web, String twitter) {
        this.web = web;
        this.twitter = twitter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Links links = (Links) o;

        return Objects.equals(web, links.web)
                && Objects.equals(twitter, links.twitter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(web, twitter);
    }

}
