package com.majors.paranshusinghal.krishi;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class TabActivity extends AppCompatActivity {

    private static final String TAGlog = "myTAG";
    private String cropType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Downloading required data. This might take a few minutes.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fab.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate));

            }
        });
        cropType = getIntent().getExtras().getString("id");

        final ImageView imageView = (ImageView)findViewById(R.id.activity_tab_imageView);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                int imgSrc = getResources().getIdentifier(cropType.toLowerCase() + "_bright", "drawable", getPackageName());
                try {
                    Drawable rBlack;
                    if (android.os.Build.VERSION.SDK_INT >= 21) {
                        rBlack = getResources().getDrawable(imgSrc, getTheme());
                    } else {
                        rBlack = getResources().getDrawable(imgSrc);
                    }
                    imageView.setImageDrawable(rBlack);
                }
                catch (Throwable t) {
                    Log.d(TAGlog, t.getMessage());
                }
            }
        });


        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(cropType);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        String array[] = getResources().getStringArray(R.array.crop_category);

        for (int i = 0; i < array.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(array[i]);
            tabLayout.addTab(tabLayout.newTab().setCustomView(textView), i);
        }

        ViewPager mPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter adaptor = new ScreenSlidePageAdaptor(getSupportFragmentManager(), this, cropType);
        mPager.setAdapter(adaptor);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        tabLayout.setupWithViewPager(mPager);
    }


    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            //Log.d(TAGlog, String.format("%f, %f", view.getPivotX(), view.getPivotY()));

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
