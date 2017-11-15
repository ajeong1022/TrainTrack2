package com.example.andy.traintrack2.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(Context context){
        super(context,ExerciseContract.DATABASE_NAME,null, ExerciseContract.DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ExerciseContract.ExerciseTable.CREATE_SQL_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.delete(ExerciseContract.ExerciseTable.TABLE_NAME, null, null);
        sqLiteDatabase.execSQL(ExerciseContract.ExerciseTable.CREATE_SQL_ENTRIES);
    }
}
