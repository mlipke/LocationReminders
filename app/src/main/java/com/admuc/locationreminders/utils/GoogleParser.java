package com.admuc.locationreminders.utils;

import com.admuc.locationreminders.models.GooglePlace;
import com.admuc.locationreminders.models.Location;
import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GoogleParser {

    public static List<GooglePlace> parse(String response, Location location, int limit) {

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
                        poi.setIcon(jsonArray.getJSONObject(i).optString("icon"));
                        poi.setRating(jsonArray.getJSONObject(i).optString("rating", " "));
                        double lat = jsonArray.getJSONObject(i)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lat");
                        double lon = jsonArray.getJSONObject(i)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lng");
                        double distance = MapHelper.CalculationByDistance(location,
                                new Location(lat, lon));
                        poi.setDistance(distance);
                        poi.setLocation(new Location(lat, lon));

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

                    // is this place already saved in the db (check by name)
                    List gp = SugarRecord.find(GooglePlace.class, "name=?", poi.getName());
                    if (gp.size() == 0)
                        poi.save();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        if (limit > temp.size()) {
            return temp;
        } else {
            return temp.subList(0, limit);
        }
    }
}
