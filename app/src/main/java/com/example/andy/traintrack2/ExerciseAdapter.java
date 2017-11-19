package com.example.andy.traintrack2;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andy.traintrack2.Data.ExerciseContract.ExerciseTable;
import com.google.gson.Gson;

public class ExerciseAdapter extends RecyclerView.Adapter {
    private Cursor mExerciseCursor;

    public ExerciseAdapter(Cursor exercises) {
        mExerciseCursor = exercises;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView exerciseView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);

        return new ViewHolder(exerciseView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        mExerciseCursor.moveToPosition(position);
        String title = mExerciseCursor.getString(mExerciseCursor
                .getColumnIndexOrThrow(ExerciseTable.COLUMN_TITLE));
        ((ViewHolder) holder).mTextView.setText(title);
        ((ViewHolder) holder).mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExerciseCursor.moveToPosition(position);
                //We reconstruct the exercise object from the cursor and pass it along with the intent.
                String title = mExerciseCursor.getString(mExerciseCursor.getColumnIndexOrThrow(ExerciseTable.COLUMN_TITLE));
                int set = mExerciseCursor.getInt(mExerciseCursor.getColumnIndexOrThrow(ExerciseTable.COLUMN_SET));
                int rep = mExerciseCursor.getInt(mExerciseCursor.getColumnIndexOrThrow(ExerciseTable.COLUMN_REP));
                Exercise currentExercise = new Exercise(title, set, rep);

                //We use Google GSON to serialize the Exercise object to pass to EditorActivity.
                String json = new Gson().toJson(currentExercise);
                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                intent.putExtra("Edit Mode", true);
                intent.putExtra("Exercise", json);
                intent.putExtra("Index", position);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExerciseCursor == null ? 0 : mExerciseCursor.getCount();
    }

    public void swapCursor(Cursor c) {
        mExerciseCursor = c;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }

}
