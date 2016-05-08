package com.majors.paranshusinghal.krishi;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuyerSearchResult extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ActionBar actionBar;
    AutoCompleteTextView autoCompleteTextView;
    EditText priceQuintal;
    EditText minBuy;
    String[] crops;

    String crop;
    String pricequintal;
    String minbuy;

    String phone_buyer;

    private static final String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";
    private static final String TAGlog = "myTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();
        phone_buyer = getIntent().getExtras().getString("phone");
        showSearchDialog();
    }


    private void showSearchDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.buyer_search_dialog, null);

        autoCompleteTextView = (AutoCompleteTextView)view.findViewById(R.id.editAutoCompleteCrop);
        priceQuintal = (EditText)view.findViewById(R.id.editTextPriceQuintal);
        minBuy = (EditText)view.findViewById(R.id.editTextMinBuy);

        crops = getResources().getStringArray(R.array.crop_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, crops);
        autoCompleteTextView.setAdapter(adapter);


        builder.setTitle(getResources().getString(R.string.search_to_buy));
        builder.setView(view)
                .setPositiveButton(getResources().getString(R.string.search), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }})
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyData()) {
                    getLoaderManager().initLoader(0,null, BuyerSearchResult.this);
                    dialog.dismiss();
                }
            }
        });
    }

    private boolean verifyData(){

        pricequintal = priceQuintal.getText().toString();
        minbuy = minBuy.getText().toString();
        crop = autoCompleteTextView.getText().toString();

        if(!Arrays.asList(crops).contains(crop)){
            autoCompleteTextView.setError(getResources().getString(R.string.FromListError));
            return false;
        }
        if(Integer.parseInt(pricequintal)<=0 || pricequintal.length()<=0){
            priceQuintal.setError(getResources().getString(R.string.totProduceError));
            return false;
        }
        if(Integer.parseInt(minbuy)<=0 || minbuy.length()<=0){
            minBuy.setError(getResources().getString(R.string.totProduceError));
            return false;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/sellerList/BuyerQuery");
        String selection = "name_crop=? AND min_price<=? AND min_vol<=?";
        String selectionArgs[] = {crop, pricequintal, minbuy};
        return new CursorLoader(this, uri, null, selection, selectionArgs, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<BuyerSearchHolderClass> list = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast()) {
            BuyerSearchHolderClass temp = new BuyerSearchHolderClass(data.getInt(data.getColumnIndex("id")),
                    data.getString(data.getColumnIndex("name_crop")),
                    data.getString(data.getColumnIndex("min_price")),
                    data.getString(data.getColumnIndex("min_vol")),
                    data.getString(data.getColumnIndex("date")),
                    null,
                    data.getString(data.getColumnIndex("phone")),
                    data.getString(data.getColumnIndex("max_bid"))
            );
            list.add(temp);
            data.moveToNext();
        }
        Log.d(TAGlog, String.format("size %d", list.size()));

        ExpandableAdaptor adaptor = new ExpandableAdaptor(BuyerSearchResult.this, list, phone_buyer);
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableListView2);
        listView.setAdapter(adaptor);
    }
    @Override
    public void onLoaderReset(Loader loader) {

    }
}
