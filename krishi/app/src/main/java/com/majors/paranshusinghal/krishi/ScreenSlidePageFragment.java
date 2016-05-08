package com.majors.paranshusinghal.krishi;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ScreenSlidePageFragment extends Fragment {

    private Context ctx;
    private static final String TAGlog = "myTAG";
    private String cropType;
    private int position;
    private TextView textView;
    private String[] tabs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        cropType = args.getString("cropType");
        position = args.getInt("position");
        tabs = ctx.getResources().getStringArray(R.array.crop_category);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        textView = (TextView)rootView.findViewById(R.id.textView);

        getText();
        return rootView;
    }

    public void getText(){
        InputStream input;

        try {
            input = ctx.getAssets().open("crop.txt");

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            String text = new String(buffer);
            try {
                JSONObject json = new JSONObject(text);
                String abc = json.getJSONObject(cropType).getString(tabs[position]);
                textView.setText(abc);
            }
            catch (Throwable e) {
                Log.d(TAGlog, e.getMessage());}
        } catch (IOException e) {
            Log.d(TAGlog, e.getMessage());
            e.printStackTrace();
        }
    }
}
