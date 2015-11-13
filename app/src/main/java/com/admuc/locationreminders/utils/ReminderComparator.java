package com.admuc.locationreminders.utils;

import com.admuc.locationreminders.models.Reminder;

import java.util.Comparator;

/**
 * Created by matt on 13/11/15.
 */
public class ReminderComparator implements Comparator<Reminder> {
    @Override
    public int compare(Reminder lhs, Reminder rhs) {
        return Long.compare(lhs.getTimestamp(), rhs.getTimestamp());
    }
}
