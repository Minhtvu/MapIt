package com.csci448.minhtvu_a3.mapit;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by minhtvu on 4/5/17.
 */

public class MapFragment extends SupportMapFragment {
    private static final String TAG = "MapFragment";
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        checkPermission();
        setHasOptionsMenu(true);

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
    }
    public void checkPermission(){
        Log.d(TAG, "checkPermission()");
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this.getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "Requesting Permission");
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }
    }

}
