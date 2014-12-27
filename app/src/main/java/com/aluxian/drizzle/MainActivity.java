package com.aluxian.drizzle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toolbar;

import com.aluxian.drizzle.api.ApiRequest;
import com.aluxian.drizzle.api.Dribbble;
import com.aluxian.drizzle.api.Params;
import com.aluxian.drizzle.fragments.DrawerFragment;
import com.aluxian.drizzle.fragments.ShotsCategoryFragment;
import com.aluxian.drizzle.fragments.ShotsFragment;
import com.aluxian.drizzle.utils.Config;
import com.aluxian.drizzle.utils.Log;
import com.anupcowkur.reservoir.Reservoir;

import java.util.Arrays;

public class MainActivity extends FragmentActivity implements DrawerFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        DrawerFragment drawerFragment = (DrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        drawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        getWindow().setAllowEnterTransitionOverlap(true);

        try {
            Reservoir.init(this, Config.CACHE_SIZE);
        } catch (Exception e) {
            Log.e(e);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (position) {
            case 0:
                transaction.replace(R.id.container, new ShotsFragment());
                break;
        }

        transaction.commit();
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
                View view = getLayoutInflater().inflate(R.layout.sort_dialog, null);

                final Spinner timeframeSpinner = (Spinner) view.findViewById(R.id.timeframe_spinner);
                final Spinner sortSpinner = (Spinner) view.findViewById(R.id.sort_spinner);

                ViewPager viewPager = (ViewPager) findViewById(R.id.container).findViewById(R.id.view_pager);

                ShotsFragment shotsFragment = (ShotsFragment) getSupportFragmentManager().getFragments().get(0);
                final ShotsCategoryFragment fragment = (ShotsCategoryFragment) shotsFragment.getChildFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem());

                timeframeSpinner.setSelection(Arrays.asList(Params.Timeframe.values()).indexOf(fragment.getTimeframeParam()));
                sortSpinner.setSelection(Arrays.asList(Params.Sort.values()).indexOf(fragment.getSortParam()));

                new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.setTimeframeParam(Params.Timeframe.values()[timeframeSpinner.getSelectedItemPosition()]);
                                fragment.setSortParam(Params.Sort.values()[sortSpinner.getSelectedItemPosition()]);
                                fragment.applyParams();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
