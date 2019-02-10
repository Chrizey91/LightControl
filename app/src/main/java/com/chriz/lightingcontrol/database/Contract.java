package com.chriz.lightingcontrol.database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
    public static final String DATABASE_NAME = "lightbulbs";

    public static final int ALL_ITEMS = -2;
    public static final String COUNT = "count";

    public static final String AUTHORITY =
            "com.android.example.chriz.lightningcontrol.database.provider";

    public static final String CONTENT_PATH = "bulbs";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
    public static final Uri ROW_COUNT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH + "/" + COUNT);

    static final String SINGLE_RECORD_MIME_TYPE =
            "vnd.android.cursor.item/vnd.com.example.provider.bulbs";
    static final String MULTIPLE_RECORDS_MIME_TYPE =
            "vnd.android.cursor.dir/vnd.com.example.provider.bulbs";

    private Contract() {

    }

    public static abstract class LightBulbs implements BaseColumns {
        public static final String LIGHT_BULBS_TABLE = "light_bulbs";
        public static final String KEY_ID = "_id";
        public static final String KEY_IP = "ipaddress";
        public static final String KEY_NAME = "name";
    }
}
