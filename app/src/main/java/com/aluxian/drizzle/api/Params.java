package com.aluxian.drizzle.api;

import android.content.Context;

import com.aluxian.drizzle.R;

import java.util.Arrays;

/**
 * Holds enums with possible parameter values used across the Dribbble API.
 */
public class Params {

    public enum List {
        ANY(null),
        TEAM_SHOTS("teams"),
        DEBUTS("debuts"),
        PLAYOFFS("playoffs"),
        REBOUNDS("rebounds"),
        ANIMATED_GIFS("animated"),
        WITH_ATTACHMENTS("attachments");

        public final String apiValue;

        List(String apiValue) {
            this.apiValue = apiValue;
        }

        public String getHumanReadableValue(Context context) {
            return context.getResources().getStringArray(R.array.filter_list_options)[Arrays.asList(List.values()).indexOf(this)];
        }
    }

    public enum Timeframe {
        NOW(null),
        THIS_PAST_WEEK("week"),
        THIS_PAST_MONTH("month"),
        THIS_PAST_YEAR("year"),
        ALL_TIME("ever");

        public final String apiValue;

        Timeframe(String apiValue) {
            this.apiValue = apiValue;
        }
    }

    public enum Sort {
        POPULAR(null),
        RECENT("recent"),
        MOST_VIEWED("views"),
        MOST_COMMENTED("comments");

        public final String apiValue;

        Sort(String apiValue) {
            this.apiValue = apiValue;
        }
    }

}
