package com.example.onurp.myapplication.fragments.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.example.onurp.myapplication.interfaces.MainFavRemove;
import com.example.onurp.myapplication.interfaces.MainItemRemove;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by onurp on 16.09.2017.
 */

public class FragmentThisWeek extends android.support.v4.app.Fragment {
    private static final String TAG = "FragmentThisWeek";
    private static final String EMPTY_TEXT ="Favori Görev Yok";
    @BindView(R.id.recylerviewThisWeek)RecyclerView recyclerView;
    @BindView(R.id.empty_text)TextView emptyText;
    @BindView(R.id.fabBtn)FloatingActionButton fab;
    MainFavRemove mainFavRemove;
    MainItemRemove mainItemRemove;
    public ArrayList<Tasks> taskThisWeek=new ArrayList<>();
    private SectionedRecyclerViewAdapter sectionAdapter;
    private DatabaseReference databaseSections;
    private String uID;



    public FragmentThisWeek(){

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mainItemRemove = (MainItemRemove) context;
        mainFavRemove = (MainFavRemove) context;
    }


    public static FragmentThisWeek newInstance(ArrayList<Tasks> thisw,String uID) {
        FragmentThisWeek result = new FragmentThisWeek();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("THISW",thisw);
        bundle.putString("ID",uID);
        Log.e(TAG,"TASKS NEW INSTANCE: "+thisw.size());
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        taskThisWeek = bundle.getParcelableArrayList("THISW");
        uID=bundle.getString("ID");
        Log.e(TAG,"TASKS THIS WEEK SİZE: "+taskThisWeek.size());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_this_week,container,false);
        ButterKnife.bind(this,view);
        fab.hide();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sectionAdapter = new SectionedRecyclerViewAdapter();
        sectionAdapter.addSection(new Sections(Sections.FAVOURITE,taskThisWeek,communication,getContext(),fragmentItemUpdate,favouriteItem));
        recyclerView.setAdapter(sectionAdapter);
        databaseSections = FirebaseDatabase.getInstance().getReference("tasks");
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
        if (taskThisWeek.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            emptyText.setText(EMPTY_TEXT);
        }
    }

    Sections.FragmentCommunication communication=new Sections.FragmentCommunication() {
        @Override
        public void respond(final ArrayList<Tasks> task,final Tasks oneTask,boolean isChecked,final int position) {
            if(task != null){
                for (ListIterator<Tasks> i = task.listIterator(); i.hasNext(); i.next())
                {
                    final Tasks element = i.next();
                    element.setSelected(isChecked);
                }

            } else if(oneTask != null){
                oneTask.setSelected(isChecked);
            }

            if(checkSelectedItem()){
                fab.hide();
            } else {
                fab.show();
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Iterator<Tasks> i = taskThisWeek.iterator();
                    while (i.hasNext()) {
                        Tasks fav = i.next();
                        if(fav.isSelected()){
                            i.remove();
                            databaseSections.child(uID).child(fav.getIdRow()).setValue(null);
                        }
                    }
                    mainItemRemove.itemRemoved(taskThisWeek,null,null,null);
                    fab.hide();
                }
            });
            sectionAdapter.notifyDataSetChanged();
        }
    };

    public boolean checkSelectedItem(){
        boolean result = true;
        for (Tasks task: taskThisWeek){
            if(task.isSelected()){
                result = false;
            }
        }
        return result;
    }


    Sections.FragmentItemUpdate fragmentItemUpdate=new Sections.FragmentItemUpdate() {
        @Override
        public void updateItem(Tasks task) {
            databaseSections.child(uID).child(task.getIdRow()).setValue(task);

            sectionAdapter.notifyDataSetChanged();
        }
    };

    Sections.FavouriteItem favouriteItem = new Sections.FavouriteItem() {
        @Override
        public void addFavItem(Tasks favTask,String id) {

        }

        @Override
        public void deleteFavItem(int position,Tasks task,String id) {
            databaseSections.child(uID).child(id).child("isFavourite").setValue(false);
            mainFavRemove.favRemove(task);
            sectionAdapter.notifyItemRemoved(position);
        }
    };


}
