package com.aluxian.drizzle.recycler;

import android.os.AsyncTask;

import com.aluxian.drizzle.api.exceptions.BadRequestException;
import com.aluxian.drizzle.api.exceptions.TooManyRequestsException;
import com.aluxian.drizzle.api.providers.ItemsProvider;

import java.io.IOException;
import java.util.List;

/**
 * AsyncTask used by an {@link com.aluxian.drizzle.recycler.ItemLoader} to load items in the background.
 *
 * @param <T> The type of items to load.
 */
public class LoadItemsTask<T> extends AsyncTask<Void, Void, List<T>> {

    private boolean mPendingReload;
    private ItemsProvider<T> mItemsProvider;
    private ItemLoader.Listener<T> mListener;

    private Exception mException;

    LoadItemsTask(ItemsProvider<T> itemsProvider, ItemLoader.Listener<T> listener) {
        mItemsProvider = itemsProvider;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.onStartedLoading();
    }

    @Override
    protected List<T> doInBackground(Void... params) {
        try {
            if (mPendingReload) {
                mPendingReload = false;
                return mItemsProvider.refresh();
            } else {
                return mItemsProvider.load();
            }
        } catch (IOException | BadRequestException | TooManyRequestsException e) {
            mException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<T> response) {
        mListener.onFinishedLoading(response != null);

        if (response != null) {
            mListener.onItemsLoaded(response);
        }

        if (mException != null) {
            mListener.onItemsLoadError(mException);
        }
    }

    public void setPendingReload(boolean pending) {
        mPendingReload = pending;
    }

}
