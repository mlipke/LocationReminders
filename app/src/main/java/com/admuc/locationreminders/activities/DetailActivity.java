package com.admuc.locationreminders.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.GooglePlace;
import com.admuc.locationreminders.models.Location;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;
import com.admuc.locationreminders.services.GooglePlaces;
import com.admuc.locationreminders.services.PlacesCallback;
import com.admuc.locationreminders.utils.GoogleParser;
import com.admuc.locationreminders.utils.MapHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Reminder reminder;

    private long _id;
    private String type;
    private GoogleMap mMap;
    private boolean _isCompleted;
    private ListView poiListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        type = intent.getStringExtra("REMINDER_TYPE");
        _id = intent.getLongExtra("REMINDER_ID", 0);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMap = mapFragment.getMap();
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mapFragment.getMapAsync(new MapListener());

        poiListView = (ListView) findViewById(R.id.listView);

        setupViews();

        //List listTitle = new ArrayList();
        //ArrayAdapter myAdapter = new ArrayAdapter(DetailActivity.class, android.R.layout.simple_list_item_1, listTitle);

    }

    private void setupViews() {
        if (type.equals("MANUAL")) {
            reminder = ManualReminder.findById(ManualReminder.class, _id);

            LatLng position = MapHelper.convertLatLng(((ManualReminder)reminder).getLocation());
            MarkerOptions options = new MarkerOptions().position(position);
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

            Location location = ((ManualReminder) reminder).getLocation();
            new GooglePlaces(location, reminder, new Callback(location)).execute();
        } else if (type.equals("AUTOMATIC")) {
            reminder = AutomaticReminder.findById(AutomaticReminder.class, _id);

            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(android.location.Location location) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    LatLng ll = new LatLng(lat, lng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

                    Location loc = new Location(lat, lng);
                    new GooglePlaces(loc, reminder, new Callback(loc)).execute();
                }
            });
        }

        _isCompleted = reminder.isCompleted();

        TextView titleView = (TextView) findViewById(R.id.titleView);
        titleView.setText(reminder.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        MenuItem completed = menu.findItem(R.id.action_complete);

        if (_isCompleted)
            completed.setVisible(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {

            Intent intent = new Intent(getApplicationContext(), ManageActivity.class);
            intent.putExtra("REMINDER_ID", _id);
            intent.putExtra("REMINDER_TYPE", type);
            startActivity(intent);

            return true;
        } else if (id == R.id.action_remove) {

            // show remove confirmation dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(DetailActivity.this);
            alert.setTitle("Alert!!");
            alert.setMessage("Are you sure to delete this reminder?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (type.equals("MANUAL")) {
                        ManualReminder mReminder = ManualReminder.findById(ManualReminder.class, _id);
                        mReminder.delete();
                    } else if (type.equals("AUTOMATIC")) {
                        AutomaticReminder aReminder = AutomaticReminder.findById(AutomaticReminder.class, _id);
                        aReminder.delete();
                    }

                    finish();

                    Toast.makeText(getApplicationContext(), "Reminder was deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.show();

            return true;
        } else if (id == R.id.action_complete) {
            reminder.setCompleted(true);

            if (type.equals("MANUAL")) {
                ((ManualReminder) reminder).save();
            } else if (type.equals("AUTOMATIC")) {
                ((AutomaticReminder) reminder).save();
            }

            finish();
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private class MapListener implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            toolbar.bringToFront();
        }
    }

    private class Callback implements PlacesCallback {

        private Location location;

        public Callback(Location location) {
            this.location = location;
        }


        @Override
        public void call(String response) {
            ArrayList<GooglePlace> venuesList = GoogleParser.parse(response, location);

            List listTitle = new ArrayList();

            for (int i = 0; i < venuesList.size(); i++) {
                // make a list of the venus that are loaded in the list.
                // show the name, the category and the city
                listTitle.add(i,
                        venuesList.get(i).getName() +
                                "\nOpen Now: " + venuesList.get(i).getOpenNow() +
                                "\n(" + venuesList.get(i).getCategory() + ")" +
                                "\n(" + venuesList.get(i).getDistance() + ")");
            }

            // set the results to the list
            // and show them in the xml
            ArrayAdapter myAdapter = new ArrayAdapter(DetailActivity.this, android.R.layout.simple_list_item_1, listTitle);
            poiListView.setAdapter(myAdapter);
        }
    }

}
