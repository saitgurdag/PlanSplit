package com.example.plansplit.ui;

public class Addgroups_Person {
    private String name;
    private String mail;

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public int getCardView_addgroupsPicture() {
        return cardView_addgroupsPicture;
    }

    private int cardView_addgroupsPicture;

    public Addgroups_Person(String name,String mail , int cardView_addgroupsPicture){
    this.name=name;
    this.mail=mail;
    this.cardView_addgroupsPicture=cardView_addgroupsPicture;
    }
}
