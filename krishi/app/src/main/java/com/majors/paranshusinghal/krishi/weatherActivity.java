package com.majors.paranshusinghal.krishi;

 import android.app.Activity;
 import android.content.Intent;
 import android.content.IntentSender;
 import android.location.Location;
 import android.os.Bundle;
 import android.support.v7.app.AppCompatActivity;
 import android.util.Log;
 import android.widget.TextView;
 import android.widget.Toast;

 import android.os.Handler;
 import com.google.android.gms.common.ConnectionResult;
 import com.google.android.gms.common.api.GoogleApiClient;
 import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
 import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
 import com.google.android.gms.common.api.PendingResult;
 import com.google.android.gms.common.api.ResultCallback;
 import com.google.android.gms.common.api.Status;
 import com.google.android.gms.location.LocationRequest;
 import com.google.android.gms.location.LocationServices;
 import com.google.android.gms.location.LocationSettingsRequest;
 import com.google.android.gms.location.LocationSettingsResult;
 import com.google.android.gms.location.LocationSettingsStatusCodes;

 public class weatherActivity extends AppCompatActivity implements
         ConnectionCallbacks,
         OnConnectionFailedListener,
         ResultCallback<LocationSettingsResult> {

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
     protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected static final String TAG = "weatherActivity";
    protected LocationRequest mlocationrequest;
    protected LocationSettingsRequest mlocationsettingsrequest;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mLatitudeText = (TextView)findViewById(R.id.lat_txt);
        mLongitudeText= (TextView)findViewById(R.id.long_txt);

        buildGoogleApiClient();

   //     checkLocationSettings();
    }

    protected synchronized void buildGoogleApiClient() {
                 mGoogleApiClient = new GoogleApiClient.Builder(this)
                         .addConnectionCallbacks(this)
                         .addOnConnectionFailedListener(this)
                         .addApi(LocationServices.API)
                         .build();
    }
     protected void createLocationRequest() {
         mlocationrequest = new LocationRequest();
         mlocationrequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
         mlocationrequest.setFastestInterval((UPDATE_INTERVAL_IN_MILLISECONDS / 2));
         mlocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
     }

     protected void buildLocationSettingsRequest() {
         LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
         builder.addLocationRequest(mlocationrequest);
         mlocationsettingsrequest = builder.build();
         PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(
                 mGoogleApiClient,
                 mlocationsettingsrequest
         );
        result.setResultCallback(this);
     }
     @Override
     public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    getLocation();
                    // NO need to show the dialog;
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    //  Location settings are not satisfied. Show the user a dialog

                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(weatherActivity.this, REQUEST_CHECK_SETTINGS);

                    } catch (IntentSender.SendIntentException e) {

                        //unable to execute request
                    }
                    break;

                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are inadequate, and cannot be fixed here. Dialog not created
                    break;
            }
        }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch (requestCode) {
             // Check for the integer request code originally supplied to startResolutionForResult().
             case REQUEST_CHECK_SETTINGS:
                 switch (resultCode) {
                     case Activity.RESULT_OK:
                         Toast.makeText(this, R.string.location_detected, Toast.LENGTH_LONG).show();
                         final Handler handler = new Handler();
                         handler.postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                getLocation();
                             }
                         }, 2000);
                         break;
                     case Activity.RESULT_CANCELED:
                         Log.i(TAG, "User chose not to make required location settings changes.");
                         break;
                 }
                 break;
         }
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
         createLocationRequest();
         buildLocationSettingsRequest();
    }

    public void getLocation(){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.format("%f",mLastLocation.getLatitude()));
            mLongitudeText.setText(String.format("%f",mLastLocation.getLongitude()));
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //             Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
             }
    @Override
    public void onConnectionSuspended(int cause) {
                 // The connection to Google Play services was lost for some reason. We call connect() to
                 // attempt to re-establish the connection.
            mGoogleApiClient.connect();
                 }
 }
