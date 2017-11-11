package com.example.android.traintrack;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.traintrack.data.RoutineDbHelper;

import java.util.ArrayList;

import static com.example.android.traintrack.HomeActivity.dayCount;
import static com.example.android.traintrack.HomeActivity.loadedRoutine;
import static com.example.android.traintrack.HomeActivity.loadedRoutineCategories;
import static com.example.android.traintrack.RoutineCreatorActivity.clearDialog;
import static com.example.android.traintrack.RoutineCreatorActivity.createExerciseInDialog;
import static com.example.android.traintrack.RoutineCreatorActivity.dialog;


public class RoutineRecyclerAdapter extends RecyclerView.Adapter {

    //We define member variables.
    public static ArrayList mItemList;

    private boolean mIsInLoader;
    private boolean mIsInOverview;

    //We define constants that will denote view types.
    private static final int TYPE_CREATOR = 1;
    private static final int TYPE_EDITOR = 2;
    private static final int TYPE_LOADER = 3;
    private static final int TYPE_OVERVIEW = 4;


    public RoutineRecyclerAdapter(ArrayList dataSet, boolean inLoader,
                                  boolean inOverview) {
        mItemList = dataSet;
        mIsInLoader = inLoader;
        mIsInOverview = inOverview;
    }

    private class RoutineViewHolderFactory {
        private RoutineViewHolderFactory() {
        }

        //This method obtains the suitable layout resource Id for the recycler view.
        private int getLayoutResId(int type) {
            switch (type) {
                case TYPE_CREATOR:
                    return R.layout.exercise_list_item;
                case TYPE_EDITOR:
                    return R.layout.routine_list_item;
                case TYPE_LOADER:
                    return R.layout.routine_loader_list_item;
                case TYPE_OVERVIEW:
                    return R.layout.exercise_list_item;
                default:
                    throw new IllegalStateException("Invalid item view type.");
            }
        }

        private RecyclerView.ViewHolder getViewHolder(int type, View itemView) {
            switch (type) {
                case TYPE_CREATOR:
                    return new CreatorViewHolder(itemView);
                case TYPE_EDITOR:
                    return new EditorViewHolder(itemView);
                case TYPE_LOADER:
                    return new LoaderViewHolder(itemView);
                case TYPE_OVERVIEW:
                    return new OverviewVieHolder(itemView);
                default:
                    throw new IllegalStateException("Invalid item view type");
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RoutineViewHolderFactory VHFactory = new RoutineViewHolderFactory();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(VHFactory.getLayoutResId(viewType), parent, false);
        RecyclerView.ViewHolder viewHolder = VHFactory.getViewHolder(viewType, view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();

        if (type == TYPE_CREATOR) {
            CreatorViewHolder creatorVH = (CreatorViewHolder) holder;
            creatorVH.bindCreator(position);
        } else if (type == TYPE_EDITOR) {
            EditorViewHolder editorVH = (EditorViewHolder) holder;
            editorVH.bindEditor(position);
        } else if (type == TYPE_LOADER) {
            LoaderViewHolder loaderVH = (LoaderViewHolder) holder;
            loaderVH.bindLoader(position);
        } else {
            OverviewVieHolder overviewVH = (OverviewVieHolder) holder;
            overviewVH.bindOverview(position);

        }

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (mItemList.get(position) instanceof Exercise && !mIsInOverview) {
            return TYPE_CREATOR;
        } else if (mItemList.get(position) instanceof Exercise) {
            return TYPE_OVERVIEW;
        } else if (mItemList.get(position) instanceof String && !mIsInLoader) {
            return TYPE_EDITOR;
        } else {
            return TYPE_LOADER;
        }
    }

    //We define the four different ViewHolders to be used by this adapter.
    public class CreatorViewHolder extends RecyclerView.ViewHolder {
        private TextView mExerciseDetailView;
        private ImageButton mDeleteButton;
        private Exercise mCurrentExercise;
        private View mView;

        public CreatorViewHolder(View itemView) {
            super(itemView);
            mExerciseDetailView = (TextView) itemView.findViewById(R.id.tv_exercise_detail_view);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.bt_delete_exercise);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            mView = itemView;
        }

        //The bind method for the creator view holder.
        public void bindCreator(final int position) {
            mCurrentExercise = (Exercise) mItemList.get(position);
            String exerciseTitle = mCurrentExercise.getTitle();
            float weight = mCurrentExercise.getWeight();
            int set = mCurrentExercise.getSet();
            int rep = mCurrentExercise.getRep();

            String exercise_detail = exerciseTitle;
            mExerciseDetailView.setText(exercise_detail);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoutineCreatorActivity.ExerciseList.remove(mCurrentExercise);
                    mItemList.remove(mCurrentExercise);
                    RoutineRecyclerAdapter.this.notifyDataSetChanged();
                }
            });


