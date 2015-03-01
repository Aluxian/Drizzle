package com.aluxian.drizzle.utils;

public class Config {

    /** There have to be at least this many items to be loaded in a grid, otherwise more items are loaded. */
    public static final int LOAD_ITEMS_THRESHOLD = 6;

    /** The ID of the bucket from which the featured shots are displayed. */
    public static final int FEATURED_BUCKET_ID = 256408;

    /** The ID of the bucket from which the cover shots are displayed in the drawer. */
    public static final int COVERS_BUCKET_ID = 251815;

    /** The endpoint where Dribbble API requests are made. */
    public static final String API_ENDPOINT = "https://api.dribbble.com/v1";

    /** App ID used for user authorization. */
    public static final String API_CLIENT_ID = "a2e36216a48709e24ab07c74e275fd127be662f706da238c6179c8dce8c20b8b";

    /** App secret used for user authorization. */
    public static final String API_CLIENT_SECRET = "7080f16ec3ee01626b7e043edde715d8dd2c0e1f8c397a28a78094c256b1dcaa";

    /** A token used for read-only API requests. */
    public static final String API_CLIENT_TOKEN = "ff549b889305c04600bab572bc1e9f90fd61272c5eb803bcd5ad5d68b4afa120";

    /** The url the user is redirected to during authorization. */
    public static final String API_CALLBACK_URL = "drizzle://android-oauth";

    /** The email address users will send feedback to. */
    public static final String FEEDBACK_EMAIL = "hello@getdrizzle.co";

    /** The maximum cache size for network requests. */
    public static final int CACHE_SIZE = 10 * 1024 * 1024; // 10 MB

}
