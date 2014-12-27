package com.aluxian.drizzle.api;

import android.net.Uri;

import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.Utils;
import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirPutCallback;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.reflect.TypeToken;
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
import java.util.HashMap;
import java.util.Map;

public class ApiRequest<T> extends Request.Builder {

    private static OkHttpClient mOkHttpClient = new OkHttpClient();
    private static Gson mGson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();

    private Map<String, String> mQueryParams = new HashMap<>();
    private Type mResponseType;
    private String mPath;
    private String mUrl;

    public ApiRequest<T> addQueryParam(String name, String value) {
        mQueryParams.put(name, value);
        return this;
    }

    public ApiRequest<T> responseType(TypeToken<T> responseType) {
        mResponseType = responseType.getType();
        return this;
    }

    public ApiRequest<T> path(String path) {
        mPath = path;
        return this;
    }

    @Override
    public ApiRequest<T> url(String url) {
        mUrl = url;
        super.url(url);
        return this;
    }

    @Override
    public ApiRequest<T> url(URL url) {
        mUrl = url.toString();
        super.url(url);
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

    @Override
    public Request build() {
        // Add the auth header
        addHeader("Authorization", "Bearer " + Config.API_CLIENT_TOKEN);

        // Append the path
        if (mPath != null) {
            mUrl = Config.API_ENDPOINT + mPath;
        }

        // Append the query parameters
        Uri.Builder uri = Uri.parse(mUrl).buildUpon();
        for (String key : mQueryParams.keySet()) {
            String value = mQueryParams.get(key);
            if (value != null) {
                uri.appendQueryParameter(key, value);
            }
        }

        url(uri.build().toString());
        return super.build();
    }

    /**
     * Execute the request and return the result.
     *
     * @return A ParsedResponse object.
     */
    public ParsedResponse<T> execute() {
        if (mResponseType == null) {
            throw new IllegalArgumentException("responseType is null");
        }

        Request request = build();
        String requestHash = request.method() + request.urlString();
        ParsedResponse<T> parsedResponse = null;

        // Try to get a valid response from the cache
        try {
            String cached = Reservoir.get(requestHash, String.class);

            if (cached != null) {
                Log.d("Loaded " + requestHash + " from cache");

                parsedResponse = mGson.fromJson(cached, new TypeToken<ParsedResponse<JsonElement>>() {}.getType());

                if (new Date().getTime() - parsedResponse.receivedAt > Config.CACHE_TIMEOUT) {
                    parsedResponse = null;
                } else {
                    // Recover data
                    //noinspection unchecked
                    parsedResponse = new ParsedResponse<>(
                            (T) mGson.fromJson((JsonElement) parsedResponse.data, mResponseType),
                            parsedResponse.nextPageUrl,
                            parsedResponse.receivedAt);
                }
            }
        } catch (Exception e) {
            Log.e(e);
        }

        // If nothing valid was found in cache, make the request again
        if (parsedResponse == null) {
            try {
                Response response = mOkHttpClient.newCall(request).execute();
                String body = response.body().string();

                // TODO: Improve error handling
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response + ". Response json: " + body);
                }

                // Parse the response
                T data = mGson.fromJson(body, mResponseType);
                String nextPageUrl = Utils.extractNextUrl(response.headers().get("Link"));
                parsedResponse = new ParsedResponse<>(data, nextPageUrl, new Date().getTime());
            } catch (IOException e) {
                Log.e(e);
            }

            // Cache the response
            Reservoir.putAsync(requestHash, mGson.toJson(parsedResponse), new ReservoirPutCallback() {
                @Override
                public void onSuccess() {}

                @Override
                public void onFailure(Exception e) {
                    Log.e(e);
                }
            });
        }

        return parsedResponse;
    }

}
