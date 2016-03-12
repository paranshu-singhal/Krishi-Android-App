package com.majors.paranshusinghal.krishi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class ContentClass extends ContentProvider{

    private static final String TAGlog = "TAGlog";

    private static final int Numbers = 1;
    private static final int Users = 2;

    private static String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        matcher.addURI(PROVIDER_NAME, "Numbers", Numbers);
        matcher.addURI(PROVIDER_NAME, "Users", Users);
    }
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
        switch (matcher.match(uri)){
            case Numbers:
                id= db.onInsert(values);
            case Users:
                id= db.onInsertUser(values);
        }
        return ContentUris.withAppendedId(uri, id);
    }
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (matcher.match(uri))
        {
            case Numbers:
                return db.onQuery();
            case Users:
                return db.onQueryUser();
            default:
                return null;
        }
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
