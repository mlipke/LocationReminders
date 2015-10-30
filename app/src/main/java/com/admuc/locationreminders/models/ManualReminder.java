package com.admuc.locationreminders.models;

import com.orm.SugarRecord;

import java.sql.Timestamp;

/**
 * Created by 4gray on 30.10.15.
 */
public class ManualReminder extends SugarRecord<ManualReminder> implements Reminder  {

    private String title;
    private String note;

    private long timestamp;

    private Location location;

    public ManualReminder(String title, String note, Location location) {
        this.title = title;
        this.note = note;
        this.location = location;

        timestamp = System.currentTimeMillis();

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public long getTimestamp() {
        return timestamp;
    }

}
