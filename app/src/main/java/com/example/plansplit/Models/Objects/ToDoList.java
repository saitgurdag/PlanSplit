package com.example.plansplit.Models.Objects;

import java.util.ArrayList;

public class ToDoList {
    private String description;
    private String status;
    private int symbol;
    private String who_added;
    private String resp_person_name;

    ArrayList<Person> responsible_person = new ArrayList<Person>();
    ArrayList<Groups> the_group = new ArrayList<Groups>();

    public ToDoList(String description, String status, int symbol, String who_added, String resp_person_name) {
        this.description = description;
        this.status = status;
        this.symbol = symbol;
        this.who_added = who_added;
        this.resp_person_name = resp_person_name;
    }

    public ToDoList(String description, String status, int symbol) {
        this.description = description;
        this.status = status;
        this.symbol = symbol;
    }

    public String getWho_added() {
        return who_added;
    }

    public void setWho_added(String who_added) {
        this.who_added = who_added;
    }

    public String getResp_person_name() {
        return resp_person_name;
    }

    public void setResp_person_name(String resp_person_name) {
        this.resp_person_name = resp_person_name;
    }

    public ArrayList<Groups> getThe_group() {
        return the_group;
    }

    public void setThe_group(ArrayList<Groups> the_group) {
        this.the_group = the_group;
    }

    public ArrayList<Person> getResponsible_person() {
        return responsible_person;
    }

    public void setResponsible_person(ArrayList<Person> responsible_person) {
        this.responsible_person = responsible_person;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }
}
