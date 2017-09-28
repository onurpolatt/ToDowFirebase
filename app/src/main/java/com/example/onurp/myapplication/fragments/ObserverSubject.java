package com.example.onurp.myapplication.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.onurp.myapplication.Tasks;

import java.util.ArrayList;

/**
 * Created by onurp on 17.09.2017.
 */

public interface ObserverSubject {
    void filter(String query);
}