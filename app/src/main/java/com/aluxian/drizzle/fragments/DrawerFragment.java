package com.aluxian.drizzle.fragments;

import android.animation.ValueAnimator;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.lists.DrawerListItem;
import com.aluxian.drizzle.lists.adapters.DrawerListAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.aluxian.drizzle.lists.DrawerListItem.TYPE_DIVIDER;
import static com.aluxian.drizzle.lists.DrawerListItem.TYPE_ICON_TEXT;
import static com.aluxian.drizzle.lists.DrawerListItem.TYPE_SUBHEADER;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 */
public class DrawerFragment extends Fragment {

    /** Remember the position of the selected item. */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /** A pointer to the current callbacks instance (the Activity). */
    private Callbacks mCallbacks;

    /** Helper component that ties the action bar to the navigation drawer. */
    private ActionBarDrawerToggle mDrawerToggle;

    /** A map that holds all the items that appear in the drawer. */
    private List<DrawerListItem> mItems = new ArrayList<>();

    private DrawerLayout mDrawerLayout;
    private View mDrawerView;
    private ListView mListView;
    private int mCurrentSelectedPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recover the previously selected item
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        // Add items to the list
        //mItems.add(new DrawerListItem(TYPE_ICON_TEXT, R.string.drawer_main_feed, R.drawable.ic_feed));
        mItems.add(new DrawerListItem(TYPE_ICON_TEXT, R.string.drawer_main_shots, R.drawable.ic_shots));
        mItems.add(new DrawerListItem(TYPE_SUBHEADER, R.string.drawer_personal, 0));
        mItems.add(new DrawerListItem(TYPE_ICON_TEXT, R.string.drawer_personal_sign_in, R.drawable.ic_sign_in));
        //mItems.add(new DrawerListItem(DrawerListItem.TYPE_ICON_TEXT, R.string.drawer_personal_buckets, R.drawable.ic_bucket));
        //mItems.add(new DrawerListItem(DrawerListItem.TYPE_ICON_TEXT, R.string.drawer_personal_go_pro, R.drawable.ic_dribbble));
        //mItems.add(new DrawerListItem(DrawerListItem.TYPE_ICON_TEXT, R.string.drawer_personal_account_settings, R.drawable.ic_account));
        //mItems.add(new DrawerListItem(DrawerListItem.TYPE_ICON_TEXT, R.string.drawer_personal_sign_out, R.drawable.ic_sign_out));
        mItems.add(new DrawerListItem(TYPE_DIVIDER, 0, 0));
        mItems.add(new DrawerListItem(TYPE_ICON_TEXT, R.string.drawer_app_rate, R.drawable.ic_rate));
        mItems.add(new DrawerListItem(TYPE_ICON_TEXT, R.string.drawer_app_feedback, R.drawable.ic_feedback));

        // Select either the default item (0) or the last selected item
        selectItem(mCurrentSelectedPosition);
    }

    public static enum ActionDrawableState {
        BURGER, ARROW
    }

    public void toggleActionBarIcon(ActionDrawableState state, boolean animate) {
        if (animate) {
            float start = state == ActionDrawableState.BURGER ? 1.0f : 0f;
            ValueAnimator animator = ValueAnimator.ofFloat(start, Math.abs(start - 1));
            animator.setDuration(300);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mDrawerToggle.onDrawerSlide(null, (Float) animator.getAnimatedValue());
                }
            });
            animator.start();
        } else {
            if (state == ActionDrawableState.BURGER) {
                mDrawerToggle.onDrawerClosed(null);
            } else {
                mDrawerToggle.onDrawerOpened(null);
            }
        }
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
                selectItem(position);
            }
        });

        mListView.setAdapter(new DrawerListAdapter(mItems));
        mListView.setItemChecked(mCurrentSelectedPosition, true);

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

    private void selectItem(int position) {
        if (mDrawerLayout != null) {
            closeDrawer();
        }

        if (mCallbacks != null && !mCallbacks.onDrawerItemSelected(mItems.get(position).titleResourceId)) {
            mListView.setItemChecked(mCurrentSelectedPosition, true);
        } else {
            mCurrentSelectedPosition = position;
        }
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
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration to the drawer toggle component
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item) || mDrawerToggle.onOptionsItemSelected(item);
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
        boolean onDrawerItemSelected(int titleResourceId);

    }

}
