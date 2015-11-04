package com.admuc.locationreminders.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.models.Reminder;

import java.util.List;

/**
 * Created by matt on 31/10/15.
 */
public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<Reminder> reminders;

    public ReminderAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView locationString;

        public ImageView type;
        public ImageView locationIcon;
        public ImageView edit;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.locationString = (TextView) itemView.findViewById(R.id.locationString);
            this.type = (ImageView) itemView.findViewById(R.id.type);
            this.locationIcon = (ImageView) itemView.findViewById(R.id.locationIcon);
            this.edit = (ImageView) itemView.findViewById(R.id.edit);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_list_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);

        holder.title.setText(reminder.getTitle());
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
}
