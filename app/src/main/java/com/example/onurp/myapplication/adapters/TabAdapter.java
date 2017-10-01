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

import java.util.ArrayList;


/**
 * Created by onurp on 26.08.2017.
 */

public class TabAdapter extends FragmentStatePagerAdapter {
    public int tabCount;
    public String userID;
    public ArrayList<Tasks> today=new ArrayList<>();
    public ArrayList<Tasks> tomorrow=new ArrayList<>();
    public ArrayList<Tasks> thisw=new ArrayList<>();
    public ArrayList<Tasks> nextw=new ArrayList<>();
    public ArrayList<Tasks> taskFav=new ArrayList<>();

    public TabAdapter(FragmentManager fragmentManager, int tabCount, String userID, ArrayList<Tasks> today,ArrayList<Tasks> tomorrow,ArrayList<Tasks> thisw,ArrayList<Tasks> nextw,ArrayList<Tasks> taskFav){
        super(fragmentManager);
        this.tabCount=tabCount;
        this.userID=userID;
        this.today=today;
        this.tomorrow=tomorrow;
        this.thisw=thisw;
        this.nextw=nextw;
        this.taskFav=taskFav;
    }


    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("User ID", userID);
        switch (position){
            case 0:
                return FragmentAll.newInstance(today,tomorrow,thisw,nextw,userID);
            case 1:
                return FragmentToday.newInstance(today,userID);
            case 2:
                return FragmentTomorrow.newInstance(tomorrow,userID);
            case 3:
                return FragmentThisWeek.newInstance(taskFav,userID);
            default:
                return null;
        }
    }

    public void updateData(String title,ArrayList<Tasks> newTaskList){
        switch (title){
            case "1":
                this.today=newTaskList;
                notifyDataSetChanged();
                break;
            case "2":
                this.tomorrow=newTaskList;
                notifyDataSetChanged();
                break;
            case "3":
                this.thisw=newTaskList;
                notifyDataSetChanged();
                break;
            case "4":
                this.nextw=newTaskList;
                notifyDataSetChanged();
                break;
            case "5":
                this.taskFav=newTaskList;
                notifyDataSetChanged();
                break;
            default:
                notifyDataSetChanged();
                break;
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
