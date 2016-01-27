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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service implements LocationListener {

    private static final int FREQ_LOW = 1000 * 60 * 5; // five minutes
    private static final int FREQ_MID = 1000 * 60 * 3; // three minutes
    private static final int FREQ_HIGH = 1000 * 60; // one minute

    private Location lastLocation = null;
    private List<Reminder> activeReminders;
    private LocationManager locationManager;

    private SharedPreferences preferences;
    private int radius;

    public LocationService() {}

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);

        thread.start();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        radius = Integer.parseInt(preferences.getString("pref_radius", "200"));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location", "Forbidden");
            return;
        }

        if (preferences.getBoolean("pref_accuracy", false)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0 /*radius*/, this);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0 /*radius*/, this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Timer timer = new Timer();
        TimerTask locationCheckTask = new TimerTask() {
            @Override
            public void run() {
                activeReminders = ReminderHelper.getActiveReminders();

                for (int i = 0; i < activeReminders.size(); i++) {
                    if (activeReminders.get(i) instanceof AutomaticReminder) {
                        if (lastLocation != null) {
                            new GooglePlaces(MapHelper.convertLocation(lastLocation), activeReminders.get(i), new PlacesCallback() {
                                @Override
                                public void call(String response) {
                                    int limit = Integer.parseInt(preferences.getString("pref_suggestions", "10"));
                                    List<GooglePlace> venuesList = GoogleParser.parse(response, MapHelper.convertLocation(lastLocation), limit);
                                    for (int i = 0; i < venuesList.size(); i++) {
                                        if (venuesList.get(i).getDistance() < (radius/1000.0)) {
                                            NotificationHelper.createNotification(LocationService.this,
                                                    activeReminders.get(i), venuesList.get(i).getDistance());
                                            break;
                                        }
                                    }
                                }
                            }, getApplicationContext()).execute();
                        }
                    } else {
                        if (lastLocation != null) {
                            ManualReminder r = (ManualReminder)activeReminders.get(i);
                            double distance = MapHelper.CalculationByDistance(r.getLocation(),
                                    MapHelper.convertLocation(lastLocation));
                            Log.d("distance: ", String.valueOf(distance));
                            if (distance < (radius/1000.0)) {
                                Log.d("LocationService: ", "send notification");
                                NotificationHelper.createNotification(LocationService.this, activeReminders.get(i), distance);
                            }
                        }
                    }
                }

                Log.d("Service", "Location check!");
            }
        };

        String rate = preferences.getString("pref_frequency", "pref_not_set");
        Log.d("Rate", rate);

        switch (rate) {
            case "freq_low":
                timer.scheduleAtFixedRate(locationCheckTask, 10000, FREQ_LOW);
                break;
            case "freq_mid":
                timer.scheduleAtFixedRate(locationCheckTask, 10000, FREQ_MID);
                break;
            case "freq_high":
                timer.scheduleAtFixedRate(locationCheckTask, 10000, FREQ_HIGH);
                break;
            default:
                timer.scheduleAtFixedRate(locationCheckTask, 10000, 10000);
                break;
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
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
