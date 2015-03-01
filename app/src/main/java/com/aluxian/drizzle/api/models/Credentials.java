package com.aluxian.drizzle.api.models;

import java.util.Objects;

public final class Credentials extends Model {

    public final String accessToken;
    public final String tokenType;
    public final String scope;

    public Credentials(String accessToken, String tokenType, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Credentials credentials = (Credentials) o;

        return Objects.equals(accessToken, credentials.accessToken)
                && Objects.equals(tokenType, credentials.tokenType)
                && Objects.equals(scope, credentials.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, tokenType, scope);
    }

}
