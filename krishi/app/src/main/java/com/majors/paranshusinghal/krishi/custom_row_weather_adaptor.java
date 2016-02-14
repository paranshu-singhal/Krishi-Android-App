package com.majors.paranshusinghal.krishi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
public class custom_row_weather_adaptor extends ArrayAdapter {

    public custom_row_weather_adaptor(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customview = inflater.inflate(R.layout.custom_row_weather, parent, false);

        custom_raw_weather_holderClass obj  = (custom_raw_weather_holderClass)getItem(position);
        TextView maxTemp = (TextView)customview.findViewById(R.id.maxTemp_ans);
        TextView minTemp = (TextView)customview.findViewById(R.id.minTemp_ans);
        TextView humidity= (TextView)customview.findViewById(R.id.humid_ans);
        TextView weather = (TextView)customview.findViewById(R.id.weather_ans);
        TextView clouds  = (TextView)customview.findViewById(R.id.cloud_ans);
        TextView speed   = (TextView)customview.findViewById(R.id.speed_ans);
        TextView date    = (TextView)customview.findViewById(R.id.day_ans);

        SimpleDateFormat dt = new SimpleDateFormat("EE MMM dd");
        String temp = dt.format(obj.getDate());

        maxTemp .setText(String.format("%.2f",obj.getMaxTemp()));
        minTemp .setText(String.format("%.2f", obj.getMinTemp()));
        humidity.setText(obj.getHumidity().toString());
         weather.setText(obj.getWeather());
          clouds.setText(obj.getClouds().toString());
           speed.setText(obj.getSpeed().toString());
        date.setText(temp);

        return customview;
    }
}
