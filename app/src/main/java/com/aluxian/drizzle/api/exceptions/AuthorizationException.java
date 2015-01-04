package com.aluxian.drizzle.api.exceptions;

/**
 * Thrown for errors that appear in the authorization flow.
 */
public class AuthorizationException extends Exception {

    /**
     * @param url The resulting callback url. Contains information about the error.
     */
    public AuthorizationException(String url) {
        super(url);
    }

}
