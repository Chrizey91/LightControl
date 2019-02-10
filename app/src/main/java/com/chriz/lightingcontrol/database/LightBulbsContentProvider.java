package com.chriz.lightingcontrol.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chriz.lightingcontrol.lightBulb.LightBulb;

public class LightBulbsContentProvider extends ContentProvider {
    private static final int URI_ALL_ITEMS_CODE = 10;
    private static final int URI_ONE_ITEM_CODE = 20;
    private static final int URI_COUNT_CODE = 30;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private LightBulbOpenHelper mDB;

    private void initializeUriMatching() {
        sUriMatcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH, URI_ALL_ITEMS_CODE);
        sUriMatcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH + "/#", URI_ONE_ITEM_CODE);
        sUriMatcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH + "/" + Contract.COUNT, URI_COUNT_CODE );

    }

    @Override
    public boolean onCreate() {
        mDB = new LightBulbOpenHelper(getContext());
        initializeUriMatching();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor = null;

        switch (sUriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                cursor = mDB.query(Contract.ALL_ITEMS);
                break;

            case URI_ONE_ITEM_CODE:
                cursor = mDB.query(Integer.parseInt(uri.getLastPathSegment()));
                break;

            case URI_COUNT_CODE:
                cursor = mDB.count();
                break;

            case UriMatcher.NO_MATCH:
                Log.d("CONTENT_PROVIDER", "No match");
                break;

            default:
                Log.d("CONTENT_PROVIDER", "Invalid Uri");
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                return Contract.MULTIPLE_RECORDS_MIME_TYPE;
            case URI_ONE_ITEM_CODE:
                return Contract.SINGLE_RECORD_MIME_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = mDB.insert(values);
        return Uri.parse(Contract.CONTENT_URI + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return mDB.delete(Integer.parseInt(selectionArgs[0]));
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        LightBulb lightBulb = new LightBulb(values.getAsInteger(Contract.LightBulbs.KEY_ID),
                                            values.getAsString(Contract.LightBulbs.KEY_IP),
                                            values.getAsString(Contract.LightBulbs.KEY_NAME));
        return mDB.update(Integer.parseInt(selectionArgs[0]), lightBulb);
    }
}
