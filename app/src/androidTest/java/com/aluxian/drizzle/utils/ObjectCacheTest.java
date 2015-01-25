package com.aluxian.drizzle.utils;

import android.test.AndroidTestCase;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ObjectCacheTest extends AndroidTestCase {

    private ObjectCache mObjectCache;

    private static String mTestKey = "123";
    private static String mTestValue = "abc";
    private static TypeToken<String> mTestType = new TypeToken<String>() {};

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mObjectCache = new ObjectCache(new File(getContext().getCacheDir(), ObjectCacheTest.class.getName()));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mObjectCache.clear();
        mObjectCache.close();
    }

    public void testPutGet() {
        mObjectCache.put(mTestKey, mTestValue);
        assertEquals(mTestValue, mObjectCache.get(mTestKey, mTestType));
    }

    public void testRemove() {
        mObjectCache.put(mTestKey, mTestValue);
        mObjectCache.remove(mTestKey);
        assertEquals(false, mObjectCache.containsNotExpired(mTestKey));
    }

    public void testExpired() {
        mObjectCache.put(mTestKey, mTestValue, System.currentTimeMillis() - 100);
        assertEquals(false, mObjectCache.containsNotExpired(mTestKey));
        assertEquals(null, mObjectCache.get(mTestKey, mTestType));
    }

    public void testNotExpired() {
        mObjectCache.put(mTestKey, mTestValue, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        assertEquals(true, mObjectCache.containsNotExpired(mTestKey));
        assertEquals(mTestValue, mObjectCache.get(mTestKey, mTestType));
    }

}
