package com.admuc.locationreminders.models;

import android.util.Log;

import com.orm.SugarRecord;

/**
 * Created by 4gray on 30.10.15.
 */
public class ManualReminder extends SugarRecord<ManualReminder> implements Reminder {

    private String title;
    private String note;

    private boolean completed;

    private long timestamp;

    private Location location;

    public ManualReminder() {
    }

    @Override
    public void save() {
        timestamp = System.currentTimeMillis();
        Log.d("TIME: ", String.valueOf(timestamp));
        location.save();
        super.save();
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String getLocationDescription() {
        return location.toString();
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void setLocationDescription(String locationDescription) {
        this.location.setDescription(locationDescription);
    }
}
