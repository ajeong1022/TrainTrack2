package com.example.android.traintrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.android.traintrack.data.WorkoutDbHelper;

import java.util.ArrayList;

import static com.example.android.traintrack.HomeActivity.loadedRoutine;
import static com.example.android.traintrack.HomeActivity.loadedRoutineCategories;
import static com.example.android.traintrack.HomeActivity.loadedRoutineTitle;

public class ReportActivity extends AppCompatActivity {

    private WorkoutDbHelper mDbhelper;
    private ListView reportListView;
    private Spinner categorySpinner;
    private Spinner exerciseSpinner;
    private ArrayList<String> exerciseList = new ArrayList<>();
    private ArrayAdapter<String> categorySpinnerAdapter;
    private ArrayAdapter<String> exerciseSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getSupportActionBar().setTitle("Report for " + loadedRoutineTitle);

        mDbhelper = new WorkoutDbHelper(this, loadedRoutineTitle);
        reportListView = (ListView) findViewById(R.id.lv_report_list);
        categorySpinner = (Spinner) findViewById(R.id.sp_subroutine_drop_list);
        exerciseSpinner = (Spinner) findViewById(R.id.sp_exercise_drop_list);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getExerciseList(loadedRoutine);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        categorySpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, loadedRoutineCategories);

        exerciseSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, exerciseList);

        categorySpinner.setAdapter(categorySpinnerAdapter);

        exerciseSpinner.setAdapter(exerciseSpinnerAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_show_report:
                Object selectedItem = exerciseSpinner.getSelectedItem();
                String selectedExercise = (String)selectedItem;
                ArrayList<String> workoutData =
                        mDbhelper.getWorkoutData(selectedExercise);
                ArrayAdapter reportListAdapter =
                        new ArrayAdapter(ReportActivity.this, android.R.layout.simple_list_item_1, workoutData);
                reportListView.setAdapter(reportListAdapter);
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void getExerciseList(ArrayList<ArrayList<Exercise>> routine){
        exerciseList.clear();
        int subroutinePosition = categorySpinner.getSelectedItemPosition();
        ArrayList<Exercise> selectedSubroutine = routine.get(subroutinePosition);
        for(Exercise e : selectedSubroutine){
            exerciseList.add(e.getTitle());
        }
        exerciseSpinnerAdapter.notifyDataSetChanged();

    }
}
