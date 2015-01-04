package com.aluxian.drizzle.lists.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.api.providers.FilteredShotsProvider;
import com.aluxian.drizzle.fragments.ShotsFragment;

public class ShotsPagerAdapter extends FragmentPagerAdapter {

    public ShotsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ShotsFragment.newInstance(FilteredShotsProvider.class, Params.List.values()[position]);
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
