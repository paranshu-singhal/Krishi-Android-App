package com.majors.paranshusinghal.krishi;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MinimumSupportPrice extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ActionBar actionBar;
    Spinner commodity;
    Spinner year;

    TableLayout tableLayout;
    String yr;

    private static final String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";
    private static final String TAGlog = "myTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msp);
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setSubtitle(R.string.enter_details);

        commodity = (Spinner)findViewById(R.id.spinner);
        year = (Spinner)findViewById(R.id.spinner2);
        tableLayout = (TableLayout)findViewById(R.id.table_msp);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.crop_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commodity.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this, R.array.year_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.msp_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_msp:
                queryMsp();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void queryMsp(){
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String crop = commodity.getSelectedItem().toString();
        yr = year.getSelectedItem().toString();

        Uri uri = Uri.parse("content://"+PROVIDER_NAME+"/data");
        String selection = "commodity=?";
        String[] selectArgs={crop};
        String[] projection = {"commodity", "variety", "d"+yr};
        return new CursorLoader(this, uri, projection, selection, selectArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        try {
            //Log.d(TAGlog, String.format("%d", data.getCount()));
            data.moveToFirst();
            Log.d(TAGlog, "EXECUTING2");
            while (!data.isAfterLast()) {
                TableRow row = (TableRow) LayoutInflater.from(MinimumSupportPrice.this).inflate(R.layout.table_row_msp, null);
                ((TextView)row.findViewById(R.id.textView42)).setText(data.getString(data.getColumnIndex("commodity")));
                ((TextView)row.findViewById(R.id.textView39)).setText(data.getString(data.getColumnIndex("variety")));
                ((TextView)row.findViewById(R.id.textView40)).setText(yr);
                ((TextView)row.findViewById(R.id.textView41)).setText(String.format("%d",data.getInt(data.getColumnIndex("d"+yr))));
                tableLayout.addView(row);
                data.moveToNext();
            }
            Log.d(TAGlog, "EXECUTING3");
            tableLayout.setVisibility(View.VISIBLE);
        } catch (Throwable t){Log.d(TAGlog, t.getMessage());}
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
    @Override
    protected void onStop() {
        super.onStop();
        tableLayout.setVisibility(View.GONE);
        //tableLayout.removeAllViews();
    }
}
