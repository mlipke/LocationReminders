package com.admuc.locationreminders.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by 4gray on 26.11.15.
 */
public class GooglePlace extends SugarRecord<GooglePlace> {
    private String name;
    private String type;
    private String rating;

    @Ignore
    private String open;
    private String icon;

    @Ignore
    private double distance;
    private Location location;


    public GooglePlace() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setOpenNow(String open) {
        this.open = open;
    }

    public String getOpenNow() {
        return open;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
