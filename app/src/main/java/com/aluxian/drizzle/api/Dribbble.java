package com.aluxian.drizzle.api;

import com.aluxian.drizzle.BuildConfig;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;

import static retrofit.RestAdapter.LogLevel;

public class Dribbble {

    private static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();

    private static GsonConverter gsonConverter = new GsonConverter(gson);
    private static OkHttpClient httpClient = new OkHttpClient();

    public static DribbbleService api = new RestAdapter.Builder()
            .setEndpoint(Config.API_ENDPOINT)
            .setLogLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE)
            .setConverter(gsonConverter)
            .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", "Bearer " + Config.API_CLIENT_TOKEN);
                }
            })
            .build()
            .create(DribbbleService.class);

    public static <T> ParsedResponse<T> parse(Response response, TypeToken typeToken) {
        try {
            //noinspection unchecked
            T data = (T) gsonConverter.fromBody(response.getBody(), typeToken.getType());
            String nextPageUrl = extractNextUrl(getLinkHeader(response.getHeaders()));

            return new ParsedResponse<>(data, nextPageUrl);
        } catch (ConversionException e) {
            Log.e(e);
        }

        return null;
    }

    public static <T> ParsedResponse<T> request(String url, TypeToken typeToken) {
        try {
            Request request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + Config.API_CLIENT_TOKEN).build();
            com.squareup.okhttp.Response response = httpClient.newCall(request).execute();

            JsonElement body = new JsonParser().parse(response.body().string());

            if (body.isJsonArray()) {
                T data = gson.fromJson(body, typeToken.getType());
                String nextPageUrl = extractNextUrl(response.headers().get("Link"));

                return new ParsedResponse<>(data, nextPageUrl);
            } else {
                // TODO error message
            }
        } catch (IOException e) {
            Log.e(e);
        }

        return null;
    }

    /**
     * Parses the value of a Link header in order to extract the url with rel="next".
     *
     * @param linkHeader The value of the Link header to parse.
     * @return The url with rel="next" or null if not found.
     */
    private static String extractNextUrl(String linkHeader) {
        if (linkHeader != null) {
            String[] links = linkHeader.split(",");

            for (String link : links) {
                String[] segments = link.split(";");
                if (segments.length < 2) {
                    continue;
                }

                String[] relPart = segments[1].trim().split("=");
                if (relPart.length < 2) {
                    continue;
                }

                String relValue = relPart[1];
                if (relValue.startsWith("\"") && relValue.endsWith("\"")) {
                    relValue = relValue.substring(1, relValue.length() - 1);
                }

                if ("next".equals(relValue)) {
                    String linkPart = segments[0].trim();

                    if (linkPart.startsWith("<") && linkPart.endsWith(">")) {
                        return linkPart.substring(1, linkPart.length() - 1);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Searches the list of headers to find the one named Link.
     *
     * @param headers The list of headers to look into.
     * @return The value of the Link header, if found in the list, otherwise null.
     */
    private static String getLinkHeader(List<Header> headers) {
        for (Header header : headers) {
            if ("Link".equals(header.getName())) {
                return header.getValue();
            }
        }

        return null;
    }

}
