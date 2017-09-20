package com.example.onurp.myapplication.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.onurp.myapplication.Tasks;
import com.example.onurp.myapplication.fragments.FragmentAll;
import com.example.onurp.myapplication.fragments.FragmentThisWeek;
import com.example.onurp.myapplication.fragments.FragmentToday;
import com.example.onurp.myapplication.fragments.FragmentTomorrow;
import com.example.onurp.myapplication.fragments.deleteDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static android.R.attr.fragment;


/**
 * Created by onurp on 26.08.2017.
 */

public class TabAdapter extends FragmentStatePagerAdapter  {
    public int tabCount;
    public String userID;
    public ArrayList<Tasks> today=new ArrayList<>();
    public ArrayList<Tasks> tomorrow=new ArrayList<>();
    public ArrayList<Tasks> thisw=new ArrayList<>();
    public ArrayList<Tasks> nextw=new ArrayList<>();

    public TabAdapter(FragmentManager fragmentManager, int tabCount, String userID, ArrayList<Tasks> today,ArrayList<Tasks> tomorrow,ArrayList<Tasks> thisw,ArrayList<Tasks> nextw){
        super(fragmentManager);
        this.tabCount=tabCount;
        this.userID=userID;
        this.today=today;
        this.tomorrow=tomorrow;
        this.thisw=thisw;
        this.nextw=nextw;
    }
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("User ID", userID);
        switch (position){
            case 0:
                return FragmentAll.newInstance(today,tomorrow,thisw,nextw,userID);
            case 1:
                return FragmentToday.newInstance(today);
            case 2:
                return FragmentTomorrow.newInstance(tomorrow);
            case 3:
                return FragmentThisWeek.newInstance(thisw);
            default:
                return null;
        }
    }

    public void updateData(String title,ArrayList<Tasks> newTaskList){
        switch (title){
            case "TODAY":
                this.today=newTaskList;
                notifyDataSetChanged();
            case "TOMORROW":
                this.tomorrow=newTaskList;
                notifyDataSetChanged();
            case "THIS WEEK":
                this.thisw=newTaskList;
                notifyDataSetChanged();
            case "NEXT WEEK":
                this.nextw=newTaskList;
                notifyDataSetChanged();
            default:
                notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
