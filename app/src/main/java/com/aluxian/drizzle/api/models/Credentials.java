package com.aluxian.drizzle.api.models;

public final class Credentials {

    public final String accessToken;
    public final String tokenType;
    public final String scope;

    public Credentials(String accessToken, String tokenType, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.scope = scope;
    }

}
