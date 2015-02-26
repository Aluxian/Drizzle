package com.aluxian.drizzle.adapters.pager;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.providers.FeaturedShotsProvider;
import com.aluxian.drizzle.api.providers.FollowingShotsProvider;
import com.aluxian.drizzle.api.providers.ItemsProvider;
import com.aluxian.drizzle.fragments.ShotsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedPagerAdapter extends FragmentPagerAdapter {

    private static final Map<Integer, Class<? extends ItemsProvider>> PROVIDERS = new HashMap<>();

    static {
        PROVIDERS.put(R.string.tab_following, FollowingShotsProvider.class);
        PROVIDERS.put(R.string.tab_featured, FeaturedShotsProvider.class);
    }

    private Resources mResources;

    public FeedPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mResources = context.getResources();
    }

    @Override
    public Fragment getItem(int position) {
        return ShotsFragment.newInstance(new ArrayList<>(PROVIDERS.values()).get(position), null, position * 100);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mResources.getString(new ArrayList<>(PROVIDERS.keySet()).get(position));
    }

    @Override
    public int getCount() {
        return PROVIDERS.size();
    }

}
