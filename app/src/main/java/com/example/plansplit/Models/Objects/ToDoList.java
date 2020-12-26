package com.example.plansplit.Models.Objects;

import com.example.plansplit.Controllers.HomeActivity;

import java.util.ArrayList;

public class ToDoList {
    private String description;
    private String status="waiting"; //waiting,reserved
    private String who_added;
    private String resp_person_name="none";
    private String resp_person="none";
    private String key;
    final HomeActivity home = new HomeActivity();

    ArrayList<Person> responsible_person = new ArrayList<Person>();
    ArrayList<Groups> the_group = new ArrayList<Groups>();

    public String getKey() {
        return key;
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
    public ToDoList(String description, String who_added, String key) {
        this.description = description;
        this.who_added = who_added;
        this.key=key;
    }
    
    public ToDoList(String description, String who_added, String resp_person_name,String key) {
            this.description = description;
            this.who_added = who_added;
            this.resp_person_name = resp_person_name;
            this.key=key;
    }
    public ToDoList(String description, String who_added, String resp_person_name,String resp_person,String key,String status) {
        this.description = description;
        this.who_added = who_added;
        this.resp_person_name = resp_person_name;
        this.key=key;
        this.status=status;
        this.resp_person=resp_person;
    }

   /* public ToDoList(String description, String status, int symbol) {
        this.description = description;
        this.status = status;

    }*/

    public String getWho_added() {
          System.out.println(who_added);
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
    


}
