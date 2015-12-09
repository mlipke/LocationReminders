package com.admuc.locationreminders.utils;

/**
 * Created by 4gray on 09.12.15.
 */
public class StringHelper {

    public static String convertToReadableString(String text) {
        StringBuffer sb = new StringBuffer();
        for (String s : text.split("_")) {
            sb.append(s + " ");
        }
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

}
