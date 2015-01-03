package com.aluxian.drizzle.api;

import com.aluxian.drizzle.api.models.Credentials;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Config;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.RequestBody;

import java.util.List;

public class Dribbble {

    public static ApiRequest<List<Shot>> listShots(Params.List list, Params.Timeframe timeframe, Params.Sort sort) {
        return new ApiRequest<List<Shot>>()
                .responseType(new TypeToken<List<Shot>>() {})
                .addQueryParam("per_page", String.valueOf(Config.RESULTS_PER_PAGE))
                .addQueryParam("list", list.apiValue)
                .addQueryParam("timeframe", timeframe.apiValue)
                .addQueryParam("sort", sort.apiValue)
                .path("/shots");
    }

    public static ApiRequest<Shot> getShot(int id) {
        return new ApiRequest<Shot>()
                .responseType(new TypeToken<Shot>() {})
                .path("/shots/" + id);
    }

        /*

        @POST("/shots")
        public Response createShot();

        @PUT("/shots/{id}")
        public Response updateShot(@Path("id") int shotId);

        */

    /*public static ApiRequest<Shot> deleteShot(int id) {
        return new ApiRequest<Shot>()
                .responseType(new TypeToken<Shot>() {})
                .url("/shots/" + id)
                .delete();
    }*/

    public static ApiRequest<Credentials> oauthToken(String code) {
        return new ApiRequest<Credentials>()
                .responseType(new TypeToken<Credentials>() {})
                .url("https://dribbble.com/oauth/token")
                .addQueryParam("client_id", Config.API_CLIENT_ID)
                .addQueryParam("client_secret", Config.API_CLIENT_SECRET)
                .addQueryParam("code", code)
                .post(null);
    }

    /**
     * Stores a parsed response from the Dribbble API.
     *
     * @param <T> The responseType of the expected response.
     */
    public static final class Response<T> {

        public final T data;
        public final String nextPageUrl;
        public final long receivedAt;

        public Response(T data, String nextPageUrl, long receivedAt) {
            this.data = data;
            this.nextPageUrl = nextPageUrl;
            this.receivedAt = receivedAt;
        }

    }

}
