package com.aluxian.drizzle.api;

import android.net.Uri;

import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.DiskCache;
import com.iainconnor.objectcache.PutCallback;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.iainconnor.objectcache.CacheManager.ExpiryTimes;

/**
 * Request builder for Dribbble API requests. Can be used for any other URL too, but the default is the Dribbble API endpoint.
 *
 * @param <T> The type of the expected response.
 */
public class ApiRequest<T> extends Request.Builder {

    private static final Pattern LINK_NEXT_URL_PATTERN = Pattern.compile("<([^>]*)>; rel=\"next\"");

    private static CacheManager mCacheManager;
    private static OkHttpClient mHttpClient = new OkHttpClient();
    private static Gson mGson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();

    private boolean mUseCache;
    private Type mResponseType;
    private String mUrl = Config.API_ENDPOINT;

    /**
     * @param diskCache A DiskCache instance to use for cache storage.
     */
    public static void diskCache(DiskCache diskCache) {
        if (diskCache != null) {
            mCacheManager = CacheManager.getInstance(diskCache);
        }
    }

    /**
     * @param useCache Whether the request can be loaded from the cache.
     * @return This instance.
     */
    public ApiRequest<T> useCache(boolean useCache) {
        mUseCache = useCache;
        return this;
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
     * @param responseType A TypeToken used to get the type of the expected response for Gson deserialization.
     * @return This instance.
     */
    public ApiRequest<T> responseType(TypeToken<T> responseType) {
        mResponseType = responseType.getType();
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
     * @return Whether the request can be fulfilled immediately (from the cache).
     */
    public boolean canLoadImmediately() {
        Type responseType = new TypeToken<Dribbble.Response>() {}.getType();
        return mCacheManager != null && mCacheManager.get(build().urlString(), Dribbble.Response.class, responseType) != null;
    }

    /**
     * Execute the request and return the result. Cache may be used.
     *
     * @return A Dribbble.Response object.
     * @throws IOException              For network related errors.
     * @throws BadRequestException      When the request is invalid.
     * @throws TooManyRequestsException When too many API requests in a short timeframe were made.
     */
    public Dribbble.Response<T> execute() throws IOException, BadRequestException, TooManyRequestsException {
        if (mResponseType == null) {
            throw new IllegalArgumentException("responseType is null");
        }

        Request request = build();
        Dribbble.Response<T> response = null;

        if (mUseCache && request.method().equalsIgnoreCase("GET")) {
            response = getFromCache(request.urlString());
        }

        if (response == null) {
            response = getFromNetwork(request);
        }

        return response;
    }

    /**
     * Try to get a response object from the cache.
     *
     * @param key The cache key of the response.
     * @return The Dribbble.Response object corresponding to the given key if found, otherwise null.
     */
    @SuppressWarnings("unchecked")
    private Dribbble.Response<T> getFromCache(String key) {
        if (mCacheManager != null) {
            Dribbble.Response<JsonElement> cached = (Dribbble.Response<JsonElement>)
                    mCacheManager.get(key, Dribbble.Response.class, new TypeToken<Dribbble.Response<JsonElement>>() {}.getType());

            if (cached != null) {
                Log.d("Loaded " + key + " from cache");
                return new Dribbble.Response<>((T) mGson.fromJson(cached.data, mResponseType), cached.nextPageUrl);
            }
        }

        return null;
    }

    /**
     * Execute the given network request and parse the response.
     *
     * @param request The network request to execute.
     * @return A Dribbble.Response object.
     * @throws IOException              For network related errors.
     * @throws BadRequestException      When the request is invalid.
     * @throws TooManyRequestsException When too many API requests in a short timeframe were made.
     */
    private Dribbble.Response<T> getFromNetwork(Request request) throws IOException, BadRequestException, TooManyRequestsException {
        String requestHash = request.urlString();
        Log.d("Loading " + requestHash + " from the API");

        Response httpResponse = mHttpClient.newCall(request).execute();
        String body = httpResponse.body().string();

        // Handle errors
        if (!httpResponse.isSuccessful()) {
            switch (httpResponse.code()) {
                case 429:
                    throw new TooManyRequestsException(body);

                default:
                    throw new BadRequestException(httpResponse.code(), body);
            }
        }

        // Parse the response
        T data = mGson.fromJson(body, mResponseType);
        String nextPageUrl = extractNextPageUrl(httpResponse.headers().get("Link"));
        Dribbble.Response<T> dribbbleResponse = new Dribbble.Response<>(data, nextPageUrl);

        // Cache the response
        if (mCacheManager != null && request.method().equalsIgnoreCase("GET")) {
            mCacheManager.putAsync(requestHash, dribbbleResponse, ExpiryTimes.ONE_HOUR.asSeconds(), false, new PutCallback() {
                @Override
                public void onSuccess() {
                    Log.d("Cached " + requestHash);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(e);
                }
            });
        }

        return dribbbleResponse;
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

}
