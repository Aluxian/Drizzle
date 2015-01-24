package com.aluxian.drizzle.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.api.providers.FilteredShotsProvider;
import com.aluxian.drizzle.api.providers.ShotsProvider;
import com.aluxian.drizzle.lists.ShotAnimator;
import com.aluxian.drizzle.lists.ShotsAdapter;
import com.aluxian.drizzle.utils.Log;

import java.lang.reflect.InvocationTargetException;

public class ShotsFragment extends Fragment implements ShotsAdapter.Callbacks {

    private static final String ARG_PROVIDER_CLASS = "provider_class";
    private static final String ARG_LIST_API_VALUE = "list_api_value";

    private ShotsAdapter mShotsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mErrorView;

    /**
     * Facilitates the creation of the fragment. The parameters are serialized and attached to the fragment instance in a Bundle.
     *
     * @param clazz        The class of the ShotsProvider to use for this fragments adapter.
     * @param listApiValue The Params.List argument for FilteredShotsProvider.
     * @return An instance of the fragment.
     */
    public static ShotsFragment newInstance(Class<? extends ShotsProvider> clazz, Params.List listApiValue) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PROVIDER_CLASS, clazz);

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
    private ShotsProvider getShotsProvider(Bundle args) {
        Class<? extends ShotsProvider> clazz = (Class<? extends ShotsProvider>) args.getSerializable(ARG_PROVIDER_CLASS);

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
        mShotsAdapter = new ShotsAdapter(getActivity(), this, getShotsProvider(getArguments()));

        View view = inflater.inflate(R.layout.fragment_shots, container, false);
        mErrorView = view.findViewById(R.id.error_view);

        ShotAnimator animator = new ShotAnimator();
        animator.setSupportsChangeAnimations(true);
        animator.setAddDuration(300);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.grid);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(animator);
        recyclerView.setAdapter(mShotsAdapter);

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
        mShotsAdapter.onRefresh();
    }

    @Override
    public void onAdapterLoadingFinished(boolean successful) {
        mSwipeRefreshLayout.setRefreshing(false);

        // Hide the error indicator
        if (successful && mErrorView.getVisibility() != View.GONE) {
            mErrorView.animate().alpha(0).withEndAction(() -> mErrorView.setVisibility(View.GONE));
        }
    }

    @Override
    public void onAdapterLoadingError(boolean hasItems) {
        if (hasItems) {
            Toast.makeText(getActivity(), "Adapter error.", Toast.LENGTH_LONG).show();
        } else {
            mErrorView.setVisibility(View.VISIBLE);
            mErrorView.animate().alpha(1);
        }
    }

}
