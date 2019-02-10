package com.chriz.lightingcontrol.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chriz.lightingcontrol.lightBulb.LightBulb;

public class LightBulbOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = LightBulbOpenHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;

    private static final String LIGHT_BULBS_TABLE_CREATE =
            "CREATE TABLE " + Contract.LightBulbs.LIGHT_BULBS_TABLE + " (" +
                    Contract.LightBulbs.KEY_ID + " INTEGER PRIMARY KEY, " +
                    Contract.LightBulbs.KEY_IP + " TEXT, " +
                    Contract.LightBulbs.KEY_NAME + " TEXT );";

    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public LightBulbOpenHelper(Context context) {
        super(context, Contract.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LIGHT_BULBS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(LightBulbOpenHelper.class.getName(),
                "Upgrading database from version " + i + " to "
                        + i1 + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.LightBulbs.LIGHT_BULBS_TABLE);
        onCreate(sqLiteDatabase);
    }

    public Cursor query(int position) {
        String query;
        if (position != Contract.ALL_ITEMS) {
            position++; // Because database starts counting at 1.
            query = "SELECT " + Contract.LightBulbs.KEY_ID + "," + Contract.LightBulbs.KEY_IP + "," + Contract.LightBulbs.KEY_NAME +  " FROM "
                    + Contract.LightBulbs.LIGHT_BULBS_TABLE
                    +" WHERE " + Contract.LightBulbs.KEY_ID + "=" + position + ";";
        } else {
            query = "SELECT  * FROM " + Contract.LightBulbs.LIGHT_BULBS_TABLE
                    + " ORDER BY " + Contract.LightBulbs.KEY_IP + " ASC ";
        }

        Cursor cursor = null;
        try {
            checkReadableDB();
            cursor = mReadableDB.rawQuery(query, null);
        } catch (Exception e) {
            Log.d(TAG, "QUERY EXCEPTION! " + e);
        }
        return cursor;
    }

    public long insert(ContentValues values) {
        long newId = 0;

        try {
            checkWritableDB();
            newId = mWritableDB.insert(Contract.LightBulbs.LIGHT_BULBS_TABLE, null, values);
        } catch (Exception e) {

        }
        return newId;
    }

    public Cursor count(){
        MatrixCursor cursor = new MatrixCursor(new String[] {Contract.CONTENT_PATH});
        try {
            checkReadableDB();
            int count =  (int) DatabaseUtils.queryNumEntries(mReadableDB, Contract.LightBulbs.LIGHT_BULBS_TABLE);
            cursor.addRow(new Object[]{count});
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION " + e);
        }
        return cursor;
    }

    public int delete(int id) {
        int deleted = 0;
        try {
            checkWritableDB();
            deleted = mWritableDB.delete(Contract.LightBulbs.LIGHT_BULBS_TABLE, Contract.LightBulbs.KEY_ID +  " = ? ",
                                                        new String[] {String.valueOf(id)});
        } catch (Exception e) {

        }
        return deleted;
    }

    public int update(int id, LightBulb lightBulb) {
        int mNumberOfRowsUpdated = -1;
        try {
            checkWritableDB();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.LightBulbs.KEY_IP, lightBulb.getIpAddress());
            contentValues.put(Contract.LightBulbs.KEY_NAME, lightBulb.getName());
            mNumberOfRowsUpdated = mWritableDB.update(Contract.LightBulbs.LIGHT_BULBS_TABLE, contentValues,
                                                    Contract.LightBulbs.KEY_ID + " = ?",
                                                    new String[] {String.valueOf(id)});
        } catch (Exception e) {

        }
        return mNumberOfRowsUpdated;
    }

    private void checkWritableDB() {
        if (mWritableDB == null) {
            mWritableDB = getWritableDatabase();
        }
    }

    private void checkReadableDB() {
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
    }

}
