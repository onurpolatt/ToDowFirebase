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
import com.example.onurp.myapplication.interfaces.MainFavAdd;
import com.example.onurp.myapplication.interfaces.MainFavRemove;
import com.example.onurp.myapplication.interfaces.MainItemRemove;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;

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
    @BindView(R.id.fabBtn)FloatingActionButton fab;
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
        fab.hide();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sectionAdapter = new SectionedRecyclerViewAdapter();
        databaseSections = FirebaseDatabase.getInstance().getReference("tasks");
        sectionAdapter.addSection(new Sections(Sections.TOMORROW,taskTomorrow,communication,getContext(),fragmentItemUpdate,favouriteItem));
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
        public void respond(final ArrayList<Tasks> task, final Tasks oneTask, boolean isChecked, final int position) {
            if(task != null){
                for (int i=0;i<task.size();i++)
                {
                    Log.e(TAG,"TASKLAR"+task.get(i));
                    task.get(i).setSelected(isChecked);
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
                    Iterator<Tasks> i = taskTomorrow.iterator();
                    while (i.hasNext()) {
                        Tasks tomorrow = i.next();
                        if(tomorrow.isSelected()){
                            i.remove();
                            databaseSections.child(uID).child(tomorrow.getIdRow()).setValue(null);
                        }
                    }
                    mainItemRemove.itemRemoved(taskTomorrow,null,null,null);
                    fab.hide();
                }
            });


            sectionAdapter.notifyDataSetChanged();
        }

    };

    public boolean checkSelectedItem(){
        boolean result = true;
        for (Tasks task: taskTomorrow){
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
