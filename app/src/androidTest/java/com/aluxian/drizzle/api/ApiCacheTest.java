package com.aluxian.drizzle.api;

import android.test.AndroidTestCase;

import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ApiCacheTest extends AndroidTestCase {

    private static final String TEST_KEY = "123";
    private static final Dribbble.Response<List<String>> TEST_VALUE =
            new Dribbble.Response<>(Arrays.asList("abc", "def"), "http://next.page");
    private static final TypeToken<List<String>> TEST_TYPE = new TypeToken<List<String>>() {};

    private ApiCache mApiCache;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mApiCache = new ApiCache(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mApiCache.delete();
    }

    public void testPutGet() {
        mApiCache.put(TEST_KEY, TEST_VALUE);
        assertEquals(TEST_VALUE, mApiCache.get(TEST_KEY, TEST_TYPE));
    }

    public void testRemove() {
        mApiCache.put(TEST_KEY, TEST_VALUE);
        mApiCache.remove(TEST_KEY);
        assertEquals(false, mApiCache.containsNotExpired(TEST_KEY));
    }

    public void testExpired() {
        mApiCache.put(TEST_KEY, TEST_VALUE, System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1));
        assertEquals(false, mApiCache.containsNotExpired(TEST_KEY));
        assertEquals(null, mApiCache.get(TEST_KEY, TEST_TYPE));
    }

    public void testNotExpired() {
        mApiCache.put(TEST_KEY, TEST_VALUE, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        assertEquals(true, mApiCache.containsNotExpired(TEST_KEY));
        assertEquals(TEST_VALUE, mApiCache.get(TEST_KEY, TEST_TYPE));
    }

}
