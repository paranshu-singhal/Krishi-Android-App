package com.majors.paranshusinghal.krishi;

import android.support.v7.app.ActionBar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Button buttonNewItem;
    private static final String TAGlog = "myTAG";
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();

        buttonNewItem = (Button)findViewById(R.id.buttonNewItem);
        buttonNewItem.setText(getResources().getString(R.string.Button_Add_Item));
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";
        Uri uriUser = Uri.parse("content://" + PROVIDER_NAME + "/users?phone=" + getIntent().getExtras().getString("phone"));
        return new CursorLoader(this, uriUser, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String name = data.getString(data.getColumnIndex("name"));

        actionBar.setTitle(name);
        actionBar.setSubtitle(getResources().getString(R.string.dashboard));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}


