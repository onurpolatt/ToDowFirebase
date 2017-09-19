package com.example.onurp.myapplication;

/**
 * Created by onurp on 7.09.2017.
 */

public class SectionHeaders {
    public String sectionName;
    public int id;
    public SectionHeaders(){

    }
    public SectionHeaders(String sectionName){
        this.sectionName=sectionName;
    }
    public SectionHeaders(int id,String sectionName){
        this.id=id;
        this.sectionName=sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
