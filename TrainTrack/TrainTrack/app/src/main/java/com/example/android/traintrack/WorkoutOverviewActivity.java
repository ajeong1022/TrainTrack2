package com.example.android.traintrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.android.traintrack.HomeActivity.dayCount;

public class WorkoutOverviewActivity extends AppCompatActivity {

    public static ArrayList<Exercise> todaysWorkout;
    public static ArrayList<String> todaysWorkoutData;
    private ArrayList<ArrayList<Exercise>> loadedRoutine;

    private RecyclerView overviewRecyclerView;
    private Button mStartWorkoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_overview);

        loadedRoutine = HomeActivity.loadedRoutine;

        todaysWorkoutData = new ArrayList<>();

        //If the loadedRoutine variable is null, then either there is an error in data persistence, or the user has not loaded
        //any routines.
        if (loadedRoutine != null) {
            todaysWorkout = loadedRoutine.get(dayCount);
        } else {
            throw new NullPointerException("No routine appears to be loaded");
        }

        overviewRecyclerView = (RecyclerView) findViewById(R.id.rv_workout_overview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        overviewRecyclerView.setLayoutManager(layoutManager);
        overviewRecyclerView.setAdapter(new RoutineRecyclerAdapter(todaysWorkout, false, true));


        mStartWorkoutButton = (Button) findViewById(R.id.bt_start_todays_workout);
        mStartWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutOverviewActivity.this, WorkoutActivity.class);
                intent.putExtra("exercise_count", 0);
                intent.putExtra("set_count", 1);
                startActivity(intent);
            }
        });


    }
}
