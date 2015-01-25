package com.aluxian.drizzle.api;

import android.test.AndroidTestCase;

import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ApiCacheTest extends AndroidTestCase {

    private ApiCache mApiCache;

    private static String mTestKey = "123";
    private static Dribbble.Response<List<String>> mTestValue = new Dribbble.Response<>(Arrays.asList("abc", "def"), "http://next.page");
    private static TypeToken<List<String>> mTestType = new TypeToken<List<String>>() {};

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mApiCache = new ApiCache(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mApiCache.clear();
        mApiCache.close();
    }

    public void testPutGet() {
        mApiCache.put(mTestKey, mTestValue);
        Dribbble.Response<List<String>> response = mApiCache.get(mTestKey, mTestType);
        assertEquals(mTestValue.data, response.data);
        assertEquals(mTestValue.nextPageUrl, response.nextPageUrl);
    }

    public void testRemove() {
        mApiCache.put(mTestKey, mTestValue);
        mApiCache.remove(mTestKey);
        assertEquals(false, mApiCache.containsNotExpired(mTestKey));
    }

    public void testExpired() {
        mApiCache.put(mTestKey, mTestValue, System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1));
        assertEquals(false, mApiCache.containsNotExpired(mTestKey));
        assertEquals(null, mApiCache.get(mTestKey, mTestType));
    }

    public void testNotExpired() {
        mApiCache.put(mTestKey, mTestValue, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        assertEquals(true, mApiCache.containsNotExpired(mTestKey));
        Dribbble.Response<List<String>> response = mApiCache.get(mTestKey, mTestType);
        assertEquals(mTestValue.data, response.data);
        assertEquals(mTestValue.nextPageUrl, response.nextPageUrl);
    }

}
