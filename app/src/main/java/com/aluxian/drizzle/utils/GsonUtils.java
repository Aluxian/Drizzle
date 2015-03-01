package com.aluxian.drizzle.utils;

import com.google.gson.Gson;

public class GsonUtils {

    public static <T> T clone(T source, Class<T> clazz) {
        Gson gson = new Gson();
        String json = gson.toJson(source);
        return gson.fromJson(json, clazz);
    }

}
