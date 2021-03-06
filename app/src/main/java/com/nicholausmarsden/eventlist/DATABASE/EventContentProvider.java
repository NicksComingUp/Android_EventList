package com.nicholausmarsden.eventlist.DATABASE;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.nicholausmarsden.eventlist.DATABASE.FieldName.EventFieldName;

/**
 * Created by nicho on 12/27/2017.
 */

public class EventContentProvider extends ContentProvider {
    private static final String TAG = "EventContentProvider";
    public static final String AUTHORITY = "com.nicholausmarsden.eventlist.DATABASE.EventContentProvider";
    private static final String BASE_PATH = "base";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final int CODE_1 = 1;
    private static final int CODE_2 = 2;
    private DBHelper dbHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, CODE_1);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CODE_2);
    }






    @Override
    public boolean onCreate() {
        dbHelper = DBHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder){
    Log.d(TAG,"query For MyContentProvider");
        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        sqb.setTables(DBHelper.TABLE_NAME);
        int matchCode = uriMatcher.match(uri);
        switch (matchCode) {
            case CODE_1:
                Log.d(TAG,"QUERY CODE 1");
                break;
            case CODE_2:
                Log.d(TAG,"QUERY CODE 2");
                // select= ID -------------selectArgs= 20
                sqb.appendWhere(EventFieldName.COL_ID + "=" + uri.getLastPathSegment());  // option to look for something specific
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();  // make the database readable
        Cursor cursor = sqb.query(db, projection, selection, selectionArgs, null, null, sortOrder); // this is where i get the information from database
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG,"insert For MyContentProvider");
        int matchCode = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase(); // make database writable
        long id = 0;
        switch (matchCode) {
            case CODE_1:
                id = sqlDB.insert(DBHelper.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int matchCode = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // make the database writable
        int rowsAffected = 0;
        switch (matchCode) {
            case CODE_1:
                rowsAffected = db.delete(DBHelper.TABLE_NAME, selection, selectionArgs); // delete specific
                break;
            case CODE_2:
                String id = uri.getLastPathSegment();
                //   if(TextUtils.isEmpty(selection)) {
                rowsAffected = db.delete(DBHelper.TABLE_NAME, EventFieldName.COL_ID + "=" + id, null); // delete a event
                //  } else{
                //  rowsAffected = db.delete(DBHelper.TABLE_NAME, DatabaseFieldName.COL_ID+ "=" + id + "and"+ selection,selectionArgs);
                // }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
          int matchCode = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // make database writable
        int Rowupdated ;
        switch (matchCode) {
            case CODE_1:
                Rowupdated = db.update(DBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_2:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                    Rowupdated  = db.update(DBHelper.TABLE_NAME, values, EventFieldName.COL_ID+ "=" + id, null);
                else
                    Rowupdated  = db.update(DBHelper.TABLE_NAME, values, EventFieldName.COL_ID + "=" + id + " AND " + selection, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Rowupdated ;
    }
}
