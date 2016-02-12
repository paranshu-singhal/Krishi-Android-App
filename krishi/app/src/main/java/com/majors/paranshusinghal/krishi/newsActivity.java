package com.majors.paranshusinghal.krishi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class newsActivity extends Activity {

    private static final String TAG ="com.majors.paranshusinghal.krishi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_news);

        final ImageView newsImageView = (ImageView)findViewById(R.id.news_activity_imageView);
        newsImageView.post(new Runnable() {
            @Override
            public void run() {
                int ht = newsImageView.getMeasuredHeight();
                int wd = newsImageView.getMeasuredWidth();
                int resID = getResources().getIdentifier("flat_hulk", "drawable", TAG);
                Bitmap unscaledBitmap = BitmapFactory.decodeResource(getResources(), resID);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, wd, ht, true);
                newsImageView.setImageBitmap(scaledBitmap);
            }
        });

        String[] titles = {"title1","title2","title3","title4","title5"};
        String[] descs = {"desc1","desc2","desc3","desc4","desc5"};
        List lis1 = new ArrayList();
        for(int i=0;i<titles.length;i++){
            newselement ns = new newselement(titles[i], descs[i]);
            lis1.add(ns);
        }
        ListView list = (ListView)findViewById(R.id.news_activity_listView);
        customNewsAdaptor adaptor = new customNewsAdaptor(this,R.layout.listview_raw,lis1);
        list.setAdapter(adaptor);
    }

}
