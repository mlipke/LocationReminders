package com.admuc.locationreminders.models;

/**
 * Created by 4gray on 30.10.15.
 */
public interface Reminder {

    long getTimestamp();

    Long getId();

    String getTitle();
    String getNote();

    String getLocationDescription();

    boolean isCompleted();

    void setTitle(String title);
    void setNote(String note);

    void setLocationDescription(String locationDescription);

    void setCompleted(boolean completed);

}
