package com.csci448.minhtvu_a3.mapit;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by minhtvu on 4/10/17.
 */

public class WeatherHTTPClient {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?lat=";
    private static String API_KEY = "a55203a55b4af49905d2d73ea1a053f4";
    public String getWeatherData(String location) {
        HttpURLConnection con = null ;
        InputStream is = null;
        String[] tokens = location.split(" ");
        try {
            con = (HttpURLConnection) ( new URL(BASE_URL + tokens[0] + "&lon=" + tokens[1] + "&APPID=" + API_KEY)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            //read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }

}
