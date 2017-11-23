package com.example.andy.traintrack2;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andy.traintrack2.Data.ExerciseContract.ExerciseTable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static List<Integer> sIdList;

    private RecyclerView mExerciseRecyclerView;
    private TextView mEmptyView;
    private Menu mMenu;
    private FloatingActionButton fab;
    private ExerciseAdapter mAdapter;
    private CursorLoader mLoader;
    private Cursor exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Routine OverView");

        //We initialize the loader by calling onCreateLoader via the LoaderManager.
        mLoader = (CursorLoader) getSupportLoaderManager().initLoader(0, null, this);

        //We load the currently stored exercises in a background thread using a CursorLoader.
        exercises = mLoader.loadInBackground();

        //We use a static List keeping track of all Id's. When we delete the ith element in the
        //ListView, we get the ith ID in this list and use that value to operate on the database.
        sIdList = new ArrayList<>();
        while (exercises.moveToNext()) {
            sIdList.add(exercises.getInt(exercises.getColumnIndexOrThrow(ExerciseTable.COLUMN_ID)));
        }

        //We initialize the LayoutManager and the RecyclerView.Adapter.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new ExerciseAdapter(exercises);

        mExerciseRecyclerView = findViewById(R.id.rv_exercise_list);
        mExerciseRecyclerView.setLayoutManager(layoutManager);
        mExerciseRecyclerView.setAdapter(mAdapter);

        //We add item dividers in the RecyclerView.
        mExerciseRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        //We manually implement the EmptyView functionality.
        mEmptyView = findViewById(R.id.tv_empty_view);
        if (mAdapter.getItemCount() != 0) mEmptyView.setVisibility(View.GONE);

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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /*If there exists a savedInstanceState, then screen configuration has changed.
        This line removes the bug in which rows of exercises remain after 'delete all' is pressed
        and the screen configuration is changed.*/
        mAdapter.swapCursor(mLoader.loadInBackground());
    }

    //We implement the LoaderCallbacks methods.

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //The default URI of the loader currently points to the entire table.
        //We can use the setURI method if we want to query a specific exercise at a later stage.
        return new CursorLoader(this, ExerciseTable.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //The 'delete all' menu is only visible when there is a non-zero number of exercises
        //in the database.
        if (mAdapter.getItemCount() != 0) {
            getMenuInflater().inflate(R.menu.menu_routine_creator, menu);
            mMenu = menu;
        }
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

    /**
     * This method creates an AlertDialog to confirm deletion of the routine.
     * When deletion is confirmed, the adapter is refreshed by loading data again,
     * and the menu and empty view's visibility is adjusted.
     */
    private void confirmDeleteRoutine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this routine?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getContentResolver().delete(ExerciseTable.CONTENT_URI, null, null);
                mAdapter.swapCursor(mLoader.loadInBackground());
                mAdapter.notifyDataSetChanged();
                mMenu.clear();
                mEmptyView.setVisibility(View.VISIBLE);
                onLoaderReset(mLoader);
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

    /**
     * Simple class to define the recycler view dividers.
     */
    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = ContextCompat.getDrawable(MainActivity.this, R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft() + 50;
            int right = parent.getWidth() - parent.getPaddingRight() - 50;

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}
