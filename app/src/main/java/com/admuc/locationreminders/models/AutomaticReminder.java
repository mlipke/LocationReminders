package com.admuc.locationreminders.models;

import com.orm.SugarRecord;

/**
 * Created by 4gray on 30.10.15.
 */
public class AutomaticReminder extends SugarRecord<AutomaticReminder> implements Reminder {

    private String poi;
    private String title;
    private String note;

    private long timestamp;

    public AutomaticReminder() {}

    public AutomaticReminder(String title, String note, String poi) {
        this.title = title;
        this.note = note;
        this.poi = poi;

        timestamp = System.currentTimeMillis();
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
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

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}