package com.example.onurp.myapplication.fragments.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.example.onurp.myapplication.interfaces.MainItemUpdate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


import static com.example.onurp.myapplication.Sections.TODAY;

/**
 * Created by onurp on 26.08.2017.
 */

public class FragmentAll extends android.support.v4.app.Fragment implements SearchView.OnQueryTextListener{
    private static final String TAG = "FragmentAll";
    @BindView(R.id.lvToDoList)RecyclerView recyclerView;
    @BindView(R.id.empty_text)TextView emptyText;
    @BindView(R.id.fabBtn)FloatingActionButton fab;
    MainItemRemove mainItemRemove;
    MainFavRemove mainFavRemove;
    MainFavAdd mainFavAdd;
    MainItemUpdate mainItemUpdate;
    public ArrayList<Tasks> taskToday=new ArrayList<>();
    public ArrayList<Tasks> taskTomorrow=new ArrayList<>();
    public ArrayList<Tasks> taskThisWeek=new ArrayList<>();
    public ArrayList<Tasks> taskNextWeek=new ArrayList<>();

    public ArrayList<String> emptyLists=new ArrayList<>();
    public ArrayList<Tasks> combinedLists=new ArrayList<>();
    private SectionedRecyclerViewAdapter sectionAdapter;
    private DatabaseReference databaseSections;
    private Unbinder unbinder;
    private String uID;
    private static FragmentAll INSTANCE = null;


    public FragmentAll() {
    }

    public static FragmentAll getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FragmentAll();
        }
        return INSTANCE;
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
    public void onAttach(Context context){
        super.onAttach(context);
        mainItemRemove = (MainItemRemove) context;
        mainFavRemove = (MainFavRemove) context;
        mainFavAdd = (MainFavAdd) context;
        mainItemUpdate = (MainItemUpdate) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        fab.hide();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        databaseSections = FirebaseDatabase.getInstance().getReference("tasks");
        sectionAdapter = new SectionedRecyclerViewAdapter();
        checkEmptySections();

        combinedLists.addAll(taskToday);
        combinedLists.addAll(taskTomorrow);
        combinedLists.addAll(taskThisWeek);
        combinedLists.addAll(taskNextWeek);

        //Log.e(TAG,"TASKS SİZE: "+taskToday.size()+"---"+taskTomorrow.size()+"---"+taskThisWeek.size());
        if(!emptyLists.contains("TODAY")){
            sectionAdapter.addSection(new Sections(TODAY,taskToday,communication,getContext(),fragmentItemUpdate,favouriteItem));
        }
        if(!emptyLists.contains("TOMORROW")){
            sectionAdapter.addSection(new Sections(Sections.TOMORROW,taskTomorrow,communication,getContext(),fragmentItemUpdate,favouriteItem));
        }
        if(!emptyLists.contains("THISW")){
            sectionAdapter.addSection(new Sections(Sections.THIS_WEEK,taskThisWeek,communication,getContext(),fragmentItemUpdate,favouriteItem));
        }
        if(!emptyLists.contains("NEXTW")){
            sectionAdapter.addSection(new Sections(Sections.NEXT_WEEK,taskNextWeek,communication,getContext(),fragmentItemUpdate,favouriteItem));
        }

        recyclerView.setAdapter(sectionAdapter);

        checkEmptyCase();

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Log.e("ONEMLİ","SECTİON DEĞERLERİ"+sectionAdapter.getSectionsMap().values());
        for (Section section : sectionAdapter.getSectionsMap().values()) {
            if (section instanceof FilterableSection) {
                Log.e("ONEMLİ","FİLTER CALİSTİ");
                ((FilterableSection)section).filter(query);
            }
        }
        sectionAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void checkEmptyCase(){
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
                    for(int i=0;i<combinedLists.size();i++){
                        if(combinedLists.get(i).isSelected()){
                            switch (combinedLists.get(i).getsSectionGroup()){
                                case "TODAY":
                                    taskToday.remove(combinedLists.get(i));
                                    databaseSections.child(uID).child(combinedLists.get(i).getIdRow()).setValue(null);
                                    break;
                                case "TOMORROW":
                                    taskTomorrow.remove(combinedLists.get(i));
                                    databaseSections.child(uID).child(combinedLists.get(i).getIdRow()).setValue(null);
                                    break;
                                case "THIS WEEK":
                                    taskThisWeek.remove(combinedLists.get(i));
                                    databaseSections.child(uID).child(combinedLists.get(i).getIdRow()).setValue(null);
                                    break;
                                case "NEXT WEEK":
                                    taskNextWeek.remove(combinedLists.get(i));
                                    databaseSections.child(uID).child(combinedLists.get(i).getIdRow()).setValue(null);
                                    break;
                            }
                        }
                    }
                    mainItemRemove.itemRemovedOrUpdate(taskToday,taskTomorrow,taskThisWeek,taskNextWeek);
                    removeFromDatabase();
                    fab.hide();
                }
            });

            sectionAdapter.notifyDataSetChanged();
        }

    };

    public boolean checkSelectedItem(){
        boolean result = true;
        for (Tasks task: combinedLists){
            if(task.isSelected()){
                result = false;
            }
        }
        return result;
    }

    public void removeFromDatabase(){
        databaseSections.child(uID).orderByChild("isSelected").equalTo("true").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    child.getRef().setValue(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    Sections.FragmentItemUpdate fragmentItemUpdate=new Sections.FragmentItemUpdate() {
        @Override
        public void updateItem(Tasks task,int position,String prevSection) {
                databaseSections.child(uID).child(task.getIdRow()).setValue(task);
                removeFromSection(prevSection,task);
                addItemToSection(task);
                mainItemRemove.itemRemovedOrUpdate(taskToday,taskTomorrow,taskThisWeek,taskNextWeek);
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
    public void removeFromSection(String prevSection,Tasks task){
            switch (prevSection){
                case "TODAY":
                    taskToday.remove(task);
                    break;
                case "TOMORROW":
                    taskTomorrow.remove(task);
                    break;
                case "THIS WEEK":
                    taskThisWeek.remove(task);
                    break;
                case "NEXT WEEK":
                    taskNextWeek.remove(task);
                    break;
            }
    }

    public void addItemToSection(Tasks task){
        switch (task.getsSectionGroup()){
            case "TODAY":
                taskToday.add(task);
                break;
            case "TOMORROW":
                taskTomorrow.add(task);
                break;
            case "THIS WEEK":
                taskThisWeek.add(task);
                break;
            case "NEXT WEEK":
                taskNextWeek.add(task);
                break;
        }
    }



    @Override
    public String toString() {
        return super.toString();
    }

    public interface FilterableSection {
        void filter(String query);
    }
}
