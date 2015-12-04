package com.admuc.locationreminders.services;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.admuc.locationreminders.BuildConfig;
import com.admuc.locationreminders.R;
import com.admuc.locationreminders.activities.DetailActivity;
import com.admuc.locationreminders.models.GooglePlace;
import com.admuc.locationreminders.models.Location;
import com.admuc.locationreminders.utils.MapHelper;
import com.admuc.locationreminders.utils.NotificationHelper;
import com.admuc.locationreminders.utils.PlacesAPIRequestBuilder;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4gray on 26.11.15.
 */
public class GooglePlaces extends AsyncTask {

    private String temp;
    private String type;
    private ArrayList venuesList;
    private ArrayAdapter myAdapter;
    private Location location;
    private ListView listView;
    private Activity activity;
    private Context context;

    public GooglePlaces(Location location, String type, Context context) {
        this.type = type;
        this.location = location;
        this.context = context;
    }

    public GooglePlaces(Location location, ListView listView, Activity activity) {
        this.location = location;
        this.listView = listView;
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        // make Call to the url
        //temp = makeCall("https://maps.googleapis.com/maps/api/place/search/json?location=" + locationLat + "," + locationLon + "&types=" + type + "&radius=200&sensor=true&key=" + BuildConfig.PLACES_WEB_SERVICE_API);
        temp = makeCall(PlacesAPIRequestBuilder.build(location)
                .setType(type)
                .setRadius(200)
                .setSensor(true)
                .setKey(BuildConfig.PLACES_WEB_SERVICE_API)
                .get());

        //print the call in the console
        System.out.println(PlacesAPIRequestBuilder.build(location)
                .setType(type)
                .setRadius(200)
                .setSensor(true)
                .setKey(BuildConfig.PLACES_WEB_SERVICE_API)
                .get());
        return "";
    }

    @Override
    protected void onPreExecute() {
        // we can start a progress bar here
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (temp == null) {
            // we have an error to the call
            // we can also stop the progress bar
        } else {
            // all things went right
            // parse Google places search result
            venuesList = (ArrayList) parseGoogleParse(temp);

            if (listView != null && activity != null) {
                List listTitle = new ArrayList();

                for (int i = 0; i < venuesList.size(); i++) {
                    // make a list of the venus that are loaded in the list.
                    // show the name, the category and the city
                    listTitle.add(i, ((GooglePlace) venuesList.get(i)).getName() + "\nOpen Now: " + ((GooglePlace) venuesList.get(i)).getOpenNow() + "\n(" + ((GooglePlace) venuesList.get(i)).getCategory() + ")" + "\n(" + ((GooglePlace) venuesList.get(i)).getDistance() + ")");
                }

                // set the results to the list
                // and show them in the xml
                myAdapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, listTitle);
                listView.setAdapter(myAdapter);
            } else {
                for (int i = 0; i < venuesList.size(); i++) {
                    if (((GooglePlace)venuesList.get(i)).getDistance() < 0.2) {
                        NotificationHelper.createNotification(context);
                        break;
                    }
                }
            }
        }
    }

    public static String makeCall(String url) {

        // string buffers the url
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        // instanciate an HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        // instanciate an HttpGet
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            // get the responce of the httpclient execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            // buffer input stream the result
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // the result as a string is ready for parsing
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(replyString);

        // trim the whitespaces
        return replyString.trim();
    }

    private ArrayList parseGoogleParse(final String response) {

        ArrayList temp = new ArrayList();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            // make an jsonObject in order to parse the response
            if (jsonObject.has("results")) {

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    GooglePlace poi = new GooglePlace();
                    if (jsonArray.getJSONObject(i).has("name")) {
                        poi.setName(jsonArray.getJSONObject(i).optString("name"));
                        poi.setRating(jsonArray.getJSONObject(i).optString("rating", " "));
                        double distance = MapHelper.CalculationByDistance(location,
                                new Location(jsonArray.getJSONObject(i)
                                        .getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lat"), jsonArray.getJSONObject(i)
                                        .getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lng")));
                        poi.setDistance(distance);

                        if (jsonArray.getJSONObject(i).has("opening_hours")) {
                            if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                    poi.setOpenNow("YES");
                                } else {
                                    poi.setOpenNow("NO");
                                }
                            }
                        } else {
                            poi.setOpenNow("Not Known");
                        }
                        if (jsonArray.getJSONObject(i).has("types")) {
                            JSONArray typesArray = jsonArray.getJSONObject(i).getJSONArray("types");

                            for (int j = 0; j < typesArray.length(); j++) {
                                poi.setCategory(typesArray.getString(j) + ", " + poi.getCategory());
                            }
                        }
                    }
                    temp.add(poi);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }
        return temp;

    }
}
