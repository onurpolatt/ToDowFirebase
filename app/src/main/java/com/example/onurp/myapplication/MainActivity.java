package com.example.onurp.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.onurp.myapplication.adapters.CustomAdapter;
import com.example.onurp.myapplication.adapters.TabAdapter;
import com.example.onurp.myapplication.auth.LoginActivity;
import com.example.onurp.myapplication.auth.SignupActivity;
import com.example.onurp.myapplication.database.dbHandler;
import com.example.onurp.myapplication.database.dbManager;
import com.example.onurp.myapplication.fragments.FragmentAll;
import com.example.onurp.myapplication.fragments.ObserverInterface;
import com.example.onurp.myapplication.interfaces.MainItemRemove;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by onurp on 17.08.2017.
 */

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener,MainItemRemove{
    public Toolbar toolbar;
    public TabAdapter tabAdapter;
    public ViewPager viewPager;
    public dbManager db;
    public dbHandler dHandler;
    public ArrayList<Tasks> task=new ArrayList<>();
    public ArrayList<Tasks> taskToday=new ArrayList<>();
    public ArrayList<Tasks> taskTomorrow=new ArrayList<>();
    public ArrayList<Tasks> taskThisWeek=new ArrayList<>();
    public ArrayList<Tasks> taskNextWeek=new ArrayList<>();
    private static final String TAG = "AddTaskMenu";
    private Boolean firstTime = null;
    private FirebaseUser user;
    private String uID;
    private FirebaseAuth auth;
    private DatabaseReference databaseSections;
    public  ProgressDialog progressDialog;
    public DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern("yyyy-MM-dd");
    public ArrayList<ObserverInterface> mObservers;
    private Tasks tasks;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        databaseSections = FirebaseDatabase.getInstance().getReference("tasks");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uID = user.getUid();
        tasks = new Tasks();
        fetchTasks();
        /*dHandler=new dbHandler(this);
        dHandler.getWritableDatabase();*/
        //getTaskDataFromDb();
        //checkEmptyStatement();
        Log.e(TAG,"TASKS ACTİVİTY: "+taskToday.size()+"---"+taskTomorrow.size()+"---"+taskNextWeek.size());

    }




    public void setupTabs(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("Tomorrow"));
        tabLayout.addTab(tabLayout.newTab().setText("Week"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        tabAdapter = new TabAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),uID,taskToday,taskTomorrow,taskThisWeek,taskNextWeek);
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
                Intent i = new Intent("REFRESH");
                lbm.sendBroadcast(i);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                tabAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }


    private boolean fetchTasks() {
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Giriş yapılıyor...");
        progressDialog.show();
        databaseSections.child(uID).orderByChild("endDate").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren())
                        {
                            Tasks task = child.getValue(Tasks.class);
                            switch (getSectionGroup(task.getEndDate())){
                                case "1":
                                    taskToday.add(task);
                                    break;
                                case "2":
                                    taskTomorrow.add(task);
                                    break;
                                case "3":
                                    taskThisWeek.add(task);
                                    break;
                                case "4":
                                    taskNextWeek.add(task);
                                    break;
                            }
                        }
                        if(firstTime==null){
                            setupTabs();
                            firstTime=false;
                        }
                        progressDialog.dismiss();
                        Log.e(TAG,"TASKS FETCH: "+taskToday.size()+"---"+taskTomorrow.size()+"---"+taskNextWeek.size());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        return true;
    }
