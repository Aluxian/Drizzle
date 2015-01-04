package com.aluxian.drizzle.api.exceptions;

public class BadRequestException extends Exception {

    /**
     * @param code The HTTP code of the response.
     * @param body The body of the response.
     */
    public BadRequestException(int code, String body) {
        super("Unexpected code " + code + ". Response json: " + body);
    }

}
