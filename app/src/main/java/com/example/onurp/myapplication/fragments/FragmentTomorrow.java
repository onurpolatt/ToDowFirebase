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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by onurp on 26.08.2017.
 */

public class FragmentTomorrow extends android.support.v4.app.Fragment {
    private static final String TAG ="FragmentTomorrow" ;
    private Unbinder unbinder;
    @BindView(R.id.recylerviewTomorrow)RecyclerView recyclerView;
    @BindView(R.id.empty_text)TextView emptyText;
    public ArrayList<Tasks> taskTomorrow=new ArrayList<>();
    private SectionedRecyclerViewAdapter sectionAdapter;
    MyReceiver r;

    public void refresh() {
        Log.e(TAG,"REFRESH FRAGMENTOMORROW");
        sectionAdapter.notifyDataSetChanged();
    }

    public FragmentTomorrow(){

    }

    public static FragmentTomorrow newInstance(ArrayList<Tasks> tomorrow) {
        FragmentTomorrow result = new FragmentTomorrow();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("TOMORROW",tomorrow);
        Log.e(TAG,"TASKS NEW INSTANCE: "+tomorrow.size());
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        taskTomorrow = bundle.getParcelableArrayList("TOMORROW");
        Log.e(TAG,"TASKS TODMORROW SİZE: "+taskTomorrow.size());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tomorrow,container,false);
        ButterKnife.bind(this,view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sectionAdapter = new SectionedRecyclerViewAdapter();

        sectionAdapter.addSection(new Sections(Sections.TOMORROW,taskTomorrow,communication,getContext(),fragmentItemRemove));
        recyclerView.setAdapter(sectionAdapter);

        checkEmptyStatement();

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        r = new FragmentTomorrow.MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
                new IntentFilter("REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(r);
    }

    public void checkEmptyStatement(){
        if (taskTomorrow.isEmpty()) {
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
            FragmentTomorrow.this.refresh();
        }
    }

}
