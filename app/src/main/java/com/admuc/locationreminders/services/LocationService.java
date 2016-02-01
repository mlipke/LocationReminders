package com.admuc.locationreminders.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.GooglePlace;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;
import com.admuc.locationreminders.utils.GoogleParser;
import com.admuc.locationreminders.utils.MapHelper;
import com.admuc.locationreminders.utils.NotificationHelper;
import com.admuc.locationreminders.utils.ReminderHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Background service is used for managing API requests to Google Places API
 * to check for locations near user position and send notification
 */

public class LocationService extends Service implements LocationListener {

    private static final int FREQ_LOW = 1000 * 60 * 5; // five minutes
    private static final int FREQ_MID = 1000 * 60 * 3; // three minutes
    private static final int FREQ_HIGH = 1000 * 60; // one minute

    private List<Reminder> activeReminders;

    private SharedPreferences preferences;
    private int radius;

    private LocationManager locationManager;

    public LocationService() {}

    @Override
    public void onCreate() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        radius = Integer.parseInt(preferences.getString("pref_radius", "200"));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location", "Forbidden");
            return;
        }

        String rate = preferences.getString("pref_frequency", "pref_not_set");
        long interval = 10000;

        switch (rate) {
            case "freq_low":
                interval = FREQ_LOW;
                break;
            case "freq_mid":
                interval = FREQ_MID;
                break;
            case "freq_high":
                interval = FREQ_HIGH;
                break;
            default:
                interval = 10000;
                break;
        }


        if (preferences.getBoolean("pref_accuracy", false)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 0 /*radius*/, this);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 0 /*radius*/, this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "Started!");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            locationManager.removeUpdates(this);
            locationManager = null;
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        Log.d("Service", "Stopped!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location", "Changed!");

        final Location finalLocation = location;
        activeReminders = ReminderHelper.getActiveReminders();

        for (int i = 0; i < activeReminders.size(); i++) {
            if (activeReminders.get(i) instanceof AutomaticReminder) {
                new GooglePlaces(MapHelper.convertLocation(location), activeReminders.get(i), new PlacesCallback() {
                    @Override
                    public void call(String response) {
                        int limit = Integer.parseInt(preferences.getString("pref_suggestions", "10"));
                        List<GooglePlace> venuesList = GoogleParser.parse(response, MapHelper.convertLocation(finalLocation), limit);
                        for (int i = 0; i < venuesList.size(); i++) {
                            if (venuesList.get(i).getDistance() < (radius/1000.0)) {
                                NotificationHelper.createNotification(LocationService.this,
                                        activeReminders.get(i), venuesList.get(i).getDistance());
                                break;
                            }
                        }
                    }
                }, getApplicationContext()).execute();
            } else {
                ManualReminder r = (ManualReminder)activeReminders.get(i);
                double distance = MapHelper.CalculationByDistance(r.getLocation(),
                        MapHelper.convertLocation(location));
                Log.d("distance: ", String.valueOf(distance));
                if (distance < (radius/1000.0)) {
                    Log.d("LocationService: ", "send notification");
                    NotificationHelper.createNotification(LocationService.this, activeReminders.get(i), distance);
                }
            }
        }

        Log.d("Last know location", Double.toString(location.getLatitude()) + Double.toString(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
