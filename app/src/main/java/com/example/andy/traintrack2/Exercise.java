package com.example.andy.traintrack2;

/**
 * Created by andy on 17. 11. 3.
 * This class defines an object called Exercise.
 * This object has attributes and methods that provide information about an exercise.
 */

public class Exercise {

    //We declare member variables which represent attributes of an exercise object.
    private String mTitle;
    private int mSet;
    private int mRep;

    //We declare a constructor which takes in as input the three attributes of an exercise.
    public Exercise(String title, int set, int rep){
        mTitle = title;
        mSet = set;
        mRep = rep;
    }


    //We also create getter methods that will return individual attributes of an exercise.
    public String getTitle(){
        return mTitle;
    }

    public int getSet(){
        return mSet;
    }

    public int getRep(){
        return mRep;
    }
}
