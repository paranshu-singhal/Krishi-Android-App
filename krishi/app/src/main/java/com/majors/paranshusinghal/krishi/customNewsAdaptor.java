package com.majors.paranshusinghal.krishi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
public class customNewsAdaptor extends ArrayAdapter {

    public customNewsAdaptor(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customview = inflater.inflate(R.layout.listview_raw, parent, false);

        newselement ns = (newselement) getItem(position);
        TextView titleTxt = (TextView) customview.findViewById(R.id.news_title);
        TextView descTxt = (TextView) customview.findViewById(R.id.news_description);
        TextView pubDateTxt = (TextView)customview.findViewById(R.id.news_pubDate);

        titleTxt.setText(ns.getTitle());
        descTxt.setText(ns.getDescription());
        pubDateTxt.setText(ns.getPubDate());
        return customview;
    }
}
