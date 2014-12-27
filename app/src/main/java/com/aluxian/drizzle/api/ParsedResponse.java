package com.aluxian.drizzle.api;

/**
 * Stores a parsed response from the Dribbble API.
 *
 * @param <T> The responseType of the expected response.
 */
public final class ParsedResponse<T> {

    public final T data;
    public final String nextPageUrl;
    public final long receivedAt;

    public ParsedResponse(T data, String nextPageUrl, long receivedAt) {
        this.data = data;
        this.nextPageUrl = nextPageUrl;
        this.receivedAt = receivedAt;
    }

}
