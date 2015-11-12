package com.admuc.locationreminders.models;

/**
 * Created by 4gray on 30.10.15.
 */
public interface Reminder {

    String getTitle();
    String getNote();
    String getLocationDescription();

    void setTitle(String title);
    void setNote(String note);
    void setLocationDescription(String locationDescription);

    long getTimestamp();

}
