package com.aluxian.drizzle.lists.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aluxian.drizzle.api.providers.FeaturedShotsProvider;
import com.aluxian.drizzle.api.providers.FollowingShotsProvider;
import com.aluxian.drizzle.api.providers.ShotsProvider;
import com.aluxian.drizzle.fragments.ShotsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedPagerAdapter extends FragmentPagerAdapter {

    private static final Map<String, Class<? extends ShotsProvider>> PROVIDERS = new HashMap<>();

    static {
        PROVIDERS.put("Following", FollowingShotsProvider.class);
        PROVIDERS.put("Featured", FeaturedShotsProvider.class);
    }

    public FeedPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ShotsFragment.newInstance(new ArrayList<>(PROVIDERS.values()).get(position), null);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return new ArrayList<>(PROVIDERS.keySet()).get(position);
    }

    @Override
    public int getCount() {
        return PROVIDERS.size();
    }

}
