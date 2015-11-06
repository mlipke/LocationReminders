package com.admuc.locationreminders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.Location;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;

public class ManageActivity extends AppCompatActivity {

    private String [] arraySpinner = null;

    private RadioGroup locationRadioGroup;
    private String locationRadioValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arraySpinner = getResources().getStringArray(R.array.poiArray);

        ArrayAdapter<String> poiAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, arraySpinner);
        final AutoCompleteTextView poiTextView = (AutoCompleteTextView)
                findViewById(R.id.poiTextView);
        poiTextView.setAdapter(poiAdapter);

        // get fields and values
        final EditText title = (EditText) findViewById(R.id.title);
        final EditText note = (EditText) findViewById(R.id.note);


        // get selected location detection method
        locationRadioGroup = (RadioGroup)findViewById(R.id.locationRadioGroup);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Title:", title.getText().toString());
                if (title.getText().toString().equals("")) {
                    Snackbar.make(view, "Please add title", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    locationRadioValue = ((RadioButton) findViewById(locationRadioGroup.getCheckedRadioButtonId())).getText().toString();
                    Log.d("ReminderType:", locationRadioValue);

                    String selectedPoi = poiTextView.getText().toString();
                    Log.d("Selected POI: ", selectedPoi);

                    if (locationRadioValue.equals("automatic")) {
                        AutomaticReminder reminder = new AutomaticReminder(title.getText().toString(), note.getText().toString(), selectedPoi);
                        reminder.save();
                    } else {
                        Location location = new Location(1.2, 23.6);
                        ManualReminder reminder = new ManualReminder(title.getText().toString(), note.getText().toString(), location);
                        reminder.save();
                    }

                    finish();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeLocationDetection(View view) {
        locationRadioValue = ((RadioButton)findViewById(locationRadioGroup.getCheckedRadioButtonId())).getText().toString();

        Button setLocation = (Button) findViewById(R.id.setLocation);
        AutoCompleteTextView poiTextView = (AutoCompleteTextView) findViewById(R.id.poiTextView);
        TextView poiLabel = (TextView) findViewById(R.id.poiLabel);

        if(locationRadioValue.equals("manual")) {
            //hide location radio group and shop button for manual position selection
            setLocation.setVisibility(View.VISIBLE);
            poiTextView.setVisibility(View.INVISIBLE);
            poiLabel.setText("Set position");
        }
        else {
            // toggle back
            setLocation.setVisibility(View.INVISIBLE);
            poiTextView.setVisibility(View.VISIBLE);
            poiLabel.setText("Select type of POI");
        }
    }
}
