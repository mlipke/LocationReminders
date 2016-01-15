package com.admuc.locationreminders;

import android.app.Application;

import com.admuc.locationreminders.models.Reminder;
import com.orm.SugarApp;

/**
 * Created by matt on 15/01/16.
 */
public class LocationReminders extends SugarApp {

    private boolean showUndo;
    private Reminder reminder;

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
