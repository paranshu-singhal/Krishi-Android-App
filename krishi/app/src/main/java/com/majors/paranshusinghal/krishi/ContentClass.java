package com.majors.paranshusinghal.krishi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class ContentClass extends ContentProvider{

    private static final String TAGlog = "myTAG";
    sqliteDB db;
    SellerDB sellerDB;
    DataDB dataDB;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        sellerDB = new SellerDB(context);
        dataDB = new DataDB(context);
        db = new sqliteDB(context);
        return true;
    }
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        String path = uri.getPath();
        Log.d(TAGlog, "insertPath: " + path);
        switch (path){
            case "/numbers":
                id= db.onInsert(values);
                return ContentUris.withAppendedId(uri, id);
            case "/users":
                id= db.onInsertUser(values);
                return ContentUris.withAppendedId(uri, id);
            case "/sellerList":
                id=sellerDB.onInsert("/sellerList", values);
                return ContentUris.withAppendedId(uri, id);
            case "/buyerList":
                id=sellerDB.onInsert("/buyerList", values);
                return ContentUris.withAppendedId(uri, id);
            default:
                return null;
        }
    }
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String path = uri.getPath();
        //Log.d(TAGlog,"queryPath: "+path);
        switch (path)
        {
            case "/numbers":
                return db.onQuery();
            case "/users":
                return db.onQueryUser(uri);
            case "/sellerList/BuyerQuery":
                return sellerDB.onBuyerQuery(selection, selectionArgs);
            case "/data":
                return dataDB.query(projection, selection, selectionArgs);
            default:
                return sellerDB.onQuery(uri);
        }
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String path = uri.getPath();
        switch (path)
        {
            case "/sellerList":
                return sellerDB.onDelete("/sellerList", selection,selectionArgs);
            case "/buyerList":
                return sellerDB.onDelete("/buyerList", selection,selectionArgs);
            case "/users":
                return db.onDelete(uri);
            default:
                return 0;
        }
    }
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String path = uri.getPath();
        //Log.d(TAGlog,"queryPath: "+path);
        switch (path)
        {
            case "/sellerList":
                return sellerDB.onUpdate("/sellerList", values, selection, selectionArgs);
            case "/buyerList":
                return sellerDB.onUpdate("/buyerList", values, selection, selectionArgs);
            case "/users":
                return db.onUpdate(values, selection, selectionArgs);
            default:
                return 0;
        }
    }
}
