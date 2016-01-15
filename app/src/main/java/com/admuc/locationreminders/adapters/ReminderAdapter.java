package com.admuc.locationreminders.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.activities.DetailActivity;
import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;
import com.admuc.locationreminders.utils.StringHelper;

import java.util.List;

/**
 * Created by matt on 31/10/15.
 */
public class ReminderAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private List<Reminder> reminders;

    public ReminderAdapter(List<Reminder> reminders, Context context) {
        this.context = context;
        this.reminders = reminders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_list_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, new ViewHolder.ImyViewHolderClicks() {
            @Override
            public void onReminder(long id, String type) {

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("REMINDER_TYPE", type);
                intent.putExtra("REMINDER_ID", id);
                context.startActivity(intent);

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        
        if (reminder instanceof ManualReminder) {
            holder.typeName = "MANUAL";
            holder.id = reminder.getId();
            holder.circleIcon.setColorFilter(Color.GRAY);
        } else if (reminder instanceof AutomaticReminder) {
            holder.typeName = "AUTOMATIC";
            holder.id = reminder.getId();
            holder.locationIcon.setImageResource(R.drawable.ic_store_24dp);
            holder.locationIcon.setColorFilter(Color.parseColor("#7f7f7f"));
        }

        holder.title.setText(reminder.getTitle());
        holder.locationString.setText(StringHelper.convertToReadableString(reminder.getLocationDescription()));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    @Override
    public long getItemId(int position) {
        return reminders.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        if (reminders.get(position) instanceof ManualReminder)
            return 1;
        else
            return 2;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
}
