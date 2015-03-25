package com.aluxian.drizzle.api;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.aluxian.drizzle.App;
import com.aluxian.drizzle.api.exceptions.BadCredentialsException;
import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.UserManager;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Request builder for Dribbble API requests. Can be used for any other type of request too.
 *
 * @param <T> The type of the expected response.
 */
public class ApiRequest<T> extends Request.Builder {

    /** RegExp pattern to extract the 'next' url from a Link header. */
    private static final Pattern LINK_NEXT_URL_PATTERN = Pattern.compile(".*<([^>]*)>; rel=\"next\".*");

    private TypeToken<T> mResponseType;
    private String mUrl = Config.API_ENDPOINT;

    /**
     * @param responseType A TypeToken used to get the type of the expected response for Gson deserialization.
     */
    public ApiRequest(TypeToken<T> responseType) {
        mResponseType = responseType;
    }

    /**
     * Adds a query parameter to the url of the request.
     *
     * @param name  The name of the parameter.
     * @param value The value of the parameter.
     * @return This instance.
     */
    public ApiRequest<T> queryParam(String name, String value) {
        if (value != null) {
            Uri.Builder uri = Uri.parse(mUrl).buildUpon();
            uri.appendQueryParameter(name, value);
            url(uri.build().toString());
        }

        return this;
    }

    /**
     * @param token The access token to use for this request.
     * @return This instance.
     */
    public ApiRequest<T> accessToken(String token) {
        if (token != null) {
            header("Authorization", "Bearer " + token);
        }

        return this;
    }

    /**
     * Specify a path segmented to be appended to the url.
     *
     * @param path A path to append to the url.
     * @return This instance.
     */
    public ApiRequest<T> path(String path) {
        Uri.Builder uri = Uri.parse(mUrl).buildUpon();
        uri.appendEncodedPath(path);
        url(uri.build().toString());
        return this;
    }

    @Override
    public ApiRequest<T> url(String url) {
        mUrl = url;
        super.url(mUrl);
        return this;
    }

    @Override
    public ApiRequest<T> url(URL url) {
        url(url.toString());
        return this;
    }

    @Override
    public ApiRequest<T> header(String name, String value) {
        super.header(name, value);
        return this;
    }

    @Override
    public ApiRequest<T> addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public ApiRequest<T> removeHeader(String name) {
        super.removeHeader(name);
        return this;
    }

    @Override
    public ApiRequest<T> headers(Headers headers) {
        super.headers(headers);
        return this;
    }

    @Override
    public ApiRequest<T> cacheControl(CacheControl cacheControl) {
        super.cacheControl(cacheControl);
        return this;
    }

    @Override
    public ApiRequest<T> get() {
        super.get();
        return this;
    }

    @Override
    public ApiRequest<T> head() {
        super.head();
        return this;
    }

    @Override
    public ApiRequest<T> post(RequestBody body) {
        super.post(body);
        return this;
    }

    /**
     * Mark this as a POST request.
     *
     * @return This instance.
     */
    public ApiRequest<T> post() {
        return post(null);
    }

    @Override
    public ApiRequest<T> patch(RequestBody body) {
        super.patch(body);
        return this;
    }

    @Override
    public ApiRequest<T> put(RequestBody body) {
        super.put(body);
        return this;
    }

    /**
     * Mark this as a PUT request.
     *
     * @return This instance.
     */
    public ApiRequest<T> put() {
        return put(null);
    }

    @Override
    public ApiRequest<T> delete() {
        super.delete();
        return this;
    }

    @Override
    public ApiRequest<T> method(String method, RequestBody body) {
        super.method(method, body);
        return this;
    }

    @Override
    public ApiRequest<T> tag(Object tag) {
        super.tag(tag);
        return this;
    }

