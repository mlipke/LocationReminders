package com.admuc.locationreminders.models;

import java.util.List;

/**
 * Created by 4gray on 20.11.15.
 */
public class PlaceAutocomplete {

    public CharSequence placeId;
    public CharSequence description;
    public List placeTypes;

    public PlaceAutocomplete(CharSequence placeId, CharSequence description, List placeTypes) {
        this.placeId = placeId;
        this.description = description;
        this.placeTypes = placeTypes;
    }

    @Override
    public String toString() {
        return description.toString();
    }
}