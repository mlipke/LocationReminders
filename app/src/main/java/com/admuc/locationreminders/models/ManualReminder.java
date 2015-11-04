package com.admuc.locationreminders.models;

import com.orm.SugarRecord;

/**
 * Created by 4gray on 30.10.15.
 */
public class ManualReminder extends SugarRecord<ManualReminder> implements Reminder  {

    private String title;
    private String note;

    private long timestamp;

    private Location location;

    public ManualReminder() {}

    public ManualReminder(String title, String note, Location location) {
        this.title = title;
        this.note = note;
        this.location = location;

        timestamp = System.currentTimeMillis();

    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

}
