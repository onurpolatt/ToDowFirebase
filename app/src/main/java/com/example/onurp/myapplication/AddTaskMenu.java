package com.example.onurp.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.example.onurp.myapplication.fragments.pickers.DatePickerFragment;
import com.example.onurp.myapplication.fragments.pickers.TimePickerFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


/**
 * Created by onurp on 29.08.2017.
 */

public class AddTaskMenu extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener{

    @BindView(R.id.contentEditText)EditText contentEditText;
    @BindView(R.id.dateEditText)TextInputLayout dateEditText;
    @BindView(R.id.etDate)EditText etDate;
    @BindView(R.id.contentTextInput)TextInputLayout contentTextInput;
    @BindView(R.id.radioHigh)RadioButton highPriority;
    @BindView(R.id.radioNormal)RadioButton normalPriority;
    @BindView(R.id.radioLow)RadioButton lowPriority;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.fab)FloatingActionButton fab;
    @BindView(R.id.radioGroup)RadioGroup radioGroup;
    @BindView(R.id.snackLayout)LinearLayout layout;

    private static final String myFormat = "yyyy-MM-dd HH:mm";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

    public Snackbar snackbar;
    public Date cDate;
    private DatabaseReference databaseSections;
    public Tasks task;
    private Calendar calendar;

    private static final String TAG = "AddTaskMenu";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_activity);
        JodaTimeAndroid.init(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        calendar = Calendar.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseSections = FirebaseDatabase.getInstance().getReference("sections");
        task=new Tasks();
        cDate=new Date();
    }

    @OnClick(R.id.etDate)
    public void pickDateTime(){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @OnClick(R.id.radioGroup)
    public void radioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioHigh:
                if (checked)
                    normalPriority.setChecked(false);
                    lowPriority.setChecked(false);
                    break;
            case R.id.radioLow:
                if (checked)
                    normalPriority.setChecked(false);
                    highPriority.setChecked(false);
                    break;
            case R.id.radioNormal:
                if(checked)
                    lowPriority.setChecked(false);
                    highPriority.setChecked(false);
                    break;
            default:
                    break;
        }
    }
    @OnCheckedChanged({R.id.radioHigh,R.id.radioNormal,R.id.radioLow})
    public void radioGroupListener(CompoundButton button, boolean checked){
        if (checked){
            switch (button.getId()) {
                case R.id.radioHigh:
                    Log.e(TAG,"HIGH CLICKED");
                    break;
                case R.id.radioNormal:
                    Log.e(TAG,"NORMAL CLICKED");
                    break;
                case R.id.radioLow:
                    Log.e(TAG,"LOW CLICKED");
                    break;
            }
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        Calendar c = Calendar.getInstance();

        if (calendar.getTimeInMillis() >= c.getTimeInMillis()) {
            etDate.setText(sdf.format(calendar.getTime()));
            dateEditText.setErrorEnabled(false);
        } else {
            dateEditText.setErrorEnabled(true);
            etDate.setText(null);
            dateEditText.setError("Girilen zaman geçersizdir.");
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @OnClick(R.id.fab)
    public void submitForm(){
        if( TextUtils.isEmpty(contentEditText.getText())
                || TextUtils.isEmpty(etDate.getText())){
            snackbar.make(layout,"Alanları eksiksiz doldurunuz!"+Integer.toString(radioGroup.getCheckedRadioButtonId()),Snackbar.LENGTH_SHORT).show();
        }else{
            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(radioButtonID);
            int idx = radioGroup.indexOfChild(radioButton);
            Log.e(TAG,"CONTENT"+contentEditText.getText().toString());

            Intent returnIntent = new Intent();
            returnIntent.putExtra("sGroup",Tasks.getSectionGroup(etDate.getText().toString()));
            returnIntent.putExtra("content",contentEditText.getText().toString());
            returnIntent.putExtra("date",etDate.getText().toString());
            returnIntent.putExtra("imp",Integer.toString(idx));
            returnIntent.putExtra("id",databaseSections.push().getKey());

            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }
}
