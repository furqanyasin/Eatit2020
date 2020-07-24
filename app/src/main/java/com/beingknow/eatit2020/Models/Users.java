package com.beingknow.eatit2020.Models;

import java.util.PriorityQueue;

public class Users {

    private String Name;
    private String Password;
    private String Phone;


    public Users() {
    }

    public Users(String name, String password) {
        Name = name;
        Password = password;

    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
