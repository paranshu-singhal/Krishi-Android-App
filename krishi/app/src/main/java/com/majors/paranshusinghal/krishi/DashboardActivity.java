package com.majors.paranshusinghal.krishi;

import android.app.AlertDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class DashboardActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

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


        Button buttonNewItem = (Button)findViewById(R.id.buttonNewItem);
        buttonNewItem.setText(getResources().getString(R.string.Button_Add_Item));
        buttonNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        if(id==0) {
            uri = Uri.parse("content://" + PROVIDER_NAME + "/users?phone=" + getIntent().getExtras().getString("phone"));
        }
        else{
            uri = Uri.parse("content://" + PROVIDER_NAME + "/sellerList");
        }
            return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id=loader.getId();
        if(id==0) {
            dataUser = data;
            String name = data.getString(data.getColumnIndex("name"));
            actionBar.setTitle(name);
            actionBar.setSubtitle(getResources().getString(R.string.dashboard));
        }
        else{populateListView(data);}
    }

    private void populateListView(Cursor data){

        List<seller_list_holderClass> sellers = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast()) {
            seller_list_holderClass tempClass = new seller_list_holderClass(data.getInt(data.getColumnIndex("id")),
                    data.getString(data.getColumnIndex("phone")),
                    data.getString(data.getColumnIndex("name_crop")),
                    data.getString(data.getColumnIndex("tot_volume")),
                    data.getString(data.getColumnIndex("min_vol")),
                    data.getString(data.getColumnIndex("min_price")),
                    data.getString(data.getColumnIndex("date")));

            sellers.add(tempClass);
            data.moveToNext();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
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
                        // DashboardActivity.this.getDialog().cancel();
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
        String TotProduce = mTotalProduce.getText().toString();
        String MinVolume = mMinVolume.getText().toString();
        String MinPrice = mMinPrice.getText().toString();
        String phone = dataUser.getString(dataUser.getColumnIndex("phone_no"));
        String tag = dataUser.getString(dataUser.getColumnIndex("tag"));

        if(verifyAddItem(crop, TotProduce, MinVolume, MinPrice)){
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
        if(minVolume.length()<=0 || Integer.parseInt(minVolume)<=0){
            mMinVolume.setError(getResources().getString(R.string.totProduceError));
            return false;
        }
        return true;
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
            super.onPostExecute(s);
            Log.d(TAGlog, s);
        }
    }

}


