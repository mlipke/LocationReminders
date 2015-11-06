package com.admuc.locationreminders.models;

import com.orm.SugarRecord;

/**
 * Created by 4gray on 30.10.15.
 */
public class Location extends SugarRecord<Location> {

    private double lon;
    private double lat;

    private String description;

    public Location() {}

    public Location(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return description;
    }
}
