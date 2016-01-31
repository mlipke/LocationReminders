package com.admuc.locationreminders;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.admuc.locationreminders.models.Reminder;
import com.admuc.locationreminders.services.LocationService;
import com.orm.SugarApp;

/**
 * Created by matt on 15/01/16.
 */
public class LocationReminders extends SugarApp {

    private boolean showUndo;
    private Reminder reminder;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("pref_first_start", true)) {
            startLocationService();
            preferences.edit()
                    .putBoolean("pref_first_start", false)
                    .putBoolean("pref_service_running", true)
                    .apply();
        } else {
            if (preferences.getBoolean("pref_manual_control", false)) {
                if (preferences.getBoolean("pref_service_running", false)) {
                    startLocationService();
                }
            } else {
                startLocationService();
            }
        }
    }

    private void startLocationService() {
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public boolean shouldShowUndo() {
        return showUndo;
    }

    public void setShowUndo(boolean showUndo) {
        this.showUndo = showUndo;
    }
}
