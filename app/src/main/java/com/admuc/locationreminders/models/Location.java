package com.admuc.locationreminders.models;

/**
 * Created by 4gray on 30.10.15.
 */
public class Location {

    private double lon;
    private double lat;

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

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}
