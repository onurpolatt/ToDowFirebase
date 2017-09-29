package com.example.onurp.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.onurp.myapplication.fragments.FragmentAll;
import com.example.onurp.myapplication.fragments.ObserverInterface;
import com.example.onurp.myapplication.interfaces.FilterableSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

import static java.security.AccessController.getContext;

/**
 * Created by onurp on 16.09.2017.
 */

public class Sections extends StatelessSection implements FragmentAll.FilterableSection {
    public final static int TODAY = 0;
    public final static int TOMORROW = 1;
    public final static int THIS_WEEK = 2;
    public final static int NEXT_WEEK = 3;
    public final static int FAVOURITE = 4;
    public Context context;
    private FragmentCommunication mCommunicator;
    private FragmentItemRemove mItemRemove;
    private FavouriteItem favouriteItem;
    final int topic;
    boolean expanded = true;
    String title;
    boolean isFav;
    ArrayList<Integer> positions;
    ArrayList<Tasks> list=new ArrayList<>();
    int lastPosition = -1;
    ArrayList<Tasks> filteredList;

   public Sections(int topic,ArrayList<Tasks> tasks,FragmentCommunication communication,Context context,FragmentItemRemove itemRemove,FavouriteItem favouriteItem) {
        super(new SectionParameters.Builder(R.layout.list_content)
                .headerResourceId(R.layout.list_content_headers)
                .build());
       this.topic = topic;
       mCommunicator=communication;
       mItemRemove=itemRemove;
       this.context = context;
       this.favouriteItem=favouriteItem;


        switch (topic) {
            case TODAY:
                    this.title = "TODAY";
                    this.list = tasks;
                    this.filteredList = new ArrayList<>(list);
                    break;
            case TOMORROW:
                    this.title = "TOMORROW";
                    this.list = tasks;
                    this.filteredList = new ArrayList<>(list);
                    break;
            case THIS_WEEK:
                    this.title = "THIS WEEK";
                    this.list = tasks;
                    this.filteredList = new ArrayList<>(list);
                    break;
            case NEXT_WEEK:
                    this.title = "NEXT WEEK";
                    this.list = tasks;
                    this.filteredList = new ArrayList<>(list);
                    break;
            case FAVOURITE:
                    this.title = "FAVOURİTE";
                    this.list = tasks;
                    this.filteredList = new ArrayList<>(list);
                    break;
            default:
                break;
        }
    }

    @Override
    public void filter(String query) {
        if (TextUtils.isEmpty(query)) {
            filteredList = new ArrayList<>(list);
            this.setVisible(true);
        }
        else {
            filteredList.clear();
            for (Tasks value : list) {
                if (value.getContent().toLowerCase().contains(query.toLowerCase())) {
                    Log.e("ONEMLİ","BULUNAN VERİ"+value.getContent());
                    filteredList.add(value);
                }
            }

            this.setVisible(!filteredList.isEmpty());
        }
    }




    public interface FragmentCommunication {
        void respond(ArrayList<Tasks> task,Tasks oneTask,boolean isChecked,int position);
    }

    public interface FragmentItemRemove {
        void deleteItem(Tasks task,String id,String tag ,int position,int listPosition);
    }

    public interface FavouriteItem {
        void addFavItem(Tasks favTask,String id);
        void deleteFavItem(int position,Tasks tasks,String id);
    }

    @Override
    public int getContentItemsTotal() {
        return filteredList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view,mItemRemove,favouriteItem,mCommunicator);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder h = (ItemViewHolder) holder;

        final Tasks task=filteredList.get(position);

        isFav=task.isFavourite();
        h.tImgFav.setImageResource(
                isFav ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp
        );

        h.tContent.setText(task.getContent());
        h.tDate.setText(task.getEndDate());
        h.checkBox.setOnCheckedChangeListener(null);
        h.checkBox.setChecked(task.isSelected());
        final String id = task.getIdRow();
        final String sGroup = task.sSectionGroup;

        h.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Tıklanılan item numarası "+h.getAdapterPosition(), Toast.LENGTH_LONG).show();
                Log.e("S","LIST BOYUTU "+list.size());
            }
        });

        h.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Uzun Tıklanılan item numarası "+position, Toast.LENGTH_LONG).show();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Görev Sil");
                alertDialog.setMessage("Silmek istiyor musunuz? ");
                alertDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Silme işlemi iptal", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filteredList.remove(position);
                        h.itemRemove.deleteItem(task,id,sGroup,h.getAdapterPosition(),position);
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();



                return true;
            }
        });

        h.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    task.setSelected(isChecked);
                    h.mComminication.respond(null,list.get(position),true,position);
                } else {
                    task.setSelected(isChecked);
                    h.mComminication.respond(null,list.get(position),false,position);
                }
            }
        });

        h.tImgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(task.isFavourite()){
                    Log.e("ADDFAV","SILINEN FAV:"+isFav);
                    task.setFavourite(!task.isFavourite());
                    h.favouriteItem.deleteFavItem(position,task,id);
                } else {
                    Log.e("ADDFAV","EKLENİLEN FAV:"+isFav);
                    task.setFavourite(!task.isFavourite());
                    h.favouriteItem.addFavItem(task,id);
                }
                h.tImgFav.setImageResource(
                        task.isFavourite ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp
                );

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view,mCommunicator);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final HeaderViewHolder h = (HeaderViewHolder) holder;
            h.sectionHeader.setText(title);
            h.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expanded = !expanded;
                    if(!expanded){
                        for (int i=0;i<list.size();i++){
                            list.get(i).setSelected(true);
                        }
                        h.mComminication.respond(list,null,true,0);
                    } else {
                        for (int i=0;i<list.size();i++){
                            list.get(i).setSelected(false);
                        }
                        h.mComminication.respond(list,null,false,0);
                    }
                    h.imgArrow.setImageResource(
                            expanded ? R.drawable.ic_keyboard_arrow_up_black_18dp : R.drawable.ic_keyboard_arrow_down_black_18dp
                    );

                    Log.e("S","HEADER TIKLANDI "+list.size());

                }
            });

    }


    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.txtSection)
        TextView sectionHeader;
        @BindView(R.id.imgArrow)ImageView imgArrow;
        public final View rootView;
        FragmentCommunication mComminication;
        HeaderViewHolder(View view,FragmentCommunication Communicator) {
            super(view);
            rootView=view;
            mComminication=Communicator;
            ButterKnife.bind(this,view);
        }
         @Override public void onClick(View v) {

         }
    }



    class ItemViewHolder extends RecyclerView.ViewHolder  implements View.OnLongClickListener{
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.imgFav)
        ImageView tImgFav;
        @BindView(R.id.txtContent)
        TextView tContent;
        @BindView(R.id.endDate)
        TextView tDate;
        public final View rootView;
        FragmentItemRemove itemRemove;
        FavouriteItem favouriteItem;
        FragmentCommunication mComminication;
        ItemViewHolder(View view,FragmentItemRemove itemRemove,FavouriteItem favouriteItem,FragmentCommunication communication) {
            super(view);
            rootView = view;
            this.favouriteItem=favouriteItem;
            this.itemRemove=itemRemove;
            this.mComminication = communication;
            ButterKnife.bind(this, view);
        }

        @Override public boolean onLongClick(View v) {
            //itemRemove.deleteItem(title,getAdapterPosition());
            return true;
        }
    }



}











