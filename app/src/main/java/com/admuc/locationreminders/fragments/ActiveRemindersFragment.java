package com.admuc.locationreminders.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.adapters.ReminderAdapter;
import com.admuc.locationreminders.utils.ReminderHelper;
import com.admuc.locationreminders.utils.SimpleDividerItemDecoration;

/**
 * Created by matt on 01/12/15.
 */
public class ActiveRemindersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;

    public ActiveRemindersFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_reminders, container, false);

        adapter = new ReminderAdapter(ReminderHelper.getActiveReminders(), getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        return view;
    }

    public void notifyDatasetChanged() {
        adapter.setReminders(ReminderHelper.getActiveReminders());
        adapter.notifyDataSetChanged();
    }
}
