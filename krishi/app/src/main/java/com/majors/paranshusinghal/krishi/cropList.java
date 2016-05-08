package com.majors.paranshusinghal.krishi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class cropList extends AppCompatActivity {

    private static final String TAG ="com.majors.paranshusinghal.testApp";
    private static final String TAGlog = "myTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_list);


        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeButtonEnabled(true);

        /*
        final ImageView newsImageView = (ImageView) findViewById(R.id.crop_list_imageView);
        newsImageView.post(new Runnable() {
                @Override
                public void run() {
                    int ht = newsImageView.getMeasuredHeight();
                    int wd = newsImageView.getMeasuredWidth();
                    int resID = getResources().getIdentifier("news_flat_icon", "drawable", TAG);
                    Bitmap unscaledBitmap = BitmapFactory.decodeResource(getResources(), resID);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, wd, ht, true);
                    newsImageView.setImageBitmap(scaledBitmap);
                }
            });


        */
        final String crops[] = getResources().getStringArray(R.array.crop_list);
        List<String> cropList = Arrays.asList(crops);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cropList);
        ListView listView = (ListView) findViewById(R.id.cropListView);
        listView.setAdapter(adapter);


        final Intent intent = new Intent(this, TabActivity.class);
        final Bundle bundle = new Bundle();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(TAGlog, crops[position]);
                bundle.putString("id", crops[position]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}

