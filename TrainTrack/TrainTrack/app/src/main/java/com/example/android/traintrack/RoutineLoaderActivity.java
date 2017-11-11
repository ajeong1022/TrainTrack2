package com.example.android.traintrack;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.traintrack.data.RoutineDbHelper;
import com.example.android.traintrack.data.RoutineContract.RoutineEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.example.android.traintrack.HomeActivity.loadedRoutine;
import static com.example.android.traintrack.HomeActivity.loadedRoutineCategories;

public class RoutineLoaderActivity extends AppCompatActivity {

    public static String loadableRoutineTitle;

    private RecyclerView loadableRoutineRecyclerView;
    public static RoutineDbHelper mDbHelper;

    public static SharedPreferences setting;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_loader);

        mDbHelper = new RoutineDbHelper(this);
        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        loadableRoutineRecyclerView = (RecyclerView) findViewById(R.id.rv_loadable_routine_list_view);

        //You need to get a list of the routine titles from the database to create the adapter.

        ArrayList<String> currentRoutineTitles = mDbHelper.getCurrentRoutines();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        loadableRoutineRecyclerView.setLayoutManager(layoutManager);
        loadableRoutineRecyclerView.setAdapter(new RoutineRecyclerAdapter(currentRoutineTitles, true, false));

        String json = setting.getString("loaded_routine", null);
        Type type = new TypeToken<ArrayList<ArrayList<Exercise>>>() {
        }.getType();
        ArrayList<ArrayList<Exercise>> previouslyLoadedRoutine = gson.fromJson(json, type);


    }

    @Override
    protected void onPause() {
        //We convert the ArrayList object into a json string to save in sharedprefs.
        String json = gson.toJson(loadedRoutine);
        editor.putString("loaded_routine", json);

        editor.putString("loaded_routine_title", loadableRoutineTitle);

        String json1 = gson.toJson(loadedRoutineCategories);
        editor.putString("loaded_routine_categories", json1);


        editor.commit();
        super.onPause();
    }

    /**
     * This helper method populates the loadedRoutine ArrayList with the exercises from each subroutine.
     */
    public static void getLoadableSubroutines() {

        Cursor loadableSubroutines = mDbHelper.getEditableRoutineCategories(loadableRoutineTitle);
        while (loadableSubroutines.moveToNext()) {
            String currentCategory = loadableSubroutines.getString(loadableSubroutines.getColumnIndex(RoutineEntry.COLUMN_CATEGORY));
            if (!loadedRoutineCategories.contains(currentCategory)) {
                loadedRoutineCategories.add(currentCategory);
            }
        }

        for (String subroutine : loadedRoutineCategories) {
            ArrayList<Exercise> currentSubroutineExerciseList = new ArrayList<>();
            Cursor loadableSubroutineExercises = mDbHelper.getEditableRoutineExercises(loadableRoutineTitle, subroutine);
            while (loadableSubroutineExercises.moveToNext()) {
                String currentTitle = loadableSubroutineExercises.getString(loadableSubroutineExercises.getColumnIndex(RoutineEntry.COLUMN_EXERCISE));
                float currentWeight = loadableSubroutineExercises.getFloat(loadableSubroutineExercises.getColumnIndex(RoutineEntry.COLUMN_WEIGHT));
                int currentSet = loadableSubroutineExercises.getInt(loadableSubroutineExercises.getColumnIndex(RoutineEntry.COLUMN_SET));
                int currentRep = loadableSubroutineExercises.getInt(loadableSubroutineExercises.getColumnIndex(RoutineEntry.COLUMN_REP));

                currentSubroutineExerciseList.add(new Exercise(subroutine, currentTitle, currentWeight, currentSet, currentRep));

            }

            loadedRoutine.add(currentSubroutineExerciseList);
        }

    }

}
