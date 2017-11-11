package com.example.andy.traintrack2.Data;

import android.provider.BaseColumns;



public class ExerciseContract {
    public static final String DATABASE_NAME = "routine.db";
    public static final int DATABASE_VERSION = 1;
    public static final String COMMA_SEP = ", ";

    public static class ExerciseTable implements BaseColumns{
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

        public static final String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    }
}
