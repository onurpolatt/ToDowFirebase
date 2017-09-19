package com.example.onurp.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by onurp on 25.08.2017.
 */

public class Tasks implements Parcelable {
    public String content;
    public String header;
    public String endDate;
    public String importanceLevel;
    public String sectionName;
    public Integer sectionGroup;
    public String idRow,idSection,sSectionGroup;
    public Integer intIdRow,intIdSection;
    public boolean isRow;


    public Tasks(){

    }
    public  Tasks  (String header,String content,String importanceLevel,String endDate,Integer sectionGroup){
        this.content=content;
        this.header=header;
        this.endDate=endDate;
        this.importanceLevel=importanceLevel;
        this.sectionGroup=sectionGroup;
        this.isRow=true;
    }


    public  Tasks  (Integer id,String header,String content,String importanceLevel,String endDate,Integer sectionGroup){
        this.intIdRow=id;
        this.content=content;
        this.header=header;
        this.endDate=endDate;
        this.importanceLevel=importanceLevel;
        this.sectionGroup=sectionGroup;
        this.isRow=true;
    }

    public  Tasks  (String id,String header,String content,String importanceLevel,String endDate,String sectionGroup){
        this.idRow=id;
        this.sSectionGroup=sectionGroup;
        this.content=content;
        this.header=header;
        this.endDate=endDate;
        this.importanceLevel=importanceLevel;
        this.isRow=true;
    }


    public  Tasks (String id,String sectionName){
        this.idSection=id;
        this.sectionName=sectionName;
        this.isRow=false;
    }

    public  Tasks (Integer id,String sectionName){
        this.intIdSection=id;
        this.sectionName=sectionName;
        this.isRow=false;
    }

    public String getsSectionGroup() {
        return sSectionGroup;
    }

    public void setsSectionGroup(String sSectionGroup) {
        this.sSectionGroup = sSectionGroup;
    }

    public Integer getIntIdRow() {
        return intIdRow;
    }

    public void setIntIdRow(Integer intIdRow) {
        this.intIdRow = intIdRow;
    }

    public Integer getIntIdSection() {
        return intIdSection;
    }

    public void setIntIdSection(Integer intIdSection) {
        this.intIdSection = intIdSection;
    }

    public String getIdRow() {
        return idRow;
    }

    public void setIdRow(String idRow) {
        this.idRow = idRow;
    }

    public String getIdSection() {
        return idSection;
    }

    public void setIdSection(String idSection) {
        this.idSection = idSection;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getSectionGroup() {
        return sectionGroup;
    }

    public void setSectionGroup(Integer sectionGroup) {
        this.sectionGroup = sectionGroup;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
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

    public boolean isRow(){
        return isRow;
    }

    public Tasks(Parcel in){

        content=in.readString();
        endDate=in.readString();
        header=in.readString();
        idRow=in.readString();
        importanceLevel=in.readString();
        isRow=in.readInt() == 1;
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
        dest.writeString(header);
        dest.writeString(idRow);
        dest.writeString(importanceLevel);
        dest.writeInt(isRow ? 1 : 0);
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
