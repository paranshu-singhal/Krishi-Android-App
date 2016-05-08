package com.majors.paranshusinghal.krishi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataDB extends SQLiteOpenHelper{

    private static final String TAGlog = "myTAG";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data_db";


    private static final String TABLE_NAME_MSP = "msp_prices";
    private static final String TABLE_COLUMN_MSP_1 = "commodity";
    private static final String TABLE_COLUMN_MSP_2 = "variety";
    private static final String TABLE_COLUMN_MSP_3 = "d2010_11";
    private static final String TABLE_COLUMN_MSP_4 = "d2011_12";
    private static final String TABLE_COLUMN_MSP_5 = "d2012_13";
    private static final String TABLE_COLUMN_MSP_6 = "d2013_14";
    private static final String TABLE_COLUMN_MSP_7 = "d2014_15";
    private static final String TABLE_COLUMN_MSP_8 = "d2015_16";

    public DataDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        long numRows = DatabaseUtils.queryNumEntries(getReadableDatabase(),TABLE_NAME_MSP);
        if(numRows==0) {dataInsert();}
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TABLE_NAME_MSP + " ( " + TABLE_COLUMN_MSP_1 + " TEXT, "+
                TABLE_COLUMN_MSP_2 + " TEXT, "+
                TABLE_COLUMN_MSP_3 + " INTEGER, "+
                TABLE_COLUMN_MSP_4 + " INTEGER, "+
                TABLE_COLUMN_MSP_5 + " INTEGER, "+
                TABLE_COLUMN_MSP_6 + " INTEGER, "+
                TABLE_COLUMN_MSP_7 + " INTEGER, "+
                TABLE_COLUMN_MSP_8 + " INTEGER); ";
        Log.d(TAGlog, query);
        db.execSQL(query);
        //catch (Throwable t){Log.d(TAGlog, t.getMessage());}
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MSP);
        onCreate(db);
    }
    public Cursor query(String[] projection, String selection, String[] selectArgs){
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME_MSP, projection, selection, selectArgs, null,null,null);
    }
    private void onInsert(String table, ContentValues values){
        SQLiteDatabase db = getWritableDatabase();
        db.insert(table, null, values);
    }

    private void dataInsert(){
        ContentValues values = new ContentValues();

        values.put(TABLE_COLUMN_MSP_1, "Paddy");
        values.put(TABLE_COLUMN_MSP_2, "Common");
        values.put(TABLE_COLUMN_MSP_3, 1000);
        values.put(TABLE_COLUMN_MSP_4, 1080);
        values.put(TABLE_COLUMN_MSP_5, 1250);
        values.put(TABLE_COLUMN_MSP_6, 1310);
        values.put(TABLE_COLUMN_MSP_7, 1360);
        values.put(TABLE_COLUMN_MSP_8, 1410);
        onInsert(TABLE_NAME_MSP, values);

        values.put(TABLE_COLUMN_MSP_1, "Paddy");
        values.put(TABLE_COLUMN_MSP_2, "Grade 'A' ");
        values.put(TABLE_COLUMN_MSP_3, 1030);
        values.put(TABLE_COLUMN_MSP_4, 1110);
        values.put(TABLE_COLUMN_MSP_5, 1280);
        values.put(TABLE_COLUMN_MSP_6, 1345);
        values.put(TABLE_COLUMN_MSP_7, 1400);
        values.put(TABLE_COLUMN_MSP_8, 1450);
        onInsert(TABLE_NAME_MSP, values);

        values.put(TABLE_COLUMN_MSP_1, "Maize");
        values.put(TABLE_COLUMN_MSP_2, "ALL");
        values.put(TABLE_COLUMN_MSP_3, 880);
        values.put(TABLE_COLUMN_MSP_4, 980);
        values.put(TABLE_COLUMN_MSP_5, 1175);
        values.put(TABLE_COLUMN_MSP_6, 1310);
        values.put(TABLE_COLUMN_MSP_7, 1310);
        values.put(TABLE_COLUMN_MSP_8, 1325);
        onInsert(TABLE_NAME_MSP, values);

        values.put(TABLE_COLUMN_MSP_1, "SugarCane");
        values.put(TABLE_COLUMN_MSP_2, "ALL");
        values.put(TABLE_COLUMN_MSP_3,139.12);
        values.put(TABLE_COLUMN_MSP_4,145.00);
        values.put(TABLE_COLUMN_MSP_5,170.00);
        values.put(TABLE_COLUMN_MSP_6,210.00);
        values.put(TABLE_COLUMN_MSP_7,220.00);
        values.put(TABLE_COLUMN_MSP_8,230.00);
        onInsert(TABLE_NAME_MSP, values);

        values.put(TABLE_COLUMN_MSP_1, "Tomato");
        values.put(TABLE_COLUMN_MSP_2, "ALL");
        values.put(TABLE_COLUMN_MSP_3,880);
        values.put(TABLE_COLUMN_MSP_4,980);
        values.put(TABLE_COLUMN_MSP_5,1175);
        values.put(TABLE_COLUMN_MSP_6,1250);
        values.put(TABLE_COLUMN_MSP_7,1250);
        values.put(TABLE_COLUMN_MSP_8,1275);
        onInsert(TABLE_NAME_MSP, values);

        values.put(TABLE_COLUMN_MSP_1, "Potato");
        values.put(TABLE_COLUMN_MSP_2, "ALL");
        values.put(TABLE_COLUMN_MSP_3,780);
        values.put(TABLE_COLUMN_MSP_4,980);
        values.put(TABLE_COLUMN_MSP_5,980);
        values.put(TABLE_COLUMN_MSP_6,1100);
        values.put(TABLE_COLUMN_MSP_7,1150);
        values.put(TABLE_COLUMN_MSP_8,1275);
        onInsert(TABLE_NAME_MSP, values);

        values.put(TABLE_COLUMN_MSP_1, "Wheat");
        values.put(TABLE_COLUMN_MSP_2, "ALL");
        values.put(TABLE_COLUMN_MSP_3,1120);
        values.put(TABLE_COLUMN_MSP_4,1285);
        values.put(TABLE_COLUMN_MSP_5,1350);
        values.put(TABLE_COLUMN_MSP_6,1400);
        values.put(TABLE_COLUMN_MSP_7,1450);
        values.put(TABLE_COLUMN_MSP_8,1510);
        onInsert(TABLE_NAME_MSP, values);
    }
}
