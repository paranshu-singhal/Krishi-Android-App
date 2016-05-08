package com.majors.paranshusinghal.krishi;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ScreenSlidePageAdaptor extends FragmentPagerAdapter {

    final int PAGE_COUNT;
    private String tabTitles[];
    private Context context;
    private String cropType;

    public ScreenSlidePageAdaptor(FragmentManager fm, Context context, String cropType) {
        super(fm);
        this.context = context;
        tabTitles = context.getResources().getStringArray(R.array.crop_category);
        PAGE_COUNT = tabTitles.length;
        this.cropType = cropType;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("cropType", cropType);
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
