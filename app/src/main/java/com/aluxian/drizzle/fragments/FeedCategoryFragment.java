package com.aluxian.drizzle.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aluxian.drizzle.R;

public class FeedCategoryFragment extends Fragment {

    public static final String ARG_CATEGORY_ID = "category_id";
    public static final String[] CATEGORIES = {"Following", "Suggestions"};

    public static FeedCategoryFragment newInstance(int id) {
        FeedCategoryFragment fragment = new FeedCategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_category, container, false);
    }

}
