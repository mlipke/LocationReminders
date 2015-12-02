package com.admuc.locationreminders.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.SurfaceHolder;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;
import com.admuc.locationreminders.utils.MapHelper;
import com.admuc.locationreminders.utils.NotificationHelper;
import com.admuc.locationreminders.utils.ReminderHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {

    private Location lastLocation = null;
    private List<Reminder> activeReminders;
    private GoogleApiClient googleApiClient;

    public LocationService() {}

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        buildGoogleApiClient();
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
                            new GooglePlaces(51.025914, 13.723698, LocationService.this).execute();
                            //new GooglePlaces(lastLocation.getLatitude(), lastLocation.getLongitude(), LocationService.this).execute();
                        }
                    } else {
                        if (lastLocation != null) {
                            ManualReminder r = (ManualReminder)activeReminders.get(i);
                            double distance = MapHelper.CalculationByDistance(r.getLocation(),
                                    MapHelper.convertLocation(lastLocation));
                            Log.d("distance: ", String.valueOf(distance));
                            if (distance < 0.2) {
                                NotificationHelper.createNotification(LocationService.this);
                            }
                        }
                    }
                }

                Log.d("Service", "Location check!");
            }
        };

        timer.scheduleAtFixedRate(locationCheckTask, 10000, 10000);


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected synchronized void buildGoogleApiClient() {
        Callback callback = new Callback();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(callback)
                .addOnConnectionFailedListener(callback)
                .addApi(LocationServices.API)
                .build();
    }

    private class Callback implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnected(Bundle bundle) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            Log.d("Last know location", Double.toString(lastLocation.getLatitude()) + Double.toString(lastLocation.getLongitude()));
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    }
}
