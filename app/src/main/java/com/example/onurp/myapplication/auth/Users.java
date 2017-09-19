package com.example.onurp.myapplication.auth;

/**
 * Created by onurp on 15.09.2017.
 */

public class Users {
    private String email;
    private String name;
    public Users(String email,String name){
        this.email=email;
        this.name=name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
