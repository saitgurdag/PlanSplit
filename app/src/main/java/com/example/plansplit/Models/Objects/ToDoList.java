package com.example.plansplit.Models.Objects;

import com.example.plansplit.Controllers.HomeActivity;

import java.util.ArrayList;

public class ToDoList {
    private String description;
    private String status="waiting"; //waiting,reserved
    private String who_added;
    private String who_added_id;
    private String resp_person_name="none";
    private String resp_person="none";
    private String key;

    public String getKey() {
        return key;
    }

    public String getWho_added_id() {
        return who_added_id;
    }

    public String getResp_person() {
        return resp_person;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setWho_Added(String who_added) {
        this.who_added = who_added;
    }

    public ToDoList(String description, String who_added) {
        this.description = description;
        this.who_added = who_added;

    }
    public ToDoList(String description, String who_added, String key,String who_added_id) {
        this.description = description;
        this.who_added = who_added;
        this.key=key;
        this.who_added_id=who_added_id;
    }
    
    public ToDoList(String description, String who_added, String resp_person_name,String key,String who_added_id) {
            this.description = description;
            this.who_added = who_added;
            this.resp_person_name = resp_person_name;
            this.key=key;
            this.who_added_id=who_added_id;
    }
    public ToDoList(String description, String who_added, String resp_person_name,String resp_person,String key,String status,String who_added_id) {
        this.description = description;
        this.who_added = who_added;
        this.resp_person_name = resp_person_name;
        this.key=key;
        this.status=status;
        this.resp_person=resp_person;
        this.who_added_id=who_added_id;
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
}
