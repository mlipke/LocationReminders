package com.admuc.locationreminders.models;

import com.orm.SugarRecord;

/**
 * Created by 4gray on 30.10.15.
 */
public class Location extends SugarRecord<Location> {

    private double lat;
    private double lon;

    private String description;

    public Location() {}

    public Location(double lat, double lon) {
        this.lon = lon;
        this.lat = lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return description;
    }
}