/*
    public void getTaskDataFromDb()
    {
        dbManager db=new dbManager(this);

        db.open();
        task.clear();
        db.insertTasks("merhaba","a","1","2017-09-11",1);
        db.insertTasks("merhaba","b","1","2017-09-11",1);
        db.insertTasks("merhaba","c","1","2017-09-12",2);
        db.insertTasks("merhaba","d","1","2017-09-15",3);
        db.insertTasks("merhaba","e","1","2017-09-19",4);

        task = db.getAllTasks();

        sHeaders=db.getAllHeaders();

        Collections.sort(task, new Comparator<Tasks>() {
            public int compare(Tasks o1, Tasks o2) {
                if (o1.getEndDate() == null || o2.getEndDate() == null)
                    return 0;
                return o1.getEndDate().compareTo(o2.getEndDate());
            }
        });

        for(int i=0;i<task.size();i++){
            System.out.println("TARİH: "+task.get(i).endDate+"CONTENT:"+task.get(i).content);
            Log.d(TAG, task.toString());
        }
        count=0;
        for(int i=0;i<sHeaders.size();i++){
            for(int j=count;j<task.size();j++){
                if(sHeaders.get(i).getIdSection().equals(task.get(j).getSectionGroup()) && sectionFirstAttempt==true){
                    combinedList.add(new Tasks(sHeaders.get(i).idSection,sHeaders.get(i).sectionName));
                    combinedList.add(new Tasks(task.get(j).header,task.get(j).content,task.get(j).importanceLevel,task.get(j).endDate,task.get(j).sectionGroup));
                    sectionFirstAttempt=false;
                    count++;
                }else if(sHeaders.get(i).getIdSection().equals(task.get(j).getSectionGroup()) && sectionFirstAttempt==false){
                    combinedList.add(new Tasks(task.get(j).header,task.get(j).content,task.get(j).importanceLevel,task.get(j).endDate,task.get(j).sectionGroup));
                    count++;
                }else{
                    sectionFirstAttempt=true;
                    break;
                }
            }
        }

        if(!(task.size()<1))
        {

            adapter.notifyDataSetChanged();
        }
    }

*/




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert:
                Intent intentAddtask = new Intent(this, AddTaskMenu.class);
                startActivityForResult(intentAddtask,1);
                return true;
            case R.id.action_logoff:
                FirebaseAuth.getInstance().signOut();
                Intent intentLogout = new Intent(this, LoginActivity.class);
                startActivity(intentLogout);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String sectionGroup=data.getStringExtra("sGroup");
                String content=data.getStringExtra("content");
                String header=data.getStringExtra("header");
                String date=data.getStringExtra("date");
                String imp=data.getStringExtra("imp");
                String id=data.getStringExtra("id");

                String gsGroup = getSectionGroup(date);

                switch (gsGroup){
                    case "1":
                        taskToday.add(new Tasks(id,header,content,imp,date,sectionGroup));
                        tabAdapter.updateData(gsGroup,taskToday);
                        break;
                    case "2":
                        taskTomorrow.add(new Tasks(id,header,content,imp,date,sectionGroup));
                        tabAdapter.updateData(gsGroup,taskTomorrow);
                        break;
                    case "3":
                        Log.e(TAG,"TASKK HIS WEEK ADD");
                        taskThisWeek.add(new Tasks(id,header,content,imp,date,sectionGroup));
                        tabAdapter.updateData(gsGroup,taskThisWeek);
                        break;
                    case "4":
                        Log.e(TAG,"TASKK NEXT WEEK ADD");
                        taskNextWeek.add(new Tasks(id,header,content,imp,date,sectionGroup));
                        tabAdapter.updateData(gsGroup,taskNextWeek);
                        break;
                }

                databaseSections.child(uID).child(id).setValue(new Tasks(id,header,content,imp,date,sectionGroup));
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    public String getSectionGroup(String date){
        String response="";

        LocalDate localDate = LocalDate.now();
        String newDate=date.split(" ")[0];
        LocalDate ld=dateTimeFormatter.parseLocalDate(newDate);
        int days= Days.daysBetween(localDate,ld).getDays();


        if(days == 0){
            Log.e(TAG,"COMPARE 0");
            response="1";
        }
        else if(days == 1){
            Log.e(TAG,"COMPARE 1");
            response="2";
        }
        else if(days >1 && days<7){
            Log.e(TAG,"COMPARE 1-7");
            response="3";
        }
        else{
            Log.e(TAG,"COMPARE 7>");
            response="4";
        }
        return response;
    }

    @Override
    public void itemRemoved(String tag, int position) {
        switch (tag){
            case "TODAY":
                taskToday.remove(position);
                tabAdapter.updateData("1",taskToday);
                break;
            case "TOMORROW":
                taskTomorrow.remove(position);
                tabAdapter.updateData("2",taskTomorrow);
                break;
            case "THIS WEEK":
                taskThisWeek.remove(position);
                tabAdapter.updateData("3",taskThisWeek);
                break;
            case "NEXT WEEK":
                taskNextWeek.remove(position);
                tabAdapter.updateData("4",taskNextWeek);
                break;
            default:
                break;
        }
    }
}
