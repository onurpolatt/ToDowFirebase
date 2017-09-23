package com.example.onurp.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;



import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.onurp.myapplication.R.menu.item;

/**
 * Created by onurp on 29.08.2017.
 */

public class AddTaskMenu extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener{
    @BindView(R.id.contentEditText)EditText contentEditText;
    @BindView(R.id.buttonSetDate)Button pickerButton;
    @BindView(R.id.headerEditText)EditText tHeader;
    @BindView(R.id.radioHigh)RadioButton highPriority;
    @BindView(R.id.radioNormal)RadioButton normalPriority;
    @BindView(R.id.radioLow)RadioButton lowPriority;
    @BindView(R.id.txtDate)TextView showDate;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.fab)FloatingActionButton fab;
    @BindView(R.id.radioGroup)RadioGroup radioGroup;
    @BindView(R.id.snackLayout)LinearLayout layout;
    public int day,month,year,hour,minute;
    public Snackbar snackbar;
    public String date="";
    public Date cDate;
    public DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern("yyyy-MM-dd");
    private DatabaseReference databaseSections;
    public Tasks task;

    private static final String TAG = "AddTaskMenu";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_activity);
        JodaTimeAndroid.init(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseSections = FirebaseDatabase.getInstance().getReference("sections");
        task=new Tasks();
        cDate=new Date();
    }

    @OnClick(R.id.buttonSetDate)
    public void pickDateTime(){
        java.util.Calendar calendar= java.util.Calendar.getInstance();
        year= calendar.get(java.util.Calendar.YEAR);
        month= calendar.get(java.util.Calendar.MONTH);
        day= calendar.get(java.util.Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog=new DatePickerDialog(AddTaskMenu.this,AddTaskMenu.this,year,month,day);
        datePickerDialog.setTitle("Tarih Seçiniz");
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
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
        java.util.Calendar calendar=java.util.Calendar.getInstance();
        java.util.Calendar c=java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(java.util.Calendar.MINUTE, minute);
        if (calendar.getTimeInMillis() >= c.getTimeInMillis()) {
            Log.e(TAG,"DEĞERLER: " +calendar.getTimeInMillis()+":::"+c.getTimeInMillis());
            date=date+" "+hourOfDay+":"+minute;
            showDate.setVisibility(View.VISIBLE);
            showDate.setText(date);
        } else {
            snackbar.make(layout,"Girilen zaman geçersizdir!",Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date=year+"-"+(month+1)+"-"+dayOfMonth;

        java.util.Calendar calendar=java.util.Calendar.getInstance();
        hour=calendar.get(java.util.Calendar.HOUR_OF_DAY);
        minute=calendar.get(java.util.Calendar.MINUTE);

        TimePickerDialog timePickerDialog=new TimePickerDialog(AddTaskMenu.this,AddTaskMenu.this,hour,minute, android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.setTitle("Saat Seçiniz");


        timePickerDialog.show();
    }

    @OnClick(R.id.fab)
    public void submitForm(){
        if( contentEditText.getText().toString().trim().equals("")
                || tHeader.getText().toString().trim().equals("")
                || showDate.getText().toString().trim().equals("")){
            snackbar.make(layout,"Alanları eksiksiz doldurunuz!"+Integer.toString(radioGroup.getCheckedRadioButtonId()),Snackbar.LENGTH_SHORT).show();
        }else{
            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(radioButtonID);
            int idx = radioGroup.indexOfChild(radioButton);
            Log.e(TAG,"CONTENT"+contentEditText.getText().toString());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("sGroup",Tasks.getSectionGroup(showDate.getText().toString()));
            returnIntent.putExtra("content",contentEditText.getText().toString());
            returnIntent.putExtra("header",tHeader.getText().toString());
            returnIntent.putExtra("date",showDate.getText().toString());
            returnIntent.putExtra("imp",Integer.toString(idx));
            returnIntent.putExtra("id",databaseSections.push().getKey());
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }
}
