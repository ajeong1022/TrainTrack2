package com.example.android.traintrack;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.traintrack.data.RoutineDbHelper;

import java.util.ArrayList;

public class RoutineEditorActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private TextView mEmptyView;
    private RecyclerView mRoutineListView;

    private RoutineDbHelper mDbHelper;

    private ArrayList<String> routineTitleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_editor);

        mDbHelper = new RoutineDbHelper(this);

        //We find the FAB and connect it to the RoutineCreatorActivity.
        fab = (FloatingActionButton) findViewById(R.id.fab_add_routine);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoutineEditorActivity.this, RoutineCreatorActivity.class);
                startActivity(intent);

            }
        });

        //To get the currently saved routines, we call on a helper method in the DbHelper class.
        routineTitleList = mDbHelper.getCurrentRoutines();


        mRoutineListView = (RecyclerView) findViewById(R.id.rv_routine_list_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRoutineListView.setLayoutManager(layoutManager);

        mRoutineListView.setAdapter(new RoutineRecyclerAdapter(routineTitleList, false, false));


        /*
        mEmptyView = (TextView) findViewById(R.id.tv_editor_empty_view);
        mEmptyView.setText("There are no routines to show.\n Start by adding a routine.");
        mEmptyView.setVisibility(View.GONE);

        */






    }
}
