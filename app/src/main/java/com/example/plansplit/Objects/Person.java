package com.example.plansplit.Objects;

public class Person {

    private String name;
    private String mail;
    private int cardView_shareMethodPersonPicture;
    private int checkBox_shareMethodPersonCheckBox;

    public String getName() {
        return name;
    }

    public int getCardView_shareMethodPersonPicture() {
        return cardView_shareMethodPersonPicture;
    }

    public int getCheckBox_shareMethodPersonCheckBox() {
        return checkBox_shareMethodPersonCheckBox;
    }


    public Person(String name, int cardView_shareMethodPersonPicture, int checkBox_shareMethodPersonCheckBox){
        this.name = name;
        this.cardView_shareMethodPersonPicture = cardView_shareMethodPersonPicture;
        this.checkBox_shareMethodPersonCheckBox = checkBox_shareMethodPersonCheckBox;
    }

    public Person(String name,String mail , int cardView_addgroupsPicture){
        this.name=name;
        this.mail=mail;
        this.cardView_addgroupsPicture=cardView_addgroupsPicture;
    }

    public String getMail() {
        return mail;
    }

    public int getCardView_addgroupsPicture() {
        return cardView_addgroupsPicture;
    }

    private int cardView_addgroupsPicture;



}
