package com.admuc.locationreminders.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.admuc.locationreminders.fragments.SettingsFragment;

/**
 * Created by matt on 04/12/15.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
