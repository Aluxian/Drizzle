package com.aluxian.drizzle.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.recycler.adapters.IconTextListAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 */
public class DrawerFragment extends Fragment {

    /** Remember the id of the selected item. */
    private static final String STATE_SELECTED_ID = "selected_navigation_drawer_id";

    /** A pointer to the current callbacks instance (the Activity). */
    private Callbacks mCallbacks;

    /** Helper component that ties the action bar to the navigation drawer. */
    private ActionBarDrawerToggle mDrawerToggle;

    /** A map that holds all the mItems that appear in the drawer. */
    private Map<Integer, Integer> mItems = new LinkedHashMap<>();

    private DrawerLayout mDrawerLayout;
    private View mDrawerView;
    private ListView mListView;
    private int mCurrentSelectedId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add items to the list
        //mItems.put(R.string.drawer_main_feed, R.drawable.ic_feed);
        mItems.put(R.string.drawer_main_shots, R.drawable.ic_shots);
        mItems.put(R.string.drawer_personal, 0);
        mItems.put(R.string.drawer_personal_sign_in, R.drawable.ic_sign_in);
        //mItems.put(R.string.drawer_personal_buckets, R.drawable.ic_bucket);
        //mItems.put(R.string.drawer_personal_go_pro, R.drawable.ic_dribbble);
        //mItems.put(R.string.drawer_personal_account_settings, R.drawable.ic_account);
        //mItems.put(R.string.drawer_personal_sign_out, R.drawable.ic_sign_out);

        // Recover the previously selected item
        if (savedInstanceState != null) {
            mCurrentSelectedId = savedInstanceState.getInt(STATE_SELECTED_ID);
        } else {
            mCurrentSelectedId = new ArrayList<>(mItems.keySet()).get(0);
        }

        // Select either the default item (0) or the last selected item
        selectItem(mCurrentSelectedId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        mListView = (ListView) view.findViewById(R.id.list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem((int) id);
            }
        });

        mListView.setAdapter(new IconTextListAdapter(mItems));
        setItemChecked(mCurrentSelectedId);

        return view;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mDrawerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {};

        // Defer code dependent on restoration of previous instance state
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int titleResourceId) {
        if (mDrawerLayout != null) {
            closeDrawer();
        }

        if (mCallbacks != null && !mCallbacks.onNavigationDrawerItemSelected(titleResourceId)) {
            setItemChecked(mCurrentSelectedId);
        } else {
            mCurrentSelectedId = titleResourceId;
        }
    }

    private void setItemChecked(int titleResourceId) {
        mListView.setItemChecked(new ArrayList<>(mItems.keySet()).indexOf(titleResourceId), true);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mDrawerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mDrawerView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement DrawerFragment.Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_ID, mCurrentSelectedId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration to the drawer toggle component
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Let the DrawerToggle handle the callback first, otherwise let super do it
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface Callbacks {

        /**
         * Called when an item in the navigation drawer is selected.
         *
         * @param titleResourceId The id of the selected item's title.
         * @return Whether the selected item should remain selected.
         */
        boolean onNavigationDrawerItemSelected(int titleResourceId);

    }

}
