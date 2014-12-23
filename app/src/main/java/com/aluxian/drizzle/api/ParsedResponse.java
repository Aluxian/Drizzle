package com.aluxian.drizzle.api;

public class ParsedResponse<T> {

    public final T data;
    public final String nextPageUrl;

    public ParsedResponse(T data, String nextPageUrl) {
        this.data = data;
        this.nextPageUrl = nextPageUrl;
    }

}
