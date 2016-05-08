package com.majors.paranshusinghal.krishi;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SellerDashboardActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAGlog = "myTAG";
    private static final String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";
    ActionBar actionBar;
    View view;

    protected AutoCompleteTextView mAutoCompleteTextView ;
    protected EditText mTotalProduce;
    protected EditText mMinPrice;
    protected EditText mMinVolume;


    protected Cursor dataUser;
    String[] crops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();

        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.NewButton:
                showDialog();
                return true;
            case R.id.UpdateProfileButton:
                onUpdatePressed();
                return true;
            case R.id.deleteProfileButton:
                deleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        String phone =  getIntent().getExtras().getString("phone");
        if(id==0) {
            uri = Uri.parse("content://" + PROVIDER_NAME + "/users?phone=" +phone);
        }
        else{
            uri = Uri.parse("content://" + PROVIDER_NAME + "/sellerList?phone=" +phone);
        }
            return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id=loader.getId();
        if(id==0) {
            dataUser = data;
            String name = dataUser.getString(data.getColumnIndex("name"));
            actionBar.setTitle(name);
            actionBar.setSubtitle(getResources().getString(R.string.dashboard));
        }
        else{
            populateListView(data);
        }
    }

    private void populateListView(Cursor data){

        List<seller_list_holderClass> sellers = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast()) {
            seller_list_holderClass tempClass = new seller_list_holderClass(data.getInt(data.getColumnIndex("id")),
                    data.getString(data.getColumnIndex("name_crop")),
                    data.getString(data.getColumnIndex("tot_volume")),
                    data.getString(data.getColumnIndex("min_vol")),
                    data.getString(data.getColumnIndex("min_price")),
                    data.getString(data.getColumnIndex("date")),
                    data.getString(data.getColumnIndex("max_bid")));
            sellers.add(tempClass);
            data.moveToNext();
        }
        //Log.d(TAGlog, String.format("listLength: %d", sellers.size()));
        SellerListExpandableAdaptor adaptor = new SellerListExpandableAdaptor(SellerDashboardActivity.this,sellers);
        ExpandableListView itemList = (ExpandableListView)findViewById(R.id.expandableListView2);
        //Log.d(TAGlog, "4BreakPoint");
        itemList.setAdapter(adaptor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SellerDashboardActivity.this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.new_item_dialog_layout, null);

        crops = getResources().getStringArray(R.array.crop_list);
        mAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.crop_autoCompleteTextView);
        mTotalProduce = (EditText) view.findViewById(R.id.total_produce_editText);
        mMinPrice = (EditText) view.findViewById(R.id.price_quintal_editText);
        mMinVolume = (EditText) view.findViewById(R.id.min_sell_vol_editText);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, crops);
        mAutoCompleteTextView.setAdapter(adapter);

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
                addItem(dialog);
            }
        });
    }

    private void addItem(AlertDialog dialog){
        String crop= mAutoCompleteTextView.getText().toString();
        String MinVolume = mMinVolume.getText().toString();
        String MinPrice = mMinPrice.getText().toString();
        String phone = dataUser.getString(dataUser.getColumnIndex("phone_no"));
        String tag = dataUser.getString(dataUser.getColumnIndex("tag"));

        String TotProduce = mTotalProduce.getText().toString();
        if (verifyAddItem(crop, TotProduce, MinVolume, MinPrice)) {
            NewRegister register = new NewRegister(tag, phone, crop, TotProduce, MinVolume, MinPrice);
            register.execute();
            dialog.dismiss();
        }
    }


    private boolean verifyAddItem(String crop, String totProduce, String minVolume, String minPrice){

        if(!Arrays.asList(crops).contains(crop)){
            mAutoCompleteTextView.setError(getResources().getString(R.string.FromListError));
            return false;
        }
        if(totProduce.length()<=0 || Integer.parseInt(totProduce)<=0){
            mTotalProduce.setError(getResources().getString(R.string.totProduceError));
            return false;
        }
        if(minPrice.length()<=0 || Integer.parseInt(minPrice)<=0){
            mMinPrice.setError(getResources().getString(R.string.totProduceError));
            return false;
        }
        if(minVolume.length()<=0 || Integer.parseInt(minVolume)<=0 || Integer.parseInt(minVolume)>Integer.parseInt(totProduce)){
            mMinVolume.setError(getResources().getString(R.string.totProduceError));
            return false;
        }
        return true;
    }

    private void addSqlite(){

    }

    private class NewRegister extends AsyncTask<Void, Void, String>{

        private String tag;
        private String crop;

        private String phone;
        private String totProduce;
        private String minVolume;
        private String minPrice;

        public NewRegister(String tag, String phone,String crop, String totProduce, String minVolume, String minPrice) {
            this.tag = tag;
            this.crop = crop;
            this.phone = phone;
            this.totProduce = totProduce;
            this.minVolume = minVolume;
            this.minPrice = minPrice;
        }

        @Override
        protected String doInBackground(Void... params) {
            //Log.d(TAGlog, "do in back");
            String localhost = getResources().getString(R.string.localhost);
            String answer = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) (new URL(localhost + "/krishi/item_interface.php")).openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String values = "tag=" + URLEncoder.encode(tag, "UTF-8") +
                        "&name_crop=" + URLEncoder.encode(crop, "UTF-8") +
                        "&phone=" + URLEncoder.encode(phone, "UTF-8") +
                        "&tot_volume=" + URLEncoder.encode(totProduce, "UTF-8") +
                        "&min_price=" + URLEncoder.encode(minPrice, "UTF-8") +
                        "&min_vol=" + URLEncoder.encode(minVolume, "UTF-8");

                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes(values);
                dataOutputStream.flush();
                dataOutputStream.close();

                Log.d(TAGlog, String.format("code %d", conn.getResponseCode()));

                StringBuilder buffer = new StringBuilder();
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }

                is.close();
                conn.disconnect();
                answer = buffer.toString();
            }
            catch (Throwable t){
                Log.d(TAGlog, t.getMessage());
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAGlog, s);
            ContentValues values = new ContentValues();
            try{
                JSONObject json = new JSONObject(s);
                if(Integer.parseInt(json.getString("success"))==1) {
                    values.put("id", Integer.parseInt(json.getString("id")));
                    values.put("phone", json.getString("phone"));
                    values.put("name_crop", json.getString("name_crop"));
                    values.put("tot_volume", json.getString("tot_volume"));
                    values.put("min_vol", json.getString("min_vol"));
                    values.put("min_price", json.getString("min_price"));
                    values.put("date", json.getString("date"));
                    values.put("max_bid", json.getString("min_price"));
                    values.put("buyer_phone", "NO BIDS");

                    Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/sellerList");
                    getContentResolver().insert(uri, values);
                    getLoaderManager().restartLoader(1,null,SellerDashboardActivity.this);
                }
                else{
                    Toast.makeText(SellerDashboardActivity.this, json.getString("error_msg"), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Throwable t){
                Log.d(TAGlog, t.getMessage());
            }
        }
    }

    private void deleteDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.delete_profile))
                .setMessage(getResources().getString(R.string.delete_profile_msg))
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                )
                .setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String phone = dataUser.getString(dataUser.getColumnIndex("phone_no"));
                                Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/delete?phone=" + phone);
                                int r = getContentResolver().delete(uri, null, null);
                                Log.d(TAGlog, String.format("deleted %d", r));
                                SellerDashboardActivity.this.finish();
                            }
                    }
                );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onUpdatePressed(){
        Bundle bundle = new Bundle();
        bundle.putString("tag", dataUser.getString(dataUser.getColumnIndex("tag")));
        bundle.putString("name", dataUser.getString(dataUser.getColumnIndex("name")));
        bundle.putString("phone", dataUser.getString(dataUser.getColumnIndex("phone_no")));
        bundle.putString("address1", dataUser.getString(dataUser.getColumnIndex("address1")));
        bundle.putString("address2", dataUser.getString(dataUser.getColumnIndex("address2")));
        bundle.putString("city", dataUser.getString(dataUser.getColumnIndex("city")));
        bundle.putString("state", dataUser.getString(dataUser.getColumnIndex("state")));
        bundle.putString("country", dataUser.getString(dataUser.getColumnIndex("country")));

        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("callingActivity", "SellerDashboardActivity");
        intent.putExtras(bundle);
        startActivity(intent);
    }
}


