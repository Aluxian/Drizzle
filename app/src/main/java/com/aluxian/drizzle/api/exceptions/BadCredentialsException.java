package com.aluxian.drizzle.api.exceptions;

/**
 * Thrown for API requests that respond with 401 Unauthorized.
 */
public class BadCredentialsException extends Exception {

    /**
     * @param body The body of the response.
     */
    public BadCredentialsException(String body) {
        super(body);
    }

}
