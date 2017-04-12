package com.csci448.minhtvu_a3.mapit;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by minhtvu on 4/10/17.
 */

public class CloudOrderApplication extends Application {
    private static final String TAG ="csci.cloud.mapit";
    private static final String FIREBASE_URL = "https://mapit-bc66f.firebaseio.com/";
    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase firebaseRef = new Firebase(FIREBASE_URL);

    }
}
