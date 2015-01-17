package com.aluxian.drizzle.api.exceptions;

public class BadRequestException extends Exception {

    /** The HTTP code of the request where this exception occurred. */
    public final int responseCode;

    /**
     * @param code The HTTP code of the response.
     * @param body The body of the response.
     */
    public BadRequestException(int code, String body) {
        super("Unexpected code " + code + ". Response json: " + body);
        responseCode = code;
    }

}
