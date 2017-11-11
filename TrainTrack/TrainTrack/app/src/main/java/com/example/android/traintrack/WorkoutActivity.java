package com.example.android.traintrack;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.traintrack.data.WorkoutDbHelper;

import java.util.ArrayList;

import static com.example.android.traintrack.HomeActivity.dayCount;
import static com.example.android.traintrack.HomeActivity.loadedRoutineCategories;
import static com.example.android.traintrack.HomeActivity.loadedRoutineTitle;
import static com.example.android.traintrack.WorkoutOverviewActivity.todaysWorkout;
import static com.example.android.traintrack.WorkoutOverviewActivity.todaysWorkoutData;

public class WorkoutActivity extends AppCompatActivity {
    private ArrayList<Exercise> currentWorkout;
    private Exercise currentExercise;
    private int currentExerciseCount;
    private int currentSetCount;
    private WorkoutDbHelper mDbHelper;

    private Button mNextButton;
    private EditText mWeightView;
    private EditText mRepView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        currentWorkout = todaysWorkout;
        currentExerciseCount = getIntent().getIntExtra("exercise_count", 0);
        currentSetCount = getIntent().getIntExtra("set_count", 0);
        //The default value is zero, but if the activity is summoned using the next button, then the value will be different.
        currentExercise = currentWorkout.get(currentExerciseCount);

        getSupportActionBar().setTitle(currentExercise.getTitle() + " Set " + currentSetCount);

        mNextButton = (Button) findViewById(R.id.bt_next_set);

        mWeightView = (EditText) findViewById(R.id.et_target_weight);

        mRepView = (EditText) findViewById(R.id.et_target_reps);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentSetCount == 2){
                    String todaysExerciseData = "(" + mWeightView.getText().toString() + ", " +
                            currentExercise.getSet() + ", " + mRepView.getText().toString() + ")";
                    todaysWorkoutData.add(todaysExerciseData);
                }

                if(currentExerciseCount == (currentWorkout.size() - 1) && currentSetCount == currentExercise.getSet()){
                    if(dayCount < loadedRoutineCategories.size() - 1){
                        dayCount++;
                    } else {
                        dayCount = 0;
                    }

                    Intent intent = new Intent(WorkoutActivity.this, HomeActivity.class);

                    mDbHelper = new WorkoutDbHelper(WorkoutActivity.this, loadedRoutineTitle);
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    for(int i = 0; i < todaysWorkoutData.size(); i++){
                        String exerciseTitle = todaysWorkout.get(i).getTitle();
                        String currentExerciseData = todaysWorkoutData.get(i);
                        values.put(exerciseTitle, currentExerciseData);
                    }

                    db.insertOrThrow("data", null, values);

                    Toast.makeText(WorkoutActivity.this, "Today's records were successfully saved.", Toast.LENGTH_SHORT).show();

                    startActivity(intent);

                    return;
                } else {

                    Intent intent = new Intent(WorkoutActivity.this, WorkoutActivity.class);
                    if (currentSetCount < currentExercise.getSet()) {
                        intent.putExtra("exercise_count", currentExerciseCount);
                        intent.putExtra("set_count", currentSetCount + 1);
                    } else {
                        intent.putExtra("exercise_count", currentExerciseCount + 1);
                        intent.putExtra("set_count", 1);
                    }

                    startActivity(intent);
                }
            }
        });


    }
}
