package com.aluxian.drizzle.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.lists.adapters.pager.FeedPagerAdapter;
import com.aluxian.drizzle.lists.adapters.pager.ShotsPagerAdapter;
import com.astuetz.PagerSlidingTabStrip;

public class TabsFragment extends Fragment {

    private ViewPager mViewPager;

    public static TabsFragment newInstance(Type type) {
        TabsFragment fragment = new TabsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Type.class.getName(), type.name());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);
        Type type = Type.valueOf(getArguments().getString(Type.class.getName()));

        // Initialize the ViewPager and set the adapter
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(Params.List.values().length);

        switch (type) {
            case FEED:
                mViewPager.setAdapter(new FeedPagerAdapter(getActivity(), getChildFragmentManager()));
                break;

            case SHOTS:
                mViewPager.setAdapter(new ShotsPagerAdapter(getActivity(), getChildFragmentManager()));
                break;
        }

        // Bind the tabs strip to the ViewPager
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabStrip.setShouldExpand(true);
        tabStrip.setViewPager(mViewPager);

        return view;
    }

    /**
     * @return The fragment that is currently shown in the ViewPager.
     */
    public Fragment getCurrentFragment() {
        return getChildFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getActionBar().setTitle(R.string.drawer_main_shots);
    }

    public static enum Type {
        FEED(R.string.drawer_main_feed),
        SHOTS(R.string.drawer_main_shots);

        public final int titleId;

        Type(int titleId) {
            this.titleId = titleId;
        }
    }

}
