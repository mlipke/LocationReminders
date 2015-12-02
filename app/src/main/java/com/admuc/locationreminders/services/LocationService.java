package com.admuc.locationreminders.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;
import com.admuc.locationreminders.utils.MapHelper;
import com.admuc.locationreminders.utils.NotificationHelper;
import com.admuc.locationreminders.utils.ReminderHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service implements LocationListener {

    private Location lastLocation = null;
    private List<Reminder> activeReminders;
    //private GoogleApiClient googleApiClient;
    private LocationManager locationManager;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        //buildGoogleApiClient();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location", "Forbidden");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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
                            new GooglePlaces(MapHelper.convertLocation(lastLocation), ((AutomaticReminder) activeReminders.get(i)).getPoi(), LocationService.this).execute();
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


    /*
    protected synchronized void buildGoogleApiClient() {
        Callback callback = new Callback();

        Log.d("Service", "build google api!");

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
    */
}
