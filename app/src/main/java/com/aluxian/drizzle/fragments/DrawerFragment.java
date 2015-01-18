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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.models.Shot;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Dp;
import com.aluxian.drizzle.utils.Log;
import com.aluxian.drizzle.utils.UserManager;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment used for managing interactions and presentation of a navigation drawer.
 */
public class DrawerFragment extends Fragment implements UserManager.AuthStateChangeListener {

    /** Remember the position of the selected item. */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /** The callbacks instance (the Activity). */
    private Callbacks mCallbacks;

    /** Helper component that ties the action bar to the navigation drawer. */
    private ActionBarDrawerToggle mDrawerToggle;

    /** A list that holds all the items that appear in the drawer. */
    private List<ListItem> mItems = new ArrayList<>();

    /** The layout that contains the content of the activity and the drawer view. */
    private DrawerLayout mDrawerLayout;

    /** The view that slides in from the left. */
    private View mDrawerView;

    /** The list of available navigation items. */
    private ListView mListView;

    /** The position of the currently selected item in the list view. */
    private int mCurrentSelectedPosition = 1;

    /** Whether the user is authenticated. */
    private boolean mIsAuthenticated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recover the previously selected item
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        loadItems(UserManager.getInstance().isAuthenticated());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);

        // Load the cover image
        Dribbble.listBucketShots(Config.COVERS_BUCKET_ID).queryParam("per_page", "1").execute(new ApiRequest.Callback<List<Shot>>() {
            @SuppressWarnings("CodeBlock2Expr")
            @Override
            public void onSuccess(Dribbble.Response<List<Shot>> response) {
                getActivity().runOnUiThread(() -> {
                    Picasso.with(getActivity())
                            .load(response.data.get(0).images.getLargest())
                            .into((ImageView) view.findViewById(R.id.cover));
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(e);
            }
        });

        // Load the dribbbled pixels count
        Dribbble.pixelsDribbbledCount().execute(new ApiRequest.Callback<JsonObject>() {
            @Override
            public void onSuccess(Dribbble.Response<JsonObject> response) {
                String pixels = response.data.getAsJsonObject("results").getAsJsonArray("data")
                        .get(0).getAsJsonObject().get("pixelCount").getAsString();

                ((TextView) view.findViewById(R.id.pixels_count)).setText(pixels);
                view.findViewById(R.id.pixels_description).setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                Log.e(e);
            }
        });

        // Set up the list
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setOnItemClickListener((parent, itemView, position, id) -> selectItem(position));
        mListView.setAdapter(new ListAdapter(mItems));
        mListView.setItemChecked(mCurrentSelectedPosition, true);

        // Add a margin at the top and at the bottom of the list
        View spacingView = new View(getActivity());
        spacingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, new Dp(8).toPx(getActivity())));

        mListView.addHeaderView(spacingView, null, false);
        mListView.addFooterView(spacingView, null, false);

        // Select either the default item or the last selected one
        selectItem(mCurrentSelectedPosition);

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

        // ActionBarDrawerToggle ties together the proper interactions between the drawer and the toolbar drawer icon
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.drawer_open, R.string.drawer_close) {};

        // Defer code dependent on restoration of previous instance state
        mDrawerLayout.post(mDrawerToggle::syncState);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Change the drawer icon.
     *
     * @param state   The state to which the drawer icon should be changed.
     * @param animate Whether to animate the change.
     */
    public void toggleDrawerIcon(DrawerIconState state, boolean animate) {
        if (animate) {
            ValueAnimator iconAnimator = ValueAnimator.ofFloat(state.from, state.to);
            iconAnimator.addUpdateListener(animator -> mDrawerToggle.onDrawerSlide(null, (Float) animator.getAnimatedValue()));
            iconAnimator.start();
        } else {
            if (state == DrawerIconState.BURGER) {
                mDrawerToggle.onDrawerClosed(null);
            } else {
                mDrawerToggle.onDrawerOpened(null);
            }
        }
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
     * (Re)Add items to the list. This is called when the user signs in or out, and on app start.
     *
     * @param authenticated Whether there is an authenticated user.
     */
    private void loadItems(boolean authenticated) {
        mIsAuthenticated = authenticated;
        mItems.clear();

        if (authenticated) {
            mItems.add(ListItem.IconText(R.string.drawer_main_feed, R.drawable.ic_feed));
        }

        mItems.add(ListItem.IconText(R.string.drawer_main_shots, R.drawable.ic_shots));
        mItems.add(ListItem.SubHeader(R.string.drawer_personal));
        //mItems.add(ListItem.Divider());

        if (authenticated) {
            mItems.add(ListItem.IconText(R.string.drawer_personal_buckets, R.drawable.ic_bucket));
            mItems.add(ListItem.IconText(R.string.drawer_personal_go_pro, R.drawable.ic_dribbble));
            mItems.add(ListItem.IconText(R.string.drawer_personal_account_settings, R.drawable.ic_account));
            mItems.add(ListItem.IconText(R.string.drawer_personal_sign_out, R.drawable.ic_sign_out));
        } else {
            mItems.add(ListItem.IconText(R.string.drawer_personal_sign_in, R.drawable.ic_sign_in));
        }

        mItems.add(ListItem.Divider());
        mItems.add(ListItem.IconText(R.string.drawer_app_rate, R.drawable.ic_rate));
        mItems.add(ListItem.IconText(R.string.drawer_app_feedback, R.drawable.ic_feedback));
    }

    /**
     * Called when an item has been clicked to mark it as selected and notify the callbacks object.
     *
     * @param position The position of the clicked item in the list.
     */
    private void selectItem(int position) {
        closeDrawer();

        if (position > 0 && mCallbacks != null && !mCallbacks.onDrawerItemSelected(mItems.get(position - 1).titleResourceId)) {
            mListView.setItemChecked(mCurrentSelectedPosition, true);
        } else {
            mCurrentSelectedPosition = position;
            mListView.setItemChecked(position, true);
        }
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
        getActivity().runOnUiThread(() -> {
            loadItems(authenticated);
            selectItem(1);
        });
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
     * Adapter used by the drawer list to show navigation options.
     * TODO: Use a ViewHolder
     */
    private static class ListAdapter extends BaseAdapter {

        /** The list of displayed items. */
        private List<ListItem> mItems;

        public ListAdapter(List<ListItem> items) {
            mItems = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItem item = mItems.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(item.type.layoutId, parent, false);
            }

            switch (item.type) {
                case ICON_TEXT:
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
                    imageView.setImageResource(item.iconResourceId);

                case SUBHEADER:
                    TextView textView = (TextView) convertView.findViewById(R.id.title);
                    textView.setText(item.titleResourceId);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return Arrays.asList(ListItem.Type.values()).indexOf(mItems.get(position).type);
        }

        @Override
        public int getViewTypeCount() {
            return ListItem.Type.values().length;
        }

        @Override
        public boolean isEnabled(int position) {
            return mItems.get(position).type == ListItem.Type.ICON_TEXT;
        }

    }

    /**
     * Object that stores data about an entry in the drawer's list.
     */
    private static class ListItem {

        public final Type type;
        public final int titleResourceId;
        public final int iconResourceId;

        private ListItem(Type type, int titleResourceId, int iconResourceId) {
            this.type = type;
            this.titleResourceId = titleResourceId;
            this.iconResourceId = iconResourceId;
        }

        public static ListItem IconText(int titleResourceId, int iconResourceId) {
            return new ListItem(Type.ICON_TEXT, titleResourceId, iconResourceId);
        }

        public static ListItem SubHeader(int titleResourceId) {
            return new ListItem(Type.SUBHEADER, titleResourceId, 0);
        }

        public static ListItem Divider() {
            return new ListItem(Type.DIVIDER, 0, 0);
        }

        public static enum Type {
            ICON_TEXT(R.layout.item_icon_text),
            SUBHEADER(R.layout.item_subheader),
            DIVIDER(R.layout.item_divider);

            public final int layoutId;

            Type(int layoutId) {
                this.layoutId = layoutId;
            }
        }

    }

}