            /*
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText titleView = (EditText) dialog.findViewById(R.id.et_exercise_title);
                    final EditText weightView = (EditText) dialog.findViewById(R.id.et_starting_weight);
                    final EditText setView = (EditText) dialog.findViewById(R.id.et_sets);
                    final EditText repView = (EditText) dialog.findViewById(R.id.et_reps);

                    final Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                    String title = mCurrentExercise.getTitle();
                    float weight = mCurrentExercise.getWeight();
                    int set = mCurrentExercise.getSet();
                    int rep = mCurrentExercise.getRep();

                    titleView.setText(title);
                    weightView.setText(String.valueOf(weight));
                    setView.setText(String.valueOf(set));
                    repView.setText(String.valueOf(rep));

                    neutralButton.setText("Update");
                    neutralButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Exercise updatedExercise = createExerciseInDialog(mCurrentExercise.getCategory(), dialog);
                            if (updatedExercise == null) {
                                Toast.makeText(mView.getContext(),
                                        "One or more fields are empty",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }


                            subroutineExerciseList.remove(position);
                            subroutineExerciseList.add(position, updatedExercise);
                            RoutineRecyclerAdapter.this.notifyDataSetChanged();
                            Toast.makeText(mView.getContext(),
                                    "Exercise was successfully updated.", Toast.LENGTH_SHORT).show();

                            neutralButton.setText("Add");

                            //TODO: Manually resetting the add function is a little redundant.
                            neutralButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Exercise currentExercise =
                                            createExerciseInDialog(mCurrentExercise.getCategory(), dialog);
                                    if (currentExercise == null) {
                                        Toast.makeText(mView.getContext(),
                                                "One or more fields are empty",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    subroutineExerciseList.add(currentExercise);

                                    RoutineRecyclerAdapter.this.notifyDataSetChanged();

                                    //We now use the helper method to update the list attached to the arrayadapter
                                    //and then notify a change in the data set to inflate more subroutine exercise list items.

                                    Toast.makeText(mView.getContext(),
                                            "Exercise was successfull added.",
                                            Toast.LENGTH_SHORT).show();

                                    clearDialog(dialog);

                                }
                            });
                        }
                    });
                }

            });

            */
        }
    }


    public class EditorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mRoutineTitleView;
        private ImageView mDeleteRoutineButton;
        private View view;
        private String currentRoutineTitle;

        public EditorViewHolder(View itemView) {
            super(itemView);
            mRoutineTitleView = (TextView) itemView.findViewById(R.id.tv_routine_description);
            mDeleteRoutineButton = (ImageView) itemView.findViewById(R.id.iv_delete_routine);
            view = itemView;
            view.setOnClickListener(this);
        }

        public void bindEditor(int position) {
            currentRoutineTitle = (String) mItemList.get(position);
            mRoutineTitleView.setText(currentRoutineTitle);
            mDeleteRoutineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoutineDbHelper mDbHelper = new RoutineDbHelper(v.getContext());

                    //We delete the workout data file as well as the table in the routine database.
                    mRoutineTitleView.getContext().deleteDatabase(currentRoutineTitle);
                    mDbHelper.deleteRoutine(currentRoutineTitle);

                    mItemList.clear();
                    for (String s : mDbHelper.getCurrentRoutines()) {
                        mItemList.add(s);
                    }

                    RoutineRecyclerAdapter.this.notifyDataSetChanged();

                    Toast.makeText(v.getContext(), "Program was successfully deleted",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), RoutineCreatorActivity.class);
            intent.putExtra("routine_title", currentRoutineTitle);
            v.getContext().startActivity(intent);
        }
    }


    public class LoaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mRoutineTitleView;
        private Button mRoutineLoadButton;
        private String currentRoutineTitle;

        public LoaderViewHolder(View itemView) {
            super(itemView);
            mRoutineTitleView = (TextView) itemView.findViewById(R.id.tv_routine_description);
            mRoutineLoadButton = (Button) itemView.findViewById(R.id.bt_load_individual_routine);
            mRoutineLoadButton.setOnClickListener(this);
        }

        public void bindLoader(int position) {
            currentRoutineTitle = (String) mItemList.get(position);
            mRoutineTitleView.setText(currentRoutineTitle);
        }

        @Override
        public void onClick(View v) {
            loadedRoutineCategories.clear();
            loadedRoutine.clear();
            RoutineLoaderActivity.loadableRoutineTitle = currentRoutineTitle;
            RoutineLoaderActivity.getLoadableSubroutines();
            dayCount = 0;
            Toast.makeText(v.getContext(), "The title of the currently loaded routine is "
                    + currentRoutineTitle, Toast.LENGTH_SHORT).show();
        }
    }

    //TODO:later on, create a list item layout for this view holder separately rather than using the same
    //Layout as that of the CreatorViewHolder and hiding the delete button.

    public class OverviewVieHolder extends RecyclerView.ViewHolder {
        private TextView mExerciseDetailView;
        private ImageButton mDeleteButton;

        public OverviewVieHolder(View itemView) {
            super(itemView);
            mExerciseDetailView = (TextView) itemView.findViewById(R.id.tv_exercise_detail_view);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.bt_delete_exercise);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
        }

        public void bindOverview(int position) {
            final Exercise currentExercise = (Exercise) mItemList.get(position);
            String exerciseTitle = currentExercise.getTitle();
            float weight = currentExercise.getWeight();
            int set = currentExercise.getSet();
            int rep = currentExercise.getRep();

            String exercise_detail = exerciseTitle + " " + weight + "kg " + "(" + set + ", " + rep + ")";
            mExerciseDetailView.setText(exercise_detail);

            mExerciseDetailView.setText(exercise_detail);
            mDeleteButton.setVisibility(View.GONE);
        }
    }


}
