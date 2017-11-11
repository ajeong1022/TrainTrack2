package com.example.andy.traintrack2.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andy on 17. 11. 4.
 */

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
        sqLiteDatabase.execSQL(ExerciseContract.ExerciseTable.DELETE_ENTRIES);
        sqLiteDatabase.execSQL(ExerciseContract.ExerciseTable.CREATE_SQL_ENTRIES);
    }
}
