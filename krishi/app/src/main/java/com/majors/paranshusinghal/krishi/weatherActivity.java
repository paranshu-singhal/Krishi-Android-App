package com.majors.paranshusinghal.krishi;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarContainer;
import android.support.v7.widget.ActionBarContextView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;


abstract public class weatherActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected static final String TAG = "weatherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mLatitudeText = (TextView)findViewById(R.id.lat_txt);
        mLongitudeText= (TextView)findViewById(R.id.long_txt);
        mLatitudeLabel= getResources().getString(R.string.lat_txt);
        mLongitudeLabel=getResources().getString(R.string.long_txt);

        buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
                 mGoogleApiClient = new GoogleApiClient.Builder(this)
                         .addConnectionCallbacks(this)
                         .addOnConnectionFailedListener(this)
                         .addApi(LocationServices.API)
                         .build();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
         protected void onStop() {
                 super.onStop();
                 if (mGoogleApiClient.isConnected()) {
                         mGoogleApiClient.disconnect();
                     }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
                 // Provides a simple way of getting a device's location and is well suited for
                 // applications that do not require a fine-grained location and that do not need location
                 // updates. Gets the best and most recent location currently available, which may be null
                 // in rare cases when a location is not available.
                 mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                 if (mLastLocation != null) {
                         mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                                         mLastLocation.getLatitude()));
                         mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                                         mLastLocation.getLongitude()));
                     } else {
                         Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
                     }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
                 // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
                 // onConnectionFailed.
                 Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
             }
    @Override
    public void onConnectionSuspended(int cause) {
                 // The connection to Google Play services was lost for some reason. We call connect() to
                 // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
                 mGoogleApiClient.connect();
                 }


}
