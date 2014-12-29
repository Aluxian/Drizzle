package com.aluxian.drizzle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toolbar;

import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.fragments.DrawerFragment;
import com.aluxian.drizzle.fragments.ShotsFragment;
import com.aluxian.drizzle.fragments.TabsFragment;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.anupcowkur.reservoir.Reservoir;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity implements DrawerFragment.Callbacks {

    private DrawerFragment mDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBar((Toolbar) findViewById(R.id.toolbar));
        getWindow().setAllowEnterTransitionOverlap(true);

        mDrawerFragment = (DrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        // Initialise the cache storage
        try {
            Reservoir.init(this, Config.CACHE_SIZE);
        } catch (Exception e) {
            Log.e(e);
        }
    }

    @Override
    public boolean onDrawerItemSelected(int titleResourceId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        boolean remainSelected = true;

        switch (titleResourceId) {
            case R.string.drawer_main_shots:
                transaction.replace(R.id.container, new TabsFragment());
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
                        .setPositiveButton(R.string.dialog_apply, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.setTimeframeParam(Params.Timeframe.values()[timeframeSpinner.getSelectedItemPosition()]);
                                fragment.setSortParam(Params.Sort.values()[sortSpinner.getSelectedItemPosition()]);
                                fragment.applyParams();
                            }
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
