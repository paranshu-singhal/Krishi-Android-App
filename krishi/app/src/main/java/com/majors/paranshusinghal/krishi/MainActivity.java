package com.majors.paranshusinghal.krishi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends Activity {

    private static final String TAG ="com.majors.paranshusinghal.krishi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean flag = isNetworkAvailable();
        if(!flag){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
                    .setTitle("Unable to connect")
                    .setCancelable(false)
                    .setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(i);
                                }
                            }
                    )
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    MainActivity.this.finish();
                                }
                            }
                    );
            AlertDialog alert = builder.create();
            alert.show();
        }
        final ImageView newsImageView     = (ImageView)findViewById(R.id.newsImageView);
        final ImageView weatherImageView  = (ImageView)findViewById(R.id.weatherImageView);
        final ImageView cropListImageView = (ImageView)findViewById(R.id.cropListImageView);
        final ImageView schemeImageView   = (ImageView)findViewById(R.id.schemesImageView);
        newsImageView.post(new Runnable() {
            @Override
            public void run() {
                int ht = newsImageView.getMeasuredHeight();
                int wd = newsImageView.getMeasuredWidth();
                int newsResID     = getResources().getIdentifier("news_icon", "drawable", TAG);
                int weatherResID  = getResources().getIdentifier("weather_icon" , "drawable", TAG);
                int cropListResID = getResources().getIdentifier("plant_icon" , "drawable", TAG);

                Bitmap weatherUnscaledBitmap = BitmapFactory.decodeResource(getResources(), weatherResID);
                Bitmap cropListUnscaledBitmap = BitmapFactory.decodeResource(getResources(), cropListResID);
                Bitmap newsUnscaledBitmap = BitmapFactory.decodeResource(getResources(), newsResID);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(weatherUnscaledBitmap, wd, ht, true);
                weatherImageView.setImageBitmap(scaledBitmap);
                scaledBitmap = Bitmap.createScaledBitmap(cropListUnscaledBitmap, wd, ht, true);
                cropListImageView.setImageBitmap(scaledBitmap);
                scaledBitmap = Bitmap.createScaledBitmap(newsUnscaledBitmap, wd, ht, true);
                newsImageView.setImageBitmap(scaledBitmap);

                int resID = getResources().getIdentifier("hands_icon" , "drawable", TAG);
                Bitmap unscaledBitmap = BitmapFactory.decodeResource(getResources(), resID);
                scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, wd, ht, true);
                schemeImageView.setImageBitmap(scaledBitmap);

            }
        });

    }

    public void onClickWthr(View view){

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void onClickNews(View view){

        Intent intent = new Intent(this, newsActivity.class);
        startActivity(intent);
    }
    public void onClickCrop(View view){

        Intent intent = new Intent(this, cropList.class);
        startActivity(intent);
    }
    public void onClickScheme(View view){

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
