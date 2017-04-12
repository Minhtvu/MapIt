package com.csci448.minhtvu_a3.mapit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.ArrayList;

/**
 * Created by minhtvu on 4/5/17.
 */

public class MapFragment extends SupportMapFragment {
    private static final String TAG = "MapFragment";
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Place currentItem;
    private ArrayList<Place> locationList;
    private Firebase fireBaseReference;
    private int counter;
    public static MapFragment newInstance(){
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        checkPermission();
        setHasOptionsMenu(true);
        counter = 1;
        locationList = new ArrayList<>();
        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        }).build();
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateUI();
            }
        });
        fireBaseReference = new Firebase("https://mapit-bc66f.firebaseio.com/");
        fireBaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Count " ,""+ dataSnapshot.getChildrenCount());
                for (DataSnapshot placeSnapshot: dataSnapshot.getChildren()) {
                    String child = placeSnapshot.getValue().toString();
                    if(child.length() > 1){
                        Place place = new Place(child);
                        locationList.add(place);}
                    else{
                        counter = Integer.parseInt(child);
                    }
            }}
            @Override
            public void onCancelled(FirebaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIn();
            }
        });
    }
    // for checking GPS permission
    public void checkPermission(){
        Log.d(TAG, "checkPermission()");
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this.getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "Requesting Permission");
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_map, menu);

        MenuItem searchItem = menu.findItem(R.id.action_checkin);
        searchItem.setEnabled(mClient.isConnected());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_checkin:
                checkIn();
                return true;
            case R.id.action_clear:
                clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void clear(){
        for(int i = 1; i < counter; i++)
        {
            fireBaseReference.child(Integer.toString(i)).removeValue();
        }
        fireBaseReference.child("Count").setValue(1);
        mMap.clear();
        locationList.clear();
    }
    private void checkIn(){
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        try {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mClient, request, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.i(TAG, "Got a fix: " + location);
                            currentItem = new Place(new LatLng(0,0));
                            markIt(location);
                            JSONWeatherTask task = new JSONWeatherTask();
                            task.execute(location.getLatitude() + " " + location.getLongitude());
                            updateUI();
                        }
                    });
        }
        catch(SecurityException e){
            Log.i(TAG, "Exception: "+e);
        }
    }
    //add all the markers to the map
    private void updateUI(){

        if (mMap == null) return;
        for (Place point: locationList)
        {
            Log.i(TAG,point.toString());
            mMap.addMarker(new MarkerOptions()
                    .position(point.getLocation())
                    .title(point.getLocation().toString()));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                Snackbar snackbar = Snackbar.make(getView(), getPlace(arg0).toString(), Snackbar.LENGTH_LONG);
                snackbar.show();
                return false;

            }
        });
    }
    private Place getPlace(Marker marker){
        for(Place point:locationList)
        {
            if (marker.getPosition().equals(point.getLocation())){
                return point;
            }

        }
        return null;
    }
    //add the current location to the list and mark it on the map
    private void markIt(Location location)
    {
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        currentItem.setLocation(point);
    }
    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }
    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHTTPClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            fireBaseReference.child("Count").setValue(counter + 1);
            currentItem.setWeatherCondition(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            currentItem.setTemp(Math.round((weather.temperature.getTemp() - 273.15)));
            fireBaseReference.child(Integer.toString(counter)).setValue(currentItem);
            locationList.add(currentItem);
            updateUI();
            counter += 1;
        }
    }
}
