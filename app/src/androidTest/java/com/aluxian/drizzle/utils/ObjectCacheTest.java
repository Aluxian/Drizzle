package com.aluxian.drizzle.utils;

import android.test.AndroidTestCase;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ObjectCacheTest extends AndroidTestCase {

    private ObjectCache mObjectCache;

    private static final String TEST_KEY = "123";
    private static final String TEST_VALUE = "abc";
    private static final TypeToken<String> TEST_TYPE = new TypeToken<String>() {};

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mObjectCache = new ObjectCache(new File(getContext().getCacheDir(), ObjectCacheTest.class.getName()));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mObjectCache.delete();
    }

    public void testPutGet() {
        mObjectCache.put(TEST_KEY, TEST_VALUE);
        assertEquals(TEST_VALUE, mObjectCache.get(TEST_KEY, TEST_TYPE));
    }

    public void testRemove() {
        mObjectCache.put(TEST_KEY, TEST_VALUE);
        mObjectCache.remove(TEST_KEY);
        assertEquals(false, mObjectCache.containsNotExpired(TEST_KEY));
    }

    public void testExpired() {
        mObjectCache.put(TEST_KEY, TEST_VALUE, System.currentTimeMillis() - 100);
        assertEquals(false, mObjectCache.containsNotExpired(TEST_KEY));
        assertEquals(null, mObjectCache.get(TEST_KEY, TEST_TYPE));
    }

    public void testNotExpired() {
        mObjectCache.put(TEST_KEY, TEST_VALUE, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        assertEquals(true, mObjectCache.containsNotExpired(TEST_KEY));
        assertEquals(TEST_VALUE, mObjectCache.get(TEST_KEY, TEST_TYPE));
    }

}
