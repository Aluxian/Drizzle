package com.aluxian.drizzle.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.lists.GridItemAnimator;
import com.aluxian.drizzle.lists.ShotsAdapter;
import com.squareup.okhttp.Request;

public class ShotsFragment extends Fragment {

    private static final String ARG_CATEGORY_NAME = "category_name";

    private ShotsAdapter mShotsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static ShotsFragment newInstance(String categoryName) {
        ShotsFragment fragment = new ShotsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Params.List category = Params.List.valueOf(getArguments().getString(ARG_CATEGORY_NAME));

        // Check whether the items should be loaded quickly (without delays or loading indicator)
        Request request = Dribbble.listShots(category, Params.Timeframe.NOW, Params.Sort.POPULAR).build();
        String requestHash = request.method() + " " + request.urlString();
        boolean fastLoad = ApiRequest.hasValidCache(requestHash);

        // Set up the recycler
        View view = inflater.inflate(R.layout.fragment_shots, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.grid);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        GridItemAnimator animator = new GridItemAnimator(layoutManager);
        animator.setSupportsChangeAnimations(true);
        animator.setAddDuration(fastLoad ? 300 : 500);
        animator.setAddDelay(fastLoad ? 0 : 500);

        // TODO: Restore delay/duration values on normal refresh (calculate fastLoad each time?)

        recyclerView.setItemAnimator(animator);
        recyclerView.setHasFixedSize(true);

        // Swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);

        // Adapter
        mShotsAdapter = new ShotsAdapter(getActivity(), mSwipeRefreshLayout, category, Params.Timeframe.NOW, Params.Sort.POPULAR);
        recyclerView.setAdapter(mShotsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(mShotsAdapter);
        if (!fastLoad) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        return view;
    }

    public void setTimeframeParam(Params.Timeframe timeframe) {
        mShotsAdapter.setTimeframeParam(timeframe);
    }

    public void setSortParam(Params.Sort sort) {
        mShotsAdapter.setSortParam(sort);
    }

    /**
     * Reload the items with the newly changed parameters.
     */
    public void applyParams() {
        mSwipeRefreshLayout.setRefreshing(true);
        mShotsAdapter.onRefresh();
    }

    public Params.Timeframe getTimeframeParam() {
        return mShotsAdapter.getTimeframeParam();
    }

    public Params.Sort getSortParam() {
        return mShotsAdapter.getSortParam();
    }

}
