package com.aluxian.drizzle.utils;

import com.aluxian.drizzle.BuildConfig;
import com.aluxian.drizzle.api.ApiRequest;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Wraps around DiskLruCache to simplify usage and provide expiration.
 */
public class ObjectCache {

    private static final int DATA_INDEX = 0;
    private static final int EXPIRES_INDEX = 1;

    private DiskLruCache mDiskLruCache;

    public ObjectCache(File cacheDirectory) throws IOException {
        mDiskLruCache = DiskLruCache.open(cacheDirectory, BuildConfig.VERSION_CODE, 2, Config.CACHE_SIZE);
    }

    public <T> T get(String key, TypeToken<T> typeToken) {
        try (DiskLruCache.Snapshot snapshot = mDiskLruCache.get(encodeKey(key))) {
            if (snapshot != null) {
                String value = snapshot.getString(DATA_INDEX);
                long expiresAt = Long.valueOf(snapshot.getString(EXPIRES_INDEX));

                if (expiresAt > 0 && System.currentTimeMillis() >= expiresAt) {
                    remove(key);
                } else {
                    return ApiRequest.GSON.fromJson(value, typeToken.getType());
                }
            }
        } catch (IOException e) {
            Log.e(e);
        }

        return null;
    }

    public void put(String key, Object value) {
        put(key, value, 0L);
    }

    public void put(String key, Object value, long timeToLive) {
        new Thread(() -> {
            DiskLruCache.Editor editor = null;

            try {
                editor = mDiskLruCache.edit(encodeKey(key));

                if (editor != null) {
                    editor.set(DATA_INDEX, ApiRequest.GSON.toJson(value));
                    editor.set(EXPIRES_INDEX, timeToLive <= 0 ? "0" : String.valueOf(System.currentTimeMillis() + timeToLive));
                    editor.commit();
                    mDiskLruCache.flush();
                }
            } catch (IOException e) {
                Log.e(e);
            } finally {
                if (editor != null) {
                    editor.abortUnlessCommitted();
                }
            }
        }).start();
    }

    public void remove(String key) {
        new Thread(() -> {
            try {
                mDiskLruCache.remove(encodeKey(key));
                mDiskLruCache.flush();
            } catch (IOException e) {
                Log.e(e);
            }
        }).start();
    }

    public void clear() {
        new Thread(() -> {
            try {
                mDiskLruCache.delete();
                mDiskLruCache.flush();
            } catch (IOException e) {
                Log.e(e);
            }
        }).start();
    }

    public void close() {
        try {
            mDiskLruCache.close();
        } catch (IOException e) {
            Log.e(e);
        }
    }

    public boolean containsNotExpired(String key) {
        try (DiskLruCache.Snapshot snapshot = mDiskLruCache.get(encodeKey(key))) {
            if (snapshot != null) {
                long expiresAt = Long.valueOf(snapshot.getString(EXPIRES_INDEX));

                if (expiresAt <= 0 || System.currentTimeMillis() < expiresAt) {
                    return true;
                }
            }
        } catch (IOException e) {
            Log.e(e);
        }

        return false;
    }

    private String encodeKey(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            byte[] hash = digest.digest();
            return new BigInteger(1, hash).toString(16);
        } catch (NoSuchAlgorithmException e) {
            Log.e(e);
        }

        return key;
    }

}
