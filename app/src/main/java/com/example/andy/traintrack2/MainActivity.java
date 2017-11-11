package com.example.andy.traintrack2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andy.traintrack2.Data.ExerciseContract.ExerciseTable;
import com.example.andy.traintrack2.Data.DbOpenHelper;
import com.google.gson.Gson;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Member variables for the exercise list and exercise ListView.
    private ListView mExerciseListView;
    private FloatingActionButton fab;
    private DbOpenHelper mDbOpenHelper;
    private ExerciseAdapter mAdapter;
    public static List<Integer> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbOpenHelper = new DbOpenHelper(this);

        Cursor exercises = mDbOpenHelper.getReadableDatabase().query(
                ExerciseTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        //We use a static List keeping track of all Id's. When we delete the ith element in the
        //ListView, we get the ith ID in this list and use that value to operate on the database.
        idList = new ArrayList<>();
        while(exercises.moveToNext()){
            idList.add(exercises.getInt(exercises.getColumnIndexOrThrow(ExerciseTable.COLUMN_ID)));
        }

        mExerciseListView = findViewById(R.id.lv_exercise_list);
        mAdapter = new ExerciseAdapter(exercises);
        mExerciseListView.setAdapter(mAdapter);
        mExerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //If one wishes to modify a saved exercise, we launch the editor activity with a flag.
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor editableExercise = (Cursor) adapterView.getItemAtPosition(i);

                //We reconstruct the exercise object from the cursor and pass it along with the intent.
                String title = editableExercise.getString(editableExercise.getColumnIndexOrThrow(ExerciseTable.COLUMN_TITLE));
                int set = editableExercise.getInt(editableExercise.getColumnIndexOrThrow(ExerciseTable.COLUMN_SET));
                int rep = editableExercise.getInt(editableExercise.getColumnIndexOrThrow(ExerciseTable.COLUMN_REP));
                Exercise currentExercise = new Exercise(title, set, rep);

                //We use Google GSON to serialize the Exercise object to pass to EditorActivity.
                String json = new Gson().toJson(currentExercise);
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra("Edit Mode", true);
                intent.putExtra("Exercise", json);
                intent.putExtra("Index", i);
                startActivity(intent);
            }
        });

        TextView mEmptyView = findViewById(R.id.tv_empty_view);
        mExerciseListView.setEmptyView(mEmptyView);

        fab = findViewById(R.id.fab_add_exercise);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //We launch an intent to the EditorActivity.
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * A simple ArrayAdapter that returns a list of exercise titles.
     */
    private class ExerciseAdapter extends CursorAdapter {

        public ExerciseAdapter(Cursor c) {
            super(MainActivity.this, c, 0);
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return getLayoutInflater().inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(ExerciseTable.COLUMN_TITLE));
            ((TextView) view).setText(title);
        }
    }


    /**
     * We inflate the menu option to create a new routine.
     *
     * @param menu
     * @return true if the menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_routine_creator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                confirmDeleteRoutine();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteRoutine(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this routine?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                db.execSQL(ExerciseTable.DELETE_ENTRIES);
                mDbOpenHelper.onCreate(db);
                mAdapter.swapCursor(mDbOpenHelper.getReadableDatabase().query(
                        ExerciseTable.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));

                db.close();
                Toast.makeText(MainActivity.this, "Successfully deleted routine.", Toast.LENGTH_SHORT).show();

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
