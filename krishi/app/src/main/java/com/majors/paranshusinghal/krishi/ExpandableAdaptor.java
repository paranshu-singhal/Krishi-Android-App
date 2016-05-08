package com.majors.paranshusinghal.krishi;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class ExpandableAdaptor extends BaseExpandableListAdapter {

    private Context ctx;
    private List<BuyerSearchHolderClass> list;
    private String phone_buyer;
    private static final String TAGlog = "myTAG";
    private static final String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";

    public ExpandableAdaptor(Context ctx, List<BuyerSearchHolderClass> objects, String phone_buyer) {
        this.ctx = ctx;
        this.list = objects;
        this.phone_buyer = phone_buyer;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.buyer_search_result_singlerow_parent, parent, false);
        BuyerSearchHolderClass obj = (BuyerSearchHolderClass)getGroup(groupPosition);

        TextView id = (TextView)customView.findViewById(R.id.id_text);
        TextView name_text = (TextView)customView.findViewById(R.id.name_text);
        TextView minPrice_text = (TextView)customView.findViewById(R.id.minPrice_text);
        TextView minVol_text = (TextView)customView.findViewById(R.id.minVol_text);
        TextView date_text = (TextView)customView.findViewById(R.id.date_text);
        TextView address_text = (TextView)customView.findViewById(R.id.address_text);
        TextView maxbid_text  = (TextView)customView.findViewById(R.id.maxBid_text);

        String dateString=null;
        try {
            SimpleDateFormat dt = new SimpleDateFormat("dd MMM, yyyy");
            dateString = dt.format(new SimpleDateFormat("yyyy-MM-dd").parse(obj.getDate()));
        }
        catch (Throwable t){Log.d(TAGlog, t.getMessage());}

        id.setText(String.format("%d", obj.getId()));
        name_text.setText(obj.getNameCrop());
        minPrice_text.setText(obj.getMinPrice());
        minVol_text.setText(obj.getMinVol());
        date_text.setText(dateString);
        address_text.setText(obj.getAddress());
        maxbid_text.setText(obj.getMaxBid());
        return customView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =  inflater.inflate(R.layout.buyer_search_result_singlerow_child, parent, false);

        final BuyerSearchHolderClass obj = (BuyerSearchHolderClass)getGroup(groupPosition);
        Button callBtn = (Button)v.findViewById(R.id.ContactButton);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + obj.getPhone()));
                ctx.startActivity(intent);
            }
        });

        Button bidBtn = (Button)v.findViewById(R.id.BidButton);
        bidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBidDialog(obj);
            }
        });

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    private void showBidDialog(final BuyerSearchHolderClass obj){

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.new_bid_alert_dialog, null);
        final NumberPicker picker = (NumberPicker)view.findViewById(R.id.numberPicker);

        picker.setMinValue(Integer.parseInt(obj.getMaxBid())+1);
        picker.setMaxValue(Integer.parseInt(obj.getMaxBid()) + 100);
        picker.setWrapSelectorWheel(true);

        builder.setTitle("Make New Bid")
                .setView(view)
                .setNegativeButton(ctx.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((BuyerSearchResult)ctx).finish();
                    }
                })
                .setPositiveButton("Make Bid", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/sellerList");
                                ContentValues values = new ContentValues();
                                values.put("max_bid", String.format("%d", picker.getValue()));
                                values.put("buyer_phone", phone_buyer);
                                String selection = "id=?";
                                String selectArgs[] = {String.format("%d", obj.getId())};
                                int id = ctx.getContentResolver().update(uri, values, selection, selectArgs);
                                addToDatabase(obj, String.format("%d", picker.getValue()));
                                ((BuyerSearchResult)ctx).finish();
                                //Log.d(TAGlog, String.format("%d", id));
                            }
                        });
                        thread.start();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addToDatabase(final BuyerSearchHolderClass obj, String latestBid){
        Uri url = Uri.parse("content://" + PROVIDER_NAME + "/buyerList");
        ContentValues values = new ContentValues();
        values.put("id", obj.getId());
        values.put("phone",phone_buyer);
        values.put("name_crop", obj.getNameCrop());
        values.put("vol", obj.getMinVol());
        values.put("max_price", obj.getMinPrice());
        values.put("date", obj.getDate());
        values.put("my_bid", latestBid);
        Uri url2=ctx.getContentResolver().insert(url, values);
        Log.d(TAGlog, url2.toString());
    }
}
