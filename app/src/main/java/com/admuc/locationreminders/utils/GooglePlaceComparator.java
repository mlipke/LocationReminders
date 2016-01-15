package com.admuc.locationreminders.utils;

import com.admuc.locationreminders.models.GooglePlace;

import java.util.Comparator;

public class GooglePlaceComparator implements Comparator<GooglePlace> {

    public int compare(GooglePlace one, GooglePlace another){
        int returnVal = 0;

        if(one.getDistance() < another.getDistance()){
            returnVal =  -1;
        }else if(one.getDistance() > another.getDistance()){
            returnVal =  1;
        }else if(one.getDistance() == another.getDistance()){
            returnVal =  0;
        }
        return returnVal;

    }
}
