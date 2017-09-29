package com.example.onurp.myapplication.interfaces;

import com.example.onurp.myapplication.Tasks;

import java.util.ArrayList;

/**
 * Created by onurp on 23.09.2017.
 */

public interface MainItemRemove {
    public void itemRemoved(ArrayList<Tasks> today,ArrayList<Tasks> tomorrow,ArrayList<Tasks> thisw,ArrayList<Tasks> nextw);
}
