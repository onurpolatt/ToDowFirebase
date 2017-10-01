package com.example.onurp.myapplication.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.example.onurp.myapplication.AddTaskMenu;
import com.example.onurp.myapplication.Sections;

import java.util.Calendar;

/**
 * Created by onurp on 1.10.2017.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), (AddTaskMenu)getActivity(), year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

    }

}
