package com.example.plansplit.Models.Objects;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Groups implements Serializable {
    private String group_name;
    private String group_type;
    private String groupKey;
    private String key;
    private float totDebt;

    public float getTotDebt() {
        return totDebt;
    }

    public void addDebt(float f){
        totDebt+=f;
    }

    public HashMap<String, Expense> expenses = new HashMap<>();

    ArrayList<String> group_members = new ArrayList<String>();
    ArrayList<ToDoList> group_todo_list = new ArrayList<ToDoList>();

    public Groups() {

    }

    public Groups(String group_name, String group_type) {
        this.group_name = group_name;
        this.group_type = group_type;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public void addFriend(String friendKey) {
        group_members.add(friendKey);
    }

    public void removeFriend(String friendKey){
        group_members.remove(friendKey);
    }

    public ArrayList<String> getGroup_members() {
        return group_members;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getGroup_type() {
        return group_type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}