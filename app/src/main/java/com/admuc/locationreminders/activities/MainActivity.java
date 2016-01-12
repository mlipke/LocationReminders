package com.admuc.locationreminders.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.adapters.ViewPagerAdapter;
import com.admuc.locationreminders.fragments.ActiveRemindersFragment;
import com.admuc.locationreminders.fragments.CompletedRemindersFragment;
import com.admuc.locationreminders.services.LocationService;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;

    private ActiveRemindersFragment activeRemindersFragment;
    private CompletedRemindersFragment completedRemindersFragment;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        if (preferences.getBoolean("pref_first_start", true)) {
            startLocationService();
            preferences.edit()
                    .putBoolean("pref_first_start", false)
                    .putBoolean("pref_service_running", true)
                    .apply();
        } else {
            if (preferences.getBoolean("pref_manual_control", false)) {
                if (!preferences.getBoolean("pref_service_running", false)) {
                    startLocationService();
                }
            } else {
                startLocationService();
            }
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ManageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (activeRemindersFragment != null)
            activeRemindersFragment.notifyDataSetChanged();
        if (completedRemindersFragment != null)
            completedRemindersFragment.notifyDataSetChanged();

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (preferences.getBoolean("pref_manual_control", false)) {
            menu.getItem(0).setVisible(true);
        }

        if (preferences.getBoolean("pref_service_running", false)) {
            menu.getItem(0).setIcon(R.drawable.ic_gps_fixed_24dp);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_gps_not_fixed_24dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_manual_control:
                toggleLocationService(item);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startLocationService() {
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
    }

    private void toggleLocationService(MenuItem item) {
        Intent intent = new Intent(this, LocationService.class);
        if (preferences.getBoolean("pref_service_running", false)) {
            stopService(intent);
            item.setIcon(R.drawable.ic_gps_not_fixed_24dp);
            preferences.edit().putBoolean("pref_service_running", false).apply();
        } else {
            startService(intent);
            item.setIcon(R.drawable.ic_gps_fixed_24dp);
            preferences.edit().putBoolean("pref_service_running", true).apply();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        activeRemindersFragment = new ActiveRemindersFragment();
        completedRemindersFragment = new CompletedRemindersFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment("ACTIVE", activeRemindersFragment);
        adapter.addFragment("COMPLETED", completedRemindersFragment);

        viewPager.setAdapter(adapter);
    }

}
