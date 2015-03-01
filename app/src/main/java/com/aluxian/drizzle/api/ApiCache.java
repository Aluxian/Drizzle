package com.aluxian.drizzle.api;

import android.content.Context;

import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.ObjectCache;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;

/**
 * Uses ObjectCache to cache API requests.
 */
public class ApiCache {

    private ObjectCache mObjectCache;

    public ApiCache(Context context) throws IOException {
        mObjectCache = new ObjectCache(new File(context.getCacheDir(), ApiCache.class.getName()));
    }

    @SuppressWarnings("unchecked")
    public <T> Dribbble.Response<T> get(String key, TypeToken<T> dataType) {
        Dribbble.Response<JsonElement> cached = mObjectCache.get(key, new TypeToken<Dribbble.Response<JsonElement>>() {});

        if (cached != null) {
            Log.d("Loaded " + key + " from cache");
            return new Dribbble.Response<>((T) ApiRequest.GSON.fromJson(cached.data, dataType.getType()), cached.nextPageUrl);
        }

        return null;
    }

    public void put(String key, Dribbble.Response response) {
        mObjectCache.put(key, response);
    }

    public void put(String key, Dribbble.Response response, Long timeToLive) {
        mObjectCache.put(key, response, timeToLive);
    }

    public void remove(String key) {
        mObjectCache.remove(key);
    }

    public void clear() {
        mObjectCache.clear();
    }

    public void close() {
        mObjectCache.close();
    }

    public boolean containsNotExpired(String key) {
        return mObjectCache.containsNotExpired(key);
    }

}
