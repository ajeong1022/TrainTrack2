package com.example.andy.traintrack2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andy.traintrack2.Data.ExerciseContract;
import com.example.andy.traintrack2.Data.ExerciseContract.ExerciseTable;

import com.example.andy.traintrack2.Data.DbOpenHelper;
import com.google.gson.Gson;

public class EditorActivity extends AppCompatActivity {

    private EditText mTitleView;
    private EditText mSetView;
    private EditText mRepView;
    private DbOpenHelper mDbHelper;
    private boolean mInEditMode;
    private boolean mIsEdited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mInEditMode = getIntent().getBooleanExtra("Edit Mode", false);
        mTitleView = findViewById(R.id.et_title_view);
        mSetView = findViewById(R.id.et_set_view);
        mRepView = findViewById(R.id.et_rep_view);

        mDbHelper = new DbOpenHelper(this);

        if(mInEditMode){
            //We retrive the exercise object to be updated.
            Exercise exercise = new Gson().fromJson(getIntent().getStringExtra("Exercise"), Exercise.class);
            //Then we populate the EditTexts as necessary.
            mTitleView.setText(exercise.getTitle());
            mSetView.setText(String.valueOf(exercise.getSet()));
            mRepView.setText(String.valueOf(exercise.getRep()));
        }

        mTitleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mIsEdited = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSetView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mIsEdited = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mRepView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mIsEdited = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mIsEdited = false;


    }


    @Override
    public void onBackPressed() {
        checkEdit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exercise_creator, menu);
        if(mInEditMode) {
            //Item at index 0 is the delete button.
            menu.getItem(0).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                checkEdit();
                return true;

            case R.id.action_create_exercise:

                String title = mTitleView.getText().toString();
                String sets = mSetView.getText().toString();
                String reps = mRepView.getText().toString();
                //Data sanitation
                if(title == null || title.equals("")
                        || sets.equals("") || sets == null
                        || reps.equals("") || reps == null) {
                    Toast.makeText(this, "One of the fields is empty.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                editExercise(title, sets, reps);
                break;

            case R.id.action_delete_exercise:
                confirmDelete();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method adds an exercise object to the sqlite database.
     * @param e Exercise to be added.
     */
    private void addExercise(Exercise e){

        ContentValues values = new ContentValues();
        values.put(ExerciseTable.COLUMN_TITLE, e.getTitle());
        values.put(ExerciseTable.COLUMN_SET, e.getSet());
        values.put(ExerciseTable.COLUMN_REP, e.getRep());

        getContentResolver().insert(ExerciseTable.CONTENT_URI, values);
    }

    /**
     * This method updates an exercise object already existing in the sqlite database.
     * @param e Updated exercise.
     */
    private void updateExercise(Exercise e){
        ContentValues values = new ContentValues();
        values.put(ExerciseTable.COLUMN_TITLE, e.getTitle());
        values.put(ExerciseTable.COLUMN_SET, e.getSet());
        values.put(ExerciseTable.COLUMN_REP, e.getRep());
        int index = MainActivity.sIdList.get(getIntent().getIntExtra("Index", -1));

        Uri uri = ContentUris.withAppendedId(ExerciseTable.CONTENT_URI, index);
        getContentResolver().update(uri,
                values,
                ExerciseTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(index)});
        }


    /**
     * This method performs the necessary updates to the database when an edit is requested by the user.
     * @param title Edited exercise title
     * @param sets Edited sets
     * @param reps Edited reps
     */
    private void editExercise(String title, String sets, String reps){
        //We perform data sanitation by checking whether the views are empty.


        int set = Integer.valueOf(sets);
        int rep = Integer.valueOf(reps);


        //We add further data sanitation here by checking the range of set and rep.
        if(set == 0 || rep == 0){
            Toast.makeText(this, "Your set and rep range must be positive.", Toast.LENGTH_SHORT).show();

        } else if(mInEditMode){
            updateExercise(new Exercise(title, set, rep));
            Toast.makeText(this, "Exercise was successfully updated", Toast.LENGTH_SHORT)
                    .show();
            NavUtils.navigateUpFromSameTask(this);

        }

        else {
            addExercise(new Exercise(title, set, rep));
            Toast.makeText(this, "Exercise was successfully saved", Toast.LENGTH_SHORT)
                    .show();
            NavUtils.navigateUpFromSameTask(this);

        }

    }

    /**
     * This method checks whether a field in the EditActivity has been changed, and if so confirms
     * that the user wishes to leave the activity.
     */
    private void checkEdit(){
        if(mIsEdited){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your input will be discarded. Are you sure?");
            builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        } else{
            NavUtils.navigateUpFromSameTask(EditorActivity.this);
        }
    }

    private void confirmDelete(){
        //We build an AlertDialog that confirms user's desire to delete the exercise.
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setTitle("Deletion Confirmation");
        builder.setMessage("Are you sure you want to delete this exercise?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    int index = MainActivity.sIdList.get(getIntent().getIntExtra("Index", -1));
                    Uri uri = ContentUris.withAppendedId(ExerciseTable.CONTENT_URI, index);
                    getContentResolver().delete(uri,
                            ExerciseTable.COLUMN_ID + "=?",
                            new String[]{String.valueOf(index)});
                    Toast.makeText(EditorActivity.this, "Exercise was successfully deleted.", Toast.LENGTH_SHORT).show();
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);

                } catch(CursorIndexOutOfBoundsException e){
                    Toast.makeText(EditorActivity.this, "Your exercise index is invalid.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }



}