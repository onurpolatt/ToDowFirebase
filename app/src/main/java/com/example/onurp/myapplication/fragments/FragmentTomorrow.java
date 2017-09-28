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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.onurp.myapplication.R;
import com.example.onurp.myapplication.Sections;
import com.example.onurp.myapplication.Tasks;
import com.example.onurp.myapplication.interfaces.MainFavAdd;
import com.example.onurp.myapplication.interfaces.MainFavRemove;
import com.example.onurp.myapplication.interfaces.MainItemRemove;
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

public class FragmentTomorrow extends android.support.v4.app.Fragment {
    private static final String TAG ="FragmentTomorrow" ;
    private Unbinder unbinder;
    @BindView(R.id.recylerviewTomorrow)RecyclerView recyclerView;
    @BindView(R.id.empty_text)TextView emptyText;
    public ArrayList<Tasks> taskTomorrow=new ArrayList<>();
    private SectionedRecyclerViewAdapter sectionAdapter;
    private DatabaseReference databaseSections;
    private String uID;
    MainFavRemove mainFavRemove;
    MainFavAdd mainFavAdd;
    MainItemRemove mainItemRemove;
    public void refresh() {
        Log.e(TAG,"REFRESH FRAGMENTOMORROW");
        sectionAdapter.notifyDataSetChanged();
    }

    public FragmentTomorrow(){

    }

    public static FragmentTomorrow newInstance(ArrayList<Tasks> tomorrow,String uID) {
        FragmentTomorrow result = new FragmentTomorrow();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("TOMORROW",tomorrow);
        bundle.putString("ID",uID);
        Log.e(TAG,"TASKS NEW INSTANCE: "+tomorrow.size());
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mainItemRemove = (MainItemRemove) context;
        mainFavRemove = (MainFavRemove) context;
        mainFavAdd = (MainFavAdd) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        taskTomorrow = bundle.getParcelableArrayList("TOMORROW");
        uID=bundle.getString("ID");
        Log.e(TAG,"TASKS TODMORROW SİZE: "+taskTomorrow.size());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tomorrow,container,false);
        ButterKnife.bind(this,view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sectionAdapter = new SectionedRecyclerViewAdapter();
        databaseSections = FirebaseDatabase.getInstance().getReference("tasks");
        sectionAdapter.addSection(new Sections(Sections.TOMORROW,taskTomorrow,communication,getContext(),fragmentItemRemove,favouriteItem));
        recyclerView.setAdapter(sectionAdapter);

        checkEmptyStatement();

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
        public void respond(Tasks task,boolean isChecked) {
            if(isChecked){
                task.setSelected(isChecked);
            } else {
                task.setSelected(isChecked);
            }
            sectionAdapter.notifyDataSetChanged();
        }

    };
    Sections.FragmentItemRemove fragmentItemRemove=new Sections.FragmentItemRemove() {
        @Override
        public void deleteItem(Tasks task,String id,String title,int position,int listPosition) {
            databaseSections.child(uID).child(id).setValue(null);
            mainItemRemove.itemRemoved(task,title,listPosition);
            sectionAdapter.notifyItemRemoved(position);
        }
    };

    Sections.FavouriteItem favouriteItem = new Sections.FavouriteItem() {
        @Override
        public void addFavItem(Tasks favTask,String id) {
            databaseSections.child(uID).child(id).child("isFavourite").setValue(true);
            mainFavAdd.favAdd(favTask);
        }

        @Override
        public void deleteFavItem(int position,Tasks task,String id) {
            databaseSections.child(uID).child(id).child("isFavourite").setValue(false);
            mainFavRemove.favRemove(task);
            sectionAdapter.notifyItemRemoved(position);
        }
    };

}
