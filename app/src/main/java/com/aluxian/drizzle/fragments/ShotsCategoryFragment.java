package com.aluxian.drizzle.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.recycler.ShotsGridAdapter;

public class ShotsCategoryFragment extends Fragment {

    private static final String ARG_CATEGORY_NAME = "category_name";
    private ShotsGridAdapter gridAdapter;

    public static ShotsCategoryFragment newInstance(String categoryName) {
        ShotsCategoryFragment fragment = new ShotsCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Params.List category = Params.List.valueOf(getArguments().getString(ARG_CATEGORY_NAME));
        View view = inflater.inflate(R.layout.fragment_shots_category, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.grid);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        gridAdapter = new ShotsGridAdapter(getActivity(), category, Params.Timeframe.NOW, Params.Sort.POPULAR);
        recyclerView.setAdapter(gridAdapter);

        return view;
    }

    public void setTimeframeParam(Params.Timeframe timeframe) {
        gridAdapter.setTimeframeParam(timeframe);
    }

    public void setSortParam(Params.Sort sort) {
        gridAdapter.setSortParam(sort);
    }

}
