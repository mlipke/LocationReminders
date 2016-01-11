package com.admuc.locationreminders.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
                .setContentTitle(reminder.getTitle())
                .setSmallIcon(R.drawable.ic_location_on_24dp)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE |
                        Notification.DEFAULT_SOUND)
                .setContentText(StringHelper.convertToReadableString(reminder.getLocationDescription())
                        + " | " + MapHelper.convertKmToMeter(distance) + "m");
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, DetailActivity.class);

        if (reminder instanceof AutomaticReminder) {
            notificationIntent.putExtra("REMINDER_ID", reminder.getId());
            notificationIntent.putExtra("REMINDER_TYPE", "AUTOMATIC");
        }
        else {
            notificationIntent.putExtra("REMINDER_ID", reminder.getId());
            notificationIntent.putExtra("REMINDER_TYPE", "MANUAL");
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent completedIntent = new Intent(context, CompletedListener.class);
        completedIntent.setAction("de.admuc.Completed");
        completedIntent.putExtra("REMINDER_ID", reminder.getId());
        if (reminder instanceof ManualReminder)
            completedIntent.putExtra("REMINDER_TYPE", "MANUAL");
        else {
            completedIntent.putExtra("REMINDER_TYPE", "AUTOMATIC");
        }
        PendingIntent completedPendingIntent = PendingIntent.getBroadcast(context, 0, completedIntent, Intent.FILL_IN_DATA);

        builder.addAction(R.drawable.ic_done_24dp, "Completed", completedPendingIntent);

        builder.setContentIntent(intent);
        manager.notify((int) (long) reminder.getId(), builder.build());
    }

    public static class CompletedListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Receiver", "Received");

            long id = intent.getLongExtra("REMINDER_ID", 0);
            String type = intent.getStringExtra("REMINDER_TYPE");

            Log.d("Receiver", Long.toString(id));
            Log.d("Receiver", type);

            if (type != null) {
                if (type.equals("MANUAL")) {
                    ManualReminder reminder = ManualReminder.findById(ManualReminder.class, id);
                    reminder.setCompleted(true);
                    reminder.save();
                } else {
                    AutomaticReminder reminder = AutomaticReminder.findById(AutomaticReminder.class, id);
                    reminder.setCompleted(true);
                    reminder.save();
                }
            }
        }

    }

}
