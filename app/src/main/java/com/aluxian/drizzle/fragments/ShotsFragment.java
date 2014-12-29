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
import com.aluxian.drizzle.recycler.GridItemAnimator;
import com.aluxian.drizzle.recycler.ShotsAdapter;

public class ShotsFragment extends Fragment {

    private static final String ARG_CATEGORY_NAME = "category_name";
    private ShotsAdapter gridAdapter;

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

        View view = inflater.inflate(R.layout.fragment_shots, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.grid);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.accent);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "refreshing", Toast.LENGTH_SHORT).show();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        GridItemAnimator animator = new GridItemAnimator(layoutManager);
        animator.setSupportsChangeAnimations(true);
        animator.setAddDuration(500);

        recyclerView.setItemAnimator(animator);
        recyclerView.setHasFixedSize(true);

        gridAdapter = new ShotsAdapter(getActivity(), swipeRefreshLayout, category, Params.Timeframe.NOW, Params.Sort.POPULAR);
        recyclerView.setAdapter(gridAdapter);

        return view;
    }

    public void setTimeframeParam(Params.Timeframe timeframe) {
        gridAdapter.setTimeframeParam(timeframe);
    }

    public void setSortParam(Params.Sort sort) {
        gridAdapter.setSortParam(sort);
    }

    public void applyParams() {
        // TODO
    }

    public Params.Timeframe getTimeframeParam() {
        return gridAdapter.getTimeframeParam();
    }

    public Params.Sort getSortParam() {
        return gridAdapter.getSortParam();
    }

}
