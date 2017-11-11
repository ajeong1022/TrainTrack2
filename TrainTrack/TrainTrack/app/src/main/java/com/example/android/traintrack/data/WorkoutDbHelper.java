package com.example.android.traintrack.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.traintrack.Exercise;

import java.util.ArrayList;

import static android.R.attr.data;
import static com.example.android.traintrack.RoutineCreatorActivity.ExerciseList;


public class WorkoutDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public WorkoutDbHelper(Context context, String DatabaseName){super(context, DatabaseName, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createEntries());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * This is a helper method that creates the SQL statement to be executed
     * during onCreate.
     * @return the SQL statement, concatenated with the title from RoutineCreatorActivity.
     */
    private String createEntries(){
        String SQL_ENTRIES = "CREATE TABLE data"
                + "(";
        for(int i = 0; i < ExerciseList.size() - 1; i++){
            String title = ExerciseList.get(i).getTitle();
            SQL_ENTRIES += title + " TEXT, ";
        }

        SQL_ENTRIES += ExerciseList.get(ExerciseList.size()-1).getTitle() + " TEXT);";

        return SQL_ENTRIES;
    }

    public ArrayList<String> getWorkoutData(String exercise){
        ArrayList<String> dataList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + exercise + " FROM data;", null);
        while(cursor.moveToNext()){
            String data = cursor.getString(cursor.getColumnIndex(exercise));
            if(data == null){
                continue;
            } else {
                dataList.add(data);

            }
        }
        cursor.close();
        return dataList;
    }

}
