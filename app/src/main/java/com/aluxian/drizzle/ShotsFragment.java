package com.aluxian.drizzle;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

public class ShotsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shots, container, false);

        // Initialize the ViewPager and set an adapter
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new ShotsPagerAdapter(getChildFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);

        tabs.setElevation(30);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getActionBar().setTitle(R.string.drawer_pages_shots);
    }

    public static class ShotsPagerAdapter extends FragmentPagerAdapter {

        public ShotsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return ShotsCategoryFragment.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ShotsCategoryFragment.CATEGORIES[position];
        }

        @Override
        public int getCount() {
            return ShotsCategoryFragment.CATEGORIES.length;
        }

    }

    public static class ShotsCategoryFragment extends Fragment {

        public static final String ARG_CATEGORY_ID = "category_id";
        public static final String[] CATEGORIES = {
                "Any",
                "Team Shots",
                "Debuts",
                "Playoffs",
                "Rebounds",
                "Animated GIFs",
                "With Attachments"
        };

        public static ShotsCategoryFragment newInstance(int id) {
            ShotsCategoryFragment fragment = new ShotsCategoryFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_CATEGORY_ID, id);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_shots_category, container, false);
        }

    }

}
