package com.majors.paranshusinghal.krishi;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class scrollingActivity1 extends Activity {

    protected TextView txt;
    AssetManager assetManager;
    private static final String TAG ="com.majors.paranshusinghal.krishi";
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling1);

        txt = (TextView)findViewById(R.id.text_ln);
        assetManager = getAssets();

        Bundle bundle = getIntent().getExtras();
        String id=bundle.getString("id");
        String filename = id+".txt";
        gettext(filename);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int width = displaymetrics.widthPixels;

        String imgsrc = "rsz_"+id;
        int resID = getResources().getIdentifier(imgsrc , "drawable", TAG);
        final Bitmap unscaledBitmap = BitmapFactory.decodeResource(getResources(), resID);

        img = (ImageView)findViewById(R.id.imageView);
        img.post(new Runnable() {
            @Override
            public void run() {
                int height = img.getMeasuredHeight();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, width, height, true);
                img.setImageBitmap(scaledBitmap);
            }
        });
        /*
        try {
            String[] files = assetManager.list("");
            for(int i=0; i<files.length-2; i++){
              //  txt.append("\n File :" + i + " Name => " + files[i]);
                gettxt(files[i]);
            }
        } catch (IOException e1) {
            Toast.makeText(this,"Exception1 occured", Toast.LENGTH_LONG).show();
            e1.printStackTrace();
        }
        */
    }
    public void gettext(String filename){
        InputStream input;
        try {
            input = assetManager.open(filename);

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            // byte buffer into a string
            String text = new String(buffer);
            txt.append(text+"\n");
        } catch (IOException e) {

            Toast.makeText(this,"Exception2 occured", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
