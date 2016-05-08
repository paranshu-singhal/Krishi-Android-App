package com.majors.paranshusinghal.krishi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class SellerDB extends SQLiteOpenHelper{

    private static final String TAGlog = "myTAG";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sellersDB";

    private static final String TABLE_NAME_SELL = "seller";
    private static final String COLUMN_NAME_SELL_1 = "id";
    private static final String COLUMN_NAME_SELL_2 = "phone";
    private static final String COLUMN_NAME_SELL_3 = "name_crop";
    private static final String COLUMN_NAME_SELL_4 = "tot_volume";
    private static final String COLUMN_NAME_SELL_5 = "min_price";
    private static final String COLUMN_NAME_SELL_6 = "min_vol";
    private static final String COLUMN_NAME_SELL_7 = "date";
    private static final String COLUMN_NAME_SELL_8 = "max_bid";
    private static final String COLUMN_NAME_SELL_9 = "buyer_phone";

    private static final String TABLE_NAME_BUY = "buyer";
    private static final String COLUMN_NAME_BUY_1 = "id";
    private static final String COLUMN_NAME_BUY_2 = "phone";
    private static final String COLUMN_NAME_BUY_3 = "name_crop";
    private static final String COLUMN_NAME_BUY_4 = "vol";
    private static final String COLUMN_NAME_BUY_5 = "max_price";
    private static final String COLUMN_NAME_BUY_6 = "date";
    private static final String COLUMN_NAME_BUY_7 = "my_bid";



    public SellerDB(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME_SELL_1, 200);
        values.put(COLUMN_NAME_SELL_2, "8800359250");
        values.put(COLUMN_NAME_SELL_3, "Wheat");
        values.put(COLUMN_NAME_SELL_4, "100");
        values.put(COLUMN_NAME_SELL_5, "50");
        values.put(COLUMN_NAME_SELL_6, "50");
        values.put(COLUMN_NAME_SELL_7, "2016-03-16");
        values.put(COLUMN_NAME_SELL_8, "70");
        values.put(COLUMN_NAME_SELL_9, "9876543211");
        onInsert("/sellerList", values);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME_SELL + "( " + COLUMN_NAME_SELL_1 + " INTEGER, " +
                COLUMN_NAME_SELL_2 + " TEXT, "+
                COLUMN_NAME_SELL_3 + " TEXT, "+
                COLUMN_NAME_SELL_4 + " TEXT, "+
                COLUMN_NAME_SELL_5 + " TEXT, "+
                COLUMN_NAME_SELL_6 + " TEXT, "+
                COLUMN_NAME_SELL_7 + " TEXT, "+
                COLUMN_NAME_SELL_8 + " TEXT, "+
                COLUMN_NAME_SELL_9 + " TEXT); ";
        Log.d(TAGlog, query);
        db.execSQL(query);
        query = "CREATE TABLE " + TABLE_NAME_BUY + "( " + COLUMN_NAME_BUY_1 + " INTEGER, " +
                COLUMN_NAME_BUY_2 + " TEXT, "+
                COLUMN_NAME_BUY_3 + " TEXT, "+
                COLUMN_NAME_BUY_4 + " TEXT, "+
                COLUMN_NAME_BUY_5 + " TEXT, "+
                COLUMN_NAME_BUY_6 + " TEXT, "+
                COLUMN_NAME_BUY_7 + " TEXT); ";
        Log.d(TAGlog, query);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SELL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BUY);
        onCreate(db);
    }

    public Cursor onQuery(Uri uri){

        String table = uri.getPath();
        String phone = uri.getQueryParameter("phone");
        SQLiteDatabase db = getWritableDatabase();
        String sql;
        switch (table){
            case "/sellerList":
                sql = "select * from "+TABLE_NAME_SELL+" where phone = "+phone+" ; ";
                break;
            case "/joinSellerBuyerList":
                sql = "select "+ TABLE_NAME_SELL+"."+COLUMN_NAME_SELL_8+" , "+
                        TABLE_NAME_BUY+"."+COLUMN_NAME_BUY_1+" , "+
                        TABLE_NAME_BUY+"."+COLUMN_NAME_BUY_2+" , "+
                        TABLE_NAME_BUY+"."+COLUMN_NAME_BUY_3+" , "+
                        TABLE_NAME_BUY+"."+COLUMN_NAME_BUY_4+" , "+
                        TABLE_NAME_BUY+"."+COLUMN_NAME_BUY_5+" , "+
                        TABLE_NAME_BUY+"."+COLUMN_NAME_BUY_6+" , "+
                        TABLE_NAME_BUY+"."+COLUMN_NAME_BUY_7+
                        " FROM "+TABLE_NAME_SELL+" JOIN "+TABLE_NAME_BUY+" ON "+" seller.id=buyer.id WHERE buyer.phone = "+phone+" ; ";
                break;
            case "/secondMaxBuyer":
                sql = "select max(my_bid),phone from "+TABLE_NAME_BUY+" where id="+uri.getQueryParameter("id")+" ; ";
                break;
            default:
                sql = "select * from "+TABLE_NAME_BUY+" where phone = "+phone+" ; ";
                break;
        }
        Log.d(TAGlog, sql);
        return db.rawQuery(sql, null);
    }

    public Cursor onBuyerQuery(String selection, String[] selectArgs){
        SQLiteDatabase db = getWritableDatabase();
        return db.query(TABLE_NAME_SELL, null, selection, selectArgs, null, null, COLUMN_NAME_SELL_7);
    }

    public long onInsert(String table, ContentValues values){
        long id;
        SQLiteDatabase db = getWritableDatabase();
        switch (table) {
            case "/sellerList":
                id = db.insert(TABLE_NAME_SELL, null, values);
                break;
            default:
                id = db.insert(TABLE_NAME_BUY, null, values);
                break;
        }
        return  id;
    }

    public int onDelete(String table, String selection, String[] selectionArgs){
        Log.d(TAGlog, "sellerDB Delete");
        SQLiteDatabase db = getWritableDatabase();
        switch (table) {
            case "/sellerList":
                return db.delete(TABLE_NAME_SELL, selection, selectionArgs);
            default:
                return db.delete(TABLE_NAME_BUY, selection, selectionArgs);
        }
    }
    public int onUpdate(String table, ContentValues values, String whereClause, String[] whereArgs){
        SQLiteDatabase db = getWritableDatabase();
        switch (table) {
            case "/sellerList":
                return  db.update(TABLE_NAME_SELL, values, whereClause, whereArgs);
            default:
                return  db.update(TABLE_NAME_BUY, values, whereClause, whereArgs);
        }
    }
}
