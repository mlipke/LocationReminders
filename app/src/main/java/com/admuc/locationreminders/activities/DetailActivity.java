package com.admuc.locationreminders.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    private boolean _isMyLocationDetected = false;

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
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

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


        }


        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(android.location.Location location) {
                if (!_isMyLocationDetected) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    LatLng ll = new LatLng(lat, lng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

                    Location loc = new Location(lat, lng);
                    new GooglePlaces(loc, reminder, new Callback(loc)).execute();
                    _isMyLocationDetected = true;
                }
            }
        });

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
            for (int i = 0; i < venuesList.size(); i++) {
                Log.d("Marker "+ i, venuesList.get(i).getLocation());
                LatLng position = MapHelper.convertLatLng(venuesList.get(i).getLocation());
                MarkerOptions options = new MarkerOptions().position(position);
                mMap.addMarker(options);
            }

            // set the results to the list
            // and show them in the xml
            GooglePlacesListViewAdapter myAdapter = new GooglePlacesListViewAdapter(DetailActivity.this, R.layout.googleplace_list_item_view, venuesList);
            poiListView.setAdapter(myAdapter);
        }
    }


    public class GooglePlacesListViewAdapter extends ArrayAdapter<GooglePlace> {

        Context context;

        public GooglePlacesListViewAdapter(Context context, int resourceId,
                                     List<GooglePlace> items) {
            super(context, resourceId, items);
            this.context = context;
        }

        /*private view holder class*/
        private class ViewHolder {
            ImageView imageView;
            TextView txtTitle;
            TextView txtDesc;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            GooglePlace googlePlace = getItem(position);

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.googleplace_list_item_view, null);
                holder = new ViewHolder();
                holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
                holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
                holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.txtDesc.setText((int) MapHelper.convertKmToMeter(googlePlace.getDistance()) + " m | " + googlePlace.getOpenNow());
            holder.txtTitle.setText(googlePlace.getName());
            holder.imageView.setImageResource(R.drawable.ic_location_on_24dp);  // TODO: location type icon from URL

            /*
            try {
                URL url = new URL(googlePlace.getIcon());
                Log.d("Icon: ", googlePlace.getIcon());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                holder.imageView.setImageBitmap(bmp);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */


            return convertView;
        }
    }

}
