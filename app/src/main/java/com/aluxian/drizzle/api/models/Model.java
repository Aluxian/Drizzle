package com.aluxian.drizzle.api.models;

import com.aluxian.drizzle.utils.FunctionA;
import com.google.gson.JsonObject;

import static com.aluxian.drizzle.App.GSON;

/**
 * Base class for models.
 */
public abstract class Model {

    /**
     * Convert this model into a JSON string.
     *
     * @return A JSON string.
     */
    public String toJson() {
        return GSON.toJson(this);
    }

    /**
     * Convert this model into a JSON string.
     *
     * @return A JSON object.
     */
    public JsonObject toJsonObject() {
        return GSON.toJsonTree(this).getAsJsonObject();
    }

    /**
     * Clone this model and update some fields. Used to edit models without giving up immutability or {@code public
     * final} fields.
     */
    public <T extends Model> T cloneAndUpdate(FunctionA<JsonObject> updateFunction) {
        JsonObject json = GSON.toJsonTree(this).getAsJsonObject();
        updateFunction.apply(json);
        return GSON.<T>fromJson(json, getClass());
    }

}
