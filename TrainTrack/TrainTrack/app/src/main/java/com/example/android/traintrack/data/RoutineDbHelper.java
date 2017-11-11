package com.example.android.traintrack.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

import com.example.android.traintrack.Exercise;
import com.example.android.traintrack.data.RoutineContract.RoutineEntry;

import com.example.android.traintrack.RoutineCreatorActivity;

import java.util.ArrayList;

public class RoutineDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "WorkoutData";
    public static final int DATABASE_VERSION = 1;

    public RoutineDbHelper(Context context){super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    private String LOG_TAG = RoutineDbHelper.class.getSimpleName();


    /**
     * This method is called when the database is first created.
     * For now, we will just test whether the app can create a table
     * by retrieving the routine title input by the user.
     * @param db The database that stores workout data.
     */
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
     * TODO: Figure out how you will deal with the initial table's title.
     */
    private String createEntries(){
        String SQL_ENTRIES = "CREATE TABLE Default_Exercise"
                + "("
                + RoutineEntry.COLUMN_CATEGORY + " TEXT NOT NULL, "
                + RoutineEntry.COLUMN_EXERCISE + " TEXT NOT NULL, "
                + RoutineEntry.COLUMN_WEIGHT + " INTEGER NOT NULL, "
                + RoutineEntry.COLUMN_SET + " INTEGER NOT NULL, "
                + RoutineEntry.COLUMN_REP + " INTEGER NOT NULL);";

        return SQL_ENTRIES;
    }

    public boolean isTableExists(String tablename, SQLiteDatabase db){
        String checkSQLStatement = "SELECT * FROM " + tablename + ";";
        db.rawQuery(checkSQLStatement, null);
        return true;

    }

    /**
     * This method queries the database for a list of the existing routines' titles.
     * @return a ArrayList of routine titles.
     */
    public ArrayList<String> getCurrentRoutines(){
        SQLiteDatabase db = RoutineDbHelper.this.getReadableDatabase();
        ArrayList<String> routineTitleArray = new ArrayList<>();
        String getRoutineStatement = "SELECT name FROM sqlite_master WHERE type = \"table\";";
        Cursor c = db.rawQuery(getRoutineStatement, null);


        if(c.moveToFirst()){
            while(c.moveToNext())
            routineTitleArray.add(c.getString(c.getColumnIndex("name")));

        }


        return routineTitleArray;
    }

    /**
     * This helper method obtains a cursor containing just the category column of a saved routine.
     * @param routineTitle the title of the routine
     * @return a cursor containing the categories.
     */
    public Cursor getEditableRoutineCategories (String routineTitle){
        SQLiteDatabase db = RoutineDbHelper.this.getReadableDatabase();
        if(db.isOpen()){
            String fetchRoutineCategoryStatement = "SELECT " + RoutineEntry.COLUMN_CATEGORY +  " FROM " + routineTitle + ";";
            Cursor cursor = db.rawQuery(fetchRoutineCategoryStatement, null);
            return cursor;
        } else {
            Log.e(LOG_TAG, "There was an error while opening the database");
            return null;
        }
    }


    /**
     * This method creates a cursor containing all the exercises within a subroutine of a routine.
     * @param routineTitle the routine's title.
     * @param subroutineTitle the subroutine's title.
     * @return a cursor containing all the exercises that belong to that routine.
     */
    public Cursor getEditableRoutineExercises (String routineTitle, String subroutineTitle){
        SQLiteDatabase db = RoutineDbHelper.this.getReadableDatabase();
        if(db.isOpen()){
            String fetchSubroutineExerciseStatement = "SELECT " +
                    RoutineEntry.COLUMN_EXERCISE + ", " +
                    RoutineEntry.COLUMN_WEIGHT + ", " +
                    RoutineEntry.COLUMN_SET + ", " +
                    RoutineEntry.COLUMN_REP + " FROM " + routineTitle + " WHERE " +
                    RoutineEntry.COLUMN_CATEGORY + " = \"" + subroutineTitle + "\";";

            Cursor cursor = db.rawQuery(fetchSubroutineExerciseStatement, null);
            return cursor;

        } else {
            Log.e(LOG_TAG, "There was an error while opening the database");
            return null;
        }
    }


    /**
     * This method deletes a routine from the databases by removing the table from the database.
     * @param routineTitle title of the routine to be deleted.
     */
    public void deleteRoutine(String routineTitle){

        SQLiteDatabase db = RoutineDbHelper.this.getWritableDatabase();

        String deleteSQLStatement = "DROP TABLE " + routineTitle + ";";

        db.execSQL(deleteSQLStatement);

    }


    /**
     * This method updates a routine by first deleting all the data in the table for that routine
     * and newly adding all the data in the updated ExerciseList.
     * @param routineTitle title of the updated routine.
     * @param updatedExerciseList updated list of exercises.
     */
    public void updateRoutine(String routineTitle, ArrayList<Exercise> updatedExerciseList){
        SQLiteDatabase db = RoutineDbHelper.this.getWritableDatabase();
        String deleteStatement = "DELETE FROM " + routineTitle + ";";

        db.execSQL(deleteStatement);

        String insertStatement = RoutineEntry.createSQLInsertStatement(routineTitle, updatedExerciseList);

        db.execSQL(insertStatement);


    }

}
