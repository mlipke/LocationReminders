package com.admuc.locationreminders.utils;

import android.location.LocationListener;
import android.util.Log;

import com.admuc.locationreminders.models.Location;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by 4gray on 27.11.15.
 */
public class MapHelper {

    public static double CalculationByDistance(Location StartP, Location EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.getLat();
        double lat2 = EndP.getLat();
        double lon1 = StartP.getLon();
        double lon2 = EndP.getLon();
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public static Location convertLocation(android.location.Location location) {
        return new Location(location.getLongitude(), location.getLatitude());
    }

}
