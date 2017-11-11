package com.example.android.traintrack;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andyt on 7/27/2017.
 */

public class Exercise{
    private String mCategory;
    private String mExerciseTitle;
    private float mWeight;
    private int mSet;
    private int mRep;

    public Exercise(String category, String title, float weight, int set, int rep){
        mCategory = category;
        mExerciseTitle = title;
        mWeight = weight;
        mSet = set;
        mRep = rep;
    }

    //Getter methods to be used during formulation of the SQLite statement.

    public String getCategory(){
        return mCategory;
    }

    public String getTitle(){
        return mExerciseTitle;
    }

    public float getWeight(){
        return mWeight;
    }

    public int getSet(){
        return mSet;
    }

    public int getRep(){
        return mRep;
    }


}
