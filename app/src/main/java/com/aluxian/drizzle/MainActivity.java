package com.aluxian.drizzle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
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
import com.aluxian.drizzle.fragments.ShotsFragment;
import com.aluxian.drizzle.fragments.TabsFragment;
import com.aluxian.drizzle.ui.AdvancedToolbar;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.anupcowkur.reservoir.Reservoir;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity implements DrawerFragment.Callbacks {

    private DrawerLayout mDrawerLayout;
    private DrawerFragment mDrawerFragment;
    private AdvancedToolbar mToolbar;
    private View mSearchContainer;
    private boolean mContainerHasFragment;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchContainer = findViewById(R.id.search_container);

        mToolbar = (AdvancedToolbar) findViewById(R.id.toolbar);
        setActionBar(mToolbar);

        getWindow().setAllowEnterTransitionOverlap(true);

        // Set up the navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerFragment = (DrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout);

        // Initialise the cache storage
        try {
            Reservoir.init(this, Config.CACHE_SIZE);
        } catch (Exception e) {
            Log.e(e);
        }
    }

    public void searchMode(boolean visible) {
        if (visible) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerFragment.toggleActionBarIcon(DrawerFragment.ActionDrawableState.ARROW, true);
            mToolbar.showSearchView();

            mSearchContainer.setVisibility(View.VISIBLE);
            mSearchContainer.animate().alpha(1);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerFragment.toggleActionBarIcon(DrawerFragment.ActionDrawableState.BURGER, true);
            mToolbar.hideSearchView();

            mSearchContainer.animate().alpha(0).withEndAction(() -> mSearchContainer.setVisibility(View.GONE));
        }
    }

    @Override
    public boolean onDrawerItemSelected(int titleResourceId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        boolean remainSelected = true;

        if (mContainerHasFragment) {
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        switch (titleResourceId) {
            case R.string.drawer_main_shots:
                transaction.replace(R.id.tabs_container, new TabsFragment());
                mContainerHasFragment = true;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mToolbar.isSearchViewShown()) {
                    searchMode(false);
                    return true;
                } else {
                    return false;
                }

            case R.id.action_search:
                searchMode(true);
                return true;

            case R.id.action_sort:
                @SuppressLint("InflateParams")
                View view = getLayoutInflater().inflate(R.layout.dialog_sort, null);

                final Spinner timeframeSpinner = (Spinner) view.findViewById(R.id.timeframe_spinner);
                final Spinner sortSpinner = (Spinner) view.findViewById(R.id.sort_spinner);

                // Get the selected fragment from the ViewPager
                ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
                Fragment tabsFragment = getSupportFragmentManager().getFragments().get(0);
                String fragmentTag = "android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem();
                final ShotsFragment fragment = (ShotsFragment) tabsFragment.getChildFragmentManager().findFragmentByTag(fragmentTag);

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
            /*TypedArray attrs = getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
            float actionBarSize = attrs.getDimension(0, 0);
            attrs.recycle();

            final View actionBarView = findViewById(R.id.toolbar);
            final float newTopMargin = -actionBarSize;

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionBarView.getLayoutParams();
            final int initialMargin = params.topMargin;

            Animation anim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionBarView.getLayoutParams();
                    params.topMargin = (int) (newTopMargin * interpolatedTime) + initialMargin;
                    actionBarView.setLayoutParams(params);
                }
            };

            anim.setDuration(200);
            actionBarView.startAnimation(anim);*/

            super.onBackPressed();
        }
    }

}
