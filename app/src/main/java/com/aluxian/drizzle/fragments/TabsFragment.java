package com.aluxian.drizzle.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.lists.FeedPagerAdapter;
import com.aluxian.drizzle.lists.ShotsPagerAdapter;
import com.astuetz.PagerSlidingTabStrip;

import java.util.Arrays;

public class TabsFragment extends Fragment {

    /** The ViewPager that displays the fragments for the tabs. */
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
        Type type = Type.valueOf(getArguments().getString(Type.class.getName()));
        activity.getActionBar().setTitle(type == Type.FEED ? R.string.drawer_main_feed : R.string.drawer_main_shots);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Type type = Type.valueOf(getArguments().getString(Type.class.getName()));
        inflater.inflate(type.menuId, menu);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sort, null, false);

                // Parameter spinners
                Spinner timeframeSpinner = (Spinner) view.findViewById(R.id.timeframe_spinner);
                Spinner sortSpinner = (Spinner) view.findViewById(R.id.sort_spinner);

                // Get the selected fragment from the ViewPager
                ShotsFragment fragment = (ShotsFragment) getCurrentFragment();

                // Restore current values
                timeframeSpinner.setSelection(Arrays.asList(Params.Timeframe.values()).indexOf(fragment.getTimeframeParam()));
                sortSpinner.setSelection(Arrays.asList(Params.Sort.values()).indexOf(fragment.getSortParam()));

                new AlertDialog.Builder(getActivity(), R.style.Drizzle_Widget_Dialog)
                        .setView(view)
                        .setPositiveButton(R.string.dialog_apply, (dialog, which) -> fragment.updateParameters(
                                Params.Timeframe.values()[timeframeSpinner.getSelectedItemPosition()],
                                Params.Sort.values()[sortSpinner.getSelectedItemPosition()]
                        ))
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static enum Type {
        FEED(R.string.drawer_main_feed, R.menu.feed),
        SHOTS(R.string.drawer_main_shots, R.menu.shots);

        public final int titleId;
        public final int menuId;

        Type(int titleId, int menuId) {
            this.titleId = titleId;
            this.menuId = menuId;
        }
    }

}
