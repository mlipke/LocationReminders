package com.admuc.locationreminders.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.admuc.locationreminders.R;

/**
 * Created by matt on 27/11/15.
 */
public class NotificationHelper {

    public static void createNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Reminder")
                .setSmallIcon(R.drawable.ic_location_on_24dp)
                .setContentText("Reminding you");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(12345689, builder.build());
    }

}
