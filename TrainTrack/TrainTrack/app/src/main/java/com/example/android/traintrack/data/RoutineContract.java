package com.example.android.traintrack.data;

import android.provider.BaseColumns;

import com.example.android.traintrack.Exercise;

import java.util.List;


public class RoutineContract {
    /**
     * A null constructor for the contract class.
     */
    public RoutineContract(){}

    /**
     * Inner class to define constants related to data entries in the table.
     */
    public static final class RoutineEntry implements BaseColumns {
        //Constants for the column headings.
        public static final String COLUMN_CATEGORY = "Category";
        public static final String COLUMN_EXERCISE = "Exercise";
        public static final String COLUMN_WEIGHT = "Weight";
        public static final String COLUMN_SET = "Sets";
        public static final String COLUMN_REP = "Repetitions";

        /**
         * This method creates the SQL code that will insert all the exercises into the routine's table.
         * @param tablename name of the table
         * @param list the exercise list.
         * @return the SQL statement that performs the insertion.
         */
        public static String createSQLInsertStatement(String tablename, List<Exercise> list){
            String insertStatement = "INSERT INTO " + tablename + " VALUES ";
            for(int i = 0; i < (list.size() - 1); i++){
                Exercise exercise = list.get(i);
                insertStatement += "(\"" + exercise.getCategory() + "\", \"" + exercise.getTitle() + "\","
                        + exercise.getWeight() + ", " + exercise.getSet() + ", " + exercise.getRep() + "), ";
            }
            Exercise lastExercise = list.get(list.size() - 1);
            insertStatement += "(\"" + lastExercise.getCategory() + "\", \"" + lastExercise.getTitle() +
                    "\", " + lastExercise.getWeight() + ", " + lastExercise.getSet() + ", " + lastExercise.getRep()
                    + ");";

            return insertStatement;
        }

        /**
         * This method creates the SQL code that will create a table for a routine if it does not exist.
         * @param tablename name of the routine
         * @return the SQL statement.
         */
        public static String createSQLCreateTableStatement (String tablename){
            String createTableStatement = "CREATE TABLE IF NOT EXISTS " + tablename
                    + "("
                    + RoutineEntry.COLUMN_CATEGORY + " TEXT NOT NULL, "
                    + RoutineEntry.COLUMN_EXERCISE + " TEXT NOT NULL, "
                    + RoutineEntry.COLUMN_WEIGHT + " INTEGER NOT NULL, "
                    + RoutineEntry.COLUMN_SET + " INTEGER NOT NULL, "
                    + RoutineEntry.COLUMN_REP + " INTEGER NOT NULL);";

            return createTableStatement;
        }

        /**
         * This method creates the SQL Statement that attaches a database that keeps track of
         * workout data for a particular routine when it is first created.
         * @param routineTitle title of the routine
         * @return the SQL statement.
         */
        public static String createSQLRoutineDatabaseStatement(String routineTitle){
            String statement = "ATTACH DATABASE \"" + routineTitle + ".db\" AS \"" + routineTitle
                    + "\";";
            return statement;
        }

    }
}
