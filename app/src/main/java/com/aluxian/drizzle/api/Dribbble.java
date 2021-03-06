package com.aluxian.drizzle.api;

import android.text.TextUtils;

import com.aluxian.drizzle.api.models.Attachment;
import com.aluxian.drizzle.api.models.Bucket;
import com.aluxian.drizzle.api.models.Comment;
import com.aluxian.drizzle.api.models.Credentials;
import com.aluxian.drizzle.api.models.Like;
import com.aluxian.drizzle.api.models.Project;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.UserManager;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Objects;

public final class Dribbble {

    // Suppress default constructor for noninstantiability
    private Dribbble() {
        throw new AssertionError();
    }

    /**
     * @param list      The list parameter to filter by.
     * @param timeframe The timeframe parameter to filter by.
     * @param sort      The sort parameter to filter by.
     * @return A filtered list of shots.
     */
    public static ApiRequest<List<Shot>> listShots(Params.List list, Params.Timeframe timeframe, Params.Sort sort) {
        return new ApiRequest<>(new TypeToken<List<Shot>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .queryParam("list", list.apiValue)
                .queryParam("timeframe", timeframe.apiValue)
                .queryParam("sort", sort.apiValue)
                .path("shots");
    }

    /**
     * @param id The id of the shot.
     * @return The shot for the given id.
     */
    public static ApiRequest<Shot> getShot(int id) {
        return new ApiRequest<>(new TypeToken<Shot>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + id);
    }

    /**
     * @param bucketId The id of the bucket whose shots to retrieve.
     * @return The list of shots contained in the bucket.
     */
    public static ApiRequest<List<Shot>> listBucketShots(int bucketId) {
        return new ApiRequest<>(new TypeToken<List<Shot>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("buckets/" + bucketId + "/shots");
    }

    /**
     * @return A list with only one shot, the one to use for the drawer cover.
     */
    public static ApiRequest<List<Shot>> getDrawerCoverShot() {
        return listBucketShots(Config.COVERS_BUCKET_ID).queryParam("per_page", "1");
    }

    /**
     * @param shotId The id of the shot whose attachments to get.
     * @return The list of attachments for the given shot id.
     */
    public static ApiRequest<List<Attachment>> listAttachments(int shotId) {
        return new ApiRequest<>(new TypeToken<List<Attachment>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/attachments");
    }

    /**
     * @param shotId The id of the shot whose rebounds to get.
     * @return The list of rebounds for the given shot id.
     */
    public static ApiRequest<List<Shot>> listRebounds(int shotId) {
        return new ApiRequest<>(new TypeToken<List<Shot>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/rebounds");
    }

    /**
     * @param shotId The id of the shot whose likes to get.
     * @return The list of likes for the given shot id.
     */
    public static ApiRequest<List<Like>> listLikes(int shotId) {
        return new ApiRequest<>(new TypeToken<List<Like>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/likes");
    }

    /**
     * @param shotId The id of the shot whose buckets to get.
     * @return The list of buckets for the given shot id.
     */
    public static ApiRequest<List<Bucket>> listBuckets(int shotId) {
        return new ApiRequest<>(new TypeToken<List<Bucket>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/buckets");
    }

    /**
     * @param shotId The id of the shot whose projects to get.
     * @return The list of projects for the given shot id.
     */
    public static ApiRequest<List<Project>> listProjects(int shotId) {
        return new ApiRequest<>(new TypeToken<List<Project>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/projects");
    }

    /**
     * @param userId The id of the user whose shots to get.
     * @return The list of shots for the given user id.
     */
    public static ApiRequest<List<Shot>> listUserShots(int userId) {
        return new ApiRequest<>(new TypeToken<List<Shot>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("users/" + userId + "/shots");
    }

        /*

        @POST("/shots")
        public Response createShot();

        @PUT("/shots/{id}")
        public Response updateShot(@Path("id") int shotId);

        */

    /*public static ApiRequest<Shot> deleteShot(int id) {
        return new ApiRequest<Shot>(new TypeToken<Shot>() {})
                .url("/shots/" + id)
                .delete();
    }*/

    /**
     * Exchange an OAuth code for an access token.
     *
     * @param code The code to exchange.
     * @return The new credentials to use.
     */
    public static ApiRequest<Credentials> exchangeOAuthToken(String code) {
        return new ApiRequest<>(new TypeToken<Credentials>() {})
                .url("https://dribbble.com/oauth/token")
                .queryParam("client_id", Config.API_CLIENT_ID)
                .queryParam("client_secret", Config.API_CLIENT_SECRET)
                .queryParam("code", code)
                .post();
    }

    /**
     * @return The list of shots from everyone the logged in use is following.
     */
    public static ApiRequest<List<Shot>> listShotsFromFollowing() {
        return new ApiRequest<>(new TypeToken<List<Shot>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("user/following/shots");
    }

    /**
     * Make a request to a 'next' URL to retrieve more data.
     *
     * @param nextPageUrl The URL to call.
     * @param typeToken   The type of data expected.
     * @param <T>         The type of data expected.
     * @return The corresponding data.
     */
    public static <T> ApiRequest<List<T>> listNextPage(String nextPageUrl, TypeToken<List<T>> typeToken) {
        return new ApiRequest<>(typeToken)
                .accessToken(UserManager.getInstance().getAccessToken())
                .url(nextPageUrl);
    }

    // Users

    // Users / Followers

    public static ApiRequest<Object> userFollowingUser(int userId) {
        return new ApiRequest<>(null)
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("user/following/" + userId);
    }

    public static ApiRequest<Object> followUser(int userId) {
        return new ApiRequest<>(null)
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("users/" + userId + "/follow")
                .put();
    }

    public static ApiRequest<Object> unfollowUser(int userId) {
        return new ApiRequest<>(null)
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("users/" + userId + "/follow")
                .delete();
    }

    // Shots

    // Shots / Comments

    /**
     * @param shotId The id of the shot whose comments to get.
     * @return The list of comments for the given shot id.
     */
    public static ApiRequest<List<Comment>> listCommentsForShot(int shotId) {
        return new ApiRequest<>(new TypeToken<List<Comment>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/comments");
    }

    public static ApiRequest<List<Like>> listLikesForComment(int shotId, int commentId) {
        return new ApiRequest<>(new TypeToken<List<Like>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/comments/" + commentId + "/likes");
    }

    // create comment

    public static ApiRequest<Comment> getComment(int shotId, int commentId) {
        return new ApiRequest<>(new TypeToken<Comment>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/comments/" + commentId);
    }

    // update comment

    public static ApiRequest<Object> deleteComment(int shotId, int commentId) {
        return new ApiRequest<>(null)
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/comments/" + commentId)
                .delete();
    }

    public static ApiRequest<Like> userLikesComment(int shotId, int commentId) {
        return new ApiRequest<>(new TypeToken<Like>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/comments/" + commentId + "/like");
    }

    public static ApiRequest<Like> likeComment(int shotId, int commentId) {
        return new ApiRequest<>(new TypeToken<Like>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/comments/" + commentId + "/like")
                .post();
    }

    public static ApiRequest<Object> unlikeComment(int shotId, int commentId) {
        return new ApiRequest<>(null)
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/comments/" + commentId + "/like")
                .delete();
    }

    // Shots / Likes

    public static ApiRequest<List<Like>> listLikesForShot(int shotId) {
        return new ApiRequest<>(new TypeToken<List<Like>>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/likes");
    }

    public static ApiRequest<Like> userLikesShot(int shotId) {
        return new ApiRequest<>(new TypeToken<Like>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/like");
    }

    public static ApiRequest<Like> likeShot(int shotId) {
        return new ApiRequest<>(new TypeToken<Like>() {})
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/like")
                .post();
    }

    public static ApiRequest<Object> unlikeShot(int shotId) {
        return new ApiRequest<>(null)
                .accessToken(UserManager.getInstance().getAccessToken())
                .path("shots/" + shotId + "/like")
                .delete();
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Response)) return false;

            Response response = (Response) o;

            return data.equals(response.data)
                    && TextUtils.equals(nextPageUrl, response.nextPageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, nextPageUrl);
        }

    }

}
