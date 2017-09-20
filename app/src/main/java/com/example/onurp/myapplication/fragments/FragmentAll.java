package com.example.onurp.myapplication.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onurp.myapplication.R;
import com.example.onurp.myapplication.Sections;
import com.example.onurp.myapplication.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

import static com.example.onurp.myapplication.Sections.TODAY;

/**
 * Created by onurp on 26.08.2017.
 */

public class FragmentAll extends android.support.v4.app.Fragment {
    private static final String TAG = "FragmentAll";
    @BindView(R.id.lvToDoList)RecyclerView recyclerView;
    @BindView(R.id.empty_text)TextView emptyText;
    public ArrayList<Tasks> taskToday=new ArrayList<>();
    public ArrayList<Tasks> taskTomorrow=new ArrayList<>();
    public ArrayList<Tasks> taskThisWeek=new ArrayList<>();
    public ArrayList<Tasks> taskNextWeek=new ArrayList<>();

    public ArrayList<String> emptyLists=new ArrayList<>();

    private SectionedRecyclerViewAdapter sectionAdapter;
    private DatabaseReference databaseSections;
    private Unbinder unbinder;
    private String uID;
    boolean yes = false;
    boolean no =false;
    MyReceiver r;

    public void refresh() {
        Log.e(TAG,"REFRESH FRAGMENTALL");
        //sectionAdapter.notifyDataSetChanged();
    }

    public FragmentAll() {
    }


    public static FragmentAll newInstance(ArrayList<Tasks> today,ArrayList<Tasks> tomorrow,ArrayList<Tasks> thisw,ArrayList<Tasks> nextw,String uID) {
        FragmentAll result = new FragmentAll();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("TODAY",today);
        bundle.putParcelableArrayList("TOMORROW",tomorrow);
        bundle.putParcelableArrayList("THISW",thisw);
        bundle.putParcelableArrayList("NEXTW",nextw);
        bundle.putString("ID",uID);
        Log.e(TAG,"TASKS NEW INSTANCE: "+today.size()+"---"+tomorrow.size()+"---"+thisw.size());
        result.setArguments(bundle);
        return result;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        taskToday = bundle.getParcelableArrayList("TODAY");
        taskTomorrow = bundle.getParcelableArrayList("TOMORROW");
        taskThisWeek = bundle.getParcelableArrayList("THISW");
        taskNextWeek = bundle.getParcelableArrayList("NEXTW");
        uID=bundle.getString("ID");
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_all,container,false);
        ButterKnife.bind(this,view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        databaseSections = FirebaseDatabase.getInstance().getReference("tasks");
        sectionAdapter = new SectionedRecyclerViewAdapter();
        checkEmptySections();



        //Log.e(TAG,"TASKS SİZE: "+taskToday.size()+"---"+taskTomorrow.size()+"---"+taskThisWeek.size());
        if(!emptyLists.contains("TODAY")){
            sectionAdapter.addSection(new Sections(TODAY,taskToday,communication,getContext(),fragmentItemRemove));
        }
        if(!emptyLists.contains("TOMORROW")){
            sectionAdapter.addSection(new Sections(Sections.TOMORROW,taskTomorrow,communication,getContext(),fragmentItemRemove));
        }
        if(!emptyLists.contains("THISW")){
            sectionAdapter.addSection(new Sections(Sections.THIS_WEEK,taskThisWeek,communication,getContext(),fragmentItemRemove));
        }
        if(!emptyLists.contains("NEXTW")){
            sectionAdapter.addSection(new Sections(Sections.NEXT_WEEK,taskNextWeek,communication,getContext(),fragmentItemRemove));
        }

        recyclerView.setAdapter(sectionAdapter);

        checkEmptyStatement();

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        r = new MyReceiver ();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
                new IntentFilter("REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(r);
    }

    public void checkEmptyStatement(){
        if (taskToday.isEmpty() && taskTomorrow.isEmpty() && taskThisWeek.isEmpty() && taskNextWeek.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    public void checkEmptySections(){
        if(taskToday.isEmpty()){
            emptyLists.add("TODAY");
        }if(taskTomorrow.isEmpty()){
            emptyLists.add("TOMORROW");
        }if(taskThisWeek.isEmpty()){
            emptyLists.add("THISW");
        }if (taskNextWeek.isEmpty()){
            emptyLists.add("NEXTW");
        }
    }
    Sections.FragmentCommunication communication=new Sections.FragmentCommunication() {
        @Override
        public void respond(int position) {
           sectionAdapter.notifyDataSetChanged();
        }

    };

    Sections.FragmentItemRemove fragmentItemRemove=new Sections.FragmentItemRemove() {
        @Override
        public void deleteItem(String title,int position) {
                databaseSections.child(uID).child(title).setValue(null);
                sectionAdapter.notifyItemRemoved(position);
        }
    };

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FragmentAll.this.refresh();
        }
    }

}
