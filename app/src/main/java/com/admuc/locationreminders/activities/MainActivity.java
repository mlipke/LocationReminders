package com.admuc.locationreminders.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.admuc.locationreminders.LocationReminders;
import com.admuc.locationreminders.R;
import com.admuc.locationreminders.adapters.ViewPagerAdapter;
import com.admuc.locationreminders.fragments.ActiveRemindersFragment;
import com.admuc.locationreminders.fragments.CompletedRemindersFragment;
import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.Location;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;
import com.admuc.locationreminders.services.LocationService;

import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;

    private LocationReminders application;

    private Toolbar toolbar;
    private TabLayout tabLayout;

    private ActiveRemindersFragment activeRemindersFragment;
    private CompletedRemindersFragment completedRemindersFragment;

    private FloatingActionButton fab;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (LocationReminders) getApplication();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        checkPermissions();

        fab = (FloatingActionButton) findViewById(R.id.fab);
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

        refresh();

        if (application != null && application.shouldShowUndo())
            showUndoSnackBar();

        invalidateOptionsMenu();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        refresh();
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

    private void showUndoSnackBar() {
        final Reminder deletedReminder = application.getReminder();

        Snackbar.make(fab, "Deleted " + deletedReminder.getTitle(), Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (deletedReminder instanceof ManualReminder) {
                            ((ManualReminder) deletedReminder).setId(null);
                            ((ManualReminder) deletedReminder).save();
                        } else {
                            ((AutomaticReminder) deletedReminder).setId(null);
                            ((AutomaticReminder) deletedReminder).save();
                        }

                        refresh();
                    }
                }).show();

        application.setShowUndo(false);
        application.setReminder(null);
    }

    private void refresh() {
        if (activeRemindersFragment != null)
            activeRemindersFragment.notifyDataSetChanged();
        if (completedRemindersFragment != null)
            completedRemindersFragment.notifyDataSetChanged();
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }
    }
}
