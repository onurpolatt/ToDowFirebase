package com.example.onurp.myapplication.fragments;

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
import android.widget.TextView;

import com.example.onurp.myapplication.R;
import com.example.onurp.myapplication.Sections;
import com.example.onurp.myapplication.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by onurp on 26.08.2017.
 */

public class FragmentToday extends android.support.v4.app.Fragment {
    private Unbinder unbinder;
    private static final String TAG = "FragmentAll";
    @BindView(R.id.recylerviewToday)RecyclerView recyclerView;
    @BindView(R.id.empty_text)TextView emptyText;
    public ArrayList<Tasks> taskToday=new ArrayList<>();
    private SectionedRecyclerViewAdapter sectionAdapter;
    private DatabaseReference databaseSections;
    private String uID;

    MyReceiver r;

    public void refresh() {
        Log.e(TAG,"REFRESH FRAGMENT TODAY");
        sectionAdapter.notifyDataSetChanged();
    }

    public FragmentToday(){

    }

    public static FragmentToday newInstance(ArrayList<Tasks> today) {
        FragmentToday result = new FragmentToday();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("TODAY",today);
        Log.e(TAG,"TASKS NEW INSTANCE: "+today.size());
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        taskToday = bundle.getParcelableArrayList("TODAY");
        //Log.e(TAG,"TASKS TODAY SİZE: "+taskToday.size());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_today,container,false);
        ButterKnife.bind(this,view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        databaseSections = FirebaseDatabase.getInstance().getReference("tasks");
        sectionAdapter = new SectionedRecyclerViewAdapter();
        uID = getArguments().getString("User ID");

        sectionAdapter.addSection(new Sections(Sections.TODAY,taskToday,communication,getContext(),fragmentItemRemove));

        recyclerView.setAdapter(sectionAdapter);

        checkEmptyStatement();

        recyclerView.setItemAnimator(new DefaultItemAnimator());


        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        r = new FragmentToday.MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
                new IntentFilter("REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(r);
    }

    public void checkEmptyStatement(){
        if (taskToday.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
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
            sectionAdapter.notifyItemRemoved(position);
        }
    };
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FragmentToday.this.refresh();
        }
    }
}
