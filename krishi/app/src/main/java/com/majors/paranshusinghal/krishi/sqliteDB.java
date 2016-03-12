package com.majors.paranshusinghal.krishi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
    private static final String COLUMN_NAME_4 = "password";
    private static final String COLUMN_NAME_5 = "address1";
    private static final String COLUMN_NAME_6 = "address2";
    private static final String COLUMN_NAME_7 = "state";
    private static final String COLUMN_NAME_8 = "country";



    public sqliteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ContentValues val= new ContentValues();
        val.put(COLUMN_NAME_NUMBER, "8800359250");
        onInsert(val);
        val.put(COLUMN_NAME_NUMBER, "9810162034");
        onInsert(val);
        val = new ContentValues();
        val.put(COLUMN_NAME_1,"56dc0285198414.83539582");
        val.put(COLUMN_NAME_2,"Labhansh");
        val.put(COLUMN_NAME_3,"9876543211");
        val.put(COLUMN_NAME_4,"labhansh");
        val.put(COLUMN_NAME_5,"23, DLF");
        val.put(COLUMN_NAME_6,"Gurgaon");
        val.put(COLUMN_NAME_7,"Delhi");
        val.put(COLUMN_NAME_8,"India");
        onInsertUser(val);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + "( " + COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_NUMBER + " TEXT UNIQUE NOT NULL);";
     //   Log.d(TAGlog, query);
        db.execSQL(query);

        query = "CREATE TABLE "+ TABLE_NAME_2 + " ( " + COLUMN_NAME_1 + "TEXT PRIMARY KEY, "+
                COLUMN_NAME_2 + " TEXT, "+
                COLUMN_NAME_3 + " TEXT UNIQUE, "+
                COLUMN_NAME_4 + " TEXT, "+
                COLUMN_NAME_5 + " TEXT, "+
                COLUMN_NAME_6 + " TEXT, "+
                COLUMN_NAME_7 + " TEXT, "+
                COLUMN_NAME_8 + " TEXT); ";
        //   Log.d(TAGlog, query);
        db.execSQL(query);
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
        long id=0;
        try {
            SQLiteDatabase db = getWritableDatabase();
            id = db.insert(TABLE_NAME, null, values);
            Log.d(TAGlog, String.format("inserted with id: %d ", id));
        }
        catch (Throwable t){Log.d(TAGlog,t.getMessage());}
        return  id;
    }

    public Cursor onQueryUser(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select * from "+TABLE_NAME_2+" ; ";
        return db.rawQuery(sql,null);
    }
    public long onInsertUser(ContentValues values){
        long id=0;
        try {
            SQLiteDatabase db = getWritableDatabase();
            id = db.insert(TABLE_NAME_2, null, values);
            Log.d(TAGlog, String.format("inserted user with id: %d ", id));
        }
        catch (Throwable t){Log.d(TAGlog,t.getMessage());}
        return  id;
    }

}
