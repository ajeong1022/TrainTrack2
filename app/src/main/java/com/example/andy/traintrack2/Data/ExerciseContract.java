package com.example.andy.traintrack2.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines some constants related to the database that will be accessed by other classes.
 */

public final class ExerciseContract {
    public static final String DATABASE_NAME = "routine.db";
    public static final int DATABASE_VERSION = 1;
    public static final String COMMA_SEP = ", ";

    public static final String CONTENT_AUTHORITY = "com.example.andy.traintrack2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //You can have multiple path constants if you have more than one table.
    public static final String PATH_EXERCISES = "exercises";
    public static final String PATH_EXERCISES_ID = PATH_EXERCISES + "/#";

    public static final class ExerciseTable implements BaseColumns{
        //For each table, you will have an entry class like this, and for each entry class,
        //you will have a concrete content URI constant.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXERCISES);
        public static final Uri CONTENT_URI_ID = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXERCISES_ID);

        public static final String COLUMN_ID = "_id";
        public static final String TABLE_NAME = "exercises";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SET = "sets";
        public static final String COLUMN_REP = "rep";

        public static final String CREATE_SQL_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        COLUMN_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                        COLUMN_TITLE + " TEXT NOT NULL" + COMMA_SEP +
                        COLUMN_SET + " INTEGER NOT NULL" + COMMA_SEP +
                        COLUMN_REP + " INTEGER NOT NULL);";

    }
}
