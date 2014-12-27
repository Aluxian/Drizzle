package com.aluxian.drizzle.api;

/**
 * Holds enums with possible parameter values used across the Dribbble API.
 */
public class Params {

    public enum List {
        ANY(null, "Any"),
        TEAM_SHOTS("teams", "Team Shots"),
        DEBUTS("debuts", "Debuts"),
        PLAYOFFS("playoffs", "Playoffs"),
        REBOUNDS("rebounds", "Rebounds"),
        ANIMATED_GIFS("animated", "Animated GIFs"),
        WITH_ATTACHMENTS("attachments", "Shots with Attachments");

        public final String apiValue;
        public final String humanReadableValue;

        List(String apiValue, String humanReadableValue) {
            this.apiValue = apiValue;
            this.humanReadableValue = humanReadableValue;
        }
    }

    public enum Timeframe {
        NOW(null, "Now"),
        THIS_PAST_WEEK("week", "This Past Week"),
        THIS_PAST_MONTH("month", "This Past Month"),
        THIS_PAST_YEAR("year", "This Past Year"),
        ALL_TIME("ever", "All Time");

        public final String apiValue;
        public final String humanReadableValue;

        Timeframe(String apiValue, String humanReadableValue) {
            this.apiValue = apiValue;
            this.humanReadableValue = humanReadableValue;
        }
    }

    public enum Sort {
        POPULAR(null, "Popular"),
        RECENT("recent", "Recent"),
        MOST_VIEWED("views", "Most Viewed"),
        MOST_COMMENTED("comments", "Most Commented");

        public final String apiValue;
        public final String humanReadableValue;

        Sort(String apiValue, String humanReadableValue) {
            this.apiValue = apiValue;
            this.humanReadableValue = humanReadableValue;
        }
    }

}
