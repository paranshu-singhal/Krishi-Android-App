package com.majors.paranshusinghal.krishi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class sqliteDB extends SQLiteOpenHelper{

    private static final String TAGlog = "myTAG";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "phone_numbersDB";

    private static final String TABLE_NAME = "numbers";
    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_NUMBER = "number";

    private static final String TABLE_NAME_2 = "users";
    private static final String COLUMN_NAME_1 = "unique_id";
    private static final String COLUMN_NAME_2 = "name";
    private static final String COLUMN_NAME_3 = "phone_no";
    private static final String COLUMN_NAME_5 = "address1";
    private static final String COLUMN_NAME_6 = "address2";
    private static final String COLUMN_NAME_7 = "state";
    private static final String COLUMN_NAME_8 = "country";
    private static final String COLUMN_NAME_9 = "city";
    private static final String COLUMN_NAME_10 = "tag";


    public sqliteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ContentValues val= new ContentValues();
        val.put(COLUMN_NAME_NUMBER, "8800359250");
        onInsert(val);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + "( " + COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_NUMBER + " TEXT UNIQUE NOT NULL);";
        Log.d(TAGlog, query);
        db.execSQL(query);

        String query2 = "CREATE TABLE "+ TABLE_NAME_2 + " ( " + COLUMN_NAME_1 + " TEXT PRIMARY KEY, "+
                COLUMN_NAME_2 + " TEXT, "+
                COLUMN_NAME_3 + " TEXT UNIQUE, "+
                COLUMN_NAME_5 + " TEXT, "+
                COLUMN_NAME_6 + " TEXT, "+
                COLUMN_NAME_9 + " TEXT, "+
                COLUMN_NAME_7 + " TEXT, "+
                COLUMN_NAME_10 + " TEXT, "+
                COLUMN_NAME_8 + " TEXT); ";

        Log.d(TAGlog, query2);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        onCreate(db);
    }
    public Cursor onQuery(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select "+COLUMN_NAME_NUMBER+" from "+TABLE_NAME+";";
        return db.rawQuery(sql,null);
    }
    public long onInsert(ContentValues values){
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME, null, values);
    }

    public Cursor onQueryUser(Uri uri){
        String phone = uri.getQueryParameter("phone");
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select * from "+TABLE_NAME_2+" where "+COLUMN_NAME_3+" = " +phone+";";
        Cursor data = db.rawQuery(sql,null);
        data.moveToFirst();
        return data;
    }
    public long onInsertUser(ContentValues values){
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME_2, null, values);
    }
    public int onDelete(Uri uri){
        String phone[]={uri.getQueryParameter("phone")};
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_NAME_NUMBER + "=?", phone);
        return db.delete(TABLE_NAME_2, COLUMN_NAME_3+"=?", phone);
    }
    public int onUpdate(ContentValues values, String whereClause, String[] whereArgs){
        SQLiteDatabase db = getWritableDatabase();
        return  db.update(TABLE_NAME_2, values, whereClause, whereArgs);
    }
}
