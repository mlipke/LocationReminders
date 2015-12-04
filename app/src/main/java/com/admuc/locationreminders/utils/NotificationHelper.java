package com.admuc.locationreminders.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.activities.DetailActivity;
import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;

/**
 * Created by matt on 27/11/15.
 */
public class NotificationHelper {

    public static void createNotification(Context context, Reminder reminder, double distance) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Reminder")
                .setSmallIcon(R.drawable.ic_location_on_24dp)
             //   .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentText(reminder.getLocationDescription() + ". Distance: "+ distance);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, DetailActivity.class);

        if (reminder instanceof AutomaticReminder) {
            notificationIntent.putExtra("REMINDER_ID", ((AutomaticReminder)reminder).getId());
            notificationIntent.putExtra("REMINDER_TYPE", "AUTOMATIC");
        }
        else {
            notificationIntent.putExtra("REMINDER_ID", ((ManualReminder)reminder).getId());
            notificationIntent.putExtra("REMINDER_TYPE", "MANUAL");
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(intent);
        manager.notify(12345689, builder.build());
    }

}