    /**
     * @return Whether the request can be fulfilled immediately (from cache).
     */
    public boolean canLoadImmediately() {
        Cache cache = App.OK_HTTP_CLIENT.getCache();

        if (cache != null) {
            try {
                Iterator<String> urls = cache.urls();

                while (urls.hasNext()) {
                    if (TextUtils.equals(urls.next(), build().urlString())) {
                        return true;
                    }
                }
            } catch (IOException e) {
                Log.e(e);
            }
        }

        return false;
    }

    /**
     * Execute the request and return the result. Cache may be used.
     *
     * @return A {@link com.aluxian.drizzle.api.Dribbble.Response} object.
     * @throws IOException              For network related errors.
     * @throws BadRequestException      When the request is invalid.
     * @throws TooManyRequestsException When too many API requests in a short timeframe were made.
     */
    public Dribbble.Response<T> execute() throws IOException, BadRequestException, TooManyRequestsException {
        Request request = build();
        Dribbble.Response<T> response = null;

        try {
            response = getFromNetwork(request);
        } catch (BadCredentialsException e) {
            if (UserManager.getInstance().isAuthenticated()) {
                UserManager.getInstance().clearAccessToken();
                // TODO: show bottom toast: you have been signed out
            } else {
                throw new BadRequestException(401, e.getMessage());
            }
        }

        return response;
    }

    /**
     * Execute the request asynchronously and send the result to the callback. Cache may be used.
     *
     * @param callback A callback instance to be called when execution is complete.
     */
    public void execute(Callback<T> callback) {
        ApiRequest<T> self = this;
        new AsyncTask<Void, Void, Dribbble.Response<T>>() {

            private Exception mException;

            @Override
            protected Dribbble.Response<T> doInBackground(Void... params) {
                try {
                    return self.execute();
                } catch (IOException | BadRequestException | TooManyRequestsException e) {
                    mException = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Dribbble.Response<T> result) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError(mException);
                }
            }

        }.execute();
    }

    /**
     * Execute the given network request and parse the response.
     *
     * @param request The network request to execute.
     * @return A {@link com.aluxian.drizzle.api.Dribbble.Response} object.
     * @throws IOException              For network related errors.
     * @throws BadRequestException      When the request is invalid.
     * @throws TooManyRequestsException When too many API requests in a short timeframe were made.
     */
    private Dribbble.Response<T> getFromNetwork(Request request)
            throws IOException, BadCredentialsException, BadRequestException, TooManyRequestsException {
        Response httpResponse = App.OK_HTTP_CLIENT.newCall(request).execute();
        String body = httpResponse.body().string();

        // Handle errors
        if (!httpResponse.isSuccessful()) {
            Log.d(httpResponse.code(), body);

            switch (httpResponse.code()) {
                case 401:
                    throw new BadCredentialsException(body);
                case 429:
                    throw new TooManyRequestsException(body);
                default:
                    throw new BadRequestException(httpResponse.code(), body);
            }
        }

        // Nothing to parse
        if (mResponseType == null) {
            return new Dribbble.Response<>(null, null);
        }

        // Parse the response
        T data = App.GSON.fromJson(body, mResponseType.getType());
        String nextPageUrl = extractNextPageUrl(httpResponse.headers().get("Link"));
        return new Dribbble.Response<>(data, nextPageUrl);
    }

    /**
     * Parses the value of a Link header in order to extract the url with rel="next".
     *
     * @param linkHeader The value of the Link header to parse.
     * @return The url with rel="next" or null if not found.
     */
    private static String extractNextPageUrl(String linkHeader) {
        if (linkHeader != null) {
            Matcher matcher = LINK_NEXT_URL_PATTERN.matcher(linkHeader);

            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    /**
     * Callback used for async executions.
     *
     * @param <T> The type of the response data.
     */
    public static interface Callback<T> {

        /**
         * Called when a request execution is completed successfully.
         *
         * @param response The response of the execution.
         */
        public void onSuccess(Dribbble.Response<T> response);

        /**
         * Called when an error happens.
         *
         * @param e The exception thrown during execution.
         */
        public void onError(Exception e);

    }

}
