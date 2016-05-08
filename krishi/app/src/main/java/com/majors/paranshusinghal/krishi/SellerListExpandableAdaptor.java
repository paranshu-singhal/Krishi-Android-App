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
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class SellerListExpandableAdaptor extends BaseExpandableListAdapter{

    private Context ctx;
    private List<seller_list_holderClass> list;
    private static final String TAGlog = "myTAG";
    private static final String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";

    protected String[] crops;
    protected AutoCompleteTextView mAutoCompleteTextView ;
    protected EditText mTotalProduce;
    protected EditText mMinPrice;
    protected EditText mMinVolume;

    public SellerListExpandableAdaptor(Context ctx, List<seller_list_holderClass> list) {
        this.ctx = ctx;
        this.list = list;
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

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View CustomView = inflater.inflate(R.layout.custom_row_sellerlist_parent, parent, false);

        seller_list_holderClass obj = (seller_list_holderClass)getGroup(groupPosition);

        TextView textID = ((TextView) CustomView.findViewById(R.id.textView6));
        TextView textDate = ((TextView) CustomView.findViewById(R.id.textView7));
        TextView textName =((TextView)CustomView.findViewById(R.id.textView8));
        TextView textVolume =((TextView)CustomView.findViewById(R.id.textView9));
        TextView textPrice =((TextView)CustomView.findViewById(R.id.textView10));
        TextView textMinVol =((TextView)CustomView.findViewById(R.id.textView12));
        TextView textMaxBid =((TextView)CustomView.findViewById(R.id.textView17));

        textID.setText(String.format("%d",obj.getId()));
        textDate.setText(obj.getDate());
        textName.setText(obj.getName_crop());
        textVolume.setText(obj.getTot_volume());
        textPrice.setText(obj.getMin_price());
        textMinVol.setText(obj.getMin_volume());
        textMaxBid.setText(obj.getMaxBid());

        return CustomView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =  inflater.inflate(R.layout.custom_row_sellerlist_child, parent, false);

        final seller_list_holderClass obj = (seller_list_holderClass)getGroup(groupPosition);

        Button updateBtn = (Button)v.findViewById(R.id.updateItemButton);
        Button deleteBtn = (Button)v.findViewById(R.id.deleteItemButton);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(obj);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/sellerList");
                String selection = "id=?";
                String args[] = {String.format("%d", obj.getId())};
                ctx.getContentResolver().delete(uri, selection, args);
                ((Activity)ctx).getLoaderManager().restartLoader(1,null, (android.app.LoaderManager.LoaderCallbacks<Cursor>)ctx);
                SellerListExpandableAdaptor.this.notifyDataSetChanged();
            }
        });
        return v;
    }

    private void showDialog(final seller_list_holderClass obj){

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.new_item_dialog_layout, null);

        crops = ctx.getResources().getStringArray(R.array.crop_list);
        mAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.crop_autoCompleteTextView);
        mTotalProduce = (EditText) view.findViewById(R.id.total_produce_editText);
        mMinPrice = (EditText) view.findViewById(R.id.price_quintal_editText);
        mMinVolume = (EditText) view.findViewById(R.id.min_sell_vol_editText);
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_dropdown_item_1line, crops);
        //mAutoCompleteTextView.setAdapter(adapter);

        mAutoCompleteTextView.setText(obj.getName_crop());
        mTotalProduce.setText(obj.getTot_volume());
        mMinPrice.setText(obj.getMin_price());
        mMinVolume.setText(obj.getMin_volume());

        builder.setTitle("Add New Item");
        builder.setView(view)
                .setPositiveButton(R.string.add_item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // SellerDashboardActivity.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(dialog, obj);
            }
        });
    }

    private void addItem(AlertDialog dialog, seller_list_holderClass obj){
        String crop= mAutoCompleteTextView.getText().toString();
        String MinVolume = mMinVolume.getText().toString();
        String MinPrice = mMinPrice.getText().toString();
        String TotProduce = mTotalProduce.getText().toString();

        if (verifyAddItem(crop, TotProduce, MinVolume, MinPrice)) {
            Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/sellerList");
            String selection = "id=?";
            String args[] = {String.format("%d", obj.getId())};
            ContentValues vals = new ContentValues();
            vals.put("name_crop", crop);vals.put("tot_volume",TotProduce);vals.put("min_price",MinPrice);vals.put("min_vol", MinVolume);
            int id=ctx.getContentResolver().update(uri, vals, selection, args);
            Log.d(TAGlog, String.format("updated id: %d", id));
            dialog.dismiss();
            ((Activity)ctx).getLoaderManager().restartLoader(1,null, (android.app.LoaderManager.LoaderCallbacks<Cursor>)ctx);
            this.notifyDataSetChanged();
        }
    }


    private boolean verifyAddItem(String crop, String totProduce, String minVolume, String minPrice){

        if(!Arrays.asList(crops).contains(crop)){
            mAutoCompleteTextView.setError(ctx.getResources().getString(R.string.FromListError));
            return false;
        }
        if(totProduce.length()<=0 || Integer.parseInt(totProduce)<=0){
            mTotalProduce.setError(ctx.getResources().getString(R.string.totProduceError));
            return false;
        }
        if(minPrice.length()<=0 || Integer.parseInt(minPrice)<=0){
            mMinPrice.setError(ctx.getResources().getString(R.string.totProduceError));
            return false;
        }
        if(minVolume.length()<=0 || Integer.parseInt(minVolume)<=0 || Integer.parseInt(minVolume)>Integer.parseInt(totProduce)){
            mMinVolume.setError(ctx.getResources().getString(R.string.totProduceError));
            return false;
        }
        return true;
    }


}
