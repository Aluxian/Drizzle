package com.aluxian.drizzle.api;

import com.aluxian.drizzle.api.models.Credentials;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.UserManager;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Dribbble {

    public static ApiRequest<List<Shot>> listShots(Params.List list, Params.Timeframe timeframe, Params.Sort sort) {
        return new ApiRequest<List<Shot>>()
                .responseType(new TypeToken<List<Shot>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .useCache(true)
                .queryParam("per_page", String.valueOf(Config.RESULTS_PER_PAGE))
                .queryParam("list", list.apiValue)
                .queryParam("timeframe", timeframe.apiValue)
                .queryParam("sort", sort.apiValue)
                .path("shots");
    }

    public static ApiRequest<Shot> getShot(int id) {
        return new ApiRequest<Shot>()
                .responseType(new TypeToken<Shot>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .useCache(true)
                .path("shots/" + id);
    }

    public static ApiRequest<List<Shot>> listBucketShots(int id) {
        return new ApiRequest<List<Shot>>()
                .responseType(new TypeToken<List<Shot>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .useCache(true)
                .queryParam("per_page", String.valueOf(Config.RESULTS_PER_PAGE))
                .path("buckets/" + id + "/shots");
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
                .queryParam("client_id", Config.API_CLIENT_ID)
                .queryParam("client_secret", Config.API_CLIENT_SECRET)
                .queryParam("code", code)
                .post(null);
    }

    public static ApiRequest<JsonObject> pixelsDribbbledCount() {
        return new ApiRequest<JsonObject>()
                .responseType(new TypeToken<JsonObject>() {})
                .useCache(true)
                .url(Config.KIMONO_API_URL);
    }

    public static ApiRequest<List<Shot>> listFollowing() {
        return new ApiRequest<List<Shot>>()
                .responseType(new TypeToken<List<Shot>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .useCache(true)
                .queryParam("per_page", String.valueOf(Config.RESULTS_PER_PAGE))
                .path("user/following/shots");
    }

    public static ApiRequest<List<Shot>> listNextPage(String nextPageUrl) {
        return new ApiRequest<List<Shot>>()
                .responseType(new TypeToken<List<Shot>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .useCache(true)
                .url(nextPageUrl);
    }

    /**
     * Stores a parsed response from the Dribbble API.
     *
     * @param <T> The type of the expected response data.
     */
    public static final class Response<T> {

        public final T data;
        public final String nextPageUrl;

        public Response(T data, String nextPageUrl) {
            this.data = data;
            this.nextPageUrl = nextPageUrl;
        }

    }

}
