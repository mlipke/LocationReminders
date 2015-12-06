package com.admuc.locationreminders.utils;

import com.admuc.locationreminders.models.Location;

/**
 * Created by matt on 02/12/15.
 */
public class PlacesAPIRequestBuilder {

    public static BuilderInstance build(Location location) {

        return new BuilderInstance().setLocation(location);
    }

    public static class BuilderInstance {

        private static final String BASE = "https://maps.googleapis.com/maps/api/place/search/json";

        private Location location;
        private String type;
        private double radius;
        private String key;

        private String parameters;

        public BuilderInstance() {

        }

        public BuilderInstance setLocation(Location location) {
            this.location = location;

            return this;
        }

        public BuilderInstance setType(String type) {
            this.type = type;

            return this;
        }

        public BuilderInstance setRadius(double radius) {
            this.radius = radius;

            return this;
        }

        public BuilderInstance setKey(String key) {
            this.key = key;

            return this;
        }

        public String get() {
            return BASE + "?location=" + location.getLat() + "," + location.getLon()
                    + "&type=" + type
                    + "&radius=" + Double.toString(radius)
                    + "&key=" + key;
        }

    }

}
