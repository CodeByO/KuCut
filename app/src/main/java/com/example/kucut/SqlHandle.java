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

        }
        public class FeedProfile implements BaseColumns{
            public static final String PROFILE_TABLE_NAME = "profile";
            public static final String PROFILE_COLUMN_NAME_NAME = "name";
            public static final String PROFILE_COLUMN_NAME_STUDENT_NUMBER = "student_number";
            public static final String PROFILE_COLUMN_NAME_DEPARTMENT = "department";
            public static final String PROFILE_COLUMN_NAME_ID = "id";
            public static final String PROFILE_COLUMN_NAME_PASSWORD = "password";
        }
        public static final String SQL_CREATE_SHORTCUT =
                "CREATE TABLE " + SqlHandle.FeedShortCut.SHORTCUT_TABLE_NAME + " (" +
                        SqlHandle.FeedShortCut._ID + " INTEGER PRIMARY KEY," +
                        SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_NAME + " TEXT," +
                        SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_LINK + " TEXT," +
                        SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_IMAGE + " TEXT)";

        public static final String SQL_DELETE_SHORTCUT =
                "DROP TABLE IF EXISTS " + SqlHandle.FeedShortCut.SHORTCUT_TABLE_NAME;
        public static final String SQL_CREATE_PROFILE =
                "CREATE TABLE " + SqlHandle.FeedProfile.PROFILE_TABLE_NAME + " (" +
                        SqlHandle.FeedProfile._ID + " INTEGER PRIMARY KEY," +
                        SqlHandle.FeedProfile.PROFILE_COLUMN_NAME_NAME + " TEXT," +
                        SqlHandle.FeedProfile.PROFILE_COLUMN_NAME_STUDENT_NUMBER + " TEXT," +
                        SqlHandle.FeedProfile.PROFILE_COLUMN_NAME_DEPARTMENT + " TEXT," +
                        SqlHandle.FeedProfile.PROFILE_COLUMN_NAME_ID + " TEXT," +
                        SqlHandle.FeedProfile.PROFILE_COLUMN_NAME_PASSWORD + " TEXT)";

        public static final String SQL_DELETE_PROFILE =
                "DROP TABLE IF EXISTS " + SqlHandle.FeedProfile.PROFILE_TABLE_NAME;



}






