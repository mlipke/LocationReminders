package com.admuc.locationreminders.utils;

import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by matt on 27/11/15.
 */
public class ReminderHelper {

    public static List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();

        Iterator<AutomaticReminder> automaticRemindersIterator = AutomaticReminder.findAll(AutomaticReminder.class);
        while (automaticRemindersIterator.hasNext()) {
            reminders.add(automaticRemindersIterator.next());
        }

        Iterator<ManualReminder> manualReminderIterator = ManualReminder.findAll(ManualReminder.class);
        while (manualReminderIterator.hasNext()) {
            reminders.add(manualReminderIterator.next());
        }

        Collections.sort(reminders, new ReminderComparator());

        return reminders;
    }

    public static List<Reminder> getActiveReminders() {
        List<Reminder> reminders = new ArrayList<>();
        Iterator<AutomaticReminder> automaticRemindersIterator = AutomaticReminder.findAsIterator(AutomaticReminder.class, "COMPLETED = ?", "0");
        while (automaticRemindersIterator.hasNext()) {
            reminders.add(automaticRemindersIterator.next());
        }

        Iterator<ManualReminder> manualReminderIterator = ManualReminder.findAsIterator(ManualReminder.class, "COMPLETED = ?", "0");
        while (manualReminderIterator.hasNext()) {
            reminders.add(manualReminderIterator.next());
        }

        Collections.sort(reminders, new ReminderComparator());

        return reminders;
    }

    public static List<Reminder> getCompletedReminders() {
        List<Reminder> reminders = new ArrayList<>();
        Iterator<AutomaticReminder> automaticRemindersIterator = AutomaticReminder.findAsIterator(AutomaticReminder.class, "COMPLETED = ?", "1");
        while (automaticRemindersIterator.hasNext()) {
            reminders.add(automaticRemindersIterator.next());
        }

        Iterator<ManualReminder> manualReminderIterator = ManualReminder.findAsIterator(ManualReminder.class, "COMPLETED = ?", "1");
        while (manualReminderIterator.hasNext()) {
            reminders.add(manualReminderIterator.next());
        }

        Collections.sort(reminders, new ReminderComparator());

        return reminders;
    }

}
