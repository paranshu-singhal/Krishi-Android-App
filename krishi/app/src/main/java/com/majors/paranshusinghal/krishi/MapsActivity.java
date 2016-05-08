package com.majors.paranshusinghal.krishi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapsActivity extends Activity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult>,
        OnMapReadyCallback {

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected static final String TAG = "MapsActivity";
    protected LocationRequest mlocationrequest;
    protected LocationSettingsRequest mlocationsettingsrequest;
    protected ProgressBar progressBar;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final String TAGlog = "myTAG";

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
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                buildGoogleApiClient();
            }
        }, 2000);
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
                        MapsActivity.this.finish();
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

    public void getLocation()throws SecurityException{
        //if(isOnline()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
       // }
       /*
        else{
            SharedPreferences sharedPref =MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
            mLastLocation.setLatitude(Double.parseDouble(sharedPref.getString("latitude", "0")));
            mLastLocation.setLongitude(Double.parseDouble(sharedPref.getString("longitude", "0")));
        }
        */
        if (mLastLocation != null) {
            MapFragment map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
            map.getMapAsync(this);
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng  sydney = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Current location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("latitude", String.format("%f",mLastLocation.getLatitude()));
        editor.putString("longitude", String.format("%f", mLastLocation.getLongitude()));
        editor.apply();

        weatherForecast();
    }
    public void weatherForecast() {
        progressBar.setVisibility(View.GONE);
        File fileWeatherCache = new File(getExternalCacheDir(), "weatherCache");
        if(isOnline()){
     //       Toast.makeText(this, "Connected to network", Toast.LENGTH_LONG).show();
        afterConn();}
        else if(fileWeatherCache.exists()){
            ListView listView = (ListView)findViewById(R.id.listWeather);
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileWeatherCache));
                List<custom_raw_weather_holderClass> list = convert2List(new JSONArray((String) ois.readObject()));
                custom_row_weather_adaptor adaptor = new custom_row_weather_adaptor(MapsActivity.this, R.layout.custom_row_weather, list);
                listView.setAdapter(adaptor);
            }
            catch (Throwable t){
                Log.d(TAGlog, t.getMessage());
                t.printStackTrace();
            }
        }
        else{
            Toast.makeText(MapsActivity.this, "Please check network settings", Toast.LENGTH_LONG).show();
            MapsActivity.this.finish();
        }
    }

    public void afterConn() {
        callHttp obj = new callHttp();
        obj.execute();
    }

    private class callHttp extends AsyncTask<Void,Void,String>{
        @Override
        protected  String doInBackground(Void... locs) {

            final String urls = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="+mLastLocation.getLatitude()+"&lon="+mLastLocation.getLongitude()+"&cnt=10&mode=json&appid=3a42ffe69d01b3960c783884803669c9";
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
            List<custom_raw_weather_holderClass> list = new ArrayList<>();
            try{
                json = new JSONObject(s);
                JSONArray jsonArray = json.getJSONArray("list");
                Toast.makeText(MapsActivity.this, String.format("size: %d", jsonArray.length()), Toast.LENGTH_SHORT).show();
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

                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(getExternalCacheDir(), "weatherCache")));
                JSONArray array = convert2JSON(list);
                oos.writeObject(array.toString());
            }
            catch (Throwable t){t.printStackTrace();}
        }

    }
    private JSONArray convert2JSON(List<custom_raw_weather_holderClass> list) {

        JSONArray array = new JSONArray();
        for (custom_raw_weather_holderClass element : list) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("maxTemp", element.getMaxTemp());
                obj.put("minTemp", element.getMinTemp());
                obj.put("humidity", element.getHumidity());
                obj.put("clouds", element.getClouds());
                obj.put("speed", element.getSpeed());
                obj.put("weather", element.getWeather());
                obj.put("date", element.getDate());
            } catch (Throwable t) {
                Log.d(TAGlog,"convert2JSON  "+t.getMessage());
                t.printStackTrace();
            }
            array.put(obj);
        }
        return array;
    }

    private List<custom_raw_weather_holderClass> convert2List(JSONArray array) {

        List<custom_raw_weather_holderClass> list = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                custom_raw_weather_holderClass element = new custom_raw_weather_holderClass(obj.getDouble("maxTemp"), obj.getDouble("minTemp"),
                        obj.getDouble("humidity"),obj.getDouble("clouds"), obj.getDouble("speed"), obj.getString("weather"),
                        new SimpleDateFormat("EE MMM dd").parse(obj.getString("date")));
                list.add(element);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Log.d(TAGlog, "convert2List "+t.getMessage());
        }
        return list;
    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}



