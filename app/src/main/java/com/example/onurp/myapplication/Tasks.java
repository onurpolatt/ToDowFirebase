package com.example.onurp.myapplication;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static android.content.ContentValues.TAG;

/**
 * Created by onurp on 25.08.2017.
 */

public class Tasks implements Parcelable {
    public String content;
    public String endDate;
    public String importanceLevel;
    public String sectionName;
    public Integer sectionGroup;
    public String idRow,idSection,sSectionGroup;
    public Integer intIdRow,intIdSection;
    public boolean isFavourite,isSelected;

    public Tasks(){

    }

    public  Tasks  (Integer id,String content,String importanceLevel,String endDate,Integer sectionGroup){
        this.intIdRow=id;
        this.content=content;
        this.endDate=endDate;
        this.importanceLevel=importanceLevel;
        this.sectionGroup=sectionGroup;
        this.isFavourite=false;
        this.isSelected=false;
    }

    public  Tasks  (String id,String content,String importanceLevel,String endDate,String sectionGroup){
        this.idRow=id;
        this.sSectionGroup=sectionGroup;
        this.content=content;
        this.endDate=endDate;
        this.importanceLevel=importanceLevel;
        this.isFavourite=false;
        this.isSelected=false;
    }



    public  Tasks (Integer id,String sectionName){
        this.intIdSection=id;
        this.sectionName=sectionName;
    }


    public String getIdRow() {
        return idRow;
    }


    public String getIdSection() {
        return idSection;
    }


    public String getSectionName() {
        return sectionName;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImportanceLevel() {
        return importanceLevel;
    }

    public void setImportanceLevel(String importanceLevel) {
        this.importanceLevel = importanceLevel;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getsSectionGroup() {
        return sSectionGroup;
    }

    public void setsSectionGroup(String sSectionGroup) {
        this.sSectionGroup = sSectionGroup;
    }

    public static String getSectionGroup(String date){
        String response="";
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern("yyyy-MM-dd");

        LocalDate localDate = LocalDate.now();
        String newDate=date.split(" ")[0];
        LocalDate ld=dateTimeFormatter.parseLocalDate(newDate);
        int days= Days.daysBetween(localDate,ld).getDays();
        Log.e(TAG,"İKİ TARİH ARASI GÜN SAYISI"+days);
        Log.e(TAG,"TARİHLER"+localDate+"/////"+ld);

        if(days == 0){
            response="TODAY";
        }
        else if(days == 1){
            response="TOMORROW";
        }
        else if(days >1 && days<7){
            response="THIS WEEK";
        }
        else{
            response="NEXT WEEK";
        }
        return response;
    }

    public Tasks(Parcel in){

        content=in.readString();
        endDate=in.readString();
        idRow=in.readString();
        importanceLevel=in.readString();
        isFavourite=in.readInt() == 1;
        sSectionGroup=in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(endDate);
        dest.writeString(idRow);
        dest.writeString(importanceLevel);
        dest.writeInt(isFavourite ? 1 : 0);
        dest.writeString(sSectionGroup);
    }

    public static final Parcelable.Creator<Tasks> CREATOR = new Parcelable.Creator<Tasks>() {
        public Tasks createFromParcel(Parcel in) {
            return new Tasks(in);
        }

        public Tasks[] newArray(int size) {
            return new Tasks[size];
        }
    };
}
