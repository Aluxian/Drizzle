package com.aluxian.drizzle.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.ShotsAdapter;
import com.aluxian.drizzle.adapters.items.LoadingItem;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.api.providers.FilteredShotsProvider;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.recycler.ShotAnimator;
import com.aluxian.drizzle.utils.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ShotsFragment extends Fragment implements ShotsAdapter.StatusListener {

    private static final String ARG_PROVIDER_CLASS = "provider_class";
    private static final String ARG_LIST_API_VALUE = "list_api_value";
    private static final String ARG_LOAD_DELAY = "load_delay";

    private ShotsAdapter mShotsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mErrorText;

    /**
     * Facilitates the creation of the fragment. The parameters are serialized and attached to the fragment instance in a Bundle.
     *
     * @param clazz        The class of the ShotsProvider to use for this fragments adapter.
     * @param listApiValue The Params.List argument for FilteredShotsProvider.
     * @param loadDelay    How much time to wait before loading the items.
     * @return An instance of the fragment.
     */
    public static ShotsFragment newInstance(Class<? extends ItemsProvider> clazz, Params.List listApiValue, int loadDelay) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PROVIDER_CLASS, clazz);
        bundle.putInt(ARG_LOAD_DELAY, loadDelay);

        if (listApiValue != null) {
            bundle.putString(ARG_LIST_API_VALUE, listApiValue.name());
        }

        ShotsFragment fragment = new ShotsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Instantiate a new ShotsProvider from the data stored in the fragment's bundle.
     *
     * @param args A Bundle of arguments.
     * @return A ShotsProvider instance.
     */
    @SuppressWarnings("unchecked")
    private ItemsProvider<Shot> getShotsProvider(Bundle args) {
        Class<? extends ItemsProvider> clazz = (Class<? extends ItemsProvider>) args.getSerializable(ARG_PROVIDER_CLASS);

        try {
            if (clazz.equals(FilteredShotsProvider.class)) {
                Params.List listParam = Params.List.valueOf(args.getString(ARG_LIST_API_VALUE));
                return clazz.getConstructor(Params.List.class).newInstance(listParam);
            }

            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | java.lang.InstantiationException e) {
            Log.e(e);
        }

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mShotsAdapter = new ShotsAdapter(getShotsProvider(getArguments()), this);

        View view = inflater.inflate(R.layout.fragment_shots, container, false);
        mErrorText = (TextView) view.findViewById(R.id.error_text);

        ShotAnimator animator = new ShotAnimator();
        animator.setSupportsChangeAnimations(true);
        animator.setAddDuration(300);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mShotsAdapter.itemsList().get(position) instanceof LoadingItem ? 2 : 1;
            }
        });

        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setAdapter(mShotsAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
        mSwipeRefreshLayout.setOnRefreshListener(mShotsAdapter);

        if (!mShotsAdapter.getShotsProvider().hasItemsAvailable()) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
        }

        return view;
    }

    /**
     * @return The Params.Timeframe parameter that the fragment's FilteredShotsProvider uses.
     */
    public Params.Timeframe getTimeframeParam() {
        if (!(mShotsAdapter.getShotsProvider() instanceof FilteredShotsProvider)) {
            throw new Error("This method can only be called for fragments that use a FilteredShotsProvider.");
        }

        return ((FilteredShotsProvider) mShotsAdapter.getShotsProvider()).timeframeParam;
    }

    /**
     * @return The Params.Sort parameter that the fragment's FilteredShotsProvider uses.
     */
    public Params.Sort getSortParam() {
        if (!(mShotsAdapter.getShotsProvider() instanceof FilteredShotsProvider)) {
            throw new Error("This method can only be called for fragments that use a FilteredShotsProvider.");
        }

        return ((FilteredShotsProvider) mShotsAdapter.getShotsProvider()).sortParam;
    }

    /**
     * Change the parameters of the used FilteredShotsProvider and refresh the list.
     *
     * @param timeframeParam The 'timeframe' parameter used for future requests.
     * @param sortParam      The 'sort' parameter used for future requests.
     */
    public void updateParameters(Params.Timeframe timeframeParam, Params.Sort sortParam) {
        if (!(mShotsAdapter.getShotsProvider() instanceof FilteredShotsProvider)) {
            throw new Error("This method can only be called for fragments that use a FilteredShotsProvider.");
        }

        FilteredShotsProvider shotsProvider = (FilteredShotsProvider) mShotsAdapter.getShotsProvider();
        shotsProvider.timeframeParam = timeframeParam;
        shotsProvider.sortParam = sortParam;

        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.smoothScrollToPosition(0);
        mShotsAdapter.onRefresh();
    }

    @Override
    public void onAdapterLoadingFinished(boolean successful) {
        mSwipeRefreshLayout.setRefreshing(false);

        // Hide the error indicator
        if (successful && mErrorText.getVisibility() != View.GONE) {
            mErrorText.animate().alpha(0).withEndAction(() -> mErrorText.setVisibility(View.GONE));
        }
    }

    @Override
    public void onAdapterLoadingError(Exception e, boolean hasItems) {
        int errorId = R.string.dribbble_error_generic;

        if (e instanceof IOException) {

        }

        if (hasItems) {
            Toast.makeText(getActivity(), getString(errorId), Toast.LENGTH_LONG).show();
        } else {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(errorId);
            mErrorText.animate().alpha(1);
        }
    }

}
