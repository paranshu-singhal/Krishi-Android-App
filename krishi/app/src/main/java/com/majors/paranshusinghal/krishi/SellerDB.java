package com.majors.paranshusinghal.krishi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SellerDB extends SQLiteOpenHelper{

    private static final String TAGlog = "myTAG";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BiddingSysDB";

    private static final String TABLE_NAME_SELL = "seller";
    private static final String COLUMN_NAME_SELL_1 = "id";
    private static final String COLUMN_NAME_SELL_2 = "phone";
    private static final String COLUMN_NAME_SELL_3 = "name_crop";
    private static final String COLUMN_NAME_SELL_4 = "tot_volume";
    private static final String COLUMN_NAME_SELL_5 = "min_price";
    private static final String COLUMN_NAME_SELL_6 = "min_vol";
    private static final String COLUMN_NAME_SELL_7 = "date";

    public SellerDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME_SELL + "( " + COLUMN_NAME_SELL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_SELL_2 + " TEXT UNIQUE, "+
                COLUMN_NAME_SELL_3 + " TEXT, "+
                COLUMN_NAME_SELL_4 + " TEXT, "+
                COLUMN_NAME_SELL_5 + " TEXT, "+
                COLUMN_NAME_SELL_6 + " TEXT, "+
                COLUMN_NAME_SELL_7 + " TEXT); ";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SELL);
        onCreate(db);
    }

    public Cursor onQuery(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select * from "+TABLE_NAME_SELL+";";
        return db.rawQuery(sql, null);
    }
    public long onInsert(ContentValues values){
        long id;
        SQLiteDatabase db = getWritableDatabase();
        id = db.insert(TABLE_NAME_SELL, null, values);
        return  id;
    }
}
