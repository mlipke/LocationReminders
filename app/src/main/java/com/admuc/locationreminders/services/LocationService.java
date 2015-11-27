package com.admuc.locationreminders.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.admuc.locationreminders.R;

import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {

    public LocationService() {
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Timer timer = new Timer();
        TimerTask locationCheckTask = new TimerTask() {
            @Override
            public void run() {
                // new GooglePlaces().execute(51.025914, 13.723698, )

                createNotification();
                Log.d("Service", "Location check!");
            }
        };

        timer.scheduleAtFixedRate(locationCheckTask, 0, 10000);


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Reminder")
                .setSmallIcon(R.drawable.ic_location_on_24dp)
                .setContentText("Reminding you");

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(12345689, builder.build());
    }
}
