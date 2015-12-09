package com.admuc.locationreminders.utils;

import com.admuc.locationreminders.models.GooglePlace;
import com.admuc.locationreminders.models.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GoogleParser {

    public static ArrayList<GooglePlace> parse(String response, Location location) {

        ArrayList<GooglePlace> temp = new ArrayList<>();
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
                                    poi.setOpenNow("Opened");
                                } else {
                                    poi.setOpenNow("Closed");
                                }
                            }
                        } else {
                            poi.setOpenNow("Not Known");
                        }
                        if (jsonArray.getJSONObject(i).has("types")) {
                            JSONArray typesArray = jsonArray.getJSONObject(i).getJSONArray("types");

                            for (int j = 0; j < typesArray.length(); j++) {
                                poi.setType(typesArray.getString(j) + ", " + poi.getType());
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
