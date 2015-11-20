package com.admuc.locationreminders.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.admuc.locationreminders.R;
import com.admuc.locationreminders.models.AutomaticReminder;
import com.admuc.locationreminders.models.ManualReminder;
import com.admuc.locationreminders.models.PlaceAutocomplete;
import com.admuc.locationreminders.models.Reminder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    private Reminder reminder;

    private long _id;
    private String type;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private double _locationLat;
    private double _locationLon;
    private double _locationRadius = 0.01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        type = intent.getStringExtra("REMINDER_TYPE");
        _id = intent.getLongExtra("REMINDER_ID", 0);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMap = mapFragment.getMap();
        mapFragment.getMapAsync(new MapListener());

        setupViews();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    private void setupViews() {
        if (type.equals("MANUAL")) {
            reminder = ManualReminder.findById(ManualReminder.class, _id);
            _locationLat = ((ManualReminder) reminder).getLocation().getLat();
            _locationLon = ((ManualReminder) reminder).getLocation().getLon();

            LatLng position = new LatLng(_locationLat, _locationLon);
            MarkerOptions options = new MarkerOptions().position(position);
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        } else if (type.equals("AUTOMATIC")) {
            reminder = AutomaticReminder.findById(AutomaticReminder.class, _id);
        }

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
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private class MapListener implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {

        }
    }



    @Override
    public void onConnected(Bundle bundle) {

        LatLng southWest = new LatLng(_locationLat - _locationRadius, _locationLon - _locationRadius);
        LatLng northEast = new LatLng(_locationLat + _locationRadius, _locationLon + _locationRadius);
        LatLngBounds bounds = new LatLngBounds(southWest, northEast);
        List<Integer> filterTypes = new ArrayList<Integer>();
        //TODO: filter
        /*filterTypes.add(Place.TYPE_GEOCODE);
        AutocompleteFilter filter = AutocompleteFilter.create(filterTypes);*/
        String query = "restaurant";
        PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient,
                query, bounds, null);
        results.setResultCallback(this);

    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onResult(Result result) {
        Log.d("Place API: ", String.valueOf(result.getStatus().getStatus()));

        AutocompletePredictionBuffer autocompletePredictions = (AutocompletePredictionBuffer) result;
        Log.i("Message: ", "Query completed. Received " + autocompletePredictions.getCount()
                + " predictions.");
        Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
        ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
        while (iterator.hasNext()) {
            AutocompletePrediction prediction = iterator.next();

            resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                    prediction.getDescription(), prediction.getPlaceTypes()));
            Log.d("Message: ", prediction.getDescription());
            Log.d("Message: ", String.valueOf(prediction.getPlaceTypes().get(0)));
        }

    }
}
