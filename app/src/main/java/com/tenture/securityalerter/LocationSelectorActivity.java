package com.tenture.securityalerter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
// Add an import statement for the client library.


import androidx.annotation.Nullable;


public class LocationSelectorActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Initialize Places.
        //Places.initialize(getApplicationContext(), String.valueOf(R.string.google_maps_key));

// Create a new Places client instance.
        //PlacesClient placesClient = Places.createClient(this);

    }
}
