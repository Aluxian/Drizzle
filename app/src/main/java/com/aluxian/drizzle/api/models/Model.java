package com.aluxian.drizzle.api.models;

import com.google.gson.Gson;

/**
 * Base class for models.
 */
public abstract class Model {

    private static final Gson GSON = new Gson();

    /**
     * Convert this model into a JSON string.
     *
     * @return A JSON string.
     */
    public String toJson() {
        return GSON.toJson(this);
    }

}
