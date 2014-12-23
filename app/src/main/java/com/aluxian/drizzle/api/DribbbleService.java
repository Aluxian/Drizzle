package com.aluxian.drizzle.api;

import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DribbbleService {

    @GET("/shots?per_page=50")
    public Response listShots(@Query("list") String list, @Query("timeframe") String timeframe, @Query("sort") String sort);

    @GET("/shots/{id}")
    public Response getShot(@Path("id") int shotId);

    @POST("/shots")
    public Response createShot();

    @PUT("/shots/{id}")
    public Response updateShot(@Path("id") int shotId);

    @DELETE("/shots/{id}")
    public Response deleteShot(@Path("id") int shotId);

}
