package com.aluxian.drizzle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.fragments.DrawerFragment;
import com.aluxian.drizzle.fragments.IntroFragment;
import com.aluxian.drizzle.fragments.ShotsFragment;
import com.aluxian.drizzle.fragments.TabsFragment;
import com.aluxian.drizzle.ui.AdvancedToolbar;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.anupcowkur.reservoir.Reservoir;

import java.util.Arrays;
import java.util.List;

import static com.aluxian.drizzle.fragments.DrawerFragment.DrawerIconState;

public class MainActivity extends FragmentActivity implements DrawerFragment.Callbacks, IntroFragment.Callbacks {

    private static final String PREF_INTRO_FINISHED = "intro_finished";

    private SharedPreferences mSharedPrefs;

    private DrawerFragment mDrawerFragment;
    private AdvancedToolbar mToolbar;
    private View mSearchContainer;
    private boolean mHasFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSearchContainer = findViewById(R.id.search_results_container);

        // Set the toolbar
        mToolbar = (AdvancedToolbar) findViewById(R.id.toolbar);
        setActionBar(mToolbar);

        // Set up the navigation drawer
        mDrawerFragment = (DrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        // Intro view
        if (!mSharedPrefs.getBoolean(PREF_INTRO_FINISHED, false)) {
            mToolbar.hide(false);
            mHasFragment = true;
            mDrawerFragment.setDrawerLocked(true);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, new IntroFragment());
            transaction.commit();
        }

        // Initialise the cache storage
        try {
            Reservoir.init(this, Config.CACHE_SIZE);
        } catch (Exception e) {
            Log.e(e);
        }

        //getWindow().setAllowEnterTransitionOverlap(true);
    }

    /**
     * Toggle search mode. When search mode is active, a view is drawn above the existing tabs and shots grid. Search results will be shown
     * in this view, along with search suggestions from the past.
     *
     * @param active Whether to make search mode active or not.
     */
    public void searchMode(boolean active) {
        if (active) {
            mToolbar.showSearchView();
            //mSearchContainer.setVisibility(View.VISIBLE);
            //mSearchContainer.animate().alpha(1);

            findViewById(R.id.main_container).animate().alpha(0);

            mDrawerFragment.setDrawerLocked(true);
            mDrawerFragment.toggleDrawerIcon(DrawerIconState.ARROW, true);
        } else {
            mToolbar.hideSearchView();
            //mSearchContainer.setVisibility(View.GONE);
            //mSearchContainer.animate().alpha(0).withEndAction(() -> mSearchContainer.setVisibility(View.GONE));

            findViewById(R.id.main_container).animate().alpha(1);

            mDrawerFragment.setDrawerLocked(false);
            mDrawerFragment.toggleDrawerIcon(DrawerIconState.BURGER, true);
        }
    }

    @Override
    public boolean onDrawerItemSelected(int titleResourceId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        boolean remainSelected = true;

        // Only use a fade animation if there's an existing fragment in the main container already; prevents fading on app start
        if (mHasFragment) {
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        switch (titleResourceId) {
            case R.string.drawer_main_shots:
                transaction.replace(R.id.main_container, new TabsFragment());
                mHasFragment = true;
                break;

            case R.string.drawer_personal_sign_in:
                remainSelected = false;
                break;

            case R.string.drawer_app_rate:
                Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                List<ResolveInfo> list = getPackageManager().queryIntentActivities(rateIntent, 0);

                if (list.size() > 0) {
                    startActivity(rateIntent);
                } else {
                    rateIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    startActivity(rateIntent);
                }

                remainSelected = false;
                break;

            case R.string.drawer_app_feedback:
                String uri = "mailto:" + Uri.encode(getString(R.string.send_feedback_email))
                        + "?subject=" + Uri.encode(getString(R.string.send_feedback_subject))
                        + "&body=" + Uri.encode(getString(R.string.send_feedback_body));

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
                startActivity(Intent.createChooser(sendIntent, getString(R.string.send_feedback_with)));

                remainSelected = false;
                break;
        }

        transaction.commit();
        return remainSelected;
    }

    @Override
    public void onIntroButtonClicked(int id) {
        Log.d(id);

        switch (id) {
            case R.id.btn_sign_in:
                mSharedPrefs.edit().putBoolean(PREF_INTRO_FINISHED, true).apply();

                // TODO: Open sign in view

                break;

            case R.id.btn_sign_up:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.SIGN_UP_URL)));
                break;

            case R.id.btn_skip:
                mToolbar.show(true);
                mDrawerFragment.setDrawerLocked(false);
                mSharedPrefs.edit().putBoolean(PREF_INTRO_FINISHED, true).apply();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.main_container, new TabsFragment());
                transaction.commit();

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mToolbar.isSearchViewShown()) {
                    searchMode(false);
                    return true;
                }

                return false;

            case R.id.action_search:
                searchMode(true);
                return true;

            case R.id.action_sort:
                @SuppressLint("InflateParams")
                View view = getLayoutInflater().inflate(R.layout.dialog_sort, null, false);

                Spinner timeframeSpinner = (Spinner) view.findViewById(R.id.timeframe_spinner);
                Spinner sortSpinner = (Spinner) view.findViewById(R.id.sort_spinner);

                Log.d(getSupportFragmentManager().getFragments());

                // Get the selected fragment from the ViewPager
                Fragment tabsFragment = null;

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment != null && fragment.getClass().equals(TabsFragment.class)) {
                        tabsFragment = fragment;
                    }
                }

                ViewPager viewPager = (ViewPager) tabsFragment.getView().findViewById(R.id.view_pager);
                String fragmentTag = "android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem();
                ShotsFragment fragment = (ShotsFragment) tabsFragment.getChildFragmentManager().findFragmentByTag(fragmentTag);

                // Restore active values
                timeframeSpinner.setSelection(Arrays.asList(Params.Timeframe.values()).indexOf(fragment.getTimeframeParam()));
                sortSpinner.setSelection(Arrays.asList(Params.Sort.values()).indexOf(fragment.getSortParam()));

                new AlertDialog.Builder(this, R.style.DrizzleTheme_Dialog)
                        .setView(view)
                        .setPositiveButton(R.string.dialog_apply, (dialog, which) -> {
                            fragment.setTimeframeParam(Params.Timeframe.values()[timeframeSpinner.getSelectedItemPosition()]);
                            fragment.setSortParam(Params.Sort.values()[sortSpinner.getSelectedItemPosition()]);
                            fragment.applyParams();
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerFragment.isDrawerOpen()) {
            mDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

}
