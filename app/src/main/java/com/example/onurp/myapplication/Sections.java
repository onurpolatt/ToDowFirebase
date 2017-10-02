package com.example.onurp.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.example.onurp.myapplication.fragments.tabs.FragmentAll;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

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
    private FragmentItemUpdate mItemUpdate;
    private FavouriteItem favouriteItem;
    final int topic;
    boolean expanded = true;
    String title;
    boolean isFav;
    private Toast toast;
    ArrayList<Integer> positions;
    ArrayList<Tasks> list=new ArrayList<>();
    int lastPosition = -1;
    ArrayList<Tasks> filteredList;

   public Sections(int topic,ArrayList<Tasks> tasks,FragmentCommunication communication,Context context,FragmentItemUpdate itemUpdate,FavouriteItem favouriteItem) {
        super(new SectionParameters.Builder(R.layout.list_content)
                .headerResourceId(R.layout.list_content_headers)
                .build());
       this.topic = topic;
       mCommunicator=communication;
       mItemUpdate=itemUpdate;
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

    public interface FragmentItemUpdate {
        void updateItem(Tasks task);
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
        return new ItemViewHolder(view,mItemUpdate,favouriteItem,mCommunicator);
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
                final EditText uContent;
                Toast.makeText(context, "Uzun Tıklanılan item numarası "+position, Toast.LENGTH_LONG).show();

                final MaterialDialog dialog =
                        new MaterialDialog.Builder(context)
                                .title("Düzenle")
                                .customView(R.layout.custom_dialog, true)
                                .positiveText("Onayla")
                                .negativeText("Kapat")
                                .build();

                h.rootView = dialog.getActionButton(DialogAction.POSITIVE);
                uContent = dialog.getCustomView().findViewById(R.id.updateContent);
                uContent.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                h.rootView.setEnabled(s.toString().trim().length() > 0);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });

                final Calendar myCalendar = Calendar.getInstance();
                final String myFormat = "yyyy-MM-dd HH:mm";
                final SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                final TextInputLayout textInputLayout = dialog.getCustomView().findViewById(R.id.dateEditTextLayout);
                final EditText edittext= dialog.getCustomView().findViewById(R.id.updateDateTime);

                final TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        myCalendar.set(Calendar.HOUR_OF_DAY,i);
                        myCalendar.set(Calendar.MINUTE,i1);
                        Calendar c = Calendar.getInstance();
                        if (myCalendar.getTimeInMillis() >= c.getTimeInMillis()) {
                            edittext.setText(sdf.format(myCalendar.getTime()));
                            textInputLayout.setErrorEnabled(false);
                        } else {
                            textInputLayout.setErrorEnabled(true);
                            edittext.setText(null);
                            textInputLayout.setError("Girilen zaman geçersizdir.");
                        }

                    }
                };

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        int hour=myCalendar.get(Calendar.HOUR_OF_DAY);
                        int minute=myCalendar.get(Calendar.MINUTE);
                        new TimePickerDialog(context,timePicker,hour,minute, DateFormat.is24HourFormat(context)).show();
                    }
                };



                edittext.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                        datePickerDialog.show();
                    }
                });

                int widgetColor = ThemeSingleton.get().widgetColor;
                MDTintHelper.setTint(
                        uContent,
                        widgetColor == 0 ? ContextCompat.getColor(context, R.color.accent) : widgetColor);


                h.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filteredList.get(position).setContent(uContent.getText().toString());
                        if(TextUtils.isEmpty(edittext.getText())){
                            h.itemUpdate.updateItem(filteredList.get(position));
                            dialog.dismiss();
                        } else {
                            filteredList.get(position).setEndDate(edittext.getText().toString());
                            h.itemUpdate.updateItem(filteredList.get(position));
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
                h.rootView.setEnabled(false);

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

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
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
        public  View rootView;
        FragmentItemUpdate itemUpdate;
        FavouriteItem favouriteItem;
        FragmentCommunication mComminication;
        ItemViewHolder(View view,FragmentItemUpdate itemUpdate,FavouriteItem favouriteItem,FragmentCommunication communication) {
            super(view);
            rootView = view;
            this.favouriteItem=favouriteItem;
            this.itemUpdate=itemUpdate;
            this.mComminication = communication;
            ButterKnife.bind(this, view);
        }

        @Override public boolean onLongClick(View v) {
            //itemRemove.deleteItem(title,getAdapterPosition());
            return true;
        }
    }



}











