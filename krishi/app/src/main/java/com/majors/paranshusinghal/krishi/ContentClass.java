package com.majors.paranshusinghal.krishi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class ContentClass extends ContentProvider{

    private static final String TAGlog = "myTAG";
    sqliteDB db;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        db = new sqliteDB(context);
        return true;
    }
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id=0;
        String path = uri.getPath();
        Log.d(TAGlog,"insertPath: "+path);
        switch (path){
            case "/numbers":
                id= db.onInsert(values);
                return ContentUris.withAppendedId(uri, id);
            case "/users":
                id= db.onInsertUser(values);
                return ContentUris.withAppendedId(uri, id);
            default:
                return null;
        }
    }
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String path = uri.getPath();
        Log.d(TAGlog,"queryPath: "+path);
        switch (path)
        {
            case "/numbers":
                return db.onQuery();
            case "/users":
                return db.onQueryUser(uri);
            default:
                return null;
        }
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
