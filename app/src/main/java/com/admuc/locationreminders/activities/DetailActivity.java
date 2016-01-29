package com.admuc.locationreminders.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.admuc.locationreminders.LocationReminders;
import com.admuc.locationreminders.R;
import com.admuc.locationreminders.adapters.GooglePlacesListViewAdapter;
import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.GooglePlace;
import com.admuc.locationreminders.models.Location;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.Reminder;
import com.admuc.locationreminders.services.GooglePlaces;
import com.admuc.locationreminders.services.PlacesCallback;
import com.admuc.locationreminders.utils.GoogleParser;
import com.admuc.locationreminders.utils.GooglePlaceComparator;
import com.admuc.locationreminders.utils.MapHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Reminder reminder;

    private long _id;
    private String type;
    private GoogleMap mMap;
    private boolean _isCompleted;
    private ListView poiListView;
    private boolean _isMyLocationDetected = false;
    private int _poiLimit;
    private int _radius;
    private ProgressBar _loadingIndicator;
    private LinearLayout preloaderBackground;
    private android.location.Location _startLocation = null;
    private double locationDistance;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         preloaderBackground = (LinearLayout) findViewById(R.id.preloaderBackground);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        _loadingIndicator = (ProgressBar) findViewById(R.id.preloader);
        _loadingIndicator.setVisibility(View.VISIBLE);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _poiLimit = Integer.parseInt(preferences.getString("pref_suggestions", "5"));
        _radius = Integer.parseInt(preferences.getString("pref_radius", "200"));

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
        //mMap.getUiSettings().setScrollGesturesEnabled(false);
        //mMap.getUiSettings().setZoomControlsEnabled(false);
        //mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);


        mapFragment.getMapAsync(new MapListener());

        poiListView = (ListView) findViewById(R.id.listView);

        setupViews();

    }

    private void setupViews() {
        if (type.equals("MANUAL")) {
            reminder = ManualReminder.findById(ManualReminder.class, _id);

            LatLng position = MapHelper.convertLatLng(((ManualReminder)reminder).getLocation());
            MarkerOptions options = new MarkerOptions().position(position);
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

            Location location = ((ManualReminder) reminder).getLocation();

            new GooglePlaces(location, reminder, new Callback(location), getApplicationContext()).execute();
        } else if (type.equals("AUTOMATIC")) {
            reminder = AutomaticReminder.findById(AutomaticReminder.class, _id);

        }


        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(android.location.Location location) {

                if (!_isMyLocationDetected) {
                    _startLocation = location;
                    getAndShowPois(location);
                }

                // distance between first location, which was detected on activity start and actual location (for live/dynamic map)
                locationDistance = MapHelper.CalculationByDistance(
                        MapHelper.convertLocation(_startLocation),
                        MapHelper.convertLocation(location));
                //Log.d("Location distance: ", String.valueOf(locationDistance));
                //Log.d("Location radius: ", String.valueOf(_radius/1000.0/2.0));

                if (locationDistance > (_radius/1000.0/2.0)) {
                    Log.d("Map update ", "");
                    getAndShowPois(location);
                }

            }
        });

        _isCompleted = reminder.isCompleted();

        TextView titleView = (TextView) findViewById(R.id.titleView);
        titleView.setText(reminder.getTitle());
    }


    public void getAndShowPois(android.location.Location location) {

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng ll = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

        Location loc = new Location(lat, lng);
        _isMyLocationDetected = true;

        // location caching for automatic reminders
        if (type.equals("AUTOMATIC")) {
            List<GooglePlace> poiList = checkForNearLocationsInCache(loc, reminder);

            if (poiList.size() != 0) {
                showCachedLocations(poiList);
                Log.d("Location caching ", "works");
                _loadingIndicator.setVisibility(View.INVISIBLE);
                preloaderBackground.setVisibility(View.INVISIBLE);
            }
            else {
                new GooglePlaces(loc, reminder, new Callback(loc), getApplicationContext()).execute();
                _loadingIndicator.setVisibility(View.INVISIBLE);
                preloaderBackground.setVisibility(View.INVISIBLE);
            }
        }
        else {
            new GooglePlaces(loc, reminder, new Callback(loc), getApplicationContext()).execute();
            _loadingIndicator.setVisibility(View.INVISIBLE);
            preloaderBackground.setVisibility(View.INVISIBLE);
        }
    }

    private List<GooglePlace> checkForNearLocationsInCache(Location location, Reminder reminder) {
        List<GooglePlace> places = new ArrayList<>();
        Location dbLocation;
        String type;
        String reminderType;
        double distance;

        // get saved locations near my location
        // iterate over saved locations and check distance
        // if distance < 0.2 save it to list
        // if list is empty -> request google places api
        //Iterator<GooglePlace> googlePlaceIterator = GooglePlace.findAll(GooglePlace.class);
        //while (googlePlaceIterator.hasNext()) {
        List<GooglePlace> googlePlacesList = GooglePlace.listAll(GooglePlace.class);
        for (int i = 0; i < googlePlacesList.size(); i++) {
            dbLocation = googlePlacesList.get(i).getLocation();
            type = googlePlacesList.get(i).getType();
            Log.d("Caching: ", "try to get pois from cache");

            // check type of location - for automatic reminders
            List<String> poiTypes = Arrays.asList(type.split("\\s*,\\s*"));
            if (poiTypes.size() != 0) {
                //Log.d("type check: ", googlePlacesList.get(i).getType());
                for (int x = 0; x < poiTypes.size(); x++) {

                    reminderType = ((AutomaticReminder)reminder).getPoi();
                    if (poiTypes.get(x).equals(reminderType)) {

                        distance = MapHelper.CalculationByDistance(dbLocation, location);
                        if (distance <= (_radius/1000.0)) {
                            //Log.d("match!", googlePlacesList.get(i).getName());
                            googlePlacesList.get(i).setDistance(distance);
                            googlePlacesList.get(i).setOpenNow("");
                            places.add(googlePlacesList.get(i));
                        }
                    }
                }
            }
        }

        // if number of locations from cache < suggestion limit from preferences
        if (places.size() < _poiLimit) {
            places.clear();
            Log.d("Caching: ", "clear places");
        }
        else {
            places = limitList(places);
        }
        return places;
    }

    private List<GooglePlace> limitList(List places) {
        if (_poiLimit > places.size()) {
            return places;
        } else {
            return places.subList(0, _poiLimit);
        }
    }

    private void showCachedLocations(List<GooglePlace> cachedLocations) {
        for (int i = 0; i < cachedLocations.size(); i++) {
            LatLng position = MapHelper.convertLatLng(cachedLocations.get(i).getLocation());
            MarkerOptions options = new MarkerOptions().position(position);
            mMap.addMarker(options).setTitle(cachedLocations.get(i).getName());
            Log.d("Fill list with: ", cachedLocations.get(i).getName());
        }

        // set the results to the list
        // and show them in the xml
        GooglePlacesListViewAdapter myAdapter = new GooglePlacesListViewAdapter(DetailActivity.this, R.layout.googleplace_list_item_view, cachedLocations);
        poiListView.setAdapter(myAdapter);
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
            LocationReminders application = (LocationReminders) getApplication();

            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(type, (int)_id);

            if (type.equals("MANUAL")) {
                ManualReminder mReminder = ManualReminder.findById(ManualReminder.class, _id);
                application.setReminder(mReminder);
                mReminder.delete();
            } else if (type.equals("AUTOMATIC")) {
                AutomaticReminder aReminder = AutomaticReminder.findById(AutomaticReminder.class, _id);
                application.setReminder(aReminder);
                aReminder.delete();
            }

            application.setShowUndo(true);

            finish();

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

        // method definition
        public BitmapDescriptor getMarkerIcon(String color) {
            float[] hsv = new float[3];
            Color.colorToHSV(Color.parseColor(color), hsv);
            return BitmapDescriptorFactory.defaultMarker(hsv[0]);
        }


        @Override
        public void call(String response) {
            List<GooglePlace> venuesList = GoogleParser.parse(response, location, _poiLimit);
            for (int i = 0; i < venuesList.size(); i++) {
                LatLng position = MapHelper.convertLatLng(venuesList.get(i).getLocation());
                MarkerOptions options = new MarkerOptions().position(position).icon(getMarkerIcon("#03A9F4"));
                mMap.addMarker(options).setTitle(venuesList.get(i).getName());
            }

            // set the results to the list
            // and show them in the xml
            Collections.sort(venuesList, new GooglePlaceComparator());
            GooglePlacesListViewAdapter myAdapter = new GooglePlacesListViewAdapter(DetailActivity.this, R.layout.googleplace_list_item_view, venuesList);
            poiListView.setAdapter(myAdapter);


        }
    }




}


