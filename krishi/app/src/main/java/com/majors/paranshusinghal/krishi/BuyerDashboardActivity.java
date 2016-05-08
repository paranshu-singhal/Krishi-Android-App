package com.majors.paranshusinghal.krishi;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

public class BuyerDashboardActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAGlog = "myTAG";
    private static final String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";
    private static final String DATA_TABLE = "/joinSellerBuyerList";

    private ActionBar actionBar;
    private String userPhone;
    private Cursor dataUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();
        userPhone = getIntent().getExtras().getString("phone");
        getLoaderManager().initLoader(0,null,this);
        getLoaderManager().initLoader(1,null,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.buyer_dashboard_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(BuyerDashboardActivity.this, BuyerSearchResult.class);
        Bundle bun = new Bundle();
        bun.putString("phone", userPhone);
        intent.putExtras(bun);
        switch (item.getItemId()) {
            case R.id.deleteProfileButton:
                deleteDialog();
                break;
            case R.id.UpdateProfileButton:
                onUpdatePressed();
                break;
            case R.id.SearchButton:
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Uri uri = Uri.parse("content://"+PROVIDER_NAME+"/buyerList");
        Uri uri;
        if(id==0) {
            uri = Uri.parse("content://" + PROVIDER_NAME + "/users?phone=" +userPhone);
        }
        else{
            uri = Uri.parse("content://" + PROVIDER_NAME + DATA_TABLE+"?phone=" +userPhone);
        }
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       // Log.d(TAGlog, "onLoadFinished");
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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    private void populateListView(Cursor data){
       // Log.d(TAGlog, "populate");
        List<BuyerListHolderClass> sellers = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast()) {
            BuyerListHolderClass tempClass = new BuyerListHolderClass(data.getInt(data.getColumnIndex("id")),
                    data.getString(data.getColumnIndex("date")),
                    data.getString(data.getColumnIndex("name_crop")),
                    data.getString(data.getColumnIndex("max_price")),
                    data.getString(data.getColumnIndex("vol")),
                    data.getString(data.getColumnIndex("max_bid")),
                    data.getString(data.getColumnIndex("my_bid")));
            sellers.add(tempClass);
            data.moveToNext();
        }
        Log.d(TAGlog, String.format("listLength: %d", sellers.size()));
        BuyerDashboardAdaptor adaptor = new BuyerDashboardAdaptor(BuyerDashboardActivity.this,sellers, userPhone);
        ExpandableListView itemList = (ExpandableListView)findViewById(R.id.expandableListView2);
        //Log.d(TAGlog, "4BreakPoint");
        itemList.setAdapter(adaptor);
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
                                Uri uri = Uri.parse("content://" + PROVIDER_NAME + "/delete?phone=" + userPhone);
                                int r = getContentResolver().delete(uri, null, null);
                                Log.d(TAGlog, String.format("deleted %d", r));
                                BuyerDashboardActivity.this.finish();
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
