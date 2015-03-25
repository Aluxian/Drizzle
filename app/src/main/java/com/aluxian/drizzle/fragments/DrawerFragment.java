package com.aluxian.drizzle.fragments;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.adapters.DrawerAdapter;
import com.aluxian.drizzle.adapters.items.DrawerItem;
import com.aluxian.drizzle.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.utils.UserManager;
import com.aluxian.drizzle.views.CustomEdgeRecyclerView;

import java.lang.reflect.Field;
import java.util.List;

import static com.aluxian.drizzle.utils.UserManager.AuthStateChangeListener;

/**
 * Fragment used for managing interactions and presentation of a navigation drawer.
 */
public class DrawerFragment extends Fragment implements AuthStateChangeListener {

    /** Remember the position of the selected item. */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /** The callbacks instance (the Activity). */
    private DrawerCallbacks mDrawerCallbacks;

    /** Helper component that ties the action bar to the navigation drawer. */
    private ActionBarDrawerToggle mDrawerToggle;

    /** The layout that contains the content of the activity and the drawer view. */
    private DrawerLayout mDrawerLayout;

    /** The view that slides in from the left. */
    private View mDrawerView;

    /** The RecyclerView that displays the items. */
    private CustomEdgeRecyclerView mRecyclerView;

    /** The adapter responsible for managing items. */
    private DrawerAdapter mAdapter;

    /** The position of the currently selected item in the list view. */
    private int mSelectedItemPosition;

    /** Whether the user is authenticated. */
    private boolean mIsAuthenticated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recover the previously selected item
        if (savedInstanceState != null) {
            mSelectedItemPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        mIsAuthenticated = UserManager.getInstance().isAuthenticated();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drawer, container, false);

        mRecyclerView = (CustomEdgeRecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new DrawerAdapter(mDrawerCallbacks);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        onItemSelected(mIsAuthenticated ? 2 : 3);
        mAdapter.updateItems(mIsAuthenticated);

        rootView.post(() -> ((DrawerItem) mAdapter.itemsList().get(mSelectedItemPosition)).onClick(mSelectedItemPosition));
        return rootView;
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

        // ActionBarDrawerToggle ties together the proper interactions between the drawer and the toolbar drawer icon
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.drawer_open, R.string.drawer_close) {};

        // Defer code dependent on restoration of previous instance state
        drawerLayout.post(mDrawerToggle::syncState);
        drawerLayout.setDrawerListener(mDrawerToggle);

        // Set the status bar colour
        TypedArray a = getActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.statusBarColor});
        drawerLayout.setStatusBarBackgroundColor(a.getColor(0, 0));
        a.recycle();
    }

    /**
     * Change the drawer icon.
     *
     * @param state The state to which the drawer icon should be changed.
     */
    public void toggleDrawerIcon(DrawerIconState state) {
        ValueAnimator iconAnimator = ValueAnimator.ofFloat(state.from, state.to);
        iconAnimator.addUpdateListener(animator -> mDrawerToggle.onDrawerSlide(null, (Float) animator.getAnimatedValue()));
        iconAnimator.start();
    }

    /**
     * @param locked Whether the drawer should be locked (cannot be opened) or not.
     */
    public void setDrawerLocked(boolean locked) {
        mDrawerLayout.setDrawerLockMode(locked ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * @return True if the drawer is currently open.
     */
    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mDrawerView);
    }

    /**
     * Close the drawer.
     */
    public void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mDrawerView);
        }
    }

    /**
     * Checks whether the auth state has changed, and reloads the items if so.
     */
    public void checkAuthState() {
        boolean authenticated = UserManager.getInstance().isAuthenticated();

        if (authenticated != mIsAuthenticated) {
            onAuthenticationStateChanged(authenticated);
        }
    }

    /**
     * Sets the given style on the adapter.
     *
     * @param swatch The colour style to set.
     */
    public void setStyle(UberSwatch swatch) {
        mRecyclerView.setEdgeColor(swatch.rgb);
        mAdapter.setStyle(swatch);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkAuthState();
        UserManager.getInstance().registerStateChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        UserManager.getInstance().unregisterStateChangeListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mDrawerCallbacks = (DrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement DrawerFragment.Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDrawerCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mSelectedItemPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the toolbar
        setHasOptionsMenu(true);
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

    @Override
    public void onAuthenticationStateChanged(boolean authenticated) {
        mIsAuthenticated = authenticated;
        mAdapter.updateItems(authenticated);
        onItemSelected(authenticated ? 2 : 3);
    }

    /**
     * Called when an item is clicked.
     *
     * @param position The position of the item in the adapter.
     */
    public void onItemSelected(int position) {
        Log.d(position);
        mSelectedItemPosition = position;
        mAdapter.selectItem(position);
    }

    /**
     * The possible states of the icon used in the toolbar to toggle the drawer.
     */
    public static enum DrawerIconState {
        BURGER(1f, 0f),
        ARROW(0f, 1f);

        public final float from;
        public final float to;

        DrawerIconState(float from, float to) {
            this.from = from;
            this.to = to;
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface DrawerCallbacks {

        /**
         * Load items in the drawer.
         *
         * @param items The list into which the new items should be added.
         */
        void onLoadDrawerItems(List<MultiTypeBaseItem<? extends MultiTypeBaseItem.ViewHolder>> items);

    }

}
