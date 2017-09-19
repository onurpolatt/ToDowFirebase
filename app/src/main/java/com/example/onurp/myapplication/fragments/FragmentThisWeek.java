package com.example.onurp.myapplication.fragments;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.onurp.myapplication.R;
import com.example.onurp.myapplication.Sections;
import com.example.onurp.myapplication.Tasks;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by onurp on 16.09.2017.
 */

public class FragmentThisWeek extends android.support.v4.app.Fragment {
    private static final String TAG = "FragmentThisWeek";
    @BindView(R.id.recylerviewThisWeek)RecyclerView recyclerView;
    @BindView(R.id.empty_text)TextView emptyText;
    public ArrayList<Tasks> taskThisWeek=new ArrayList<>();
    private SectionedRecyclerViewAdapter sectionAdapter;
    MyReceiver r;

    public void refresh() {
        Log.e(TAG,"REFRESH FRAGMENT THIS WEEK");
        sectionAdapter.notifyDataSetChanged();
    }

    public FragmentThisWeek(){

    }

    public static FragmentThisWeek newInstance(ArrayList<Tasks> thisw) {
        FragmentThisWeek result = new FragmentThisWeek();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("THISW",thisw);
        Log.e(TAG,"TASKS NEW INSTANCE: "+thisw.size());
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        taskThisWeek = bundle.getParcelableArrayList("THISW");
        Log.e(TAG,"TASKS THIS WEEK SİZE: "+taskThisWeek.size());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_this_week,container,false);
        ButterKnife.bind(this,view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sectionAdapter = new SectionedRecyclerViewAdapter();
        sectionAdapter.addSection(new Sections(Sections.THIS_WEEK,taskThisWeek,communication,getContext(),fragmentItemRemove));
        recyclerView.setAdapter(sectionAdapter);

        checkEmptyStatement();

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        r = new FragmentThisWeek.MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
                new IntentFilter("REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(r);
    }

    public void checkEmptyStatement(){
        if (taskThisWeek.isEmpty()) {
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
            FragmentThisWeek.this.refresh();
        }
    }

}
