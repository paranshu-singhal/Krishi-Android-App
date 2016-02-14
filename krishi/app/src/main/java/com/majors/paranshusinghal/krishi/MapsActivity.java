package com.majors.paranshusinghal.krishi;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

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

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapsActivity extends Activity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult>,
        OnMapReadyCallback {

    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected static final String TAG = "MapsActivity";
    protected LocationRequest mlocationrequest;
    protected LocationSettingsRequest mlocationsettingsrequest;
    protected ProgressBar progressBar;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    List list = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        progressBar = (ProgressBar)findViewById(R.id.maps_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        buildGoogleApiClient();
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
        createLocationRequest();
        buildLocationSettingsRequest();
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //             Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
                        Toast.makeText(this, R.string.conn_failed, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getLocation();
                            }
                        }, 3000);
                        break;
                    case Activity.RESULT_CANCELED:
                        //Log.d(TAG, "User chose not to make required location settings changes.");
                        Toast.makeText(this, R.string.RESULT_CANCELED, Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                getLocation();
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {
                    //unable to execute request
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are inadequate, and cannot be fixed here. Dialog not created
                break;
        }
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
    public void getLocation(){

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            MapFragment map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
            map.getMapAsync(this);
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng  sydney = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        weatherForecast();
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void weatherForecast() {
        if(isOnline()){
     //       Toast.makeText(this, "Connected to network", Toast.LENGTH_LONG).show();
        afterConn();}
        else{
            Toast.makeText(this, "Please check network settings", Toast.LENGTH_LONG).show();
        }
    }

    public void afterConn() {
        callHttp obj = new callHttp();
        obj.execute();
    }

    private class callHttp extends AsyncTask<Void,Void,String>{
        @Override
        protected  String doInBackground(Void... locs) {

            final String urls = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="+mLastLocation.getLatitude()+"&lon="+mLastLocation.getLongitude()+"&cnt=10&mode=json&appid=44db6a862fba0b067b1930da0d769e98";
            InputStream is = null;
            String answer=null;
            try {
                HttpURLConnection con = (HttpURLConnection)(new URL(urls)).openConnection();
                con.setReadTimeout(10000 /* milliseconds */);
                con.setConnectTimeout(15000 /* milliseconds */);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();

                StringBuilder buffer = new StringBuilder();
                is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while (  (line = br.readLine()) != null ) {
                    buffer.append(line + "\r\n");
                }
                is.close();
                con.disconnect();
                answer = buffer.toString();

            }
            catch (Throwable t){t.printStackTrace();}
            finally {
                try { is.close(); } catch(Throwable t) {t.printStackTrace();}
                //try { con.disconnect(); } catch(Throwable t) {}
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            JSONObject json;
            try{
                json = new JSONObject(s);
                JSONArray jsonArray = json.getJSONArray("list");
                Date date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                for(int i=0;i<json.getInt("cnt");i++){
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Double maxtemp = obj.getJSONObject("temp").getDouble("max");
                    Double mintemp = obj.getJSONObject("temp").getDouble("min");
                    Double humidity= obj.getDouble("humidity");
                    Double speed   = obj.getDouble("speed");
                    Double cloud   = obj.getDouble("clouds");
                    String weather = obj.getJSONArray("weather").getJSONObject(0).getString("description");
                    custom_raw_weather_holderClass obj2 = new custom_raw_weather_holderClass(maxtemp,mintemp,humidity,cloud,speed,weather,cal.getTime());
                    list.add(obj2);
                    cal.add(Calendar.DATE, 1);
                }

                ListView listView = (ListView)findViewById(R.id.listWeather);
                custom_row_weather_adaptor adaptor = new custom_row_weather_adaptor(MapsActivity.this, R.layout.custom_row_weather, list);
                listView.setAdapter(adaptor);
            }
            catch (Throwable t){t.printStackTrace();}
        }

    }
}



