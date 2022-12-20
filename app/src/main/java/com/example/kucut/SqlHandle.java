package com.example.kucut;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class SqlHandle{
        public SqlHandle(){}
        public class FeedShortCut implements BaseColumns {
            public static final String SHORTCUT_TABLE_NAME = "shortcut";
            public static final String SHORTCUT_COLUMN_NAME_NAME = "name";
            public static final String SHORTCUT_COLUMN_NAME_LINK = "link";
            public static final String SHORTCUT_COLUMN_NAME_IMAGE="image";
            public static final String SHORTCUT_COLUMN_NAME_TYPE="type";
        }

        public static final String SQL_CREATE_SHORTCUT =
                "CREATE TABLE " + SqlHandle.FeedShortCut.SHORTCUT_TABLE_NAME + " (" +
                        SqlHandle.FeedShortCut._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_NAME + " TEXT," +
                        SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_LINK + " TEXT," +
                        SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_IMAGE + " TEXT," +
                        SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_TYPE + " TEXT)";

        public static final String SQL_DELETE_SHORTCUT =
                "DROP TABLE IF EXISTS " + SqlHandle.FeedShortCut.SHORTCUT_TABLE_NAME;




}






