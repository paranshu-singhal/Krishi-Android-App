package com.majors.paranshusinghal.krishi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.List;

public class BuyerDashboardAdaptor extends BaseExpandableListAdapter{

    private Context ctx;
    private List<BuyerListHolderClass> list;
    private String phone;
    private static final String TAGlog = "myTAG";
    private static final String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";

    private int maxBid;

    public BuyerDashboardAdaptor(Context ctx, List<BuyerListHolderClass> list, String phone) {
        this.ctx = ctx;
        this.list = list;
        this.phone = phone;
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

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View CustomView = inflater.inflate(R.layout.buyer_dashboard_singlerow_parent, parent, false);

        BuyerListHolderClass obj = (BuyerListHolderClass) getGroup(groupPosition);

        TextView textID = ((TextView) CustomView.findViewById(R.id.textView20));
        TextView textDate = ((TextView) CustomView.findViewById(R.id.textView33));
        TextView textName = ((TextView) CustomView.findViewById(R.id.textView23));
        TextView textVolume = ((TextView) CustomView.findViewById(R.id.textView25));
        TextView textPrice = ((TextView) CustomView.findViewById(R.id.textView27));
        TextView textMaxBid = ((TextView) CustomView.findViewById(R.id.textView29));
        TextView MyBid = ((TextView) CustomView.findViewById(R.id.textView31));

        maxBid = Integer.parseInt(obj.getMaxbid());
        textID.setText(String.format("%d", obj.getId()));
        textDate.setText(obj.getDate());
        textName.setText(obj.getName());
        textVolume.setText(obj.getMaxvol());
        textPrice.setText(obj.getMaxprice());
        textMaxBid.setText(obj.getMaxbid());
        MyBid.setText(obj.getMybid());

        return CustomView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.buyer_dashboard_singlerow_child, parent, false);

        final BuyerListHolderClass obj = (BuyerListHolderClass) getGroup(groupPosition);

        //Button updateBtn = (Button) v.findViewById(R.id.button1);
        Button deleteBtn = (Button) v.findViewById(R.id.button2);
        Button updateBidBtn = (Button) v.findViewById(R.id.button3);

        updateBidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker(obj, parent);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(obj);
            }
        });
        return v;
    }

    private void showNumberPicker(final BuyerListHolderClass obj, ViewGroup parent){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.new_bid_alert_dialog, parent, false);
        final NumberPicker picker = (NumberPicker)view.findViewById(R.id.numberPicker);

        picker.setMinValue(maxBid+1);
        picker.setMaxValue(maxBid + 100);
        picker.setWrapSelectorWheel(true);

        builder.setTitle("Make New Bid")
                .setView(view)
                .setNegativeButton(ctx.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //((BuyerSearchResult)ctx).finish();
                    }
                })
                .setPositiveButton("Make Bid", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/sellerList");
                        ContentValues values = new ContentValues();
                        values.put("max_bid", String.format("%d", picker.getValue()));
                        values.put("buyer_phone", phone);
                        String selection = "id=?";
                        String selectArgs[] = {String.format("%d", obj.getId())};
                        ctx.getContentResolver().update(uri, values, selection, selectArgs);
                        updateBuyerTable(String.format("%d", picker.getValue()), String.format("%d", obj.getId()));
                        ((Activity) ctx).getLoaderManager().restartLoader(1, null, (android.app.LoaderManager.LoaderCallbacks<Cursor>) ctx);
                        BuyerDashboardAdaptor.this.notifyDataSetChanged();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteDialog(final BuyerListHolderClass obj){

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getResources().getString(R.string.confirm))
                .setMessage(ctx.getResources().getString(R.string.delete_item_msg))
                .setNegativeButton(ctx.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(ctx.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/buyerList");
                        String selection = "id=? AND phone=?";
                        String selectArgs[] = {String.format("%d", obj.getId()), phone};
                        ctx.getContentResolver().delete(uri, selection, selectArgs);

                        uri = Uri.parse("content://" + PROVIDER_NAME + "/secondMaxBuyer?id="+String.format("%d", obj.getId()));
                        Cursor crs = ctx.getContentResolver().query(uri, null, null, null, null);
                        crs.moveToFirst();

                        ContentValues values = new ContentValues();

                        try {
                            values.put("max_bid", crs.getString(crs.getColumnIndex("max(my_bid)")));
                            //Log.d(TAGlog, crs.getString(1));
                            values.put("buyer_phone", crs.getString(crs.getColumnIndex("phone")));
                        }
                        catch (Throwable t){Log.d(TAGlog, t.getMessage());}
                        crs.close();

                        uri = Uri.parse("content://" + PROVIDER_NAME + "/sellerList");
                        selection = "id=?";
                        String selectArgs2[] = {String.format("%d", obj.getId())};
                        ctx.getContentResolver().update(uri, values, selection, selectArgs2);
                        Log.d(TAGlog, "15");

                        ((Activity) ctx).getLoaderManager().restartLoader(1, null, (android.app.LoaderManager.LoaderCallbacks<Cursor>)ctx);
                        BuyerDashboardAdaptor.this.notifyDataSetChanged();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateBuyerTable(String latestBid, String id){
        Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/buyerList");
        ContentValues values = new ContentValues();
        values.put("my_bid", latestBid);
        String selection = "id=? AND phone=?";
        String selectArgs[] = {id, phone};
        ctx.getContentResolver().update(uri, values, selection, selectArgs);
    }
}
