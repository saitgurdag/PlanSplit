package com.example.plansplit.Models.Objects;


import java.io.Serializable;
import java.util.ArrayList;

public class Groups implements Serializable {
    private String group_name;
    private String group_type;
    private String key;

    ArrayList<String> group_members = new ArrayList<String>();
    ArrayList<Expense> group_expenses = new ArrayList<Expense>();
    ArrayList<ToDoList> group_todo_list = new ArrayList<ToDoList>();
    ArrayList<GroupNotification> group_notifications = new ArrayList<GroupNotification>();

    public Groups() {
    }

    public Groups(String group_name, String group_type) {
        this.group_name = group_name;
        this.group_type = group_type;
    }

    public void addFriend(String friendKey){
        group_members.add(friendKey);
    }

    public ArrayList<String> getGroup_members() {
        return group_members;
    }

    public void setGroup_members(ArrayList<String> group_members) {
        this.group_members = group_members;
    }

    public ArrayList<Expense> getGroup_expenses() {
        return group_expenses;
    }

    public void setGroup_expenses(ArrayList<Expense> group_expenses) {
        this.group_expenses = group_expenses;
    }

    public ArrayList<ToDoList> getGroup_todo_list() {
        return group_todo_list;
    }

    public void setGroup_todo_list(ArrayList<ToDoList> group_todo_list) {
        this.group_todo_list = group_todo_list;
    }

    public ArrayList<GroupNotification> getGroup_notifications() {
        return group_notifications;
    }

    public void setGroup_notifications(ArrayList<GroupNotification> group_notifications) {
        this.group_notifications = group_notifications;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}