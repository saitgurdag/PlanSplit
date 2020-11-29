package com.example.plansplit.Objects;

import java.util.ArrayList;

public class Groups {
    private int group_id;
    private String group_name;
    private String group_type;
    private int group_photo;
    private int group_notification_symbol;
    private int group_balance_sheet;

    private ArrayList<Person> persons_inGroup = new ArrayList<Person>();
    private ArrayList<ToDoList> toDoList_inGroup = new ArrayList<ToDoList>();

    public Groups(int group_id, String group_name, String group_type, int group_photo, int group_notification_symbol, int group_balance_sheet) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_type = group_type;
        this.group_photo = group_photo;
        this.group_notification_symbol = group_notification_symbol;
        this.group_balance_sheet = group_balance_sheet;
    }

    public void addToDo(ToDoList toDo) {
        toDoList_inGroup.add(toDo);
    }


    public ArrayList<Person> getPersons_inGroup() {
        return persons_inGroup;
    }

    public void setPersons_inGroup(ArrayList<Person> persons_inGroup) {
        this.persons_inGroup = persons_inGroup;
    }

    public int getGroup_notification_symbol() {
        return group_notification_symbol;
    }

    public void setGroup_notification_symbol(int group_notification_symbol) {
        this.group_notification_symbol = group_notification_symbol;
    }

    public String getGroup_balance_sheet() {
        return String.valueOf(group_balance_sheet) + "  TL";
    }

    public void setGroup_balance_sheet(int group_balance_sheet) {
        this.group_balance_sheet = group_balance_sheet;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
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

    public int getGroup_photo() {
        return group_photo;
    }

    public void setGroup_photo(int group_photo) {
        this.group_photo = group_photo;
    }
}
