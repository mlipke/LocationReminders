package com.admuc.locationreminders.models;

import com.orm.SugarRecord;

/**
 * Created by 4gray on 30.10.15.
 */
public class AutomaticReminder extends SugarRecord<AutomaticReminder> implements Reminder {

    private String poi;
    private String title;
    private String note;

    private boolean completed;

    private long timestamp;

    public AutomaticReminder() {}

    public AutomaticReminder(String title, String note, String poi) {
        this.title = title;
        this.note = note;
        this.poi = poi;

        timestamp = System.currentTimeMillis();
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    public String getPoi() {
        return poi;
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

    @Override
    public String getLocationDescription() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
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

    @Override
    public void setLocationDescription(String locationDescription) {
        setPoi(locationDescription);
    }
}
