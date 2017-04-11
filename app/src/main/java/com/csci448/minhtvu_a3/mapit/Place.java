package com.csci448.minhtvu_a3.mapit;

import android.os.AsyncTask;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.GregorianCalendar;

/**
 * Created by minhtvu on 4/7/17.
 */

public class Place {
    LatLng location;
    GregorianCalendar time;
    double temp;
    String weatherCondition;

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Place(LatLng location) {
        this.location = location;
        this.time = new GregorianCalendar();
    }
    public LatLng getLocation() {
        return location;
    }
    public GregorianCalendar getTime() {
        return time;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public double getTemp() {
        return temp;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    @Override
    public String toString() {
        return "You were here: " + time.getTime() + ".\n" + "Temp: " + temp + "C " +weatherCondition;
    }
}