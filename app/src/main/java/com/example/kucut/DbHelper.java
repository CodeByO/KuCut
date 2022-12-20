package com.example.kucut;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//DB 사용을 위한 소스

public class DbHelper extends SQLiteOpenHelper {
    SqlHandle sqlHandle = new SqlHandle();
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "shortcut.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlHandle.SQL_CREATE_SHORTCUT);
       // db.execSQL(SqlHandle.SQL_CREATE_PROFILE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SqlHandle.SQL_DELETE_SHORTCUT);
        //db.execSQL(SqlHandle.SQL_DELETE_PROFILE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
