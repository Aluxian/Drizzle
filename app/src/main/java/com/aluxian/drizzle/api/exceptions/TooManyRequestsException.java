package com.aluxian.drizzle.api.exceptions;

/**
 * Thrown for requests that return HTTP code 429 (too many requests).
 */
public class TooManyRequestsException extends Exception {

    /**
     * @param body The body of the response.
     */
    public TooManyRequestsException(String body) {
        super(body);
    }

}
