package com.aluxian.drizzle.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.Params;
import com.astuetz.PagerSlidingTabStrip;

public class TabsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        // Initialize the ViewPager and set the adapter
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new ShotsPagerAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(10);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //noinspection ConstantConditions
        activity.getActionBar().setTitle(R.string.drawer_main_shots);
    }

    public static class ShotsPagerAdapter extends FragmentPagerAdapter {

        public ShotsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ShotsFragment.newInstance(Params.List.values()[position].name());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Params.List.values()[position].humanReadableValue;
        }

        @Override
        public int getCount() {
            return Params.List.values().length;
        }

    }

}
