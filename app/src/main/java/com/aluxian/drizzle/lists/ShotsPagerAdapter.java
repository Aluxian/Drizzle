package com.aluxian.drizzle.lists;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.api.providers.FilteredShotsProvider;
import com.aluxian.drizzle.fragments.ShotsFragment;

public class ShotsPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public ShotsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return ShotsFragment.newInstance(FilteredShotsProvider.class, Params.List.values()[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Params.List.values()[position].getHumanReadableValue(mContext);
    }

    @Override
    public int getCount() {
        return Params.List.values().length;
    }

}
