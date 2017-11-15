package com.example.andy.traintrack2.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.andy.traintrack2.Data.ExerciseContract.ExerciseTable;


public class ExerciseProvider extends ContentProvider {
    private DbOpenHelper mDbHelper = new DbOpenHelper(getContext());

    private static final int EXERCISES = 1;
    private static final int EXERCISES_ID = 2;

    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //The matcher can now match the content URI's for accessing the table as a whole and individual rows.

    static {
        sMatcher.addURI(ExerciseContract.CONTENT_AUTHORITY, ExerciseContract.PATH_EXERCISES, EXERCISES);
        sMatcher.addURI(ExerciseContract.CONTENT_AUTHORITY, ExerciseContract.PATH_EXERCISES_ID, EXERCISES_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor result = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch(sMatcher.match(uri)){
            case EXERCISES:
                result = db.query(
                        ExerciseTable.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;

            case EXERCISES_ID:
                result = db.query(
                        ExerciseTable.TABLE_NAME,
                        null,
                        s,
                        strings1,
                        null,
                        null,
                        null,
                        null);
                break;
        }

        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String mime = null;
        switch(sMatcher.match(uri)){
            case EXERCISES:
                mime = "vnd.android.cursor.dir/exercises";
                break;
            case EXERCISES_ID:
                mime = "vnd.android.cursor.item/exercises";
                break;
        }
        return mime;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = -1;
        switch(sMatcher.match(uri)){
            case EXERCISES:
                rowId = db.insert(
                        ExerciseTable.TABLE_NAME,
                        null,
                        contentValues);
                db.close();
                break;
        }
        return ContentUris.withAppendedId(ExerciseTable.CONTENT_URI_ID, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rows = 0;
        switch(sMatcher.match(uri)){
            case EXERCISES:
                rows = db.delete(ExerciseTable.TABLE_NAME, null, null);
                break;

            case EXERCISES_ID:
                rows = db.delete(ExerciseTable.TABLE_NAME, s, strings);
                break;
        }

        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rows = 0;

        //In the current version of the app, we only ever update single rows. Later, we may add
        //another switch clause for updating the entire table.
        switch(sMatcher.match(uri)){
            case EXERCISES_ID:
                rows = db.update(ExerciseTable.TABLE_NAME,
                        contentValues,
                        s,
                        strings);
                break;
        }
        return rows;
    }
}
