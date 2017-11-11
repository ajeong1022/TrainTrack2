package com.example.android.traintrack;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.traintrack.data.RoutineContract.RoutineEntry;
import com.example.android.traintrack.data.RoutineDbHelper;
import com.example.android.traintrack.data.WorkoutDbHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RoutineCreatorActivity extends AppCompatActivity {

    /**
     * We declare some member variables that will be used throughout the class.
     */

    private RoutineDbHelper mDbHelper;
    private WorkoutDbHelper wDbHelper;

    private EditText mRoutineLengthEditText;
    private EditText mRoutineTitleView;
    private LinearLayout mRootView;
    private ImageButton mLengthUpButton;
    private ImageButton mLengthDownButton;

    //This list will contain all the exercises (regardless of subroutine) for this routine.
    public static ArrayList<Exercise> ExerciseList = new ArrayList<>();

    public static String routineTitle;
    public static String mCycleLength;
    private String editableRoutineTitle;
    //private String LOG_TAG = RoutineCreatorActivity.class.getSimpleName();

    public static AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_creator);

        //We initialize some of the member variables such as the database openhelper and layout views.
        mDbHelper = new RoutineDbHelper(this);

        //This string will be used to determine whether the activity is opened in 'edit' mode or not.
        editableRoutineTitle = getIntent().getStringExtra("routine_title");


        mRoutineLengthEditText = (EditText) findViewById(R.id.et_routine_length);
        //Since the routine length is never less than or equal to zero, we initialize the length to 1.
        //this way, the EditText is never empty.

        mRoutineTitleView = (EditText) findViewById(R.id.et_routine_title);
        //This is a LinearLayout in which all the subroutine views will be listed.
        //TODO: You need to perform database queries on a Background Thread using an AsyncTaskLoader.
        mRootView = (LinearLayout) findViewById(R.id.ll_subroutine_list_root);

        //This string must be defined because the (@Link RoutineDBHelper) needs an initial table name
        //to use during onCreate().

        routineTitle = mRoutineTitleView.getText().toString();

        //We warn the user about changing the cycle length mid-edit, which would result in the clearing of
        //ExerciseList and hence any temporarily saved data about the routine.
        //TODO: At a later stage during refactoring, think about a way to preserve already created subroutines when the user increments cycle length.
        //If that's too difficult, then at least warn the user when they actually try to increment or decrement
        //rather than showing them the same AlertDialog every time they open this activity.

        showCycleLengthChangeWarningDialog();




        /*
         * We add a TextChangedListener on the EditText in order to determine how many
         * subroutine layouts we need to inflate.
         */
        mRoutineLengthEditText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                //This cycle length must initially be stored in a string object because
                //a null integer object will throw an exception.

                mCycleLength = mRoutineLengthEditText.getText().toString();

                //We hence declare an integer variable that will eventually represent the numerical value
                //of the cycle length.
                int mLength;

                if (mCycleLength.equals("")) {
                    mLength = 0;
                } else {
                    mLength = Integer.parseInt(mCycleLength);
                }


                //We clear the root view to adjust the number of subroutine layouts that are inflated.
                mRootView.removeAllViews();

                for (int i = 0; i < mLength; i++) {

                    //We now loop for as many subroutines as necessary and inflate the subroutine list item layouts.

                    getLayoutInflater().inflate(R.layout.routine_creator_list_item, mRootView, true);

                    //This view object represents the i'th subroutine layout.
                    View view = mRootView.getChildAt(i);

                    //We then find the TextView that represents the Day number and set the text appropriately.
                    TextView dayView = (TextView) view.findViewById(R.id.tv_day_view);
                    dayView.setText("Day " + (i + 1));

                    //We find some other views in the subroutine list item layout for use later.
                    final EditText subRoutineTitleView =
                            (EditText) view.findViewById(R.id.et_subroutine_title);

                    //We also initialize an ArrayList that holds exercise objects for each subroutine.
                    final ArrayList<Exercise> subroutineExerciseList = new ArrayList<>();


                    //The following code adds the appropriate functionality to the add exercise button.
                    Button addExerciseButton = (Button) view.findViewById(R.id.bt_edit_subroutine);
                    addExerciseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Data validation.

                            if (isEmpty(subRoutineTitleView)) {
                                Toast.makeText(RoutineCreatorActivity.this,
                                        "You must first enter the title of the subroutine.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //If data validation has passed, then we proceed with creating a dialog to add an exercise.
                            //To do this, we first define the custom layout for the AlertDialog object, and also
                            //get the subroutine title to use as the dialog title.

                            final LinearLayout DialogLayout = (LinearLayout) getLayoutInflater()
                                    .inflate(R.layout.routine_creator_add_exercise_dialog, null);

                            final String subroutineTitle = subRoutineTitleView.getText().toString();

                            //We first create an AlertDialog object.--
                            dialog = createAddExerciseDialog(subroutineTitle, DialogLayout);

                            final RecyclerView exerciseListView =
                                    (RecyclerView) DialogLayout
                                            .findViewById(R.id.rv_exercise_list_view);

                            exerciseListView
                                    .setLayoutManager(new LinearLayoutManager
                                            (RoutineCreatorActivity.this));

                            //We then initialize a custom ArrayAdapter that populates the previously defined ListView,
                            //given a list of exercise objects.
                            //We define the adapter inside the dialog because otherwise the data set of the next iteration
                            //of the big for loop will override the data set of previous adapters.
                            //TODO: By defining subroutineExerciseList as a public static variable, you can't isolate each subroutine exercise list in the dialog
                            //TODO: I would suggest you bring the declaration back inside the appropriate scope, but that's going to mess up other functions..

                            final RoutineRecyclerAdapter mAdapter =
                                    new RoutineRecyclerAdapter(subroutineExerciseList, false, false);

                            exerciseListView.setAdapter(mAdapter);


                            //This code allows us to manually control the dialog's behaviour when the positive button is clicked.
                            //All of the code inside the onClickListener object is executed when the positive button is clicked.
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(final DialogInterface dialog) {
                                    Button neutralButton =
                                            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);

                                    //We only set a new OnClickListener on the neutral button if the
                                    //dialog is in create mode and not edit mode.
                                    neutralButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Exercise currentExercise =
                                                    createExerciseInDialog(subroutineTitle, dialog);
                                            if (currentExercise == null) {
                                                Toast.makeText(RoutineCreatorActivity.this,
                                                        "One or more fields are empty",
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            subroutineExerciseList.add(currentExercise);

                                            mAdapter.notifyDataSetChanged();

                                            //We now use the helper method to update the list attached to the arrayadapter
                                            //and then notify a change in the data set to inflate more subroutine exercise list items.

                                            /*
                                            Toast.makeText(getApplicationContext(),
                                                    "Exercise was successfull added.",
                                                    Toast.LENGTH_SHORT).show();

                                            */

                                            clearDialog(dialog);

                                        }
                                    });


                                    //Positive button function stays the same regardless of the mode of the dialog.
                                    Button positiveButton =
                                            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                    positiveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //We first perform data validation on the subroutinenExerciseList.
                                            if (subroutineExerciseList.size() == 0) {
                                                Toast.makeText(RoutineCreatorActivity.this,
                                                        "You must add at least one exercise to this subroutine.",
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            //We first remove all the exercise objects in ExerciseList that contain
                                            //the same category as the subroutine. This ensures that when the user
                                            //clicks on save for the same subroutine multiple times, only the final
                                            //set of exercises will be saved in the database.

                                            if (ExerciseList.size() != 0) {
                                                for (Iterator<Exercise> exerciseIterator = ExerciseList.iterator(); exerciseIterator.hasNext(); ) {
                                                    Exercise e = exerciseIterator.next();
                                                    if (e.getCategory().equals(subroutineTitle)) {
                                                        exerciseIterator.remove();
                                                    }
                                                }
                                            }

                                            //Then, we loop through each exercise object in the
                                            //subroutineExerciseList and add it to the ExerciseList.

                                            for (Exercise exercise : subroutineExerciseList) {
                                                ExerciseList.add(exercise);
                                            }

                                            /*
                                            Toast.makeText(RoutineCreatorActivity.this,
                                                    "Subroutine was successfully saved.",
                                                    Toast.LENGTH_SHORT).show();

                                            */

                                            dialog.dismiss();
                                        }
                                    });

                                }
                            });

                            dialog.show();


                            //When the edit subroutine button is clicked, if the activity is opened in edit mode,
                            //then the exercises saved in the database under that category are queried and added to the
                            //adapter that populates the dialog listview.

                            //This conditional fixes the edit mode bug where clicking on edit button keeps adding listview children.
                            if (editableRoutineTitle != null) {

                                exerciseListView.setAdapter(mAdapter);

                                if (subroutineExerciseList.size() == 0) {
                                    Cursor exerciseCursor = mDbHelper.getEditableRoutineExercises(editableRoutineTitle, subroutineTitle);

                                    ArrayList<Exercise> editableExerciseList = getEditableRoutineExerciseList(subroutineTitle, exerciseCursor);

                                    for (Exercise e : editableExerciseList) {
                                        subroutineExerciseList.add(e);
                                    }
                                }

                            }
                        }
                    });
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //We initialize the value of the Length EditText because the routine is never going to be shorter than 1 day.
        mRoutineLengthEditText.setText(String.valueOf(1));

        //We define the up and down buttons and hook up their functions using a helper method.
        mLengthUpButton = (ImageButton) findViewById(R.id.bt_cycle_length_up);
        mLengthDownButton = (ImageButton) findViewById(R.id.bt_cycle_length_down);

        setCycleLengthButtons(mLengthUpButton, mLengthDownButton);


        //We must check the intent that created this activity to check whether the creator is in
        //'edit' mode or 'create' mode.

        if (editableRoutineTitle != null) {
            Cursor c = mDbHelper.getEditableRoutineCategories(editableRoutineTitle);
            mRoutineTitleView.setText(editableRoutineTitle);

            //We first obtain a List of categories (subroutine titles) to populate the subroutines.
            List<String> categoryList = getCategoryList(c);
            mRoutineLengthEditText.setText(String.valueOf(categoryList.size()));

            //This loop iterates through the category list and populates the subroutine titles.

            for (int i = 0; i < mRootView.getChildCount(); i++) {
                View view = mRootView.getChildAt(i);
                String currentSubroutineTitle = categoryList.get(i);

                //Here we set each subroutine title TextView to the appropriate title.
                ((TextView) view.findViewById(R.id.et_subroutine_title)).setText(currentSubroutineTitle);

            }

        }


    }

    /**
     * We override this method in order to let the user know that unsaved changes will be lost.
     */
    @Override
    public void onBackPressed() {
        createDiscardDialog();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.routine_creator_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Before proceeding to saving the routine inside the database, we perform another data validation.
            case R.id.action_save:
                if (isEmpty(mRoutineTitleView) || isEmpty(mRoutineLengthEditText) || isSubroutineTitlesEmpty()) {
                    Toast.makeText(RoutineCreatorActivity.this,
                            "You have not yet entered all the details about the routine.",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                //We define this String object right before we call on the mDbHelper to ensure that
                //the routine title is filled if not accidentally left blank.

                routineTitle = mRoutineTitleView.getText().toString();

                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                //If the editor is opened in edit mode, then we don't proceed with the usual protocol of
                //adding a new routine, but follow a different path to update an existing one.

                if (editableRoutineTitle != null) {
                    mDbHelper.updateRoutine(routineTitle, ExerciseList);
                    Toast.makeText(this, "Routine was successfully updated", Toast.LENGTH_SHORT).show();
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }


                /*
                We perform data validation to first check whether there is a table with the equivalent
                title in the database.
                 */

                boolean tableExists = false;

                try {
                    tableExists = mDbHelper.isTableExists(routineTitle, db);
                } catch (SQLiteException e) {
                    if (e.getMessage().contains("no such table")) {
                        tableExists = false;
                    }
                }

                if (tableExists) {

                    Toast.makeText(this, "There is already a routine with the same title.",
                            Toast.LENGTH_SHORT).show();
                    return true;

                }

                /*
             If the database is created for the first time, the call on getWritableDatabase()
            will already create a table with the given table name. However, if the database
            is already there, then this code will create a table if it does not exist already.
              */
                String createSQLStatement = RoutineEntry.createSQLCreateTableStatement(routineTitle);
                db.execSQL(createSQLStatement);

            /*
            This code loops through all the exercise objects in the ExerciseList and creates an
            SQL statement that adds all the exercises into the table that corresponds to the
            current routine.
             */
                String insertSQLStatement = RoutineEntry.createSQLInsertStatement(routineTitle, ExerciseList);
                db.execSQL(insertSQLStatement);
                Toast.makeText(this, "Program was successfully created.", Toast.LENGTH_SHORT).show();


                wDbHelper = new WorkoutDbHelper(this, routineTitle);
                //You have now created a database by the name of the routine and a default table.
                //Fill that table's first row with the initial data provided by the user.
                //You must first create a content values object.

                SQLiteDatabase wdb = wDbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                for(Exercise e : ExerciseList){
                    String tuple = "(" + e.getWeight()
                            + ", " + e.getSet() + ", " + e.getRep() + ")";
                    values.put(e.getTitle(), tuple);
                }

                wdb.insertOrThrow("data", null, values);


                //If saving is successful, then we exit to the home activity.
                NavUtils.navigateUpFromSameTask(this);

                return true;



            case android.R.id.home:
                createDiscardDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * This method checks whether an EditText is empty.
     *
     * @param e The EditText that is to be checked
     * @return true if the EditText is empty, false otherwise.
     */
    public static boolean isEmpty(EditText e) {
        String s = e.getText().toString().trim();

        return s.isEmpty() || s.equals("") || s.length() == 0;

    }

    /**
     * This method checks whether any one of the subroutine titles are empty.
     * The method also checks whether any one subroutine has a title but has no
     * exercise associated with it.
     * If so, the routine should not be saved as the user input is incomplete.
     *
     * @return If any of the input data is incomplete, then the method returns true.
     */
    private boolean isSubroutineTitlesEmpty() {

        //We loop through each existing child of (@Link mRootView) and check whether that child's
        //subroutine title is empty.
        for (int i = 0; i < mRootView.getChildCount(); i++) {

            View currentChild = mRootView.getChildAt(i);
            EditText mSubroutineTitleView =
                    (EditText) currentChild.findViewById(R.id.et_subroutine_title);

            if (isEmpty(mSubroutineTitleView)) {
                return true;
            }

        }
        return false;

    }

    /**
     * This helper method was created to make the code in OnCreate() more readable.
     * It essentially creates an AlertDialog object whose positive button will be modified.
     *
     * @param subroutineTitle the title of the subroutine, which is also the title of the dialog.
     * @param DialogLayout    the custom layout of the dialog.
     * @return the AlertDialog object that will be modified.
     */

    private AlertDialog createAddExerciseDialog(String subroutineTitle, LinearLayout DialogLayout) {
        //We now instantiate an AlertDialog.Builder object to create and show the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(RoutineCreatorActivity.this);
        return builder.setTitle(subroutineTitle)
                .setView(DialogLayout)
                //We set the onClickListener parameter for the positive button to null
                //because we need customized behaviour for data validation.
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Add", null)
                .create();

    }


    /**
     * This helper method gathers the data input in the dialog, performs a data validation,
     * and creates a new exercise object based on the gathered data.
     *
     * @param subroutineTitle the title of the subroutine, used during creation of new Exercise.
     * @param dialog          dialog from which data is gathered.
     * @return a new Exercise object.
     */
    public static Exercise createExerciseInDialog(String subroutineTitle, DialogInterface dialog) {

        //Get all the views needed to extract data about the current exercise.
        AlertDialog currentDialog = (AlertDialog) dialog;
        EditText titleView = (EditText) currentDialog.findViewById(R.id.et_exercise_title);
        EditText weightView = (EditText) currentDialog.findViewById(R.id.et_starting_weight);
        EditText setView = (EditText) currentDialog.findViewById(R.id.et_sets);
        EditText repView = (EditText) currentDialog.findViewById(R.id.et_reps);


        //Before proceeding with creating a new Exercise object, first validate the input data.
        //We return null if any of the views are empty, and then create a toast in the
        //OnClick method that notifies the user that data input is incomplete.
        if (isEmpty(titleView) || isEmpty(weightView) || isEmpty(setView) || isEmpty(repView)) {
            return null;
        }

        //Since data validation has passed, we now get the data from the defined views.
        String title = titleView.getText().toString();
        float weight = Float.parseFloat(weightView.getText().toString());
        int set = Integer.parseInt(setView.getText().toString());
        int rep = Integer.parseInt(repView.getText().toString());


        //Create a new exercise object with the acquired data and add it to
        //ExerciseList and subroutineExerciseList.

        return new Exercise(subroutineTitle, title, weight, set, rep);

    }

    /**
     * This helper method checks creates a dialog that ensures the user's will to discard data.
     */
    private void createDiscardDialog() {
        //We check whether the input data for the routine title or the length are null, and if so
        //Show a dialog that double checks whether the user wants to discard data.
        routineTitle = mRoutineTitleView.getText().toString();
        String mCycleLength = mRoutineLengthEditText.getText().toString();

        if (!(routineTitle == null || routineTitle.equals(""))
                || !(mCycleLength == null || mCycleLength.equals(""))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Any unsaved data will be lost.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavUtils.navigateUpFromSameTask(RoutineCreatorActivity.this);
                        }
                    })
                    .show();

            return;


        }

        super.onBackPressed();

    }

    /**
     * Get the number of subroutines in routine when the activity is opened in edit mode.
     *
     * @param cursor the cursor that corresponds to the routine.
     * @return the number of subroutines.
     */
    private List<String> getCategoryList(Cursor cursor) {
        List<String> categoryList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String category = cursor.getString(cursor.getColumnIndex(RoutineEntry.COLUMN_CATEGORY));
            if (!categoryList.contains(category)) {
                categoryList.add(category);
            }
        }
        return categoryList;
    }


    private ArrayList<Exercise> getEditableRoutineExerciseList(String category, Cursor c) {
        ArrayList<Exercise> exerciseList = new ArrayList<>();
        while (c.moveToNext()) {
            String exerciseTitle = c.getString(c.getColumnIndex(RoutineEntry.COLUMN_EXERCISE));
            float weight = c.getFloat(c.getColumnIndex(RoutineEntry.COLUMN_WEIGHT));
            int set = c.getInt(c.getColumnIndex(RoutineEntry.COLUMN_SET));
            int rep = c.getInt(c.getColumnIndex(RoutineEntry.COLUMN_REP));

            Exercise currentExercise = new Exercise(category, exerciseTitle, weight, set, rep);

            exerciseList.add(currentExercise);
        }

        return exerciseList;

    }

    /**
     * This method clears the views in the subroutine editor dialog after an exercise is added.
     *
     * @param dialog the editor dialog.
     */
    public static void clearDialog(DialogInterface dialog) {
        AlertDialog alertDialog = (AlertDialog) dialog;
        EditText titleView = (EditText) alertDialog.findViewById(R.id.et_exercise_title);
        EditText weightView = (EditText) alertDialog.findViewById(R.id.et_starting_weight);
        EditText setView = (EditText) alertDialog.findViewById(R.id.et_sets);
        EditText repView = (EditText) alertDialog.findViewById(R.id.et_reps);

        titleView.setText("");
        weightView.setText("");
        setView.setText("");
        repView.setText("");
    }


    /**
     * This method hooks up the buttons used to increment and decrement the routine cycle length.
     *
     * @param upButton   the increment button
     * @param downButton the decrement button
     */
    private void setCycleLengthButtons(ImageButton upButton, ImageButton downButton) {


        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentLength = mRoutineLengthEditText.getText().toString();
                int cycleLength = Integer.parseInt(currentLength);
                mRoutineLengthEditText.setText(String.valueOf(cycleLength + 1));
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentLength = mRoutineLengthEditText.getText().toString();
                int cycleLength = Integer.parseInt(currentLength);
                if (cycleLength == 1) {
                    Toast.makeText(RoutineCreatorActivity.this, "Your routine must be at least one day long", Toast.LENGTH_SHORT).show();
                    return;
                }
                mRoutineLengthEditText.setText(String.valueOf(cycleLength - 1));
            }
        });
    }



    private void showCycleLengthChangeWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RoutineCreatorActivity.this);
        builder.setMessage("If you change the cycle length after editing subroutines, your subroutine data will be lost.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


}
