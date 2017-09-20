package com.example.onurp.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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

public class Sections extends StatelessSection {
    public final static int TODAY = 0;
    public final static int TOMORROW = 1;
    public final static int THIS_WEEK = 2;
    public final static int NEXT_WEEK = 3;
    public Context context;
    private FragmentCommunication mCommunicator;
    private FragmentItemRemove mItemRemove;
    final int topic;
    boolean expanded = true;
    String title;
    ArrayList<Tasks> list=new ArrayList<>();
    int lastPosition = -1;

   public Sections(int topic,ArrayList<Tasks> tasks,FragmentCommunication communication,Context context,FragmentItemRemove itemRemove) {
        super(new SectionParameters.Builder(R.layout.list_content)
                .headerResourceId(R.layout.list_content_headers)
                .build());

       this.topic = topic;
       mCommunicator=communication;
       mItemRemove=itemRemove;
       this.context = context;

        switch (topic) {
            case TODAY:
                    this.title = "TODAY";
                    this.list = tasks;
                    break;
            case TOMORROW:
                    this.title = "TOMORROW";
                    this.list = tasks;
                    break;
            case THIS_WEEK:
                    this.title = "THIS WEEK";
                    this.list = tasks;
                    break;
            case NEXT_WEEK:
                    this.title = "NEXT WEEK";
                    this.list = tasks;
                    break;
            default:
                break;
        }

    }

    public interface FragmentCommunication {
        void respond(int position);
    }

    public interface FragmentItemRemove {
        void deleteItem(String tag,int position);
    }


    @Override
    public int getContentItemsTotal() {
        return  expanded ? list.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view,mItemRemove);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
       final ItemViewHolder h = (ItemViewHolder) holder;

        Tasks task=list.get(position);


        h.tHeader.setText(task.getHeader());
        h.tContent.setText(task.getContent());
        h.tDate.setText(task.getEndDate());
        final String id = task.getIdRow();
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
                Toast.makeText(context, "Uzun Tıklanılan item numarası "+h.getAdapterPosition(), Toast.LENGTH_LONG).show();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Görev Sil");
                alertDialog.setMessage("Silmek istiyor musunuz? ");
                alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Silme işlemi iptal", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);
                        h.itemRemove.deleteItem(id,h.getAdapterPosition());
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();



                return true;
            }
        });
        /*
        if(h.getAdapterPosition() >lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.up_from_bottom);
            h.itemView.startAnimation(animation);
            lastPosition = h.getAdapterPosition();
        } */
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
                    h.imgArrow.setImageResource(
                            expanded ? R.drawable.ic_keyboard_arrow_up_black_18dp : R.drawable.ic_keyboard_arrow_down_black_18dp
                    );

                    Log.e("S","HEADER TIKLANDI "+h.getAdapterPosition());
                    h.mComminication.respond(h.getAdapterPosition());
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
             mComminication.respond(getAdapterPosition());
         }
    }



    class ItemViewHolder extends RecyclerView.ViewHolder  implements View.OnLongClickListener{

        @BindView(R.id.impLevel)
        View impLevel;
        @BindView(R.id.txtHeader)
        TextView tHeader;
        @BindView(R.id.txtContent)
        TextView tContent;
        @BindView(R.id.endDate)
        TextView tDate;
        public final View rootView;
        FragmentItemRemove itemRemove;
        ItemViewHolder(View view,FragmentItemRemove itemRemove) {
            super(view);
            rootView = view;
            this.itemRemove=itemRemove;
            ButterKnife.bind(this, view);
        }

        @Override public boolean onLongClick(View v) {
            itemRemove.deleteItem(title,getAdapterPosition());
            return true;
        }
    }


}











