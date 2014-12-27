package com.aluxian.drizzle.api;

import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Config;
import com.google.gson.reflect.TypeToken;

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
                .url("/shots/" + id);
    }

        /*

        @POST("/shots")
        public Response createShot();

        @PUT("/shots/{id}")
        public Response updateShot(@Path("id") int shotId);

        */

    public static ApiRequest<Shot> deleteShot(int id) {
        return new ApiRequest<Shot>()
                .responseType(new TypeToken<Shot>() {})
                .url("/shots/" + id)
                .delete();
    }

}
