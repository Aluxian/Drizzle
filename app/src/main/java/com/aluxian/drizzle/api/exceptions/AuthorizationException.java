package com.aluxian.drizzle.api.exceptions;

public class AuthorizationException extends Exception {

    public AuthorizationException(String url) {
        super(url);
    }

}
