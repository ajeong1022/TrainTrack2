package com.example.android.traintrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.traintrack.data.WorkoutDbHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;

import static com.example.android.traintrack.RoutineLoaderActivity.setting;

public class HomeActivity extends AppCompatActivity {

    Button mEditRoutineButton;
    Button mLoadRoutineButton;
    Button mStartWorkoutButton;
    Button mGenerateReportButton;

    Gson gson = new Gson();

    public static ArrayList<String> loadedRoutineCategories;
    public static ArrayList<ArrayList<Exercise>> loadedRoutine;
    public static String loadedRoutineTitle;
    public static int dayCount;
    SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        setting = getSharedPreferences("setting", 0);

        String json = setting.getString("loaded_routine", null);
        Type type = new TypeToken<ArrayList<ArrayList<Exercise>>>() {}.getType();
        ArrayList<ArrayList<Exercise>> previouslyLoadedRoutine = gson.fromJson(json, type);

        String json1 = setting.getString("loaded_routine_categories", null);
        Type type1 = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> previouslyLoadedRoutineCategories = gson.fromJson(json1, type1);

        String RoutineTitle = setting.getString("loaded_routine_title", null);

        if(previouslyLoadedRoutine != null){
            loadedRoutine = previouslyLoadedRoutine;

            loadedRoutineCategories = previouslyLoadedRoutineCategories;

            loadedRoutineTitle = RoutineTitle;
        } else {
            loadedRoutine = new ArrayList<>();
            loadedRoutineCategories = new ArrayList<>();
        }

        mEditRoutineButton = (Button) findViewById(R.id.bt_edit_routine);
        mEditRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RoutineEditorActivity.class);
                startActivity(intent);
            }
        });

        mLoadRoutineButton = (Button) findViewById(R.id.bt_load_routine);
        mLoadRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RoutineLoaderActivity.class);
                startActivity(intent);
            }
        });

        mStartWorkoutButton = (Button) findViewById(R.id.bt_start_workout);
        mStartWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WorkoutOverviewActivity.class);
                startActivity(intent);
            }
        });

        mGenerateReportButton = (Button) findViewById(R.id.bt_report);
        mGenerateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
    }
}
