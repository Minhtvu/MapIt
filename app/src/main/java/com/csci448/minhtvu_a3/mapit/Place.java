package com.csci448.minhtvu_a3.mapit;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    String time;
    double temp;
    String weatherCondition;

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Place(LatLng location) {
        this.location = location;
        this.time = new GregorianCalendar().getTime().toString();
    }
    public Place(String JSon)
    {
        Log.i("A",JSon);
        String time = JSon.substring(JSon.indexOf("time=") + 5,JSon.indexOf(',', JSon.indexOf("time=")));
        String lat = JSon.substring(JSon.indexOf("latitude=") + 9,JSon.indexOf(',', JSon.indexOf("latitude") + 1));
        String lon = JSon.substring(JSon.indexOf("longitude=") + 10 ,JSon.indexOf('}'));
        String weaCon = JSon.substring(JSon.indexOf("weatherCondition=") + 17 ,JSon.indexOf(',', JSon.indexOf("weatherCondition") + 1));
        String temp = JSon.substring(JSon.indexOf("temp=") + 5 ,JSon.indexOf('}', JSon.indexOf("temp") + 1));
        this.location = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
        this.weatherCondition = weaCon;
        this.temp = Double.parseDouble(temp);
        this.time = time;
    }
    public LatLng getLocation() {
        return location;
    }
    public String getTime() {
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
        return "You were here: " + time + ".\n" + "Temp: " + temp + "C " +weatherCondition;
    }
}