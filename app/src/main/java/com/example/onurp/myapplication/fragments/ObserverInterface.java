package com.example.onurp.myapplication.fragments;

import com.example.onurp.myapplication.Tasks;

import java.util.ArrayList;

/**
 * Created by onurp on 22.09.2017.
 */

public interface ObserverInterface {
    void onUserDataChanged(ArrayList<Tasks> fullname);
}
