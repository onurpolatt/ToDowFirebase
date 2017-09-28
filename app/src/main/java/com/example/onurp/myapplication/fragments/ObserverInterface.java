package com.example.onurp.myapplication.fragments;

import com.example.onurp.myapplication.Tasks;

import java.util.ArrayList;

/**
 * Created by onurp on 22.09.2017.
 */

public interface ObserverInterface {
    public void registerObserver(ObserverInterface observer);

    public void removeObserver(ObserverInterface observer);

    public void notifyObservers();
}
