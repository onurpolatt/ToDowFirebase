package com.example.onurp.myapplication.fragments.pickers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.onurp.myapplication.AddTaskMenu;
import com.example.onurp.myapplication.Sections;

import java.util.Calendar;

/**
 * Created by onurp on 1.10.2017.
 */

public class TimePickerFragment extends DialogFragment {


    public TimePickerFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        return new TimePickerDialog(getActivity(), (AddTaskMenu)getActivity(), hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

}