package com.majors.paranshusinghal.krishi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class cropList extends Activity {

    private static final String TAG ="com.majors.paranshusinghal.krishi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_list);

        final ImageView newsImageView = (ImageView)findViewById(R.id.crop_list_imageView);
        newsImageView.post(new Runnable() {
            @Override
            public void run() {
                int ht = newsImageView.getMeasuredHeight();
                int wd = newsImageView.getMeasuredWidth();
                int resID = getResources().getIdentifier("flat_plant", "drawable", TAG);
                Bitmap unscaledBitmap = BitmapFactory.decodeResource(getResources(), resID);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, wd, ht, true);
                newsImageView.setImageBitmap(scaledBitmap);
            }
        });

    }
    public void onclick(View view){
            Intent intent = new Intent(this, scrollingActivity1.class);
            Bundle bundle = new Bundle();
            switch (view.getId()){
                case R.id.paddy:
                    bundle.putString("id","paddy");
                    break;
                case R.id.wheat:
                    bundle.putString("id","wheat");
                    break;
                case R.id.maize:
                    bundle.putString("id","maize");
                    break;
                case R.id.suger:
                    bundle.putString("id","sugar");
                    break;
                case R.id.potato:
                    bundle.putString("id","potato");
                    break;
                case R.id.tomato:
                    bundle.putString("id","tomato");
                    break;
            }
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

